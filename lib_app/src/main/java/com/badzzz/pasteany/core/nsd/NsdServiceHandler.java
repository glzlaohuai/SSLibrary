package com.badzzz.pasteany.core.nsd;

import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.nsd.peer.client.ConnectedClientsManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.SettingsManager;
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
            public void onCreateFailed(ServerNode serverNode, Exception exception) {
                super.onCreateFailed(serverNode, exception);
                triggerNsdServiceStarterRedoStuff("server node create failed", exception);
            }

            @Override
            public void onDestroyed(ServerNode serverNode, String reason, Exception e) {
                super.onDestroyed(serverNode, reason, e);
                triggerNsdServiceStarterRedoStuff("server node destroyed with reason: " + reason, e);
            }

            void triggerNsdServiceStarterRedoStuff(String reason, Exception e) {
                Logger.i(tag, "trigger nsd service start redo stuff, reason: " + reason + ", e: " + e);
                serverNode.unmonitorServerStatus(this);
                NsdServiceStarter.redoIfSomethingWentWrong(reason, e);
            }
        }, true), new PeerListenerWrapper(new PeerListenerAdapter(), false));
        serverNode.create(Constants.Others.TIMEOUT);
    }

    private synchronized void createNsdNode() {
        if (serverNode != null && serverNode.isInUsing()) {
            nsdNode = new NsdNode(PlatformManagerHolder.get().getAppManager().getNsdServiceManager().getExtraActionPerformer(), PlatformManagerHolder.get().getAppManager().getNetworkManager().getLocalNotLoopbackAddress(), Constants.NSD.NSD_HOST_NAME, new NsdEventListenerWrapper(new NsdEventListenerAdapter() {
                @Override
                public void onCreated(NsdNode nsdNode) {
                    if (!nsdNode.isDestroyed()) {
                        nsdNode.registerService(Constants.NSD.NSD_SERVICE_TYPE, INSDServiceManager.buildServiceName(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID(), SettingsManager.getInstance().getServiceName()), INSDServiceManager.buildServiceText(SettingsManager.getInstance().getDeviceName(), PlatformManagerHolder.get().getPlatformName()), serverNode.getPort());
                        nsdNode.watchService(Constants.NSD.NSD_SERVICE_TYPE, null);
                    }
                }

                @Override
                public void onCreateFailed(String msg, Exception e) {
                    //something went wrong, redo it
                    NsdServiceStarter.redoIfSomethingWentWrong("nsd node create failed, msg: " + msg, e);
                }


                @Override
                public void onServiceDiscovered(NsdNode nsdNode, ServiceInfo info) {
                    //find a nsdNode, try to connect to it
                    ConnectedClientsManager.afterServiceDiscovered(NsdServiceHandler.this, nsdNode, info);
                }
            }));
            nsdNode.create();
        }
    }


    public synchronized void destroy(INsdServiceHandlerDestroyListener listener, String reason, Exception e) {
        if (!isDestroyCalled) {
            isDestroyCalled = true;
            doDestroy(listener, reason, e);
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

    private synchronized void doDestroy(final INsdServiceHandlerDestroyListener listener, String reason, final Exception exception) {
        if (serverNode != null && !serverNode.isDestroyed()) {
            serverNode.monitorServerStatus(new ServerListenerAdapter() {
                @Override
                public void onDestroyed(ServerNode serverNode, String reason, Exception e) {
                    super.onDestroyed(serverNode, reason, e);

                    if (nsdNode != null && !nsdNode.isDestroyed()) {
                        nsdNode.destroy(reason, exception);
                        nsdNode.registerListener(new NsdEventListenerAdapter() {
                            @Override
                            public void onDestroyed(NsdNode nsdNode, String reason, Exception e) {
                                super.onDestroyed(nsdNode, reason, e);
                                doStuffAfterHandlerBeDestroyed(listener);
                            }
                        });

                    } else {
                        doStuffAfterHandlerBeDestroyed(listener);
                    }
                }
            });
            serverNode.destroy(reason, exception);
        } else {
            doStuffAfterHandlerBeDestroyed(listener);
        }
    }


    public NsdNode getNsdNode() {
        return nsdNode;
    }
}
