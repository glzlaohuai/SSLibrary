package com.badzzz.pasteany.lib.core.desktop.mac;

import com.badzzz.pasteany.core.interfaces.INetworkManager;
import com.imob.lib.lib_common.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.e(ex);
        }
        return null;
    }
}
