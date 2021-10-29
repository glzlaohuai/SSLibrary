package com.imob.lib.nsd_test;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceTypeListener;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static WifiManager.MulticastLock multicastLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createServer(View view) {
        if (ServerManager.getManagedServerNode() != null && ServerManager.getManagedServerNode().isRunning()) {
            try {
                registerService(ServerManager.getManagedServerNode().getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ServerManager.createServerNode(new ServerListener() {
                @Override
                public void onCreated() {
                    try {
                        registerService(ServerManager.getManagedServerNode().getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCreateFailed(Exception exception) {

                }

                @Override
                public void onDestroyed() {

                }

                @Override
                public void onCorrupted(String msg, Exception e) {

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
                public void onIncomingMsg(Peer peer, String id, int available) {

                }

                @Override
                public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {

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
            });
        }
    }

    private static JmDNS jmdns;


    private InetAddress getDeviceIpAddress(WifiManager wifi) {
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

    private void doRegister() {
        try {
            Log.i(TAG, "Starting Mutlicast Lock...");
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            // get the device ip address
            final InetAddress deviceIpAddress = getDeviceIpAddress(wifi);
            multicastLock = wifi.createMulticastLock(getClass().getName());
            multicastLock.setReferenceCounted(true);
            multicastLock.acquire();

            Log.i(TAG, "Starting ZeroConf probe....");
            jmdns = JmDNS.create(deviceIpAddress, "xxx-hostname");
            jmdns.addServiceTypeListener(new ServiceTypeListener() {
                @Override
                public void serviceTypeAdded(ServiceEvent serviceEvent) {
                }

                @Override
                public void subTypeForServiceTypeAdded(ServiceEvent serviceEvent) {
                }
            });

            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "example - " + Build.DEVICE, 1234, "path=index.html");
            jmdns.registerService(serviceInfo);

        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        Log.i(TAG, "Started ZeroConf probe....");
    }


    private static void stopScan() {
        try {
            if (jmdns != null) {
                Log.i(TAG, "Stopping ZeroConf probe....");
                jmdns.unregisterAllServices();
                jmdns.close();
                jmdns = null;
            }
            if (multicastLock != null) {
                Log.i(TAG, "Releasing Mutlicast Lock...");
                multicastLock.release();
                multicastLock = null;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }


    public void registerService(int port) throws IOException {
        doRegister();
    }
}