package com.badzzz.pasteany.core.nsd;

import com.badzzz.pasteany.core.interfaces.IAppManager;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.net.nsd.NsdEventListener;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerNode;

import org.json.simple.JSONObject;

public class NsdServiceHandler {

    private static final String TAG = "NsdServiceHandler";

    private boolean isInited;
    private boolean isDestroyCalled;
    private boolean isDestroyed;

    private ServerNode serverNode;
    private NsdNode nsdNode;

    public interface INsdServiceHandlerDestroyListener {
        void onDestroyed(NsdServiceHandler handler);
    }


    public synchronized void init() {
        if (!isDestroyCalled && !isInited) {
            isInited = true;
            ConnectedPeersManager.setCurrentlyUsedHandler(this);
            doInit();
        }
    }

    private synchronized void createServerNodeAndCreateNsdNodeAfterServerNodeCreated() {
        serverNode = new ServerNode(new ServerListenerAdapter() {
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

            @Override
            public void onCorrupted(ServerNode serverNode, String msg, Exception e) {
                super.onCorrupted(serverNode, msg, e);

                triggerNsdServiceStarterRedoStuff();
            }

            void triggerNsdServiceStarterRedoStuff() {
                serverNode.monitorServerStatus(this);
                NsdServiceStarter.redoIfSomethingWentWrong();
            }
        }, new PeerListenerAdapter());
        serverNode.create(Constants.Others.TIMEOUT);
    }


    private static String createRegisterServiceName() {
        JSONObject jsonObject = new JSONObject();

        IAppManager appManager = PlatformManagerHolder.get().getAppManager();

        jsonObject.put(Constants.NSD.Key.DEVICE_ID, appManager.getDeviceInfoManager().getDeviceID());
        jsonObject.put(Constants.NSD.Key.DEVICE_NAME, appManager.getDeviceInfoManager().getDeviceName());
        jsonObject.put(Constants.NSD.Key.SERVICE_NAME, PreferenceManagerWrapper.getInstance().getServiceName());

        return jsonObject.toJSONString();
    }


    private synchronized void createNsdNode() {
        if (serverNode != null && serverNode.isInUsing()) {
            nsdNode = new NsdNode(PlatformManagerHolder.get().getAppManager().getNsdServiceManager().getExtraActionPerformer(), PlatformManagerHolder.get().getAppManager().getNetworkManager().getLocalNotLoopbackAddress(), Constants.NSD.NSD_HOST_NAME, new NsdEventListener() {
                @Override
                public void onCreated(NsdNode nsdNode) {
                    if (!nsdNode.isDestroyed()) {
                        nsdNode.registerService(Constants.NSD.NSD_SERVICE_TYPE, createRegisterServiceName(), null, serverNode.getPort());
                        nsdNode.watchService(Constants.NSD.NSD_SERVICE_TYPE, null);
                    }
                }

                @Override
                public void onCreateFailed(String msg, Exception e) {
                    //something went wrong, redo it
                    NsdServiceStarter.redoIfSomethingWentWrong();
                }

                @Override
                public void onDestroyed(NsdNode nsdNode) {

                }

                @Override
                public void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e) {

                }

                @Override
                public void onServiceDiscoveryed(NsdNode nsdNode, javax.jmdns.ServiceEvent event) {
                    //find a nsdNode, try to connect to it
                    ConnectedPeersManager.afterServiceDiscoveryed(NsdServiceHandler.this, nsdNode, event);
                }

                @Override
                public void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name) {

                }

                @Override
                public void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e) {

                }

                @Override
                public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {

                }
            });
            nsdNode.create();
        }
    }


    private synchronized void doInit() {
        createServerNodeAndCreateNsdNodeAfterServerNodeCreated();
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
        ConnectedPeersManager.destroyRelatedConnectedPeerHolder(this);
    }

    private synchronized void doDestroy(final INsdServiceHandlerDestroyListener listener) {
        if (serverNode != null) {
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
}
