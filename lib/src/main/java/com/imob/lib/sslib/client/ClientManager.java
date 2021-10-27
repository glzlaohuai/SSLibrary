package com.imob.lib.sslib.client;

import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.msg.StringMsg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.utils.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClientManager {
    private static final String TAG = "ClientManager";

    private final static Map<String, Set<ClientNode>> inUsingClientMap = new HashMap<>();

    private static class ClientListenerWrapper implements ClientListener {

        private ClientListener base;

        public ClientListenerWrapper(ClientListener base) {
            this.base = base;
        }

        @Override
        public void onClientCreated(ClientNode clientNode) {
            Logger.i(TAG, "onClientCreated");

            base.onClientCreated(clientNode);
        }


        private void removeClientNodeFromMap(ClientNode clientNode) {
            if (clientNode == null) return;

            Set<ClientNode> clientNodes = inUsingClientMap.get(generateClientKeyInMap(clientNode.getIp(), clientNode.getPort()));
            if (clientNodes != null) {
                clientNodes.remove(clientNode);
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
            Logger.i(TAG, "onMsgIntoQueue, id: " + id);

            base.onMsgIntoQueue(peer, id);
        }

        @Override
        public void onMsgSendStart(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendStart, id: " + id);

            base.onMsgSendStart(peer, id);
        }

        @Override
        public void onMsgSendSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onMsgSendSucceeded, id: " + id);

            base.onMsgSendSucceeded(peer, id);
        }

        @Override
        public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
            Logger.i(TAG, "onMsgSendSucceeded, id: " + id + ", msg: " + msg + ", exception: " + exception);

            base.onMsgSendFailed(peer, id, msg, exception);
        }

        @Override
        public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {
            Logger.i(TAG, "onMsgChunkSendSucceeded, id: " + id + ", chunkSize: " + chunkSize);

            base.onMsgChunkSendSucceeded(peer, id, chunkSize);
        }

        @Override
        public void onIOStreamOpened(Peer peer) {
            Logger.i(TAG, "onIOStreamOpened");

            base.onIOStreamOpened(peer);
        }

        @Override
        public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {
            Logger.i(TAG, "onIOStreamOpenFailed, msg: " + errorMsg + ", exception: " + exception);

            base.onIOStreamOpenFailed(peer, errorMsg, exception);
        }

        @Override
        public void onCorrupted(Peer peer, String msg, Exception e) {
            Logger.i(TAG, "onCorrupted, msg: " + msg + ", exception: " + e);

            removeClientNodeFromMap((ClientNode) peer.getLocalNode());

            base.onCorrupted(peer, msg, e);
        }

        @Override
        public void onDestroy(Peer peer) {
            Logger.i(TAG, "onDestroy");

            base.onDestroy(peer);
        }

        @Override
        public void onIncomingMsg(Peer peer, String id, int available) {
            Logger.i(TAG, "onIncomingMsg, id: " + id + ", available: " + available);

            base.onIncomingMsg(peer, id, available);
        }

        @Override
        public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgChunkReadFailedDueToPeerIOFailed, id: " + id);

            base.onIncomingMsgChunkReadFailedDueToPeerIOFailed(peer, id);
        }

        @Override
        public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {
            Logger.i(TAG, "onIncomingMsgChunkReadSucceeded, id: " + id + ", chunkSize: " + chunkSize + ", soFar: " + soFar + ", chunkBytes: " + Arrays.toString(chunkBytes));

            base.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, chunkBytes);
        }

        @Override
        public void onIncomingMsgReadSucceeded(Peer peer, String id) {
            Logger.i(TAG, "onIncomingMsgReadSucceeded, id: " + id);

            base.onIncomingMsgReadSucceeded(peer, id);
        }

        @Override
        public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
            Logger.i(TAG, "onIncomingMsgReadFailed, id: " + id + ", total: " + total + ", soFar: " + soFar);

            base.onIncomingMsgReadFailed(peer, id, total, soFar);
        }
    }


    /**
     *
     * @param ip
     * @param port
     * @param clientListener
     * @return true - valid para
     */
    public static boolean createClient(String ip, int port, ClientListener clientListener) {
        if (ip == null || ip.equals("") || port <= 0) {
            return false;
        } else {
            ClientNode clientNode = new ClientNode(ip, port, new ClientListenerWrapper(clientListener));
            addClientToInUsingMap(clientNode);
            clientNode.create();
            return true;
        }
    }

    private static void addClientToInUsingMap(ClientNode clientNode) {
        String key = generateClientKeyInMap(clientNode.getIp(), clientNode.getPort());
        Set<ClientNode> clientNodes = inUsingClientMap.get(key);
        if (clientNodes == null) {
            clientNodes = new HashSet<>();
            inUsingClientMap.put(key, clientNodes);
        }
        clientNodes.add(clientNode);
    }


    public static Set<ClientNode> getClients(String ip, int port) {
        return inUsingClientMap.get(generateClientKeyInMap(ip, port));
    }


    private static String generateClientKeyInMap(String ip, int port) {
        return ip + " # " + port;
    }

    public static Map<String, Set<ClientNode>> getInUsingClientMap() {
        return inUsingClientMap;
    }


    public static boolean sendMsgByAllClients(Msg msg) {
        Map<String, Set<ClientNode>> inUsingClientMap = ClientManager.getInUsingClientMap();

        if (inUsingClientMap == null || inUsingClientMap.isEmpty()) {
            return false;
        }

        Set<String> keySet = inUsingClientMap.keySet();
        for (String key : keySet) {
            Set<ClientNode> clientNodes = inUsingClientMap.get(key);
            if (clientNodes != null) {
                for (ClientNode clientNode : clientNodes) {
                    clientNode.sendMsg(StringMsg.build(UUID.randomUUID().toString(), "a test msg from client to server"));
                }
            }
        }
        return true;
    }
}
