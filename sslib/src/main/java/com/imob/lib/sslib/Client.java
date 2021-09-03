package com.imob.lib.sslib;

import android.text.TextUtils;

import com.imob.lib.sslib.send.msg.IMsg;
import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class Client {

    private static final String TAG = "Client";


    private String ip;
    private int port;

    private byte[] lock = new byte[1];

    private ExecutorService connectService = Executors.newSingleThreadExecutor();

    private Peer connectedPeer;

    private OnClientStateListener listener;
    private boolean isConnected;
    private boolean isConnecting;

    public interface OnClientStateListener {
        void onConnectFailed(String msg, Exception e);

        void onAlreadyConnected();

        void onConnected();

        void onConnectCoruppted(String error, Exception e);

        void onAlreadyStopped();

        void onStop();

    }

    public Client(@NonNull String ip, int port, @NonNull OnClientStateListener listener) {
        this.ip = ip;
        this.port = port;
        this.listener = listener;
    }


    private void cleanup() {
        if (isConnected) {
            isConnecting = false;
            isConnected = false;
            if (connectedPeer != null) {
                connectedPeer.destroy();
                connectedPeer = null;
            }
        }
    }

    public void connect() {

        if (TextUtils.isEmpty(ip) || port <= 0) {
            listener.onConnectFailed("invalid ip or port", null);
            return;
        }

        if (isConnecting) {
            Logger.i(TAG, "is still connecting");
            return;
        }
        if (!isConnected) {
            synchronized (lock) {
                isConnecting = true;
                if (!isConnected) {
                    connectService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket socket = new Socket(ip, port);

                                connectedPeer = new Peer(socket, new Peer.OnPeerListener() {
                                    @Override
                                    public void onConnected(String ip, int port) {
                                        isConnected = true;
                                        isConnecting = false;

                                        listener.onConnected();
                                    }

                                    @Override
                                    public void onConnectFailed(String msg, Exception e) {
                                        isConnecting = false;
                                        isConnected = false;

                                        listener.onConnectFailed(msg, e);
                                    }

                                    @Override
                                    public void onConnectCorrupted(String msg, Exception e) {
                                        cleanup();
                                    }

                                    @Override
                                    public void onConnectDestroyed() {
                                        cleanup();
                                    }

                                    @Override
                                    public void onSendMessageToQueue(IMsg msg) {

                                    }

                                    @Override
                                    public void onSendMessageStarted(IMsg msg) {

                                    }

                                    @Override
                                    public void onSendMessageFailed(IMsg msg, String error, Exception e) {

                                    }

                                    @Override
                                    public void onSendMessageChunk(IMsg msg, byte chunkType, int chunkLen) {

                                    }

                                    @Override
                                    public void onSendMessageCompleted(IMsg msg) {

                                    }

                                    @Override
                                    public void onIncomingMessage(String msgID, byte msgType, byte userDefiniedType) {

                                    }

                                    @Override
                                    public void onIncomingMessageChunk(String msgID, byte msgType, byte userDefiniedType, byte[] bytes) {

                                    }

                                    @Override
                                    public void onIncomingMessageCompleted(String msgID, byte msgType, byte userDefiniedType) {

                                    }

                                    @Override
                                    public void onIncomingMessageInteruppted(String msgID, byte msgType, byte userDefiniedType) {

                                    }
                                });

                            } catch (IOException e) {
                                Logger.e(e);
                                listener.onConnectFailed("socket connect failed", e);
                            }
                        }
                    });
                } else {
                    listener.onAlreadyConnected();
                }
            }
        } else {
            listener.onAlreadyConnected();
        }
    }


    public boolean stop() {

        if (isConnecting) {
            Logger.i(TAG, "client is still in connecting prorgess, stop operation failed");
            return false;
        }

        if (isConnected) {
            cleanup();
            listener.onStop();
        } else {
            listener.onAlreadyStopped();
        }
        return true;
    }


    public void sendMessage(IMsg msg) {
        if (msg != null && msg.isValid() && isConnected && connectedPeer != null) {
            connectedPeer.send(msg);
        }
    }


}
