package com.imob.lib.net.nsd;

import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.utils.SSThreadFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class NsdNode {

    private static final String ERROR_INIT_FAILED_INVALID_ARGUMENT_STATE = "create failed, invalid arguments or illegal state.";
    private static final String ERROR_INIT_FAILED_ERROR_OCCURED = "nsd manager setup failed due to error occured";
    private static final String ERROR_INIT_FAILED_EXTRA_PERFORMER_FAILED = "create failed due to error occured during extra performer in action";

    private static final String ERROR_REGISTER_SERVICE_FAILED_NO_INSTANCE_FOUND = "register service failed due to no JmDns instance found, maybe it's already been destroyed.";
    private static final String ERROR_WATCH_SERVICE_FAILED_NO_INSTANCE_FOUND = "watch service failed due to no jmDNS instance found.";
    private static final String ERROR_REGISTER_SERVICE_FAILED_ERROR_OCCURED = "register service failed due to error occured.";

    private static final String S_TAG = "NsdNode";

    private final ExecutorService createDestroyRegisterService = Executors.newSingleThreadExecutor(SSThreadFactory.build("nsd-init"));
    private final ExecutorService retrieveServiceInfoService = Executors.newCachedThreadPool(SSThreadFactory.build("nsd-retri"));

    private final Byte lock = 0x0;

    private JmDNS jmDNS;

    private INsdExtraActionPerformer performer;
    private InetAddress inetAddress;
    private String hostName;
    private NsdEventListenerGroup listener = new NsdEventListenerGroup();

    private static NsdEventListenerGroup globalListenerGroup = new NsdEventListenerGroup();
    private static NsdEventListenerGroup monitorListenerGroup = new NsdEventListenerGroup();
    private static NsdEventListenerGroup routerListenerGroup = new NsdEventListenerGroup();

    private boolean isDestroyed;
    private boolean isCreating;

    private String tag;

    private static AtomicReference<NsdNode> activeNsdNodeRef = new AtomicReference<>();
    private static String activeRegisterServiceText = null;
    private static int activeRegisterPort = -1;

    static {
        routerListenerGroup.add(globalListenerGroup);
        routerListenerGroup.add(monitorListenerGroup);

        routerListenerGroup.add(new NsdEventListenerAdapter() {

            @Override
            public void onDestroyed(NsdNode nsdNode, String reason, Exception e) {
                super.onDestroyed(nsdNode, reason, e);

                NsdNode.activeNsdNodeRef.compareAndSet(nsdNode, null);
                NsdNode.activeRegisterServiceText = null;
                NsdNode.activeRegisterPort = -1;
            }

            @Override
            public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {
                super.onSuccessfullyRegisterService(nsdNode, type, name, text, port);

                NsdNode.activeNsdNodeRef.set(nsdNode);
                NsdNode.activeRegisterServiceText = text;
                NsdNode.activeRegisterPort = port;
            }
        });
    }

    public NsdNode(INsdExtraActionPerformer performer, InetAddress inetAddress, String hostName, NsdEventListener listener) {
        this.performer = performer;
        this.inetAddress = inetAddress;
        this.hostName = hostName;
        this.listener.add(listener);
        this.listener.add(routerListenerGroup);

        tag = S_TAG + " # " + hashCode();
    }

    public synchronized void registerListener(NsdEventListener listener) {
        this.listener.add(listener);
    }

    public synchronized void unregisterListener(NsdEventListener listener) {
        this.listener.remove(listener);
    }

    public static void setGlobalListener(NsdEventListener listener) {
        NsdNode.globalListenerGroup.clear();
        NsdNode.globalListenerGroup.add(listener);
    }

    public static void monitorListener(NsdEventListener listener) {
        monitorListenerGroup.add(listener);
    }

    public static void unmonitorListener(NsdEventListener listener) {
        monitorListenerGroup.remove(listener);
    }

    public boolean isRunning() {
        return jmDNS != null && !isDestroyed;
    }

    public boolean isInUsing() {
        return isCreating || isRunning();
    }


    public boolean create() {
        Logger.i(tag, "create");
        //valid arguments, go on
        synchronized (lock) {
            if (inetAddress != null && hostName != null && !hostName.equals("") && !isCreating && !isRunning() && !isDestroyed) {
                isCreating = true;
                createDestroyRegisterService.execute(new Runnable() {
                    @Override
                    public void run() {
                        doCreateStuff();
                    }
                });
                return true;
            } else {
                listener.onCreateFailed(ERROR_INIT_FAILED_INVALID_ARGUMENT_STATE, null);
                return false;
            }
        }
    }

    private void doCreateStuff() {
        Logger.i(tag, "do create stuff");
        synchronized (lock) {
            if (isRunning() || isDestroyed) {
                return;
            } else {
                try {
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
                            listener.onCreateFailed(ERROR_INIT_FAILED_EXTRA_PERFORMER_FAILED, e);
                            return;
                        }
                    }
                    try {
                        Logger.i(tag, "create JMDNS: " + inetAddress + ", " + hostName);
                        jmDNS = JmDNS.create(inetAddress, hostName);

                        listener.onCreated(NsdNode.this);
                    } catch (IOException e) {
                        Logger.e(e);
                        listener.onCreateFailed(ERROR_INIT_FAILED_ERROR_OCCURED, e);
                    }
                } finally {
                    isCreating = false;
                }
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
            createDestroyRegisterService.execute(new Runnable() {
                @Override
                public void run() {
                    doRegisterService(type, name, text, port);
                }
            });
            return true;
        }
        return false;
    }


    /**
     * @param type
     * @param name
     * @return true - has a running jmDNS instance && valid arguments | false - the opposite
     */
    public boolean watchService(final String type, final String name) {
        if (jmDNS != null && type != null && !type.equals("")) {
            createDestroyRegisterService.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock) {
                        if (jmDNS != null) {
                            jmDNS.addServiceListener(type, new ServiceListener() {
                                @Override
                                public void serviceAdded(final ServiceEvent event) {
                                    if (event != null && event.getName() != null && (name == null || event.getName().equals(name))) {
                                        synchronized (lock) {
                                            retrieveServiceInfoService.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (jmDNS != null) {
                                                        jmDNS.getServiceInfo(event.getType(), event.getName());
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
                                    listener.onServiceDiscovered(NsdNode.this, event.getInfo());
                                }
                            });
                            listener.onSuccessfullyWatchService(NsdNode.this, type, name);
                        } else {
                            listener.onWatchServiceFailed(NsdNode.this, type, name, ERROR_WATCH_SERVICE_FAILED_NO_INSTANCE_FOUND, null);
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

    private void doDestroy(String reason, Exception exception) {
        Logger.i(tag, "destroy stuff called, reason: " + reason + ", exception: " + exception);
        synchronized (lock) {
            Logger.i(tag, "do destroy got lock!");
            if (jmDNS != null) {
                try {
                    jmDNS.unregisterAllServices();
                    jmDNS.close();
                } catch (IOException e) {
                    Logger.e(e);
                }
            }

            if (performer != null) {
                try {
                    performer.cleaup();
                } catch (Exception e) {
                    Logger.e(e);
                }
            }

            performer = null;
            jmDNS = null;

            listener.onDestroyed(this, reason, exception);

            createDestroyRegisterService.shutdown();
            retrieveServiceInfoService.shutdown();
        }
    }

    public void triggerServiceInfoResolve(final String type, final String name) {
        if (jmDNS != null) {
            retrieveServiceInfoService.execute(new Runnable() {
                @Override
                public void run() {
                    if (jmDNS != null) {
                        Logger.i(tag, "get service info, type: " + type + ", name: " + name);
                        ServiceInfo serviceInfo = null;
                        try {
                            serviceInfo = jmDNS.getServiceInfo(type, name);
                        } catch (Exception e) {
                            Logger.e(e);
                        }
                        Logger.i(tag, "got service info: " + serviceInfo);
                        if (serviceInfo != null) {
                            listener.onServiceDiscovered(NsdNode.this, serviceInfo);
                        }

                    }
                }
            });
        }
    }

    /**
     */
    public void destroy(String reason, Exception exception) {
        if (!isDestroyed) {
            Logger.i(tag, "destroy, reason: " + reason + ", exception: " + exception);
            handleDestroy(reason, exception);
        }
    }


    private void handleDestroy(final String reason, final Exception e) {
        createDestroyRegisterService.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (!isDestroyed) {
                        isDestroyed = true;
                        if (jmDNS != null) {
                            doDestroy(reason, e);
                        }
                    }
                }

            }
        });
    }

    public static NsdNode getActiveNsdNode() {
        return activeNsdNodeRef.get();
    }

    public static String getActiveRegisteredServiceText() {
        return activeRegisterServiceText;
    }

    public static int getActiveRegisterPort() {
        return activeRegisterPort;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public String toString() {
        return tag;
    }
}
