package com.badzzz.pasteany.core.nsd.peer;

import com.badzzz.pasteany.core.nsd.NsdServiceHandler;

import java.util.HashMap;
import java.util.Map;

public class ConnectedPeersManager {

    private static NsdServiceHandler inUsingServiceHandler;
    private static Map<NsdServiceHandler, ConnectedPeersHolder> connectedPeersHolderMap = new HashMap<>();

    public static void destroyAllRelatedPeers(NsdServiceHandler handler) {
        ConnectedPeersHolder connectedPeersHolder = connectedPeersHolderMap.get(handler);
        if (connectedPeersHolder != null) {
            connectedPeersHolder.destroy();
        }
    }


    public static void setCurrentlyUsedHandler(NsdServiceHandler handler) {
        ConnectedPeersManager.inUsingServiceHandler = handler;
        connectedPeersHolderMap.put(handler, new ConnectedPeersHolder());
    }











}
