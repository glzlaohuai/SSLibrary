package com.badzzz.pasteany.lib.core.android;

import android.content.Context;

import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.imob.lib.common.android.NetworkUtils;

import java.net.InetAddress;

public class AndroidNetworkManager implements INetworkManager {

    private Context context;

    public AndroidNetworkManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean isWIFIConnected() {
        return NetworkUtils.isWIFIConnected(context);
    }

    @Override
    public void monitorNetworkChange(NetworkChangeListener listener) {
        NetworkUtils.monitorNetworkChange(context, new NetworkUtils.NetworkChangeListener() {
            @Override
            public void onNetworkStateChanged() {
                listener.onNetworkChanged();
            }
        });
    }

    @Override
    public InetAddress getLocalNotLoopbackAddress() {
        return NetworkUtils.getNoneLoopLocalAddress(context);
    }
}
