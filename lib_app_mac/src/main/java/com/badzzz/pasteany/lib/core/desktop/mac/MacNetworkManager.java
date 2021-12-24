package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.imob.lib.lib_common.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

// TODO: 2021/12/24 need os specific codes
public class MacNetworkManager implements INetworkManager {
    @Override
    public boolean isWIFIConnected() {
        return true;
    }

    @Override
    public void monitorNetworkChange(NetworkChangeListener listener) {
        // TODO: 2021/12/24

    }

    @Override
    public InetAddress getLocalNotLoopbackAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            Logger.e(e);
        }
        return null;
    }
}
