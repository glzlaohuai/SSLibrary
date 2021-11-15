package com.imob.lib.net.nsd;

import com.imob.lib.lib_common.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class NsdManager {

    private static final String ERROR_INIT_FAILED_INVALID_ARGUMENT = "nsd manager setup failed due to invalid arguments.";
    private static final String ERROR_INIT_FAILED_ALREADY_HAS_A_INSTANCE = "nsd manager setup failed, there already has a running instance, no need to resetup";
    private static final String ERROR_INIT_FAILED_ERROR_OCCURED = "nsd manager setup failed due to error occured";
    private static final String ERROR_INIT_FAILED_EXTRA_PERFORMER_FAILED = "nsd manager setup failed due to error occured during extra performer in action";

    private static final String ERROR_REGISTER_SERVICE_FAILED_NO_INSTANCE_FOUND = "register service failed due to no JmDns instance found, maybe it's already been destroyed.";
    private static final String ERROR_WATCH_SERVICE_FAILED_NO_INSTANCE_FOUND = "watch service failed due to no jmDNS instance found.";
    private static final String ERROR_REGISTER_SERVICE_FAILED_ERROR_OCCURED = "register service failed due to error occured.";

    private static final String TAG = "NsdManager";

    private static final ExecutorService initExecutorService = Executors.newSingleThreadExecutor();
    private static final ExecutorService retrieveServiceInfoService = Executors.newCachedThreadPool();

    private final static Byte lock = 0x0;

    private JmDNS jmDNS;
    private INsdExtraActionPerformer performer;
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

        @Override
        public void onRegisterServiceFailed(NsdManager nsdManager, String type, String name, int port, String text, String msg, Exception e) {
            Logger.i(TAG, "onRegisterServiceFailed, nsgManager: " + nsdManager + ", type:" + type + ", name: " + name + ", txt: " + text + ", port: " + port + ", msg: " + msg + ", exception: " + e);
            base.onRegisterServiceFailed(nsdManager, type, name, port, text, msg, e);
        }

        @Override
        public void onServiceDiscoveryed(NsdManager nsdManager, ServiceEvent event) {
            Logger.i(TAG, "onServiceDiscoveryed, nsdManager: " + nsdManager + ", event: " + event.getInfo());
            base.onServiceDiscoveryed(nsdManager, event);
        }

        @Override
        public void onSuccessfullyWatchService(NsdManager nsdManager, String type, String name) {
            Logger.i(TAG, "onSuccessfullyWatchService, nsdManager: " + nsdManager + ", type: " + type + ", name: " + name);
            base.onSuccessfullyWatchService(nsdManager, type, name);
        }

        @Override
        public void onWatchServiceFailed(NsdManager nsdManager, String type, String name, String msg, Exception e) {
            Logger.i(TAG, "onWatchServiceFailed, nsdManager: " + nsdManager + ", type: " + type + ", name: " + name + ", msg: " + msg + ", exception: " + e);
            base.onWatchServiceFailed(nsdManager, type, name, msg, e);
        }

        @Override
        public void onSuccessfullyRegisterService(NsdManager nsdManager, String type, String name, String text, int port) {
            Logger.i(TAG, "onSuccessfullyRegisterService, nsdManager: " + nsdManager + ", type: " + type + ", name: " + name + ", text: " + text + ", port: " + port);
            base.onSuccessfullyRegisterService(nsdManager, type, name, text, port);
        }
    }

    public NsdManager(JmDNS jmDNS, INsdExtraActionPerformer performer, NsdEventListener listener) {
        this.jmDNS = jmDNS;
        this.performer = performer;
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
                initExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        doSetupStuff(nsdExtraActionPerformer, inetAddress, hostName, wrapper);
                    }
                });
            } else {
                wrapper.onInitFailed(ERROR_INIT_FAILED_INVALID_ARGUMENT, null);
            }
        }
    }

    public static NsdManager getInstance() {
        return nsdManager;
    }

    private static void doSetupStuff(INsdExtraActionPerformer performer, InetAddress netAddress, String hostName, NsdEventListener wrapper) {
        synchronized (lock) {
            if (nsdManager == null) {

                if (performer != null) {
                    try {
                        performer.setup();
                    } catch (Exception e) {
                        Logger.e(e);

                        try {
                            performer.cleaup();
                        } catch (Exception ex) {
                            Logger.e(ex);
                        }
                        wrapper.onInitFailed(ERROR_INIT_FAILED_EXTRA_PERFORMER_FAILED, e);
                        return;
                    }
                }
                try {
                    Logger.i(TAG, "create JMDNS: " + netAddress + ", " + hostName);
                    JmDNS jmDNS = JmDNS.create(netAddress, hostName);
                    nsdManager = new NsdManager(jmDNS, performer, wrapper);

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

    /**
     * @param type
     * @param name
     * @return true - has running jmDNs instance && arguments invalid, false - the opposite
     */
    public boolean registerService(final String type, final String name, final String text, final int port) {
        if (jmDNS != null && type != null && !type.equals("") && name != null && !name.equals("")) {
            initExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    doRegisterService(type, name, text, port);
                }
            });
            return true;
        }
        return false;
    }


    private boolean isThisInstanceStillInUse() {
        return this == NsdManager.nsdManager;
    }


    /**
     * @param type
     * @param name
     * @return true - has a running jmDNS instance && valid arguments | false - the opposite
     */
    public boolean watchService(final String type, final String name) {
        if (jmDNS != null && type != null && !type.equals("")) {
            initExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock) {
                        if (jmDNS != null) {
                            jmDNS.addServiceListener(type, new ServiceListener() {
                                @Override
                                public void serviceAdded(final ServiceEvent event) {
                                    if (event != null && event.getName() != null && (name == null || event.getName().equals(name)) && isThisInstanceStillInUse()) {
                                        synchronized (lock) {
                                            retrieveServiceInfoService.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (isThisInstanceStillInUse()) {
                                                        jmDNS.getServiceInfo(event.getType(), event.getName());
                                                    } else {
                                                        Logger.i(TAG, "try to get detailed service info of: " + event + ", but found that its related nsd manager was already destroyed at this time, so just do nothing and that's it.");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void serviceRemoved(ServiceEvent event) {

                                }

                                @Override
                                public void serviceResolved(ServiceEvent event) {
                                    if (isThisInstanceStillInUse()) {
                                        listener.onServiceDiscoveryed(NsdManager.this, event);
                                    } else {
                                        Logger.i(TAG, "got detailed service info: " + event + ", but its related manager was already destroyed currently, so just stop and do nothing, no further callback needed.");
                                    }
                                }
                            });
                            listener.onSuccessfullyWatchService(NsdManager.this, type, name);
                        } else {
                            listener.onWatchServiceFailed(NsdManager.this, type, name, ERROR_WATCH_SERVICE_FAILED_NO_INSTANCE_FOUND, null);
                        }
                    }
                }
            });
        }
        return false;
    }

    private void doRegisterService(String type, String name, String text, int port) {
        text = text == null ? "" : text;
        synchronized (lock) {
            if (jmDNS != null) {
                try {
                    jmDNS.registerService(ServiceInfo.create(type, name, port, text));
                    listener.onSuccessfullyRegisterService(this, type, name, text, port);
                } catch (IOException e) {
                    Logger.e(e);
                    listener.onRegisterServiceFailed(this, type, name, port, text, ERROR_REGISTER_SERVICE_FAILED_ERROR_OCCURED, e);
                }
            } else {
                listener.onRegisterServiceFailed(this, type, name, port, text, ERROR_REGISTER_SERVICE_FAILED_NO_INSTANCE_FOUND, null);
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
            jmDNS = null;
            if (performer != null) {
                try {
                    performer.cleaup();
                } catch (Exception e) {
                    Logger.e(e);
                }
            }
            performer = null;
            listener.onDestroyed(this);
        }
    }

    public void triggerServiceInfoResolve(final String type, final String name) {
        if (jmDNS != null) {
            retrieveServiceInfoService.execute(new Runnable() {
                @Override
                public void run() {
                    if (jmDNS != null) {
                        jmDNS.getServiceInfo(type, name);
                    }
                }
            });
        }
    }

    /**
     * @return true - has jmDns instance, false - the opposite
     */
    public boolean destroy() {
        synchronized (lock) {
            try {
                if (jmDNS != null) {
                    initExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            doDestroy();
                        }
                    });
                    return true;
                }
            } finally {
                // assign null to indicate that there has no running nsd manager instance, you need to recall setup() before run any of other methods.
                NsdManager.nsdManager = null;
            }
        }
        return false;
    }


}
