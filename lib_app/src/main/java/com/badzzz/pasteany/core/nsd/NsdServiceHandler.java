package com.badzzz.pasteany.core.nsd;

import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.net.nsd.NsdEventListener;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerNode;

public class NsdServiceHandler {

    private static final String TAG = "NsdServiceHandler";

    private boolean isInited;
    private boolean isDestroyCalled;
    private boolean isDestroyed;

    private ServerNode serverNode;
    private NsdNode nsdNode;


    private INsdServiceHandlerDestroyListener listener;

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
                //what's the fuck? this should never happen, but if did, just simply redo it.
                NsdServiceStarter.redoIfSomethingWentWrong();
            }

        }, new PeerListenerAdapter());
        serverNode.create(Constants.Others.TIMEOUT);
    }


    private synchronized void createNsdNode() {
        if (serverNode != null && serverNode.isInUsing()) {
            nsdNode = new NsdNode(PlatformManagerHolder.get().getAppManager().getNsdServiceManager().getExtraActionPerformer(), PlatformManagerHolder.get().getAppManager().getNetworkManager().getLocalNotLoopbackAddress(), Constants.NSD.NSD_HOST_NAME, new NsdEventListener() {
                @Override
                public void onCreated(NsdNode nsdNode) {

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
        ConnectedPeersManager.destroyAllRelatedPeers(this);
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
