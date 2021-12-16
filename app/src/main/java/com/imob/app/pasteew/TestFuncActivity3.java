package com.imob.app.pasteew;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.manager.TotalEverConnectedDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListener;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.sslib.msg.StringMsg;
import com.imob.lib.sslib.peer.Peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestFuncActivity3 extends AppCompatActivity {

    private TextView connectedPeersView;
    private ListView msgListView;

    private boolean isLoading = false;

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

    private Comparator<MsgEntity> comparator = new Comparator<MsgEntity>() {
        @Override
        public int compare(MsgEntity o1, MsgEntity o2) {
            return o1.getAutoID() - o2.getAutoID();
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

            sb.append("time: " + new Date(msgEntity.getMsgTime()).toString());


            Map<String, IDeviceInfoManager.DeviceInfo> totalKnownDevices = TotalEverConnectedDeviceInfoManager.getTotalKnownDevices();


            msgView.setText(sb.toString());
            msgFromView.setText("fromDeviceID: " + msgEntity.getFromDeviceID() + "\n" + "fromDeviceName: " + TotalEverConnectedDeviceInfoManager.getDeviceNameById(msgEntity.getFromDeviceID()));

            Map<String, String> msgSendStates = msgEntity.getMsgSendStates();
            sendingStateLayout.removeAllViews();
            for (String toID : msgSendStates.keySet()) {
                TextView textView = new TextView(TestFuncActivity3.this);
                textView.setText(TotalEverConnectedDeviceInfoManager.getDeviceNameById(toID) + ", " + Constants.DB.toReadableSendState(msgSendStates.get(toID)));
                textView.setPadding(15, 15, 15, 15);
                textView.setBackgroundColor(Color.BLUE);
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
                loadNextBatchMsg();
            }
        });


        ConnectedPeersManager.monitorConnectedPeersEvent(connectedPeerEventListener);
        TotalEverConnectedDeviceInfoManager.monitorTotalEverConnectedDeviceListUpdate(deviceInfoListener);
        updateConnectedPeersInfoView();
        queryAllInSendingMsgsAndMarkThemAsFailed();
    }


    private void loadNextBatchMsg() {
        if (!isLoading) {
            isLoading = true;

            int maxID = msgEntities.size() > 0 ? msgEntities.get(0).getAutoID() : Integer.MAX_VALUE;
            DBManagerWrapper.getInstance().queryAllMsgs(maxID, 2, new DBManagerWrapper.IDBActionFinishListener() {
                @Override
                public void onFinished() {
                    msgEntities.addAll(MsgEntity.buildWithDBQueryList(getResultList()));
                    Collections.sort(msgEntities, comparator);

                    notifyMsgAdapter();
                }
            });
        }
    }


    private void notifyMsgAdapter() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            msgAdapter.notifyDataSetChanged();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    private void queryAllInSendingMsgsAndMarkThemAsFailed() {
        isLoading = true;
        DBManagerWrapper.getInstance().queryAllSendingMsgsAndMarkThemAsFailed(new DBManagerWrapper.IDBActionFinishListener() {
            @Override
            public void onFinished() {
                DBManagerWrapper.getInstance().queryAllMsgs(Integer.MAX_VALUE, 2, new DBManagerWrapper.IDBActionFinishListener() {
                    @Override
                    public void onFinished() {
                        msgEntities.addAll(MsgEntity.buildWithDBQueryList(getResultList()));
                        Collections.sort(msgEntities, comparator);
                        notifyMsgAdapter();

                        isLoading = false;
                    }
                });
            }
        });
    }


    private static String[] peerTagSetToDeviceIdArray(Set<String> tagSet) {
        try {
            String[] tags = tagSet.toArray(new String[0]);
            for (int i = 0; i < tags.length; i++) {
                tags[i] = PeerUtils.getDeviceIDFromPeerTag(tags[i]);
            }
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void sendTestMsgsToAllConnectedPeers() {
        //in canse if concurrent exception occure
        Set<String> tagSet = new HashSet<>(ConnectedPeersManager.getConnectedPeersTagSet());

        String msgID = UUID.randomUUID().toString();
        String msgContent = "hello world, " + UUID.randomUUID().toString();
        StringMsg stringMsg = MsgCreator.createNormalStringMsg(msgID, msgContent);

        String fromDeviceID = PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID();

        MsgEntity msgEntity = MsgEntity.buildMsgEntity(msgID, Constants.PeerMsgType.TYPE_STR, msgContent, fromDeviceID, stringMsg.getAvailable(), peerTagSetToDeviceIdArray(tagSet));

        if (msgEntity.isValid()) {
            doSendMsgEntity(msgEntity, tagSet);
        } else {
            Toast.makeText(this, "not valid, maybe has no peers connected now.", Toast.LENGTH_SHORT).show();
        }
    }

    private void doSendMsgEntity(MsgEntity msgEntity, Set<String> tagSet) {
        msgEntities.add(msgEntity);

        msgEntity.insertIntoMsgSendingTable(new DBManagerWrapper.IDBActionFinishListener() {
            @Override
            public void onFinished() {

            }
        });

        msgEntity.insertIntoMsgTable(new DBManagerWrapper.IDBActionFinishListener() {
            @Override
            public void onFinished() {

            }
        });
        msgAdapter.notifyDataSetChanged();

        for (String tag : tagSet) {
            Peer peer = ConnectedPeersManager.getConnectedPeerByTag(tag);
            if (peer == null) {
                //send failed
            } else {
                peer.sendMessage(MsgCreator.createNormalStringMsg(msgEntity.getMsgID(), msgEntity.getMsgData()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectedPeersManager.unmonitorConnectedPeersEvent(connectedPeerEventListener);
        TotalEverConnectedDeviceInfoManager.unmonitorTotalEventConnectedDeviceListUpdate(deviceInfoListener);
    }
}
