package com.imob.lib.sslib.client;

import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientNode {

    private static final String ERROR_INVALID_PARAMETERS = "ip or port invalid";

    private String ip;
    private int port;

    private ClientListener listener;

    private boolean isCreating = false;
    private Peer peer;

    private ExecutorService socketCreateService = Executors.newSingleThreadExecutor();

    public ClientNode(String ip, int port, ClientListener clientListener) {
        this.ip = ip;
        this.port = port;

        this.listener = clientListener;
    }

    public void create() {
        if (ip == null || ip.equals("") || port <= 0) {
            listener.onClientCreateFailed(this, ERROR_INVALID_PARAMETERS, null);
        } else {
            if (!isCreating) {
                isCreating = true;
                socketCreateService.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!isCreating) {
                            try {
                                Socket socket = new Socket(ip, port);
                            } catch (IOException e) {
                                Logger.e(e);
                                listener.onClientCreateFailed(ClientNode.this, "create client failed due to error occured", e);
                            }
                        }
                    }
                });
            }
        }
    }
}
