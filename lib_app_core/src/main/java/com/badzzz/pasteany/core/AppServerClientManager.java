package com.badzzz.pasteany.core;

import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.server.ServerNode;

import java.util.ArrayList;
import java.util.List;

public class AppServerClientManager {

    private ServerNode serverNode;
    private List<ClientNode> clientNodes = new ArrayList<>();
    private NsdNode nsdNode;


    public AppServerClientManager(ServerNode serverNode, NsdNode nsdNode) {
        this.serverNode = serverNode;
        this.nsdNode = nsdNode;
    }


    public void destroy() {

    }
}
