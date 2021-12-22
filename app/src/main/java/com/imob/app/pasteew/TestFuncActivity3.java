package com.imob.app.pasteew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.manager.MsgEntitiesManager;
import com.badzzz.pasteany.core.manager.TotalEverConnectedDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListener;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Closer;
import com.imob.lib.sslib.peer.Peer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestFuncActivity3 extends AppCompatActivity {

    private static final int SELECT_FILE_REQUEST_CODE = 0xff;

    private TextView connectedPeersView;
    private ListView msgListView;

    private List<MsgEntity> msgEntities = new ArrayList<>(MsgEntitiesManager.getAllMsgEntities());


    private ConnectedPeerEventListener connectedPeerEventListener = new ConnectedPeerEventListenerAdapter() {
        @Override
        public void onIncomingPeer(Peer peer) {
            super.onIncomingPeer(peer);

            updateConnectedPeersInfoView();
        }

        @Override
        public void onPeerLost(Peer peer) {
            super.onPeerLost(peer);

            updateConnectedPeersInfoView();
        }
    };

    private TotalEverConnectedDeviceInfoManager.ITotalEverConnectedDeviceInfoListener deviceInfoListener = new TotalEverConnectedDeviceInfoManager.ITotalEverConnectedDeviceInfoListener() {
        @Override
        public void onUpdated(Map<String, IDeviceInfoManager.DeviceInfo> all) {

            notifyMsgAdapter();
        }
    };


    private MsgEntitiesManager.IMsgEntityBatchLoadListener batchLoadListener = new MsgEntitiesManager.IMsgEntityBatchLoadListener() {
        @Override
        public void onFinished() {
            msgEntities.clear();
            msgEntities.addAll(MsgEntitiesManager.getAllMsgEntities());

            notifyMsgAdapter();
        }
    };


    private MsgEntitiesManager.IMsgEntityListUpdateListener msgEntityListUpdateListener = new MsgEntitiesManager.IMsgEntityListUpdateListener() {
        @Override
        public void onGotNewMsgEntities(List<MsgEntity> msgEntityList) {
            updateMsgEntitiesAndNotifyAdapter();
        }

        @Override
        public void onMsgEntitySendStateUpdated(MsgEntity msgEntity) {
            notifyMsgAdapter();
        }
    };

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

            sb.append("autoID: " + msgEntity.getAutoID());
            sb.append("\n");
            sb.append("msgID: " + msgEntity.getMsgID());
            sb.append("\n");
            sb.append("msgType: " + msgEntity.getMsgType());
            sb.append("\n");
            sb.append("msgData: " + msgEntity.getMsgData());
            sb.append("\n");

            sb.append("time: " + new Date(msgEntity.getMsgTime()).toString());

            msgView.setText(sb.toString());
            msgFromView.setText("fromDeviceID: " + msgEntity.getFromDeviceID() + "\n" + "fromDeviceName: " + TotalEverConnectedDeviceInfoManager.getDeviceNameById(msgEntity.getFromDeviceID()));

            Map<String, String> msgSendStates = msgEntity.getMsgSendStates();
            sendingStateLayout.removeAllViews();
            for (String toID : msgSendStates.keySet()) {
                TextView textView = new TextView(TestFuncActivity3.this);
                textView.setText(TotalEverConnectedDeviceInfoManager.getDeviceNameById(toID) + ", " + Constants.DB.toReadableSendState(msgSendStates.get(toID)));
                textView.setPadding(15, 15, 15, 15);

                if (msgSendStates.get(toID).equals(Constants.DB.MSG_SEND_STATE_FAILED)) {
                    textView.setBackgroundColor(Color.RED);
                } else if (msgSendStates.get(toID).equals(Constants.DB.MSG_SEND_STATE_SUCCEEDED)) {
                    textView.setBackgroundColor(Color.BLUE);
                } else {
                    //in sending state
                    textView.setBackgroundColor(Color.DKGRAY);
                    textView.append("\n" + msgEntity.getProgressByDeviceID(toID) + "%");
                }
                textView.setTextColor(Color.WHITE);
                sendingStateLayout.addView(textView);


            }
            return view;
        }
    };


    private void updateConnectedPeersInfoView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedPeersView.setText(ConnectedPeersManager.getConnectedPeersTagSet().toString());
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_func_3);

        connectedPeersView = findViewById(R.id.connectedPersView);
        msgListView = findViewById(R.id.listView);
        msgListView.setAdapter(msgAdapter);
        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTestMsgsToAllConnectedPeers();
            }
        });

        findViewById(R.id.loadNextBatch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgEntitiesManager.loadNextBatch(batchLoadListener);
            }
        });

        findViewById(R.id.sendFileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);

            }
        });

        ConnectedPeersManager.monitorConnectedPeersEvent(connectedPeerEventListener);
        TotalEverConnectedDeviceInfoManager.monitorTotalEverConnectedDeviceListUpdate(deviceInfoListener);
        MsgEntitiesManager.monitorMsgEntitiesUpdate(msgEntityListUpdateListener);

        updateConnectedPeersInfoView();
        loadMsgBatchOrFillMsgEntitiesList();
    }

    private void loadMsgBatchOrFillMsgEntitiesList() {
        if (!MsgEntitiesManager.hasEverLoaded()) {
            MsgEntitiesManager.loadNextBatch(batchLoadListener);
        } else {
            updateMsgEntitiesAndNotifyAdapter();
        }
    }


    private synchronized void updateMsgEntitiesAndNotifyAdapter() {
        msgEntities.clear();
        msgEntities.addAll(MsgEntitiesManager.getAllMsgEntities());

        notifyMsgAdapter();
    }

    private void notifyMsgAdapter() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            msgAdapter.notifyDataSetChanged();
            msgListView.smoothScrollToPosition(msgEntities.size() - 1, msgEntities.size() - 1);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgAdapter.notifyDataSetChanged();
                    msgListView.smoothScrollToPosition(msgEntities.size() - 1, msgEntities.size() - 1);
                }
            });
        }
    }

    private void sendTestMsgsToAllConnectedPeers() {
        MsgEntitiesManager.sendStringMsgToPeers(UUID.randomUUID().toString(), "this is a test msg, time: " + new Date().toLocaleString(), ConnectedPeersManager.getConnectedPeersTagSet());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectedPeersManager.unmonitorConnectedPeersEvent(connectedPeerEventListener);
        TotalEverConnectedDeviceInfoManager.unmonitorTotalEventConnectedDeviceListUpdate(deviceInfoListener);
        MsgEntitiesManager.unmonitorMsgEntitiesUpdate(msgEntityListUpdateListener);
    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
                sendTestFileMsg(fileUri);
            }
        }
    }

    private void sendTestFileMsg(Uri uri) {
        Set<InputStream> streamSet = new HashSet<>();
        try {
            Set<String> tagSet = new HashSet<>(ConnectedPeersManager.getConnectedPeersTagSet());
            streamSet = new HashSet<>();
            for (String tag : tagSet) {
                streamSet.add(getContentResolver().openInputStream(uri));
            }
            MsgEntitiesManager.sendFileMsgToPeers(UUID.randomUUID().toString(), "a_test_file.apk", streamSet.iterator().next().available(), streamSet, tagSet);
        } catch (Exception e) {
            e.printStackTrace();
            for (InputStream inputStream : streamSet) {
                Closer.close(inputStream);
            }
        }

    }


}
