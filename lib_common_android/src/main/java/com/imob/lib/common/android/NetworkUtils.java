package com.imob.lib.common.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class NetworkUtils {

    private static boolean hasRegisteredNetworkChangeListener;
    private final static InnerNetworkChangeListener networkChangeListenerGroup = new InnerNetworkChangeListener();
    private final static BroadcastReceiver networkChangeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            networkChangeListenerGroup.onNetworkStateChanged();
        }
    };

    public interface NetworkChangeListener {
        void onNetworkStateChanged();
    }


    public static class InnerNetworkChangeListener implements NetworkChangeListener {
        private final Set<NetworkChangeListener> set = new HashSet<>();

        public void add(NetworkChangeListener listener) {
            set.add(listener);
        }

        public void remove(NetworkChangeListener listener) {
            set.remove(listener);
        }

        @Override
        public void onNetworkStateChanged() {
            for (NetworkChangeListener listener : set) {
                listener.onNetworkStateChanged();
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static boolean isWIFIConnected(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return networkInfo != null && networkInfo.getState() != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }


    public static void monitorNetworkChange(Context context, NetworkChangeListener listener) {
        if (context == null || listener == null) return;
        networkChangeListenerGroup.add(listener);

        if (!hasRegisteredNetworkChangeListener) {

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(networkChangeBroadcastReceiver, intentFilter);

            hasRegisteredNetworkChangeListener = true;
        }
    }

    public static void removeListenerFromNetworkChangeMonitorList(NetworkChangeListener listener) {
        if (listener != null) {
            networkChangeListenerGroup.remove(listener);
        }
    }


    private static InetAddress getLocalAddress(WifiManager wifiManager) {
        InetAddress result = null;
        try {
            // default to Android localhost
            result = InetAddress.getByName("10.0.0.2");

            // figure out our wifi address, otherwise bail
            WifiInfo wifiinfo = wifiManager.getConnectionInfo();
            int intaddr = wifiinfo.getIpAddress();
            byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff),
                    (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
            result = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException ex) {
        }

        return result;
    }

    public final static InetAddress getNoneLoopLocalAddress(Context context) {
        WifiManager wifiManager;
        if (context == null || (wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE)) == null)
            return null;

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
        }

        if (inetAddress == null || inetAddress.toString().contains("127.0.0.1")) {
            inetAddress = getLocalAddress(wifiManager);
        }

        return inetAddress;
    }


}
