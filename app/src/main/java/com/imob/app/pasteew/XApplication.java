package com.imob.app.pasteew;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersHandler;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.badzzz.pasteany.lib.core.android.AndroidPlatformManager;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class XApplication extends Application {

    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        AndroidPlatformManager androidPlatformManager = new AndroidPlatformManager(this);
        PreferenceManagerWrapper.getInstance().saveDeviceName(Build.BRAND + "#" + Build.DEVICE.toString());
        PreferenceManagerWrapper.getInstance().saveServiceName("a_test_service_name");
        androidPlatformManager.initPlatform();

        ConnectedPeersHandler.setEventListener(new ConnectedPeersHandler.ConnectedPeerEventListener() {
            @Override
            public void onIncomingPeer(ConnectedPeersHandler handler, Peer peer) {
            }

            @Override
            public void onPeerDropped(ConnectedPeersHandler handler, Peer peer) {

            }

            @Override
            public void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer) {

            }

            @Override
            public void onFileChunkSaved(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize, File file) {

            }

            @Override
            public void onFileChunkSaveFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, int soFar, int chunkSize) {

            }

            @Override
            public void onFileMergeFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {

            }

            @Override
            public void onFileMerged(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, File finalFile) {

            }

            @Override
            public void onIncomingStringMsg(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID, String msg) {

            }

            @Override
            public void onIncomingMsgReadFailed(ConnectedPeersHandler handler, Peer peer, String deviceID, String msgID) {

            }
        });
    }
}
