package com.imob.lib.sslib;

import android.util.Log;

import com.imob.lib.sslib.send.msg.IMsg;
import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class Server {

    private static final String TAG = "Server";

    private final static ExecutorService serverCreateService = Executors.newCachedThreadPool();
    private final static ExecutorService clientMonitorService = Executors.newCachedThreadPool();

    public interface OnServerStateListener {
        void onStarted(String ip, int port);

        void onStartFailed(String msg, Exception e);

        void onStopped();

        void onAlreadyStopped();

        void onAlreadyStarted();

        void onMonitorIncoming();

        void onServerCorrupted(String msg, Exception e);

    }

    private boolean started = false;
    private boolean isInStartingProgress = false;

    private byte[] lock = new byte[1];

    private ServerSocket serverSocket;
    private OnServerStateListener listener;

    private List<Peer> connectedPeers = new ArrayList<>();

    public Server(@NonNull OnServerStateListener listener) {
        this.listener = listener;
    }

    public boolean start() {
        if (isInStartingProgress) {
            Logger.i(TAG, "still in startup progress");
            return false;
        }

        if (!started) {
            synchronized (lock) {
                if (!started) {
                    isInStartingProgress = true;
                    serverCreateService.execute(new Runnable() {
                        @Override
                        public void run() {
                            doStartUp();
                        }
                    });
                }
            }
        } else {
            listener.onAlreadyStarted();
        }
        return true;
    }


    public boolean stop() {

        if (isInStartingProgress) {
            Logger.i(TAG, "stop failed, still in start progress");
            return false;
        }

        if (started) {
            silentlyStopServer();
            listener.onStopped();
            cleanup();
        } else {
            listener.onAlreadyStopped();
        }

        return true;
    }


    private void silentlyStopServer() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Logger.e(e);
            } finally {
                serverSocket = null;
            }
        }
    }


    private void cleanup() {
        started = false;
        silentlyStopServer();
        connectedPeers.clear();
    }

    private void doStartUp() {
        try {
            serverSocket = new ServerSocket(0);
            started = true;
            isInStartingProgress = false;

            listener.onStarted(serverSocket.getLocalSocketAddress() == null ? "" : serverSocket.getLocalSocketAddress().toString(), serverSocket.getLocalPort());

            //monitor incoming sockets
            startMonitorIncomingClients();

        } catch (IOException e) {
            Logger.e(e);
            isInStartingProgress = false;
            listener.onStartFailed("error occured during server socket create process", e);
            cleanup();
        }
    }


    public void broadcast(IMsg msg) {
        if (msg.isValid() && started && connectedPeers != null) {
            for (Peer peer : connectedPeers) {
                peer.send(msg);
            }
        }
    }


    private void startMonitorIncomingClients() {
        listener.onMonitorIncoming();

        clientMonitorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        Logger.i(TAG, "incoming client: " + socket.toString());

                        Peer peer = new Peer(socket, new Peer.OnPeerListener() {
                            String TAG = "Peer # " + UUID.randomUUID().toString().hashCode();

                            @Override
                            public void onConnected(String ip, int port) {
                                Log.i(TAG, "onConnected: " + ip + ", " + port);
                            }

                            @Override
                            public void onConnectFailed(String msg, Exception e) {
                                Log.i(TAG, "onConnectFailed: " + msg + ", " + e);
                            }

                            @Override
                            public void onConnectCorrupted(String msg, Exception e) {
                                Log.i(TAG, "onConnectCorrupted: " + msg + ", " + e);
                            }

                            @Override
                            public void onConnectDestroyed() {
                                Log.i(TAG, "onConnectDestroyed: ");
                            }

                            @Override
                            public void onSendMessageToQueue(IMsg msg) {
                                Log.i(TAG, "onSendMessageToQueue: " + msg.id());
                            }

                            @Override
                            public void onSendMessageStarted(IMsg msg) {
                                Log.i(TAG, "onSendMessageStarted: " + msg.id());
                            }

                            @Override
                            public void onSendMessageFailed(IMsg msg, String error, Exception e) {
                                Log.i(TAG, "onSendMessageFailed: " + msg.id() + ", error: " + error);
                            }

                            @Override
                            public void onSendMessageChunk(IMsg msg, byte chunkType, int chunkLen) {
                                Log.i(TAG, "onSendMessageChunk: " + msg.id() + ", " + chunkType + ", " + chunkLen);
                            }

                            @Override
                            public void onSendMessageCompleted(IMsg msg) {
                                Log.i(TAG, "onSendMessageCompleted: " + msg.id());
                            }

                            @Override
                            public void onIncomingMessage(String msgID, byte msgType, byte userDefiniedType) {
                                Log.i(TAG, "onIncomingMessage: " + msgID + ", " + msgType + ", " + userDefiniedType);
                            }

                            @Override
                            public void onIncomingMessageChunk(String msgID, byte msgType, byte userDefiniedType, byte[] bytes) {
                                Log.i(TAG, "onIncomingMessageChunk: " + msgID + ", " + msgType + ", " + userDefiniedType + ", " + (bytes == null ? 0 : bytes.length));
                            }

                            @Override
                            public void onIncomingMessageCompleted(String msgID, byte msgType, byte userDefiniedType) {
                                Log.i(TAG, "onIncomingMessageCompleted: " + msgID + ", " + msgType + ", " + userDefiniedType);
                            }

                            @Override
                            public void onIncomingMessageInteruppted(String msgID, byte msgType, byte userDefiniedType) {
                                Log.i(TAG, "onIncomingMessageInteruppted: " + msgID + ", " + msgType + ", " + userDefiniedType);
                            }
                        });

                        connectedPeers.add(peer);

                    }
                } catch (IOException e) {
                    Logger.e(e);
                    //server corrupted for some reason
                    listener.onServerCorrupted("error occured during client monitoring process", e);
                    stop();
                }
            }
        });
    }


}
