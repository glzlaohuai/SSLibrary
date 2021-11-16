package com.badzzz.pasteany.core.interfaces;

public interface IDeviceInfoManager {

    String getDeviceID();

    String getDeviceName();

    String getDeviceDetailInfo();

    String generateDeviceID();

    void setDeviceName(String deviceName);
}
