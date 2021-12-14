package com.imob.app.pasteew;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListener;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.imob.lib.sslib.peer.Peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestFuncActivity3 extends AppCompatActivity {

    private TextView connectedPeersView;
    private ListView msgListView;
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
    private List<MsgEntity> msgEntities = new ArrayList<>();


    private BaseAdapter msgAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return msgEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return msgEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return msgEntities.get(position).getAutoID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_msg, parent, false);

            TextView msgView = view.findViewById(R.id.msgView);
            TextView msgFromView = view.findViewById(R.id.msgFromView);
            LinearLayout sendingStateLayout = view.findViewById(R.id.sendingState);


            MsgEntity msgEntity = msgEntities.get(position);
            StringBuilder sb = new StringBuilder();


            sb.append("msgID: " + msgEntity.getMsgID());
            sb.append("\n");
            sb.append("msgType: " + msgEntity.getMsgType());
            sb.append("\n");
            sb.append("msgData: " + msgEntity.getMsgData());
            sb.append("\n");


            msgView.setText(sb.toString());
            msgFromView.setText(msgEntity.getFromDeviceID());

            List<String> toDeviceIDList = msgEntity.getToDeviceIDList();
            sendingStateLayout.removeAllViews();
            for (int i = 0; i < toDeviceIDList.size(); i++) {
                TextView textView = new TextView(TestFuncActivity3.this);
                textView.setText(toDeviceIDList.get(i) + " # " + msgEntity.getStateList().get(i));
            }
            return view;
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

        connectedPeersView = findViewById(R.id.connectedPersView);
        ConnectedPeersManager.monitorConnectedPeersEvent(connectedPeerEventListener);

        DBManagerWrapper.getInstance().queryAllMsgs(Integer.MAX_VALUE, Constants.DB.DEFAULT_QUERY_LIMIT, new DBManagerWrapper.IDBActionListener() {
            @Override
            public void succeeded(List<Map<String, String>> resultList) {
                List<MsgEntity> msgEntities = MsgEntity.dbQueryListToEntityList(resultList);
                TestFuncActivity3.this.msgEntities.addAll(msgEntities);
                after();
            }

            @Override
            public void failed() {
                after();
            }


            private void after() {
                msgAdapter.notifyDataSetChanged();
            }
        });


        msgListView = findViewById(R.id.listView);
        msgListView.setAdapter(msgAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectedPeersManager.unmonitorConnectedPeersEvent(connectedPeerEventListener);
    }
}