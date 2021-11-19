package com.imob.lib.sslib.server;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.FileMsg;
import com.imob.lib.sslib.msg.StringMsg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.peer.PeerListenerGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNode implements INode {

    private ExecutorService createExecutorService = Executors.newSingleThreadExecutor();

    private ExecutorService monitorExecutorService = Executors.newSingleThreadExecutor();

    private Byte createLock = 0x0;

    private boolean isCreating = false;
    private boolean isDestroyed = false;

    private boolean isDestroyedCallbacked = false;
    private boolean isCorruptedCallbacked = false;

    private ServerSocket serverSocket;
    private ServerListenerGroup serverListenerGroup = new ServerListenerGroup();
    private PeerListenerGroup peerListenerGroup = new PeerListenerGroup();

    private Queue<Peer> connectedPeers = new ConcurrentLinkedQueue<>();

    private long timeout;


    private static final String S_TAG = "ServerNode";
    private String tag;


    public ServerNode(ServerListener serverListener, PeerListener incomingPeerEventListener) {
        this.serverListenerGroup.add(new ServerListenerWrapper(serverListener));
        this.peerListenerGroup.add(new PeerListenerWrapper(incomingPeerEventListener, false));

        tag = S_TAG + " # " + hashCode();
    }

    public boolean isInUsing() {
        return (isCreating || isRunning()) && !isDestroyed;
    }

    public void monitorServerStatus(ServerListener listener) {
        serverListenerGroup.add(listener);
    }

    public void unmonitorServerStatus(ServerListener listener){
        serverListenerGroup.remove(listener);
    }

    public void monitorIncomingPeersStatus(PeerListener peerListener) {
        peerListenerGroup.add(peerListener);
    }

    public boolean create(long timeout) {

        if (isCreating || isRunning() || isDestroyed()) {
            return false;
        }
        this.timeout = timeout;
        this.isCreating = true;

        handleCreate();
        Logger.i(tag, "create");
        return true;
    }

    private void handleCreate() {
        createExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                doCreateStuff();
            }
        });
    }

    private void doCreateStuff() {
        if (isDestroyed()) {
            Logger.i(tag, "do create stuff, it's already destroyed, no need to take further actions, just stop creating process");
            return;
        }
        synchronized (createLock) {

            if (isDestroyed()) {
                Logger.i(tag, "do create stuff, it's already destroyed, no need to take further actions, just stop creating process");
                return;
            }

            try {
                serverSocket = new ServerSocket(0);
                isCreating = false;
                serverListenerGroup.onCreated(this);

                startMonitorIncomingClients();

            } catch (IOException e) {
                Logger.e(e);
                serverListenerGroup.onCreateFailed(e);
            } finally {
                isCreating = false;
            }
        }
    }


    private void startMonitorIncomingClients() {
        monitorExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                handleMonitorIncomingClients();
            }
        });
    }


    private void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
    }

    public void destroy() {
        if (!isDestroyed) {
            Logger.i(tag, "destroy called");
            synchronized (createLock) {
                Logger.i(tag, "do destroy stuff");
                isDestroyed = true;
                doDestroyStuff();
                callbackDestroyed();
            }
        }
    }

    private void callbackCorrupted(String msg, Exception e) {
        if (!isCorruptedCallbacked) {
            isCorruptedCallbacked = true;
            serverListenerGroup.onCorrupted(this, msg, e);
        }
    }


    private void callbackDestroyed() {
        if (!isDestroyedCallbacked) {
            serverListenerGroup.onDestroyed(this);
        }
    }

    private void doDestroyStuff() {
        closeServerSocket();
        destroyAllConnectedPeers();
    }

    private void destroyAllConnectedPeers() {
        for (Peer peer : connectedPeers) {
            peer.destroy();
        }
    }

    private void handleMonitorIncomingClients() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                manageIncomingClient(socket);
            }
        } catch (IOException e) {
            Logger.e(e);
            destroy();
            callbackCorrupted("server socket connection corrupted due to error occured while monitoring incoming clients", e);
        }
    }


    /**
     * @param id
     * @param msg
     * @return true - msg is valid and has connected peers
     */
    public boolean broadcastStringMsg(String id, String msg) {
        if (connectedPeers.size() <= 0 || id == null || msg == null || id.isEmpty() || msg.isEmpty()) {
            return false;
        } else {
            for (Peer peer : connectedPeers) {
                peer.sendMessage(StringMsg.create(id, msg));
            }
        }
        return true;
    }


    public boolean broadcastFileMsg(String id, String filePath) {
        if (connectedPeers.size() <= 0 || id == null || filePath == null || id.isEmpty() || filePath.isEmpty() || !new File(filePath).exists() || !new File(filePath).canRead() || new File(filePath).isDirectory()) {
            return false;
        } else {
            for (Peer peer : connectedPeers) {
                try {
                    peer.sendMessage(FileMsg.create(id, filePath));
                } catch (FileNotFoundException e) {
                    Logger.e(e);
                    return false;
                }
            }
        }
        return true;
    }


    public Queue<Peer> getConnectedPeers() {
        return connectedPeers;
    }

    private void manageIncomingClient(Socket socket) {
        PeerListener listener = new PeerListenerAdapter() {
            @Override
            public void onCorrupted(Peer peer, String msg, Exception e) {
                super.onCorrupted(peer, msg, e);
                removePeer(peer);
            }

            @Override
            public void onDestroy(Peer peer) {
                super.onDestroy(peer);
                removePeer(peer);
            }
        };
        peerListenerGroup.add(listener);

        Peer peer = new Peer(socket, ServerNode.this, peerListenerGroup);

        peer.setTimeout(timeout);
        serverListenerGroup.onIncomingClient(this, peer);
        connectedPeers.add(peer);
    }

    public void removePeer(Peer peer) {
        connectedPeers.remove(peer);
    }

    public boolean isRunning() {
        return serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public String getServerSocketInfo() {
        if (isRunning()) {
            return serverSocket.getLocalSocketAddress().toString();
        } else {
            return "server is not running";
        }
    }

    public Peer findPeerByTag(String tag) {
        if (tag == null || tag.equals("")) {
            return null;
        } else {
            for (Peer peer : connectedPeers) {
                if (peer.getTag().equals(tag)) {
                    return peer;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return tag;
    }

    @Override
    public boolean isServerNode() {
        return true;
    }
}
