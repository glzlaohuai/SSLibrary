package com.badzzz.pasteany.core.utils;

import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.badzzz.pasteany.core.nsd.NsdServiceHandler;
import com.badzzz.pasteany.core.nsd.peer.client.ConnectedClientsManager;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.net.nsd.NsdEventListenerAdapter;
import com.imob.lib.net.nsd.NsdNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jmdns.ServiceInfo;

public class NsdServiceInfoUtils {
    private static final String TAG = "NsdServiceInfoUtils";

    public interface IPositivelyNsdServiceInfoFetchListener {
        void onTimeout(String deviceID, long timeout);

        void onFetched(ServiceInfo serviceInfo);
    }

    public final static IDeviceInfoManager.DeviceInfo buildFromServiceInfo(ServiceInfo serviceInfo) {
        if (serviceInfo == null) return null;

        String name = serviceInfo.getName();
        String textString = serviceInfo.getTextString();

        Logger.i(TAG, "name: " + name + ", text: " + textString);

        if (name == null || textString == null || name.equals("") || textString.equals("")) {
            return null;
        }

        IDeviceInfoManager.DeviceInfo deviceInfo = null;
        try {
            JSONObject jsonObject = new JSONObject(name);
            String deviceID = jsonObject.getString(Constants.Device.KEY_DEVICEID);
            jsonObject = new JSONObject(textString);
            String deviceName = jsonObject.getString(Constants.Device.KEY_DEVICE_NAME);
            String platform = jsonObject.getString(Constants.Device.KEY_PLATFORM);


            deviceInfo = new IDeviceInfoManager.DeviceInfo(deviceID, deviceName, platform);
        } catch (Throwable e) {
            Logger.e(TAG, "retrieve device info from nsd service info failed", e);
        }
        return deviceInfo;

    }

    public final static String getDeviceIDFromServiceInfo(ServiceInfo serviceInfo) {
        if (serviceInfo == null || serviceInfo.getName() == null || serviceInfo.getName().equals("")) {
            return null;
        }
        try {
            return new JSONObject(serviceInfo.getName()).getString(Constants.Device.KEY_DEVICEID);
        } catch (JSONException e) {
            Logger.e(e);
        }
        return null;
    }


    public static void tryToFetchServiceInfoByID(final String id, final long timeout, final IPositivelyNsdServiceInfoFetchListener listener) {
        final AtomicBoolean callbacked = new AtomicBoolean(false);
        final Timer timer = new Timer();

        final NsdEventListenerAdapter listenerAdapter = new NsdEventListenerAdapter() {
            @Override
            public void onCreated(NsdNode nsdNode) {
                super.onCreated(nsdNode);
                nsdNode.triggerServiceInfoResolve(Constants.NSD.NSD_SERVICE_TYPE, INSDServiceManager.buildServiceName(id, PreferenceManagerWrapper.getInstance().getServiceName()));
            }

            @Override
            public void onServiceDiscovered(NsdNode nsdNode, ServiceInfo event) {
                super.onServiceDiscovered(nsdNode, event);
                if (event != null) {
                    String gotID = getDeviceIDFromServiceInfo(event);

                    if (gotID != null && gotID.equals(id) && callbacked.compareAndSet(false, true)) {
                        listener.onFetched(event);
                        timer.cancel();
                        NsdNode.unmonitorListener(this);
                    }
                }
            }
        };

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (callbacked.compareAndSet(false, true)) {
                    listener.onTimeout(id, timeout);
                }
                NsdNode.unmonitorListener(listenerAdapter);
            }
        }, timeout);

        NsdNode.monitorListener(listenerAdapter);

        NsdServiceHandler nsdServiceHandler = ConnectedClientsManager.getInUsingServiceHandler();
        if (nsdServiceHandler != null) {
            nsdServiceHandler.getNsdNode().triggerServiceInfoResolve(Constants.NSD.NSD_SERVICE_TYPE, INSDServiceManager.buildServiceName(id, PreferenceManagerWrapper.getInstance().getServiceName()));
        }
    }


}
