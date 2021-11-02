package com.imob.app.test.desktop;

import com.imob.lib.net.nsd.NsdEventListener;
import com.imob.lib.net.nsd.NsdManager;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.ServiceEvent;

public class Main {

    public static final String SERVICE_TYPE = "_paste-everywhere._tcp.local";


    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println("IP Address:- " + inetAddress.getHostAddress());
        System.out.println("Host Name:- " + inetAddress.getHostName());

        registerService();
    }

    private static void registerService() {

        ServerManager.createServerNode(new ServerListener() {
            @Override
            public void onCreated() {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                NsdManager.setup(null, inetAddress, "a-test-host-name", new NsdEventListener() {
                    @Override
                    public void onInitSucceeded(NsdManager nsdManager) {
                        nsdManager.registerService(SERVICE_TYPE, "{a test service name}", "", ServerManager.getManagedServerNode().getPort());
                    }

                    @Override
                    public void onInitFailed(String msg, Exception e) {

                    }

                    @Override
                    public void onDestroyed(NsdManager nsdManager) {

                    }

                    @Override
                    public void onRegisterServiceFailed(NsdManager nsdManager, String type, String name, int port, String text, String msg, Exception e) {

                    }

                    @Override
                    public void onServiceDiscoveryed(NsdManager nsdManager, ServiceEvent event) {

                    }

                    @Override
                    public void onServiceInWatch(NsdManager nsdManager, String type, String name) {

                    }

                    @Override
                    public void onSuccessfullyRegisterService(NsdManager nsdManager, String type, String name, String text, int port) {

                    }
                });

            }

            @Override
            public void onCreateFailed(Exception exception) {

            }

            @Override
            public void onDestroyed() {
                cleanup();
            }

            @Override
            public void onCorrupted(String msg, Exception e) {
                cleanup();
            }

            @Override
            public void onIncomingClient(Peer peer) {

            }

            private void cleanup() {
                if (NsdManager.getInstance() != null) {
                    NsdManager.getInstance().destroy();
                }
            }
        }, new PeerListener() {
            @Override
            public void onMsgIntoQueue(Peer peer, String id) {

            }

            @Override
            public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendStart(Peer peer, String id) {

            }

            @Override
            public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendSucceeded(Peer peer, String id) {

            }

            @Override
            public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {

            }

            @Override
            public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {

            }

            @Override
            public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {

            }

            @Override
            public void onIOStreamOpened(Peer peer) {

            }

            @Override
            public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {

            }

            @Override
            public void onCorrupted(Peer peer, String msg, Exception e) {

            }

            @Override
            public void onDestroy(Peer peer) {

            }

            @Override
            public void onIncomingMsg(Peer peer, String id, int available) {

            }

            @Override
            public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {

            }

            @Override
            public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {

            }

            @Override
            public void onIncomingMsgReadSucceeded(Peer peer, String id) {

            }

            @Override
            public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {

            }

            @Override
            public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendPending(Peer peer, String id) {

            }
        });

    }

}