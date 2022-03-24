package com.badzzz.pasteany.core.nsd.peer.client;

import com.badzzz.pasteany.core.nsd.NsdServiceHandler;
import com.imob.lib.net.nsd.NsdNode;

import java.util.HashMap;
import java.util.Map;

import javax.jmdns.ServiceInfo;

public class ConnectedClientsManager {

    private static NsdServiceHandler inUsingServiceHandler;
    private static Map<NsdServiceHandler, ConnectedClientsHandler> relatedConnectedPeersHandlerMap = new HashMap<>();

    public static void destroyRelatedConnectedPeerHolder(NsdServiceHandler handler) {
        ConnectedClientsHandler connectedPeersHandler = relatedConnectedPeersHandlerMap.get(handler);
        if (connectedPeersHandler != null) {
            connectedPeersHandler.destroy("destroy this client after its related nsdServiceHandler destroyed", null);
        }
    }


    public static void setCurrentlyUsedHandler(NsdServiceHandler handler) {
        ConnectedClientsManager.inUsingServiceHandler = handler;
        relatedConnectedPeersHandlerMap.put(handler, new ConnectedClientsHandler());
    }


    public static void afterServiceDiscovered(NsdServiceHandler nsdServiceHandler, NsdNode nsdNode, ServiceInfo info) {
        if (inUsingServiceHandler == nsdServiceHandler && relatedConnectedPeersHandlerMap.get(nsdServiceHandler) != null && nsdNode != null && info != null && nsdNode.isRunning()) {
            relatedConnectedPeersHandlerMap.get(nsdServiceHandler).afterServiceDiscovered(info);
        }
    }


    public static ConnectedClientsHandler getCurrentlyUsedConnectedPeerHandler() {
        if (inUsingServiceHandler != null) {
            return relatedConnectedPeersHandlerMap.get(inUsingServiceHandler);
        }
        return null;
    }


    public static NsdServiceHandler getInUsingServiceHandler() {
        return inUsingServiceHandler;
    }
}
