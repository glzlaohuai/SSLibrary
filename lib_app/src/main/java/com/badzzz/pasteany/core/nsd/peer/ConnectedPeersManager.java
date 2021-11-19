package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.nsd.NsdServiceHandler;
import com.imob.lib.net.nsd.NsdNode;

import java.util.HashMap;
import java.util.Map;

import javax.jmdns.ServiceEvent;

public class ConnectedPeersManager {

    private static NsdServiceHandler inUsingServiceHandler;
    private static Map<NsdServiceHandler, ConnectedPeersHolder> connectedPeersHolderMap = new HashMap<>();

    public static void destroyRelatedConnectedPeerHolder(NsdServiceHandler handler) {
        ConnectedPeersHolder connectedPeersHolder = connectedPeersHolderMap.get(handler);
        if (connectedPeersHolder != null) {
            connectedPeersHolder.destroy();
        }
    }


    public static void setCurrentlyUsedHandler(NsdServiceHandler handler) {
        ConnectedPeersManager.inUsingServiceHandler = handler;
        connectedPeersHolderMap.put(handler, new ConnectedPeersHolder());
    }


    public static void afterServiceDiscoveryed(NsdServiceHandler handler, NsdNode nsdNode, ServiceEvent event) {
        if (inUsingServiceHandler == handler && connectedPeersHolderMap.get(handler) != null && nsdNode != null && event != null && nsdNode.isRunning()) {
            connectedPeersHolderMap.get(handler).afterServiceDiscoveryed(event);
        }
    }


    public static ConnectedPeersHolder getCurrentlyUsedConnectedPeerHandler() {
        if (inUsingServiceHandler != null) {
            return connectedPeersHolderMap.get(inUsingServiceHandler);
        }
        return null;
    }
}
