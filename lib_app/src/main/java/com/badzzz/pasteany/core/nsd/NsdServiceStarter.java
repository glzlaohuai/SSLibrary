package com.badzzz.pasteany.core.nsd;

import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.badzzz.pasteany.core.interfaces.IPlatformManager;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.lib_common.Logger;

public class NsdServiceStarter {

    private static final String TAG = "NsdServiceStarter";

    private static boolean hasInited = false;

    private static NsdServiceHandler nsdServiceHandler;

    /**
     * this will be called by {@link IPlatformManager#initPlatform()} if serviceName is set or after serviceName is setted at {@link com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper#saveServiceName(String)}
     */
    public final static void init() {
        if (PreferenceManagerWrapper.getInstance().hasSavedServiceName()) {
            Logger.i(TAG, "try to init nsd service, but no service name set, something went wrong, ");
        } else {
            Logger.i(TAG, "start nsd service using service name: " + PreferenceManagerWrapper.getInstance().getServiceName());
            if (!hasInited) {
                doInit();
            }
        }
    }

    private synchronized final static void doInit() {
        if (!hasInited) {
            hasInited = true;

            if (PlatformManagerHolder.get().getAppManager().getNetworkManager().isWIFIConnected()) {
                //start immediately
                NsdServiceStarter.destroyPreviousNsdServiceHandlerAndCreateANewOne();
            }
            PlatformManagerHolder.get().getAppManager().getNetworkManager().monitorNetworkChange(new INetworkManager.NetworkChangeListener() {
                @Override
                public void onNetworkChanged() {
                    NsdServiceStarter.destroyPreviousNsdServiceHandlerAndCreateANewOne();
                }
            });
        }
    }

    private final static synchronized void destroyPreviousNsdServiceHandlerAndCreateANewOne() {
        if (nsdServiceHandler != null) {
            nsdServiceHandler.destroy();
        }
        nsdServiceHandler = new NsdServiceHandler();
        nsdServiceHandler.init();
    }


}