package com.badzzz.pasteany.core.nsd;

import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.nsd.peer.client.ConnectedClientsManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.net.nsd.NsdEventListenerAdapter;
import com.imob.lib.net.nsd.NsdEventListenerWrapper;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.peer.PeerListenerWrapper;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerListenerWrapper;
import com.imob.lib.sslib.server.ServerNode;

import javax.jmdns.ServiceInfo;


public class NsdServiceHandler {

    private static final String S_TAG = "NsdServiceHandler";

    private boolean isInited;
    private boolean isDestroyCalled;
    private boolean isDestroyed;

    private ServerNode serverNode;
    private NsdNode nsdNode;

    private String tag = S_TAG + " # " + hashCode();

    public interface INsdServiceHandlerDestroyListener {
        void onDestroyed(NsdServiceHandler handler);
    }


    public synchronized void init() {
        if (!isDestroyCalled && !isInited) {
            isInited = true;
            ConnectedClientsManager.setCurrentlyUsedHandler(this);
            createServerNodeAndCreateNsdNodeAfterServerNodeCreated();
        }
    }

    private synchronized void createServerNodeAndCreateNsdNodeAfterServerNodeCreated() {
        serverNode = new ServerNode(new ServerListenerWrapper(new ServerListenerAdapter() {
            @Override
            public void onCreated(ServerNode serverNode) {
                super.onCreated(serverNode);
                createNsdNode();
            }

            @Override
            public void onCreateFailed(Exception exception) {
                super.onCreateFailed(exception);
                triggerNsdServiceStarterRedoStuff();
            }

            @Override
            public void onDestroyed(ServerNode serverNode) {
                super.onDestroyed(serverNode);
                triggerNsdServiceStarterRedoStuff();
            }

            void triggerNsdServiceStarterRedoStuff() {
                Logger.i(tag, "trigger nsd service start redo stuff");
                serverNode.unmonitorServerStatus(this);
                NsdServiceStarter.redoIfSomethingWentWrong();
            }
        }, true), new PeerListenerWrapper(new PeerListenerAdapter(), true));
        serverNode.create(Constants.Others.TIMEOUT);
    }

    private synchronized void createNsdNode() {
        if (serverNode != null && serverNode.isInUsing()) {
            nsdNode = new NsdNode(PlatformManagerHolder.get().getAppManager().getNsdServiceManager().getExtraActionPerformer(), PlatformManagerHolder.get().getAppManager().getNetworkManager().getLocalNotLoopbackAddress(), Constants.NSD.NSD_HOST_NAME, new NsdEventListenerWrapper(new NsdEventListenerAdapter() {
                @Override
                public void onCreated(NsdNode nsdNode) {
                    if (!nsdNode.isDestroyed()) {
                        nsdNode.registerService(Constants.NSD.NSD_SERVICE_TYPE, INSDServiceManager.buildServiceName(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID(), PreferenceManagerWrapper.getInstance().getServiceName()), null, serverNode.getPort());
                        nsdNode.watchService(Constants.NSD.NSD_SERVICE_TYPE, null);
                    }
                }

                @Override
                public void onCreateFailed(String msg, Exception e) {
                    //something went wrong, redo it
                    NsdServiceStarter.redoIfSomethingWentWrong();
                }


                @Override
                public void onServiceDiscoveryed(NsdNode nsdNode, ServiceInfo info) {
                    //find a nsdNode, try to connect to it
                    ConnectedClientsManager.afterServiceDiscoveryed(NsdServiceHandler.this, nsdNode, info);
                }
            }));
            nsdNode.create();
        }
    }


    public synchronized void destroy(INsdServiceHandlerDestroyListener listener) {
        if (!isDestroyCalled) {
            isDestroyCalled = true;
            doDestroy(listener);
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }


    private void doStuffAfterHandlerBeDestroyed(INsdServiceHandlerDestroyListener listener) {
        isDestroyed = true;
        listener.onDestroyed(this);
        ConnectedClientsManager.destroyRelatedConnectedPeerHolder(this);
    }

    private synchronized void doDestroy(final INsdServiceHandlerDestroyListener listener) {
        if (serverNode != null && !serverNode.isDestroyed()) {
            serverNode.monitorServerStatus(new ServerListenerAdapter() {
                @Override
                public void onDestroyed(ServerNode serverNode) {
                    super.onDestroyed(serverNode);
                    doStuffAfterHandlerBeDestroyed(listener);
                }
            });
            serverNode.destroy();
            if (nsdNode != null) {
                nsdNode.destroy();
            }
        } else {
            doStuffAfterHandlerBeDestroyed(listener);
        }
    }


    public NsdNode getNsdNode() {
        return nsdNode;
    }
}
