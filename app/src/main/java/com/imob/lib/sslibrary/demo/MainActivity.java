package com.imob.lib.sslibrary.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.imob.lib.sslib.Client;
import com.imob.lib.sslib.Server;
import com.imob.lib.sslib.send.msg.IMsg;
import com.imob.lib.sslib.send.msg.StringMsg;
import com.imob.lib.sslibrary.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private Server server;
    private Client client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.create_server).setOnClickListener(this);
        findViewById(R.id.stop_server).setOnClickListener(this);
        findViewById(R.id.create_client).setOnClickListener(this);
        findViewById(R.id.stop_client).setOnClickListener(this);
        findViewById(R.id.send_msg_to_clients).setOnClickListener(this);
        findViewById(R.id.send_msg_to_server).setOnClickListener(this);
    }


    private void createClient() {
        if (client == null) {
            EditText ipPortView = new EditText(MainActivity.this);
            new AlertDialog.Builder(MainActivity.this).setView(ipPortView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String input = ipPortView.getText().toString().trim();

                    String ip = null;
                    int port = 0;

                    try {
                        ip = input.split(":")[0];
                        port = Integer.parseInt(input.split(":")[1]);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        try {
                            ip = "127.0.0.1";
                            port = Integer.parseInt(input);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }


                    client = new Client(ip, port, new Client.OnClientStateListener() {

                        private static final String TAG = MainActivity.TAG + " - Client";

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
            }).show();
        }
    }


    private void stopClient() {
        if (client != null) {
            client.stop();
        }
    }


    private void sendMessageToServer(IMsg msg) {
        if (client != null) {
            client.sendMessage(msg);
        }

    }


    private void sendMessageToClients(IMsg msg) {
        if (server != null) {
            server.broadcast(msg);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_server:
                createServer();
                break;
            case R.id.stop_server:
                stopServer();
                break;

            case R.id.create_client:
                createClient();
                break;

            case R.id.stop_client:
                stopClient();
                break;

            case R.id.send_msg_to_clients:
                sendMessageToClients(new StringMsg("hello,world, msg from server"));
                break;
            case R.id.send_msg_to_server:
                sendMessageToServer(new StringMsg("hello, world, msg from client"));
                break;


        }
    }

    private void stopServer() {
        if (server != null) {
            boolean stopped = server.stop();
            if (stopped) {
                server = null;
            }
        }
    }


    private void createServer() {
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
