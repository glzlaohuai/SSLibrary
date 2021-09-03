package com.imob.demo.desktop;


import com.imob.lib.sslib.Client;
import com.imob.lib.sslib.Server;
import com.imob.lib.sslib.send.msg.IMsg;
import com.imob.lib.sslib.send.msg.StringMsg;

import java.util.Scanner;

public class Main {
    private static final String TAG = "Main";

    private static Server server;
    private static Client client;

    private static final int msg_create_server = 0;
    private static final int msg_stop_server = 1;
    private static final int msg_create_client = 2;
    private static final int msg_stop_client = 3;
    private static final int msg_send_msg_to_clients = 4;
    private static final int msg_send_msg_to_server = 5;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("operation：\ncreate server: " + msg_create_server + " \nstop server: " + msg_stop_server + "\ncreate client: " + msg_create_client + "\nstop client: " + msg_stop_client + "\nsend msg to clients: " + msg_send_msg_to_clients + "\nsend msg to server: " + msg_send_msg_to_server);
        while (scanner.hasNext()) {
            int operation = Integer.parseInt(scanner.nextLine().trim());
            switch (operation) {
                case msg_create_server:
                    startServer();
                    break;
                case msg_create_client:
                    createClient();
                    break;
                case msg_stop_client:
                    stopClient();
                    break;
                case msg_stop_server:
                    stopServer();
                    break;
                case msg_send_msg_to_clients:
                    sendMessageToClients(new StringMsg("hello,world, msg from server"));
                    break;
                case msg_send_msg_to_server:
                    sendMessageToServer(new StringMsg("hello,world, msg from client"));
                    break;
            }
            System.out.println("operation：\ncreate server: " + msg_create_server + " \nstop server: " + msg_stop_server + "\ncreate client: " + msg_create_client + "\nstop client: " + msg_stop_client + "\nsend msg to clients: " + msg_send_msg_to_clients + "\nsend msg to server: " + msg_send_msg_to_server);
        }
    }


    private static void startServer() {
        if (server != null) {
            server = new Server(new Server.OnServerStateListener() {
                @Override
                public void onStarted(String ip, int port) {
                    System.out.println("onStarted: " + ip + ", " + port);
                }

                @Override
                public void onStartFailed(String msg, Exception e) {
                    System.out.println("onStartFailed: " + msg + ", " + e);
                }

                @Override
                public void onStopped() {
                    System.out.println("onStopped");
                }

                @Override
                public void onAlreadyStopped() {
                    System.out.println("onAlreadyStopped");
                }

                @Override
                public void onAlreadyStarted() {
                    System.out.println("onAlreadyStarted");

                }

                @Override
                public void onMonitorIncoming() {
                    System.out.println("onMonitorIncoming");

                }

                @Override
                public void onServerCorrupted(String msg, Exception e) {
                    System.out.println("onServerCorrupted: " + msg + ", " + e);

                }
            });
            server.start();
        }
    }


    private static void createClient() {
        if (client == null) {

            Scanner scanner = new Scanner(System.in);

            String ip = null;
            int port = 0;

            System.out.println("ip地址:");
            if (scanner.hasNextLine()) {
                ip = scanner.nextLine().trim();
            }

            System.out.println("port:");
            if (scanner.hasNextLine()) {
                port = Integer.parseInt(scanner.nextLine().trim());
            }


            client = new Client(ip, port, new Client.OnClientStateListener() {

                private static final String TAG = Main.TAG + " - Client";

                @Override
                public void onConnectFailed(String msg, Exception e) {
                    Log.i(TAG, "onConnectFailed: " + msg + ", " + e);
                    doStuffAfterConnectFailedOrStopped();
                }

                @Override
                public void onAlreadyConnected() {
                    Log.i(TAG, "onAlreadyConnected: ");
                }

                @Override
                public void onConnected() {
                    Log.i(TAG, "onConnected: ");
                }

                @Override
                public void onConnectCorrupted(String error, Exception e) {
                    Log.i(TAG, "onConnectCoruppted: " + error + ", " + e);
                    doStuffAfterConnectFailedOrStopped();
                }

                @Override
                public void onConnectDestroyed() {
                    Log.i(TAG, "onConnectDestroyed: ");
                    doStuffAfterConnectFailedOrStopped();
                }

                @Override
                public void onAlreadyStopped() {
                    Log.i(TAG, "onAlreadyStopped: ");
                }

                @Override
                public void onStop() {
                    Log.i(TAG, "onStop: ");

                    doStuffAfterConnectFailedOrStopped();
                }

                private void doStuffAfterConnectFailedOrStopped() {
                    client = null;
                }
            });
            client.connect();

        }
    }


    private static void stopClient() {
        if (client != null) {
            client.stop();
        }
    }


    private static void sendMessageToServer(IMsg msg) {
        if (client != null) {
            client.sendMessage(msg);
        }

    }


    private static void sendMessageToClients(IMsg msg) {
        if (server != null) {
            server.broadcast(msg);
        }
    }

    private static void stopServer() {
        if (server != null) {
            boolean stopped = server.stop();
            if (stopped) {
                server = null;
            }
        }
    }


    private static void createServer() {
        if (server == null) {
            server = new Server(new Server.OnServerStateListener() {
                @Override
                public void onStarted(String ip, int port) {
                    Log.i(TAG, "onStarted: " + ip + ", " + port);
                }

                @Override
                public void onStartFailed(String msg, Exception e) {
                    Log.i(TAG, "onStartFailed: " + msg + ", exception: " + e);
                    stuffServerFailedOrStopped();
                }

                @Override
                public void onStopped() {
                    Log.i(TAG, "onStopped: ");
                    stuffServerFailedOrStopped();
                }

                @Override
                public void onAlreadyStopped() {
                    Log.i(TAG, "onAlreadyStopped: ");
                }

                @Override
                public void onAlreadyStarted() {
                    Log.i(TAG, "onAlreadyStarted: ");
                }

                @Override
                public void onMonitorIncoming() {
                    Log.i(TAG, "onMonitorIncoming: ");
                }

                @Override
                public void onServerCorrupted(String msg, Exception e) {
                    Log.i(TAG, "onServerCorrupted: " + msg + ", " + e);
                    stuffServerFailedOrStopped();
                }


                private void stuffServerFailedOrStopped() {
                    server = null;
                }
            });
            if (!server.start()) {
                server = null;
            }
        }
    }


}