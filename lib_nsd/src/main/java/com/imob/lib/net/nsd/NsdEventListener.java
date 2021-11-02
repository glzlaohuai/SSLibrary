package com.imob.lib.net.nsd;

public interface NsdEventListener {

    void onInitSucceeded(NsdManager nsdManager);

    void onInitFailed(String msg, Exception e);

    void onDestroyed(NsdManager nsdManager);


}
