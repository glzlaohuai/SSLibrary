package com.badzzz.pasteany.core;

import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerNode;

public class NSDServiceManager {

    private static final String NSD_SERVICE_TYPE = "_pasteanywhere._tcp.local.";

    //default service name used, if user do not want to set a custom one
    public static final String NSD_SERVICE_NAME_DEFAULT = "hi_paste_anywhere";

    private static final long TIMEOUT = 10 * 1000l;

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

    private void doInitStuff() {
        serverNode = new ServerNode(new ServerListenerAdapter() {
            @Override
            public void onCreated(ServerNode serverNode) {
                super.onCreated(serverNode);
            }
        }, new PeerListenerAdapter());
        serverNode.create(TIMEOUT);
    }
}
