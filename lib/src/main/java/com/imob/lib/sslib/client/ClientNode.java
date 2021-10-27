package com.imob.lib.sslib.client;

import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientNode implements INode {

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

    public synchronized void create() {
        if (ip == null || ip.equals("") || port <= 0) {
            listener.onClientCreateFailed(this, ERROR_INVALID_PARAMETERS, null);
        } else {
            if (!isCreating) {
                isCreating = true;
                socketCreateService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(ip, port);
                            peer = new Peer(socket, ClientNode.this, listener);
                        } catch (IOException | IllegalArgumentException e) {
                            Logger.e(e);
                            listener.onClientCreateFailed(ClientNode.this, "create client failed due to error occured", e);
                        } finally {
                            isCreating = false;
                        }
                    }
                });
            }
        }
    }


    public synchronized boolean destroy() {
        if (isCreating) {
            return false;
        }

        if (peer != null) {
            peer.destroy();
        }
        return true;
    }


    public boolean sendMsg(Msg msg) {
        if (peer == null) return false;
        else {
            peer.sendMessage(msg);
            return true;
        }
    }


    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean isServerNode() {
        return false;
    }
}
