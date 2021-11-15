package com.imob.lib.net.nsd;

import java.net.InetAddress;

public class NsdManager {

    private static NsdNode nsdNode;

    /**
     *
     * @param extraActionPerformer
     * @param inetAddress
     * @param hostName
     * @param listener
     * @return true - no functional nsdNode exists | false - the opposite
     */
    public synchronized static boolean create(INsdExtraActionPerformer extraActionPerformer, InetAddress inetAddress, String hostName, NsdEventListener listener) {
        if (nsdNode != null && nsdNode.isInUsing()) {
            return false;
        } else {
            nsdNode = new NsdNode(extraActionPerformer, inetAddress, hostName, new NsdEventListenerWrapper(listener));
            nsdNode.create();
            return true;
        }
    }

    public synchronized static NsdNode getNsdNode() {
        return nsdNode;
    }

    public synchronized static void destroyNsdNode() {
        if (nsdNode != null && nsdNode.isInUsing()) {
            nsdNode.destroy();
        }
        nsdNode = null;
    }

    public synchronized static NsdNode getInUsingNsdNode() {
        if (nsdNode != null && nsdNode.isInUsing()) {
            return nsdNode;
        }
        return null;
    }

}
