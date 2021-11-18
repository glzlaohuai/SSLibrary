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
    private static final String ERROR_ALREADY_BE_DESTROYED = "already be destroyed before create called";

    private static final String S_TAG = "ClientNode";


    private String ip;
    private int port;

    private ClientListenerGroup listener = new ClientListenerGroup();

    private boolean isCreating = false;
    private boolean isDestroyed = false;
    private boolean isDestroyCallbacked = false;

    private String tag;

    private Peer peer;

    private ExecutorService socketStuffService = Executors.newSingleThreadExecutor();

    public ClientNode(String ip, int port, ClientListener clientListener) {
        this.ip = ip;
        this.port = port;

        this.listener.add(new ClientListenerWrapper(clientListener));

        tag = S_TAG + " - " + "ip: " + ip + ", port: " + port + ", hash: " + hashCode();
    }


    public String getTag() {
        return tag;
    }

    public Peer getPeer() {
        return peer;
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
                socketStuffService.execute(new Runnable() {
                    @Override
                    public void run() {
                        doCreateStuff(timeout);
                    }
                });
            }
        }
    }

    private synchronized void doCreateStuff(long timeout) {
        if (isDestroyed) {
            Logger.i(tag, "found that it's already be destroyed before createing stuff kick off, just stop creating and callback createFailed");
            listener.onClientCreateFailed(this, ERROR_ALREADY_BE_DESTROYED, null);
            return;
        }

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), 5 * 1000);
            listener.onClientCreated(ClientNode.this);
            peer = new Peer(socket, ClientNode.this, listener);
            peer.setTimeout(timeout);
        } catch (IOException | IllegalArgumentException e) {
            Logger.e(e);
            listener.onClientCreateFailed(ClientNode.this, "create client failed due to error occured", e);
        } finally {
            isCreating = false;
        }
    }

    public void destroy() {
        socketStuffService.execute(new Runnable() {
            @Override
            public void run() {
                doDestroyStuff();
            }
        });
    }


    private synchronized void doDestroyStuff() {
        if (!isDestroyed) {
            isDestroyed = true;
            callbackClientDestroy();
        }

        if (peer != null) {
            peer.destroy();
        }
    }


    private void callbackClientDestroy() {
        if (!isDestroyCallbacked) {
            isDestroyCallbacked = true;
            listener.onClientDestroyed(this);
        }
    }


    /**
     *
     * @param msg
     * @return true - has peer and msg is not null and valid | false - the opposite
     */
    public boolean sendMsg(Msg msg) {
        //without any callback if msg is null
        if (msg == null || !msg.isValid()) return false;

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
