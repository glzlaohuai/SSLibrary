package com.imob.lib.sslib.server;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;

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


    private boolean isCreating = false;
    private boolean isDestroyed = false;

    private boolean isDestroyedCallbacked = false;
    private boolean isCorruptedCallbacked = false;

    private ServerSocket serverSocket;
    private ServerListener serverListener;
    private PeerListener peerListener;

    private Queue<Peer> connectedPeers = new ConcurrentLinkedQueue<>();

    private long timeout;

    public ServerNode(ServerListener serverListener, PeerListener peerListener) {
        this.serverListener = serverListener;
        this.peerListener = peerListener;
    }

    public boolean isInUsing() {
        return (isCreating || isRunning()) && !isDestroyed;
    }

    public boolean create(long timeout) {

        if (isCreating || isRunning() || isDestroyed()) {
            return false;
        }
        this.timeout = timeout;

        handleCreate();
        return true;
    }

    private void handleCreate() {
        createExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(0);
                    isCreating = false;
                    serverListener.onCreated();

                    startMonitorIncomingClients();

                } catch (IOException e) {
                    Logger.e(e);
                    serverListener.onCreateFailed(e);
                } finally {
                    isCreating = false;
                }
            }
        });
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
            isDestroyed = true;
            doDestroyStuff();
            callbackDestroyed();
        }
    }

    private void callbackCorrupted(String msg, Exception e) {
        if (!isCorruptedCallbacked) {
            isCorruptedCallbacked = true;
            serverListener.onCorrupted(msg, e);
        }
    }


    private void callbackDestroyed() {
        if (!isDestroyedCallbacked) {
            serverListener.onDestroyed();
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
     * @param msg
     * @return  false - has no connected peers or msg is null or invalid | true - the opposite
     */
    public boolean broadcast(Msg msg) {
        if (connectedPeers.size() <= 0 || msg == null || !msg.isValid()) return false;
        for (Peer peer : connectedPeers) {
            peer.sendMessage(msg);
        }
        return true;
    }

    public Queue<Peer> getConnectedPeers() {
        return connectedPeers;
    }

    private void manageIncomingClient(Socket socket) {
        Peer peer = new Peer(socket, ServerNode.this, new PeerListener() {
            @Override
            public void onMsgIntoQueue(Peer peer, String id) {
                peerListener.onMsgIntoQueue(peer, id);
            }

            @Override
            public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {
                peerListener.onConfirmMsgIntoQueue(peer, id, soFar, total);
            }

            @Override
            public void onMsgSendStart(Peer peer, String id) {
                peerListener.onMsgSendStart(peer, id);
            }

            @Override
            public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {
                peerListener.onConfirmMsgSendStart(peer, id, soFar, total);
            }

            @Override
            public void onMsgSendSucceeded(Peer peer, String id) {
                peerListener.onMsgSendSucceeded(peer, id);
            }

            @Override
            public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
                peerListener.onConfirmMsgSendSucceeded(peer, id, soFar, total);
            }

            @Override
            public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
                peerListener.onMsgSendFailed(peer, id, msg, exception);
            }

            @Override
            public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
                peerListener.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);

            }

            @Override
            public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
                peerListener.onMsgChunkSendSucceeded(peer, id, chunkSize);
            }

            @Override
            public void onIOStreamOpened(Peer peer) {
                peerListener.onIOStreamOpened(peer);
            }

            @Override
            public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
                peerListener.onIOStreamOpenFailed(peer, errorMsg, exception);
            }

            @Override
            public void onCorrupted(Peer peer, String msg, Exception e) {
                peerListener.onCorrupted(peer, msg, e);
                removePeer(peer);
            }

            @Override
            public void onDestroy(Peer peer) {
                peerListener.onDestroy(peer);
                removePeer(peer);
            }

            @Override
            public void onTimeoutOccured(Peer peer) {
                peerListener.onTimeoutOccured(peer);
            }

            @Override
            public void onIncomingMsg(Peer peer, String id, int available) {
                peerListener.onIncomingMsg(peer, id, available);
            }

            @Override
            public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {
                peerListener.onIncomingMsgChunkReadFailedDueToPeerIOFailed(peer, id);
            }

            @Override
            public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {
                peerListener.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, chunkBytes);
            }

            @Override
            public void onIncomingMsgReadSucceeded(Peer peer, String id) {
                peerListener.onIncomingMsgReadSucceeded(peer, id);
            }

            @Override
            public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
                peerListener.onIncomingMsgReadFailed(peer, id, total, soFar);
            }

            @Override
            public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
                peerListener.onIncomingConfirmMsg(peer, id, soFar, total);
            }

            @Override
            public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
                peerListener.onConfirmMsgSendPending(peer, id, soFar, total);
            }

            @Override
            public void onMsgSendPending(Peer peer, String id) {
                peerListener.onMsgSendPending(peer, id);
            }
        });
        peer.setTimeout(timeout);
        serverListener.onIncomingClient(peer);
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
            return serverSocket.getInetAddress().toString() + " : " + serverSocket.getLocalPort();
        } else {
            return "server is not running";
        }
    }

    @Override
    public boolean isServerNode() {
        return true;
    }
}
