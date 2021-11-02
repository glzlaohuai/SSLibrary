package com.imob.lib.net.nsd;

import com.imob.lib.lib_common.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;

public class NsdManager {

    public static final String ERROR_INIT_FAILED_INVALID_ARGUMENT = "nsd manager setup failed due to invalid arguments.";
    public static final String ERROR_INIT_FAILED_ALREADY_HAS_A_INSTANCE = "nsd manager setup failed, there already has a running instance, no need to resetup";
    public static final String ERROR_INIT_FAILED_ERROR_OCCURED = "nsd manager setup failed due to error occured";

    private static final String TAG = "NsdManager";

    private static final ExecutorService initExecutorService = Executors.newSingleThreadExecutor();

    private final static Byte lock = 0x0;

    private JmDNS jmDNS;
    private NsdEventListener listener;

    private static NsdManager nsdManager;

    private static class NsdEventListenerWrapper implements NsdEventListener {

        private NsdEventListener base;

        public NsdEventListenerWrapper(NsdEventListener base) {
            this.base = base;
        }

        @Override
        public void onInitSucceeded(NsdManager nsdManager) {
            Logger.i(TAG, "onInitSucceeded: " + nsdManager);
            base.onInitSucceeded(nsdManager);
        }

        @Override
        public void onInitFailed(String msg, Exception e) {
            Logger.i(TAG, "onInitFailed, msg: " + msg + ", e: " + e);
            base.onInitFailed(msg, e);
        }

        @Override
        public void onDestroyed(NsdManager nsdManager) {
            Logger.i(TAG, "onDestroyed, nsdManager: " + nsdManager);
            base.onDestroyed(nsdManager);
        }
    }

    public NsdManager(JmDNS jmDNS, NsdEventListener listener) {
        this.jmDNS = jmDNS;
        this.listener = listener;
    }

    public static void setup(final INsdExtraActionPerformer nsdExtraActionPerformer, final InetAddress inetAddress, final String hostName, NsdEventListener listener) {
        final NsdEventListenerWrapper wrapper = new NsdEventListenerWrapper(listener);

        if (nsdManager != null) {
            wrapper.onInitFailed(ERROR_INIT_FAILED_ALREADY_HAS_A_INSTANCE, null);
            return;
        } else {
            //valid arguments, go on
            if (inetAddress != null && hostName != null && !hostName.equals("")) {
                wrapper.onInitFailed(ERROR_INIT_FAILED_INVALID_ARGUMENT, null);
            } else {
                initExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        doSetupStuff(nsdExtraActionPerformer, inetAddress, hostName, wrapper);
                    }
                });

            }
        }
    }

    public static NsdManager getInstance() {
        return nsdManager;
    }


    private static void doSetupStuff(INsdExtraActionPerformer performer, InetAddress netAddress, String hostName, NsdEventListener wrapper) {
        synchronized (lock) {
            if (nsdManager == null) {
                try {
                    JmDNS jmDNS = JmDNS.create(netAddress, hostName);
                    nsdManager = new NsdManager(jmDNS, wrapper);

                    wrapper.onInitSucceeded(nsdManager);
                } catch (IOException e) {
                    Logger.e(e);
                    wrapper.onInitFailed(ERROR_INIT_FAILED_ERROR_OCCURED, e);
                }
            } else {
                wrapper.onInitFailed(ERROR_INIT_FAILED_ALREADY_HAS_A_INSTANCE, null);
            }
        }
    }

    private void doDestroy() {
        synchronized (lock) {
            try {
                jmDNS.unregisterAllServices();
                jmDNS.close();
            } catch (IOException e) {
                Logger.e(e);
            }
            listener.onDestroyed(this);
        }
    }


    /**
     *
     * @return true - has jmDns instance, false - the opposite
     */
    public boolean destroy() {
        if (jmDNS != null) {
            initExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    doDestroy();
                }
            });

            // assign null to indicate that there has no running nsd manager instance, you need to recall setup() before run any of other methods.
            NsdManager.nsdManager = null;
            return true;
        }
        return false;
    }


}
