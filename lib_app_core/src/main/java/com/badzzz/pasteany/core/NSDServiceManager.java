package com.badzzz.pasteany.core;

import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.server.ServerNode;

public class NSDServiceManager {


    private static NSDServiceManager instance = new NSDServiceManager();

    private boolean inited = false;

    private ServerNode serverNode;
    private NsdNode nsdNode;

    public static NSDServiceManager getInstance() {
        return instance;
    }

    public String getServiceName() {
        return PreferenceManagerWrapper.getInstance().getServiceName();
    }

    public void init() {
        if (!inited) {
            inited = true;
            doInitStuff();
        }
    }

    private void startCreateServerNodeAndRegisterServiceStuffIfNetAvailable() {
        if (PlatformManagerHolder.get().getAppManager().getNetworkManager().isWIFIConnected()) {
            //network available
        }
    }

    private void doInitStuff() {

        PlatformManagerHolder.get().getAppManager().getNetworkManager().monitorNetworkChange(new INetworkManager.NetworkChangeListener() {
            @Override
            public void onNetworkChanged() {
                startCreateServerNodeAndRegisterServiceStuffIfNetAvailable();
            }
        });
        startCreateServerNodeAndRegisterServiceStuffIfNetAvailable();
    }
}
