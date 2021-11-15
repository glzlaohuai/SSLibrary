package com.imob.app.test.desktop;

import com.imob.lib.net.nsd.NsdEventListener;
import com.imob.lib.net.nsd.NsdManager;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.PeerListenerAdapter;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerManager;
import com.imob.lib.sslib.server.ServerNode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.jmdns.ServiceEvent;

public class Main {

    public static final String SERVICE_TYPE = "_pasteanywhere._tcp.local.";

    public static void main(String[] args) throws IOException {
        monitorKeyInput();
    }

    private static void monitorKeyInput() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Options:\n1 - Create Server\n2 - Stop Server\n3 - Create Client\n 4 - Destroy Client\n 5 - Create NSD Service");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    createServer();
                    break;
                case 2:
                    destroyServer();
                    break;
                case 5:
                    createNSDService();
                    break;
                default:
                    System.out.println("not supported option");
                    break;
            }
        }
    }

    private static void createNSDService() {
        if (ServerManager.getServerNode() != null) {
            try {
                doCreateNSDService();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            ServerManager.createServerNode(new ServerListenerAdapter() {
                @Override
                public void onCreated(ServerNode serverNode) {
                    super.onCreated(serverNode);
                    try {
                        doCreateNSDService();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }, new PeerListenerAdapter(), 10 * 1000);
        }
    }


    private static void doCreateNSDService() throws UnknownHostException {
        NsdManager.create(null, InetAddress.getLocalHost(), "default host name", new NsdEventListener() {
            @Override
            public void onCreated(NsdNode nsdNode) {
                nsdNode.registerService(SERVICE_TYPE, "a test name", null, ServerManager.getServerNode().getPort());
            }

            @Override
            public void onCreateFailed(String msg, Exception e) {

            }

            @Override
            public void onDestroyed(NsdNode nsdNode) {

            }

            @Override
            public void onRegisterServiceFailed(NsdNode nsdNode, String type, String name, int port, String text, String msg, Exception e) {
            }

            @Override
            public void onServiceDiscoveryed(NsdNode nsdNode, ServiceEvent event) {

            }

            @Override
            public void onSuccessfullyWatchService(NsdNode nsdNode, String type, String name) {

            }

            @Override
            public void onWatchServiceFailed(NsdNode nsdNode, String type, String name, String msg, Exception e) {

            }

            @Override
            public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {
                nsdNode.watchService(SERVICE_TYPE, null);
            }
        });
    }


    private static void destroyServer() {
        ServerNode managedServerNode = ServerManager.getServerNode();
        if (managedServerNode != null) {
            ServerManager.getServerNode().destroy();
        }
    }

    private static void createServer() {
        ServerManager.createServerNode(new ServerListenerAdapter() {
            @Override
            public void onCreated(ServerNode serverNode) {
                System.out.println(serverNode.getServerSocketInfo());
            }
        }, new PeerListenerAdapter(), 10 * 1000);
    }

}