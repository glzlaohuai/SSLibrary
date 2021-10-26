package com.imob.lib.sslib.server;

import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNode {

    private ExecutorService createExecutorService = Executors.newSingleThreadExecutor();

    private ExecutorService monitorExecutorService = Executors.newSingleThreadExecutor();


    private boolean isCreating = false;
    private boolean isDestroyed = false;

    private boolean isDestroyedCallbacked = false;
    private boolean isCorruptedCallbacked = false;

    private ServerSocket serverSocket;
    private ServerListener serverListener;
    private PeerListener peerListener;

    private List<Peer> connectedPeers = new ArrayList<>();

    public ServerNode(ServerListener serverListener, PeerListener peerListener) {
        this.serverListener = serverListener;
        this.peerListener = peerListener;
    }

    public boolean isInUsing() {
        return (isCreating || isRunning()) && !isDestroyed;
    }

    public boolean create() {

        if (isCreating || isRunning() || isDestroyed()) {
            return false;
        }

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


    public void broadcast(Msg msg) {
        for (Peer peer : connectedPeers) {
            peer.sendMessage(msg);
        }
    }


    private void manageIncomingClient(Socket socket) {
        Peer peer = new Peer(socket, peerListener);
        connectedPeers.add(peer);
    }

    public void removePeer(Peer peer) {
        connectedPeers.remove(peer);
    }

    public boolean isRunning() {
        return serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }


}
