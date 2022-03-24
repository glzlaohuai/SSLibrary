package com.badzzz.pasteany.core.nsd.peer.client;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.SettingsManager;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.client.ClientListenerAdapter;
import com.imob.lib.sslib.client.ClientListenerWrapper;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.peer.Peer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.util.LinkedList;
import java.util.List;

import javax.jmdns.ServiceInfo;

public class ConnectedClientsHandler {

    private static final String S_TAG = "ConnectedClientsHandler";

    private boolean destroyed = false;
    private String tag = S_TAG + " # " + hashCode();

    private List<ClientNode> clientNodeList = new LinkedList<>();


    public synchronized void destroy(String reason, Exception e) {
        if (!destroyed) {
            destroyed = true;
            doDestroy(reason, e);
        }
    }


    private ClientNode getClientNodeReferToThisIP(String ip) {
        if (ip == null || ip.isEmpty()) return null;
        for (ClientNode clientNode : clientNodeList) {
            if (clientNode.getIp() != null && clientNode.getIp().equals(ip)) {
                return clientNode;
            }
        }
        return null;
    }

    private synchronized void doDestroy(String reason, Exception e) {
        for (ClientNode clientNode : clientNodeList) {
            clientNode.destroy(reason, e);
        }
        clientNodeList.clear();
    }


    public synchronized void afterServiceDiscovered(final ServiceInfo info) {
        if (info != null) {

            String deviceID = null;
            String serviceName = null;

            try {
                JSONObject serviceJsonObject = new JSONObject(info.getName());
                deviceID = serviceJsonObject.getString(Constants.Device.KEY_DEVICEID);
                serviceName = serviceJsonObject.getString(Constants.NSD.Key.SERVICE_NAME);
            } catch (JSONException e) {
                Logger.e(e);
            }

            if (deviceID != null && !deviceID.equals(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID()) && serviceName != null && serviceName.equals(SettingsManager.getInstance().getServiceName())) {
                Logger.i(tag, "discovered nsd service's name match, and not the one created from localhost, so connect to it.");
                Inet4Address inetAddresses = info.getInet4Address();
                int port = info.getPort();

                if (inetAddresses != null) {
                    String ip4 = inetAddresses.getHostAddress();
                    final ClientNode referedClientNode = getClientNodeReferToThisIP(ip4);

                    if (ip4 != null && referedClientNode == null) {
                        ClientNode node = new ClientNode(ip4, port, new ClientListenerWrapper(new ClientListenerAdapter() {
                            @Override
                            public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {
                                super.onClientCreateFailed(clientNode, msg, exception);
                                clientNodeList.remove(clientNode);
                                //connect to server failed, so send another service info retrieve msg
                                if (!destroyed && ConnectedClientsManager.getCurrentlyUsedConnectedPeerHandler() == ConnectedClientsHandler.this) {
                                    Logger.i(tag, "create client failed, maybe the server info changed? do another retrieve service info loop and if retrieved, try to reconnect to it again.");
                                    NsdNode nsdNode = ConnectedClientsManager.getInUsingServiceHandler().getNsdNode();
                                    if (nsdNode != null) {
                                        nsdNode.triggerServiceInfoResolve(info.getType(), info.getName());
                                    }
                                }
                            }

                            @Override
                            public void onClientDestroyed(ClientNode clientNode) {
                                super.onClientDestroyed(clientNode);
                                afterClientDestroyed(clientNode);
                            }

                            private void afterClientDestroyed(ClientNode clientNode) {
                                clientNodeList.remove(clientNode);
                            }

                            @Override
                            public void onDestroy(Peer peer) {
                                super.onDestroy(peer);
                                afterClientDestroyed((ClientNode) peer.getLocalNode());
                            }
                        }, true));
                        node.create(Constants.Others.TIMEOUT);
                        clientNodeList.add(node);
                    } else {
                        Logger.i(tag, "there already has a peer connected to the nsd service, so no need to connect to it again, just send a ping msg to check if it's still available.");
                        referedClientNode.sendMsg(MsgCreator.createPingMsg("ping_alive_check_for_connected_client"));
                    }
                }
            } else {
                Logger.i(tag, "discovered nsd service's name mismatch or it's a localhost nsd service, ignore it.");
            }
        }
    }
}
