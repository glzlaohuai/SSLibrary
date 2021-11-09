package com.imob.lib.sslib.client;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.peer.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientNode implements INode {

    private static final String ERROR_INVALID_PARAMETERS = "ip or port invalid";
    private static final String ERROR_SEND_MSG_NULL_PEER = "peer is null";

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


    /**
     * @param timeout pass a positive number to take effect
     */
    public synchronized void create(final long timeout) {
        if (ip == null || ip.equals("") || port <= 0) {
            listener.onClientCreateFailed(this, ERROR_INVALID_PARAMETERS, null);
        } else {
            if (!isCreating) {
                isCreating = true;
                socketCreateService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), 5 * 1000);
                            peer = new Peer(socket, ClientNode.this, listener);
                            peer.setTimeout(timeout);
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
        if (peer == null) {
            listener.onMsgSendFailed(null, msg.getId(), ERROR_SEND_MSG_NULL_PEER, null);
            return false;
        } else {
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
