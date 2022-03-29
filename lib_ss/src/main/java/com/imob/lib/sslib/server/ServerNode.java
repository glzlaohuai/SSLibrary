package com.imob.lib.sslib.server;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.FileMsg;
import com.imob.lib.sslib.msg.StringMsg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.peer.PeerListenerGroup;
import com.imob.lib.sslib.peer.PeerListenerWrapper;
import com.imob.lib.sslib.utils.SSThreadFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class ServerNode implements INode {

    private ExecutorService createExecutorService = Executors.newSingleThreadExecutor(SSThreadFactory.build("server-create"));

    private ExecutorService monitorExecutorService = Executors.newSingleThreadExecutor(SSThreadFactory.build("server-moni"));

    private Byte createLock = 0x0;

    private boolean isCreating = false;
    private boolean isDestroyed = false;

    private boolean isDestroyedCallbacked = false;
    private boolean isCorruptedCallbacked = false;

    private ServerSocket serverSocket;
    private ServerListenerGroup serverListenerGroup = new ServerListenerGroup();
    private PeerListenerGroup peerListenerGroup = new PeerListenerGroup();

    private Queue<Peer> connectedPeers = new ConcurrentLinkedQueue<>();

    private final static ServerListenerGroup globalServerListener = new ServerListenerGroup();
    private final static ServerListenerGroup monitoredServerListener = new ServerListenerGroup();
    private final static ServerListenerGroup routerServerListener = new ServerListenerGroup();

    private static AtomicReference<ServerNode> activeServerNode = new AtomicReference<>();

    private long timeout;

    private static final String S_TAG = "ServerNode";
    private String tag;

    static {
        routerServerListener.add(globalServerListener);
        routerServerListener.add(monitoredServerListener);
        routerServerListener.add(new ServerListenerAdapter() {
            @Override
            public void onCreated(ServerNode serverNode) {
                super.onCreated(serverNode);
                activeServerNode.set(serverNode);
            }

            @Override
            public void onDestroyed(ServerNode serverNode) {
                super.onDestroyed(serverNode);
                activeServerNode.compareAndSet(serverNode, null);
            }
        });
    }

    public ServerNode(ServerListener serverListener, PeerListener incomingPeerEventListener) {
        this.serverListenerGroup.add(new ServerListenerWrapper(serverListener, false));
        this.serverListenerGroup.add(ServerNode.routerServerListener);

        this.peerListenerGroup.add(new PeerListenerWrapper(incomingPeerEventListener, false));

        tag = S_TAG + " # " + hashCode();
    }

    public boolean isInUsing() {
        return (isCreating || isRunning()) && !isDestroyed;
    }

    public void monitorServerStatus(ServerListener listener) {
        serverListenerGroup.add(listener);
    }

    public void unmonitorServerStatus(ServerListener listener) {
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


    private void handleDestroy() {
        createExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (createLock) {
                    if (!isDestroyed) {
                        isDestroyed = true;
                        Logger.i(tag, "do destroy stuff");
                        doDestroyStuff();
                        callbackDestroyed();
                    }
                }
            }
        });
    }

    public void destroy() {
        if (!isDestroyed) {
            Logger.i(tag, "destroy called");
            handleDestroy();
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
            peer.destroy("destroy all connected peers of servernode after related servernode destroyed", null);
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

    public ServerSocket getServerSocket() {
        return serverSocket;
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

    public final static void setGlobalServerListener(ServerListenerGroup globalServerListener) {
        ServerNode.globalServerListener.clear();
        ServerNode.globalServerListener.add(globalServerListener);
    }


    public final static void monitorServerNodeState(ServerListener serverListener) {
        ServerNode.monitoredServerListener.add(serverListener);
    }

    public final static void unmonitorServerNodeState(ServerListener serverListener) {
        ServerNode.monitoredServerListener.remove(serverListener);
    }

    public static ServerNode getActiveServerNode() {
        return activeServerNode.get();
    }
}
