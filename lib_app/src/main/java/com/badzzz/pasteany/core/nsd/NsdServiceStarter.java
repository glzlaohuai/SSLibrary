package com.badzzz.pasteany.core.nsd;

import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.badzzz.pasteany.core.interfaces.IPlatformManager;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.SettingsManager;
import com.imob.lib.lib_common.Logger;


/**
 *
 */
public class NsdServiceStarter {

    private static final String TAG = "NsdServiceStarter";

    private static boolean hasInited = false;

    private static NsdServiceHandler nsdServiceHandler;

    /**
     * this will be called by {@link IPlatformManager#initPlatform()} if serviceName is set or after serviceName is setted at {@link SettingsManager#saveServiceName(String)}
     */
    public final static void init() {
        if (!SettingsManager.getInstance().hasSavedServiceName()) {
            Logger.i(TAG, "try to init nsd service, but no service name set, something went wrong, ");
        } else {
            Logger.i(TAG, "start nsd service using service name: " + SettingsManager.getInstance().getServiceName());
            if (!hasInited) {
                doInit();
            }
        }
    }


    /**
     * will be called after service name setted(reset), see {@link SettingsManager#saveServiceName(String)}
     */
    public final static void stuffAfterServiceNameSetted() {
        if (hasInited) {
            redoIfSomethingWentWrong("service name was set", null);
        } else {
            doInit();
        }
    }

    private synchronized final static void doInit() {
        if (!hasInited) {
            hasInited = true;

            NsdServiceStarter.destroyPreviousNsdServiceHandlerAndCreateANewOne("init", null);
            PlatformManagerHolder.get().getAppManager().getNetworkManager().monitorNetworkChange(new INetworkManager.NetworkChangeListener() {
                @Override
                public void onNetworkChanged() {
                    NsdServiceStarter.destroyPreviousNsdServiceHandlerAndCreateANewOne("network changed", null);
                }
            });
        }
    }


    private static boolean isEnvironmentAvailable() {
        return PlatformManagerHolder.get().getAppManager().getNetworkManager().isWIFIConnected();
    }


    private static void createNsdServiceHandlerIfEnvironmentAvailable() {
        Logger.i(TAG, "try to create&init nsd service handler");
        if (isEnvironmentAvailable()) {
            Logger.i(TAG, "wifi connected, create and init nsd service handler");
            nsdServiceHandler = new NsdServiceHandler();
            nsdServiceHandler.init();
        } else {
            Logger.i(TAG, "wifi not be connected, do not create and init nsd service handler");
        }
    }

    public final static void redoIfSomethingWentWrong(String reason, Exception e) {
        destroyPreviousNsdServiceHandlerAndCreateANewOne(reason, e);
    }

    private final static synchronized void destroyPreviousNsdServiceHandlerAndCreateANewOne(String reason, Exception e) {
        if (nsdServiceHandler == null) {
            createNsdServiceHandlerIfEnvironmentAvailable();
        } else {
            if (!nsdServiceHandler.isDestroyed()) {
                nsdServiceHandler.destroy(new NsdServiceHandler.INsdServiceHandlerDestroyListener() {
                    @Override
                    public void onDestroyed(NsdServiceHandler handler) {
                        nsdServiceHandler = null;
                        createNsdServiceHandlerIfEnvironmentAvailable();
                    }
                }, reason, e);
            }
        }
    }


}
