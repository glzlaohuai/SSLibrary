package com.imob.lib.sslib.client;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.msg.FileMsg;
import com.imob.lib.sslib.msg.StringMsg;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientManager {
    private static final String TAG = "ClientManager";

    private final static Map<String, Set<ClientNode>> connectedClientMap = new HashMap<>();

    private static class ClientListenerWrapper implements ClientListener {

        private ClientListener base;

        public ClientListenerWrapper(ClientListener base) {
            this.base = base;
        }

        @Override
        public void onClientCreated(ClientNode clientNode) {
            Logger.i(TAG, "onClientCreated");
            addClientToInUsingMap(clientNode);
            base.onClientCreated(clientNode);
        }


        private synchronized void removeClientNodeFromMap(ClientNode clientNode) {

            if (clientNode == null) return;

            synchronized (ClientManager.class) {
                Set<ClientNode> clientNodes = connectedClientMap.get(generateClientKeyInMap(clientNode.getIp(), clientNode.getPort()));
                if (clientNodes != null) {
                    clientNodes.remove(clientNode);
                    if (clientNodes.size() == 0) {
                        connectedClientMap.remove(generateClientKeyInMap(clientNode.getIp(), clientNode.getPort()));
                    }
                }
            }
        }

        @Override
        public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {
            Logger.i(TAG, "onClientCreateFailed, msg: " + msg + ", exception: " + exception);
            removeClientNodeFromMap(clientNode);
            base.onClientCreateFailed(clientNode, msg, exception);
        }

        @Override
        public void onMsgIntoQueue(Peer peer, String id) {
            Logger.i(TAG, "onMsgIntoQueue, peer: " + peer.getTag() + ", id: " + id);
            base.onMsgIntoQueue(peer, id);
        }

        @Override
        public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgIntoQueue, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgIntoQueue(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendStart(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendStart, peer: " + peer.getTag() + ", id: " + id);

            base.onMsgSendStart(peer, id);
        }

        @Override
        public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendStart, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgSendStart(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendSucceeded, peer: " + peer.getTag() + ", id: " + id);

            base.onMsgSendSucceeded(peer, id);
        }

        @Override
        public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendSucceeded, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total);
            base.onConfirmMsgSendSucceeded(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
            Logger.i(TAG, "onMsgSendFailed, peer: " + peer.getTag() + ", id: " + id + ", msg: " + msg + ", exception: " + exception);

            base.onMsgSendFailed(peer, id, msg, exception);
        }

        @Override
        public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {
            Logger.i(TAG, "onConfirmMsgSendFailed, peer: " + peer.getTag() + ", id: " + id + ", soFar: " + soFar + ", total: " + total + ", msg: " + msg + ", exception: " + exception);
            base.onConfirmMsgSendFailed(peer, id, soFar, total, msg, exception);
        }

        @Override
        public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
            Logger.i(TAG, "onMsgChunkSendSucceeded, peer: " + peer.getTag() + ", id: " + id + ", chunkSize: " + chunkSize);

            base.onMsgChunkSendSucceeded(peer, id, chunkSize);
        }

        @Override
        public void onIOStreamOpened(Peer peer) {
            Logger.i(TAG, "onIOStreamOpened, peer: " + peer.getTag());

            base.onIOStreamOpened(peer);
        }

        @Override
        public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
            Logger.i(TAG, "onIOStreamOpenFailed, peer: " + peer.getTag() + ", msg: " + errorMsg + ", exception: " + exception);

            base.onIOStreamOpenFailed(peer, errorMsg, exception);
        }

        @Override
        public void onCorrupted(Peer peer, String msg, Exception e) {
            Logger.i(TAG, "onCorrupted, peer: " + peer.getTag() + ", msg: " + msg + ", exception: " + e);

            removeClientNodeFromMap((ClientNode) peer.getLocalNode());

            base.onCorrupted(peer, msg, e);
        }

        @Override
        public void onDestroy(Peer peer) {
            Logger.i(TAG, "onDestroy, peer: " + peer.getTag());

            base.onDestroy(peer);
        }

        @Override
        public void onTimeoutOccured(Peer peer) {
            Logger.i(TAG, "onTimeoutOccured, peer: " + peer.getTag());
            base.onTimeoutOccured(peer);
        }

        @Override
        public void onIncomingMsg(Peer peer, String id, int available) {
            Logger.i(TAG, "onIncomingMsg, peer: " + peer.getTag() + ", id: " + id + ", available: " + available);

            base.onIncomingMsg(peer, id, available);
        }

        @Override
        public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgChunkReadFailedDueToPeerIOFailed, peer: " + peer.getTag() + ", id: " + id);

            base.onIncomingMsgChunkReadFailedDueToPeerIOFailed(peer, id);
        }

        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {
            Logger.i(TAG, "onIncomingMsgChunkReadSucceeded, peer: " + peer.getTag() + ", id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar + ", chunkBytes: " + chunkBytes);

            base.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, chunkBytes);
        }

        @Override
        public void onIncomingMsgReadSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgReadSucceeded, peer: " + peer.getTag() + ", id: " + id);

            base.onIncomingMsgReadSucceeded(peer, id);
        }

        @Override
        public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
            Logger.i(TAG, "onIncomingMsgReadFailed, peer: " + peer.getTag() + ", id: " + id + ", total: " + total + ", soFar: " + soFar);

            base.onIncomingMsgReadFailed(peer, id, total, soFar);
        }

        @Override
        public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onIncomingConfirmMsg, peer: " + peer.getTag() + ", id: " + id + ", total: " + total + ", soFar: " + soFar);
            base.onIncomingConfirmMsg(peer, id, total, soFar);
        }

        @Override
        public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {
            Logger.i(TAG, "onConfirmMsgSendPending, peer: " + peer.getTag() + ", id: " + id + ", total: " + total + ", soFar: " + soFar);
            base.onConfirmMsgSendPending(peer, id, soFar, total);
        }

        @Override
        public void onMsgSendPending(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendPending, peer: " + peer.getTag() + ", id: " + id);
            base.onMsgSendPending(peer, id);
        }
    }

    /**
     * @param ip
     * @param port
     * @param clientListener
     * @return true - valid parameters
     */
    public static boolean createClient(String ip, int port, ClientListener clientListener, long timeout) {
        if (ip == null || ip.equals("") || port <= 0) {
            return false;
        } else {
            ClientNode clientNode = new ClientNode(ip, port, new ClientListenerWrapper(clientListener));
            clientNode.create(timeout);
            return true;
        }
    }

    private static void addClientToInUsingMap(ClientNode clientNode) {
        String key = generateClientKeyInMap(clientNode.getIp(), clientNode.getPort());
        Set<ClientNode> clientNodes = connectedClientMap.get(key);
        if (clientNodes == null) {
            clientNodes = new HashSet<>();
            connectedClientMap.put(key, clientNodes);
        }
        clientNodes.add(clientNode);
    }


    /**
     * @param ip remote ip
     * @param port remote port
     * @return all clientNodes that connected to the passed ip and port
     */
    public static Set<ClientNode> getConnectedClients(String ip, int port) {
        return connectedClientMap.get(generateClientKeyInMap(ip, port));
    }


    private static String generateClientKeyInMap(String ip, int port) {
        return ip + " # " + port;
    }

    public synchronized static Map<String, Set<ClientNode>> getConnectedClientMap() {
        return connectedClientMap;
    }


    /**
     *
     * @param id
     * @param msg
     * @return true - msg is valid (id && msg has has content) and has at least one connected client
     */
    public static boolean sendOutStringMsgByAllConnectedClients(String id, String msg) {
        if (id == null || id.isEmpty() || msg == null || msg.isEmpty() || !hasConnectedClients()) {
            return false;
        } else {
            for (ClientNode clientNode : getAllConnectedClients()) {
                clientNode.sendMsg(StringMsg.create(id, msg));
            }
        }
        return true;
    }


    /**
     *
     * @param id
     * @param filePath
     * @return true - has connected clients and msg is valid (id && filePath has content, file exists && file readable && file is not directory && createFileMsg succeeded) | false - the opposite
     */
    public static boolean sendOutFileMsgByAllConnectedClients(String id, String filePath) {
        if (id == null || id.isEmpty() || filePath == null || filePath.isEmpty() || !new File(filePath).exists() || !new File(filePath).canRead() || new File(filePath).isDirectory() || !hasConnectedClients()) {
            return false;
        } else {
            for (ClientNode clientNode : getAllConnectedClients()) {
                try {
                    clientNode.sendMsg(FileMsg.create(id, filePath));
                } catch (FileNotFoundException e) {
                    Logger.e(e);
                    return false;
                }
            }
        }
        return true;
    }


    public static Set<ClientNode> getAllConnectedClients() {
        Map<String, Set<ClientNode>> inUsingClientMap = ClientManager.getConnectedClientMap();

        Set<ClientNode> total = new HashSet<>();
        Set<String> keySet = inUsingClientMap.keySet();
        for (String key : keySet) {
            Set<ClientNode> clientNodes = inUsingClientMap.get(key);
            if (clientNodes != null) {
                total.addAll(clientNodes);
            }
        }

        return total;
    }

    public static boolean hasConnectedClients() {
        return getAllConnectedClients().size() > 0;
    }


}
