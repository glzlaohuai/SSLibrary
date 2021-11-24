package com.badzzz.pasteany.lib.core.android;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.badzzz.pasteany.core.interfaces.INSDServiceManager;
import com.imob.lib.net.nsd.INsdExtraActionPerformer;

public class AndroidNsdServiceManager implements INSDServiceManager {

    private Context context;

    public AndroidNsdServiceManager(Context context) {
        this.context = context;
    }

    @Override
    public INsdExtraActionPerformer getExtraActionPerformer() {
        return new INsdExtraActionPerformer() {
            private WifiManager.MulticastLock lock;

            @Override
            public void setup() {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    lock = wifiManager.createMulticastLock(getClass().getName());
                    lock.setReferenceCounted(true);
                    lock.acquire();
                } else {
                    throw new RuntimeException("setup failed due to no wifi manager instance found, this should never happen.");
                }
            }

            @Override
            public void cleaup() {
                //release wifi lock
                if (lock != null) {
                    lock.release();
                    lock = null;
                }
            }


        };
    }
}
