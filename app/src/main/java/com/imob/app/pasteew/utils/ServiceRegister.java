package com.imob.app.pasteew.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.imob.app.pasteew.XApplication;
import com.imob.lib.common.android.NetworkUtils;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.net.nsd.INsdExtraActionPerformer;
import com.imob.lib.net.nsd.NsdEventListener;
import com.imob.lib.net.nsd.NsdManager;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
                        break;
                }
            }
        }
    };

    private static boolean alreadyStarted = false;

    private static void sendCreateServerActionWithDelay() {
        mainHandler.removeMessages(MSG_CREATE_SERVER);
        mainHandler.sendEmptyMessageDelayed(MSG_CREATE_SERVER, RETRY_SERVER_CREATE_INTERVAL);
    }

    public static void startServiceRegisterStuff() {
        if (alreadyStarted) return;

        alreadyStarted = true;
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


    private static InetAddress getDeviceIpAddress(WifiManager wifi) {
        InetAddress result = null;
        try {
            // default to Android localhost
            result = InetAddress.getByName("10.0.0.2");

            // figure out our wifi address, otherwise bail
            WifiInfo wifiinfo = wifi.getConnectionInfo();
            int intaddr = wifiinfo.getIpAddress();
            byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff),
                    (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
            result = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException ex) {
            Log.w(TAG, String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
        }

        return result;
    }

    private final static InetAddress getRealLocalHost(WifiManager wifiManager) {
        InetAddress local = getLocalHost();
        if (local == null || local.toString().contains("127.0.0.1")) {
            local = getDeviceIpAddress(wifiManager);
        }
        return local;
    }


    private final static InetAddress getLocalHost() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            Logger.e(e);
        }
        return localHost;
    }

    private static void doStartServerAndRegisterService() {
        if (ServerManager.getManagedServerNode() == null) {
            ServerManager.createServerNode(new ServerListener() {
                @Override
                public void onCreated() {
                    //nsdManager stuff
                    NsdManager.setup(new INsdExtraActionPerformer() {
                        private WifiManager.MulticastLock lock;

                        @Override
                        public void setup() {
                            //acquire wifi lock
                            WifiManager wifiManager = (WifiManager) XApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            if (wifiManager != null) {
                                lock = wifiManager.createMulticastLock(getClass().getName());
                                lock.setReferenceCounted(true);
                                lock.acquire();
                            } else {
                                throw new RuntimeException("setup failed");
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
                    }, getRealLocalHost(wifiManager), SERVICE_HOST_NAME, new NsdEventListener() {
                        @Override
                        public void onInitSucceeded(NsdManager nsdManager) {
                            nsdManager.registerService(SERVICE_TYPE, "{ a name } - " + UUID.randomUUID().toString().hashCode(), null, ServerManager.getManagedServerNode().getPort());
                            nsdManager.watchService(SERVICE_TYPE, null);
                        }

                        @Override
                        public void onInitFailed(String msg, Exception e) {

                        }

                        @Override
                        public void onDestroyed(NsdManager nsdManager) {

                        }

                        @Override
                        public void onRegisterServiceFailed(NsdManager nsdManager, String type, String name, int port, String text, String msg, Exception e) {

                        }

                        @Override
                        public void onServiceDiscoveryed(NsdManager nsdManager, javax.jmdns.ServiceEvent event) {

                        }

                        @Override
                        public void onSuccessfullyWatchService(NsdManager nsdManager, String type, String name) {

                        }

                        @Override
                        public void onWatchServiceFailed(NsdManager nsdManager, String type, String name, String msg, Exception e) {

                        }

                        @Override
                        public void onSuccessfullyRegisterService(NsdManager nsdManager, String type, String name, String text, int port) {

                        }
                    });
                }

                @Override
                public void onCreateFailed(Exception exception) {
                    afterServerDestroyed();
                }

                @Override
                public void onDestroyed() {
                    afterServerDestroyed();
                }

                @Override
                public void onCorrupted(String msg, Exception e) {
                    afterServerDestroyed();

                }

                private void afterServerDestroyed() {
                    sendCreateServerActionWithDelay();
                }

                @Override
                public void onIncomingClient(Peer peer) {

                }
            }, new PeerListener() {
                @Override
                public void onMsgIntoQueue(Peer peer, String id) {

                }

                @Override
                public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {

                }

                @Override
                public void onMsgSendStart(Peer peer, String id) {

                }

                @Override
                public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {

                }

                @Override
                public void onMsgSendSucceeded(Peer peer, String id) {

                }

                @Override
                public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {

                }

                @Override
                public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {

                }

                @Override
                public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {

                }

                @Override
                public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {

                }

                @Override
                public void onIOStreamOpened(Peer peer) {

                }

                @Override
                public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {

                }

                @Override
                public void onCorrupted(Peer peer, String msg, Exception e) {

                }

                @Override
                public void onDestroy(Peer peer) {

                }

                @Override
                public void onTimeoutOccured(Peer peer) {

                }

                @Override
                public void onIncomingMsg(Peer peer, String id, int available) {

                }

                @Override
                public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {

                }


                @Override
                public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {

                }

                @Override
                public void onIncomingMsgReadSucceeded(Peer peer, String id) {

                }

                @Override
                public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {

                }

                @Override
                public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {

                }

                @Override
                public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {

                }

                @Override
                public void onMsgSendPending(Peer peer, String id) {

                }
            }, 10 * 1000);
        }
    }
}
