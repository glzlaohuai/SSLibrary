package com.imob.app.test.desktop;

import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerManager;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class Main {

    public static void main(String[] args) {
        registerService();
    }

    private static void registerService() {

        ServerManager.createServerNode(new ServerListener() {
            @Override
            public void onCreated() {
                try {
                    JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
                    // Register a service
                    ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "example - ", ServerManager.getManagedServerNode().getPort(), "path=index.html");
                    jmdns.registerService(serviceInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCreateFailed(Exception exception) {

            }

            @Override
            public void onDestroyed() {

            }

            @Override
            public void onCorrupted(String msg, Exception e) {

            }

            @Override
            public void onIncomingClient(Peer peer) {

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