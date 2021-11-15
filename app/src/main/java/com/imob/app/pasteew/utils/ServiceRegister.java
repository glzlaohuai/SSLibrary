package com.imob.app.pasteew.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.imob.app.pasteew.XApplication;
import com.imob.lib.common.android.NetworkUtils;
import com.imob.lib.net.nsd.INsdExtraActionPerformer;
import com.imob.lib.net.nsd.NsdEventListenerAdapter;
import com.imob.lib.net.nsd.NsdManager;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerManager;
import com.imob.lib.sslib.server.ServerNode;

import java.util.UUID;

public class ServiceRegister {

    private static final String SERVICE_HOST_NAME = UUID.randomUUID().toString();

    private static final String SERVICE_TYPE = "_pasteanywhere._tcp.local.";


    private static final int MSG_CREATE_SERVER = 0x0;
    private static final int RETRY_SERVER_CREATE_INTERVAL = 10 * 1000;

    private static WifiManager wifiManager;

    private static final String TAG = "ServiceRegister";

    static {
        wifiManager = (WifiManager) XApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private static boolean hasStarted = false;

    public static void startServiceRegisterStuff() {
        if (hasStarted) return;
        hasStarted = true;

        if (NetworkUtils.isWIFIConnected(XApplication.getContext())) {
            //create server、register service
            doStartServerAndRegisterService();
        } else {
            //wifi not connected
            NetworkUtils.monitorNetworkChange(XApplication.getContext(), new NetworkUtils.NetworkChangeListener() {
                @Override
                public void onNetworkStateChanged() {
                    Log.i(TAG, "onNetworkStateChanged: " + NetworkUtils.isWIFIConnected(XApplication.getContext()));
                    doStartServerAndRegisterService();
                }
            });
        }
    }


    private static void createNsd() {
        NsdManager.create(new INsdExtraActionPerformer() {
            private WifiManager.MulticastLock lock;

            @Override
            public void setup() {
                WifiManager wifiManager = (WifiManager) XApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    lock = wifiManager.createMulticastLock(getClass().getName());
                    lock.setReferenceCounted(true);
                    lock.acquire();
                } else {
                    throw new RuntimeException("setup failed due to no wifi manager instance found, this should never happen.");
                }
            }

            @Override
            public void cleaup() {
                //release wifi lock
                if (lock != null) {
                    lock.release();
                    lock = null;
                }
            }
        }, NetUtils.getNoneLoopLocalAddress(XApplication.getContext()), SERVICE_HOST_NAME, new NsdEventListenerAdapter());
    }

    private static void createServer() {
        ServerManager.createServerNode(new ServerListenerAdapter() {
            @Override
            public void onCreated(ServerNode serverNode) {
                super.onCreated(serverNode);
                createNsd();
            }


            @Override
            public void onDestroyed(ServerNode serverNode) {
                super.onDestroyed(serverNode);
                afterServerDestroyed();
            }

            @Override
            public void onCorrupted(ServerNode serverNode, String msg, Exception e) {
                super.onCorrupted(serverNode, msg, e);
                afterServerDestroyed();
            }

            private void afterServerDestroyed() {
                NsdManager.destroyNsdNode();
            }
        }, new PeerListenerAdapter(), 10 * 1000);
    }

    private static void doStartServerAndRegisterService() {
        if (ServerManager.getInUsingServerNode() != null) {
            Log.i(TAG, "doStartServerAndRegisterService: has in using server node, destroy it: " + ServerManager.getInUsingServerNode());
            ServerManager.destroyServer();
        }
        if (NsdManager.getInUsingNsdNode() != null) {
            Log.i(TAG, "doStartServerAndRegisterService: has in using nsd node, destroy it: " + NsdManager.getInUsingNsdNode());
            NsdManager.destroyNsdNode();
        } else {
            Log.i(TAG, "doStartServerAndRegisterService: has no in using nsd node");
        }

        createServer();
    }
}
