package com.imob.lib.net.nsd;

import com.imob.lib.lib_common.Logger;

import java.net.InetAddress;

public class NsdManager {
    private static final String TAG = "NsdManager";

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
            nsdNode = new NsdNode(extraActionPerformer, inetAddress, hostName, new NsdEventListenerWrapper(listener) {
                @Override
                public void onCreated(NsdNode nsdNode) {
                    super.onCreated(nsdNode);

                    if (NsdManager.nsdNode != nsdNode && !nsdNode.isDestroyed()) {
                        Logger.i(TAG, "oncreated callbacked, but the nsd node paramater not equals the static instance and not be destroyed, something went wrong.");
                        nsdNode.destroy();
                    }
                }
            });
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
