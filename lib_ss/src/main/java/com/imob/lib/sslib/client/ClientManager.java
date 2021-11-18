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
            ClientNode clientNode = new ClientNode(ip, port, new ClientListenerWrapper(clientListener, false) {
                @Override
                public void onClientDestroyed(ClientNode clientNode) {
                    super.onClientDestroyed(clientNode);
                    ClientManager.removeClientNodeFromMap(clientNode);
                }

                @Override
                public void onClientCreated(ClientNode clientNode) {
                    super.onClientCreated(clientNode);
                    ClientManager.addClientToInUsingMap(clientNode);
                }

                @Override
                public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {
                    super.onClientCreateFailed(clientNode, msg, exception);
                    ClientManager.removeClientNodeFromMap(clientNode);
                }

                @Override
                public void onCorrupted(Peer peer, String msg, Exception e) {
                    super.onCorrupted(peer, msg, e);
                    if (peer != null && peer.getLocalNode() instanceof ClientNode) {
                        ClientManager.removeClientNodeFromMap((ClientNode) peer.getLocalNode());
                    }
                }

                @Override
                public void onDestroy(Peer peer) {
                    super.onDestroy(peer);

                    if (peer != null && peer.getLocalNode() instanceof ClientNode) {
                        ClientManager.removeClientNodeFromMap((ClientNode) peer.getLocalNode());
                    }
                }
            });
            clientNode.create(timeout);
            return true;
        }
    }

    private synchronized static void addClientToInUsingMap(ClientNode clientNode) {
        String key = generateClientKeyInMap(clientNode.getIp(), clientNode.getPort());
        Set<ClientNode> clientNodes = connectedClientMap.get(key);
        if (clientNodes == null) {
            clientNodes = new HashSet<>();
            connectedClientMap.put(key, clientNodes);
        }
        clientNodes.add(clientNode);
    }

    private static synchronized void removeClientNodeFromMap(ClientNode clientNode) {

        if (clientNode == null) return;

        synchronized (ClientManager.class) {
            Set<ClientNode> clientNodes = ClientManager.connectedClientMap.get(ClientManager.generateClientKeyInMap(clientNode.getIp(), clientNode.getPort()));
            if (clientNodes != null) {
                clientNodes.remove(clientNode);
                if (clientNodes.size() == 0) {
                    ClientManager.connectedClientMap.remove(ClientManager.generateClientKeyInMap(clientNode.getIp(), clientNode.getPort()));
                }
            }
        }
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
