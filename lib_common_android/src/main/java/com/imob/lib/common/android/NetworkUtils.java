package com.imob.lib.common.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.*;

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

}
