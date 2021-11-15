package com.imob.lib.sslib.server;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.PeerListener;

public class ServerManager {

    private static final String TAG = "ServerManager";

    private static ServerNode serverNode;

    /**
     * @return true - create a new server node instance, false - has a running server node already, create failed
     */
    public synchronized static boolean createServerNode(ServerListener serverListener, PeerListener peerListener, long timeout) {
        if (serverNode != null && serverNode.isInUsing()) {
            return false;
        } else {
            serverNode = new ServerNode(new ServerListenerWrapper(serverListener) {
                @Override
                public void onCreated(ServerNode serverNode) {
                    super.onCreated(serverNode);

                    if (serverNode != ServerManager.serverNode && !serverNode.isDestroyed()) {
                        Logger.i(TAG, "onCreated called, but the server node from the callback paramater not equals the holded static instance, and it's not destroyed, something went wrong here.");
                        serverNode.destroy();
                    }
                }
            }, new PeerListenerWrapper(peerListener));
            return serverNode.create(timeout);
        }
    }


    /**
     * @param id
     * @param msg
     * @return true - serverNode is not null and {@link ServerNode#broadcastStringMsg(String, String)} return true | false - the opposite
     */
    public static boolean broadcastStringMsg(String id, String msg) {
        if (getServerNode() != null) {
            return getServerNode().broadcastStringMsg(id, msg);
        } else {
            return false;
        }
    }

    /**
     * @param id
     * @param filePath
     * @return true - serverNode is not null and {@link ServerNode#broadcastFileMsg(String, String)}} return true | false - the opposite
     */
    public static boolean broadcastFileMsg(String id, String filePath) {
        if (getServerNode() != null) {
            return getServerNode().broadcastFileMsg(id, filePath);
        } else {
            return false;
        }
    }

    public static synchronized void destroyServer() {
        if (serverNode != null && serverNode.isInUsing()) {
            serverNode.destroy();
            serverNode = null;
        }
    }


    public synchronized static ServerNode getServerNode() {
        return serverNode;
    }


    public synchronized static ServerNode getInUsingServerNode() {
        if (serverNode != null && serverNode.isInUsing()) {
            return serverNode;
        }
        return null;
    }

}
