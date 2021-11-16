package com.badzzz.pasteany.core.interfaces;

public interface INetworkManager {

    interface NetworkChangeListener {
        void onNetworkChanged();
    }

    boolean isWIFIConnected();

    void monitorNetworkChange(NetworkChangeListener listener);

}
