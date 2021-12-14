package com.imob.app.pasteew;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListener;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestFuncActivity3 extends AppCompatActivity {

    private ListView msgListView;
    private File testFile;

    private TextView connectedPeersView;
    private ConnectedPeerEventListener connectedPeerEventListener = new ConnectedPeerEventListenerAdapter() {
        @Override
        public void onIncomingPeer(Peer peer) {
            super.onIncomingPeer(peer);

            updateConnectedPeers();
        }

        @Override
        public void onPeerLost(Peer peer) {
            super.onPeerLost(peer);

            updateConnectedPeers();
        }
    };

    public void updateConnectedPeers() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedPeersView.setText(ConnectedPeersManager.getAllConnectedPeersTagSet().toString());
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_func_3);

        msgListView = findViewById(R.id.listView);
        connectedPeersView = findViewById(R.id.connectedPersView);

        ConnectedPeersManager.monitorConnectedPeersEvent(connectedPeerEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectedPeersManager.unmonitorConnectedPeersEvent(connectedPeerEventListener);
    }
}
