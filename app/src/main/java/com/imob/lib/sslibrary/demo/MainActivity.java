package com.imob.lib.sslibrary.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.imob.lib.sslib.Server;
import com.imob.lib.sslibrary.R;

import androidx.annotation.Nullable;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private Server server;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.create_server).setOnClickListener(this);
        findViewById(R.id.stop_server).setOnClickListener(this);
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
