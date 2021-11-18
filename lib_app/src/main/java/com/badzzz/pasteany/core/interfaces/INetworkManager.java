package com.badzzz.pasteany.core.interfaces;

import java.net.InetAddress;

public interface INetworkManager {

    interface NetworkChangeListener {
        void onNetworkChanged();
    }

    boolean isWIFIConnected();

    void monitorNetworkChange(NetworkChangeListener listener);

    InetAddress getLocalNotLoopbackAddress();

}
