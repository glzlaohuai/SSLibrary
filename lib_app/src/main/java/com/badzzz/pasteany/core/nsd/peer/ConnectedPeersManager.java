package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.nsd.NsdServiceHandler;
import com.imob.lib.net.nsd.NsdNode;

import java.util.HashMap;
import java.util.Map;

import javax.jmdns.ServiceInfo;

public class ConnectedPeersManager {

    private static NsdServiceHandler inUsingServiceHandler;
    private static Map<NsdServiceHandler, ConnectedPeersHandler> relatedConnectedPeersHandlerMap = new HashMap<>();

    public static void destroyRelatedConnectedPeerHolder(NsdServiceHandler handler) {
        ConnectedPeersHandler connectedPeersHandler = relatedConnectedPeersHandlerMap.get(handler);
        if (connectedPeersHandler != null) {
            connectedPeersHandler.destroy();
        }
    }


    public static void setCurrentlyUsedHandler(NsdServiceHandler handler) {
        ConnectedPeersManager.inUsingServiceHandler = handler;
        relatedConnectedPeersHandlerMap.put(handler, new ConnectedPeersHandler());
    }


    public static void afterServiceDiscoveryed(NsdServiceHandler nsdServiceHandler, NsdNode nsdNode, ServiceInfo info) {
        if (inUsingServiceHandler == nsdServiceHandler && relatedConnectedPeersHandlerMap.get(nsdServiceHandler) != null && nsdNode != null && info != null && nsdNode.isRunning()) {
            relatedConnectedPeersHandlerMap.get(nsdServiceHandler).afterServiceDiscoveryed(info);
        }
    }


    public static ConnectedPeersHandler getCurrentlyUsedConnectedPeerHandler() {
        if (inUsingServiceHandler != null) {
            return relatedConnectedPeersHandlerMap.get(inUsingServiceHandler);
        }
        return null;
    }


    public static NsdServiceHandler getInUsingServiceHandler() {
        return inUsingServiceHandler;
    }
}
