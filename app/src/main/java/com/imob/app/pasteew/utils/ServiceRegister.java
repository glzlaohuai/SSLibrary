package com.imob.app.pasteew.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.imob.app.pasteew.XApplication;
import com.imob.lib.common.android.NetworkUtils;
import com.imob.lib.net.nsd.INsdExtraActionPerformer;
import com.imob.lib.net.nsd.NsdEventListenerAdapter;
import com.imob.lib.net.nsd.NsdManager;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerManager;

import java.util.UUID;

import androidx.annotation.NonNull;

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

    private final static Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg != null) {
                switch (msg.what) {
                    case MSG_CREATE_SERVER:
                        doStartServerAndRegisterService();
                        NsdManager.destroyNsdNode();
                        break;
                }
            }
        }
    };

    private static boolean hasStarted = false;

    private static void sendCreateServerActionWithDelay() {
        mainHandler.removeMessages(MSG_CREATE_SERVER);
        mainHandler.sendEmptyMessageDelayed(MSG_CREATE_SERVER, RETRY_SERVER_CREATE_INTERVAL);
    }

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
                    if (NetworkUtils.isWIFIConnected(XApplication.getContext())) {
                        doStartServerAndRegisterService();
                    }
                }
            });
        }
    }

    private static void doStartServerAndRegisterService() {
        if (ServerManager.getServerNode() == null || !ServerManager.getServerNode().isInUsing()) {
            ServerManager.createServerNode(new ServerListenerAdapter() {
                @Override
                public void onCreated() {
                    super.onCreated();

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
                    }, NetUtils.getNoneLoopLocalAddress(XApplication.getContext()), SERVICE_HOST_NAME, new NsdEventListenerAdapter() {
                        @Override
                        public void onCreated(NsdNode nsdNode) {
                            super.onCreated(nsdNode);
                            nsdNode.registerService(SERVICE_TYPE, Build.BRAND, null, ServerManager.getServerNode().getPort());
                            nsdNode.watchService(SERVICE_TYPE, null);
                        }
                    });
                }
            }, new PeerListenerAdapter(), 10 * 1000);
        }
    }
}
