package com.imob.app.pasteew.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {

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
