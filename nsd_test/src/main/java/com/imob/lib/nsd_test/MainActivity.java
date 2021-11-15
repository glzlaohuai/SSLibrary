package com.imob.lib.nsd_test;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String SERVICE_TYPE = "_paste-everywhere._tcp.local";

    private static final String TAG = "MainActivity";
    private static WifiManager.MulticastLock multicastLock;
    private static JmDNS jmdns;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        initializeDiscoveryListener();

        String ipAddress = getIPAddress();
        Log.i(TAG, "onCreate: " + ipAddress);


        new Thread(new Runnable() {
            @Override
            public void run() {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                    System.out.println("IP Address:- " + inetAddress.getHostAddress());
                    System.out.println("Host Name:- " + inetAddress.getHostName());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void createServer(View view) {
        if (ServerManager.getServerNode() != null && ServerManager.getServerNode().isRunning()) {
            try {
                registerService(ServerManager.getServerNode().getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ServerManager.createServerNode(new ServerListener() {
                @Override
                public void onCreated() {
                    try {
                        registerService(ServerManager.getServerNode().getPort());
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

            Log.i(TAG, "getDeviceIpAddress: " + Arrays.toString(byteaddr));
            result = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException ex) {
            Log.w(TAG, String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
        }

        return result;
    }


    public void initializeDiscoveryListener() {
        NsdManager nsdManager = (NsdManager) getApplicationContext().getSystemService(Context.NSD_SERVICE);
        // Instantiate a new DiscoveryListener
        NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + service);
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            }
        };
        nsdManager.discoverServices("_http._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public static String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();

                if (netI.getDisplayName().equals("wlan0") || netI.getDisplayName().equals("eth0")) {
                    for (Enumeration<InetAddress> enumIpAddr = netI
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void doRegister() {
        try {
            Log.i(TAG, "Starting Mutlicast Lock...");
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            // get the device ip address

            final InetAddress deviceIpAddress = InetAddress.getLocalHost();
            InetAddress x = getDeviceIpAddress(wifi);

            Log.i(TAG, "doRegister: " + deviceIpAddress.toString());
            Log.i(TAG, "doRegister - 2: " + x);
            multicastLock = wifi.createMulticastLock(getClass().getName());
            multicastLock.setReferenceCounted(true);
            multicastLock.acquire();

            Log.i(TAG, "Starting ZeroConf probe....");
            jmdns = JmDNS.create(x, "xxx-hostname");
            //            jmdns.addServiceTypeListener(new ServiceTypeListener() {
            //                @Override
            //                public void serviceTypeAdded(ServiceEvent serviceEvent) {
            //
            //                    Log.i(TAG, "serviceTypeAdded: " + serviceEvent);
            //                }
            //
            //                @Override
            //                public void subTypeForServiceTypeAdded(ServiceEvent serviceEvent) {
            //                }
            //            });
            ServiceInfo serviceInfo = ServiceInfo.create(SERVICE_TYPE, "example - " + Build.DEVICE, ServerManager.getServerNode().getPort(), "path=index.html");
            jmdns.registerService(serviceInfo);
            jmdns.addServiceListener(SERVICE_TYPE, new ServiceListener() {
                @Override
                public void serviceAdded(ServiceEvent event) {
                    Log.i(TAG, "serviceAdded: " + event.toString());
                    event.getDNS().getServiceInfo(event.getType(), event.getName());
                }

                @Override
                public void serviceRemoved(ServiceEvent event) {
                    Log.i(TAG, "serviceRemoved: " + event);
                }

                @Override
                public void serviceResolved(ServiceEvent event) {
                    Log.i(TAG, "serviceResolved: " + event.toString());
                }
            });
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        Log.i(TAG, "Started ZeroConf probe....");
    }

    public void registerService(int port) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doRegister();
            }
        }).start();
    }

    public void readServiceInfo(View view) {
        ServiceInfo serviceInfo = jmdns.getServiceInfo(SERVICE_TYPE, "example - umi");
        Log.i(TAG, "readServiceInfo: " + serviceInfo);
        if (serviceInfo == null) return;

        Enumeration<String> enumeration = serviceInfo.getPropertyNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Log.i(TAG, "readServiceInfo: key" + enumeration.nextElement());
            }
        }
    }
}