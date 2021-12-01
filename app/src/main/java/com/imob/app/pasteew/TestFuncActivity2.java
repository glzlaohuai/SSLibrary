package com.imob.app.pasteew;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersHandler;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.wrap.PreferenceManagerWrapper;
import com.imob.lib.sslib.peer.Peer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestFuncActivity2 extends AppCompatActivity {

    private ListView knowNameListView;

    private List<Peer> peerList = new ArrayList<>();

    private BaseAdapter knowAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return peerList.size();
        }

        @Override
        public Object getItem(int position) {
            return peerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView infoView = new TextView(TestFuncActivity2.this);
            infoView.setText(((Peer) getItem(position)).toString());
            return infoView;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_func_2);

        knowNameListView = findViewById(R.id.knownNameListView);

        setup();
    }


    private void setup() {
        knowNameListView.setAdapter(knowAdapter);

        knowNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Peer peer = ConnectedPeersManager.getCurrentlyUsedConnectedPeerHandler().getDetailedInfoPeers().get(position);
                peer.sendMessage(MsgCreator.createNormalStringMsg("hello, from " + PreferenceManagerWrapper.getInstance().getDeviceName()));
            }
        });


        ConnectedPeersHandler.monitorConnectedPeersEvents(new ConnectedPeerEventListenerAdapter() {

            private void notifyAdapter() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        peerList.clear();
                        peerList.addAll(ConnectedPeersManager.getCurrentlyUsedConnectedPeerHandler().getDetailedInfoPeers());
                        knowAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onIncomingPeer(ConnectedPeersHandler handler, Peer peer) {
                notifyAdapter();
            }

            @Override
            public void onPeerDropped(ConnectedPeersHandler handler, Peer peer) {
                notifyAdapter();

            }

            @Override
            public void onPeerDetailedInfoGot(ConnectedPeersHandler handler, Peer peer) {
                notifyAdapter();
            }
        });
    }

    private static final String TAG = TestFuncActivity2.class.getName();

    public void testIt(View view) {
    }

}
