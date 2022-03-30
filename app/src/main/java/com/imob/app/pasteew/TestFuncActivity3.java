package com.imob.app.pasteew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.interfaces.IDeviceInfoManager;
import com.badzzz.pasteany.core.manager.MsgEntitiesManager;
import com.badzzz.pasteany.core.manager.TotalEverDiscoveredDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.NsdServiceStarter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListener;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.badzzz.pasteany.core.wrap.SettingsManager;
import com.imob.app.pasteew.utils.FileUtils;
import com.imob.lib.lib_common.Closer;
import com.imob.lib.net.nsd.NsdEventListener;
import com.imob.lib.net.nsd.NsdEventListenerAdapter;
import com.imob.lib.net.nsd.NsdNode;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerListenerAdapter;
import com.imob.lib.sslib.server.ServerNode;

import java.io.InputStream;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestFuncActivity3 extends AppCompatActivity {

    private static final int SELECT_FILE_REQUEST_CODE = 0xff;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private LinearLayout connectedDevicesLayout;
    private ListView msgListView;
    private TextView serverInfoView;
    private TextView nsdInfoView;

    private List<MsgEntity> msgEntities = new ArrayList<>(MsgEntitiesManager.getAllMsgEntities());

    private List<IDeviceInfoManager.DeviceInfo> allEverDiscoveredDevices = new LinkedList<>();
    private ConnectedPeerEventListener connectedPeerEventListener = new ConnectedPeerEventListenerAdapter() {
        @Override
        public void onIncomingPeer(Peer peer) {
            super.onIncomingPeer(peer);

            updateConnectedDeviceListView();
        }

        @Override
        public void onPeerLost(Peer peer) {
            super.onPeerLost(peer);

            updateConnectedDeviceListView();

        }
    };
    private ServerListener serverListener = new ServerListenerAdapter() {
        @Override
        public void onCreated(ServerNode serverNode) {
            super.onCreated(serverNode);
            updateServerNodeInfo(serverNode);
        }

        @Override
        public void onDestroyed(ServerNode serverNode, String reason, Exception e) {
            super.onDestroyed(serverNode, reason, e);
            updateServerNodeInfo(serverNode);
        }
    };
    private NsdEventListener nsdEventListener = new NsdEventListenerAdapter() {


        @Override
        public void onDestroyed(NsdNode nsdNode, String reason, Exception e) {
            super.onDestroyed(nsdNode, reason, e);
            updateNsdRegisterInfo(nsdNode, null, -1);
        }

        @Override
        public void onSuccessfullyRegisterService(NsdNode nsdNode, String type, String name, String text, int port) {
            super.onSuccessfullyRegisterService(nsdNode, type, name, text, port);
            updateNsdRegisterInfo(nsdNode, text, port);
        }
    };

    private void updateNsdRegisterInfo(NsdNode nsdNode, String text, int port) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (nsdNode == null || nsdNode.isDestroyed()) {
                    nsdInfoView.setText("none");
                } else {
                    StringBuilder sb = new StringBuilder("text: ");
                    sb.append(text);
                    sb.append("\nport: ");
                    sb.append(port);
                    nsdInfoView.setText(sb.toString());
                }
            }
        });
    }

    private void updateServerNodeInfo(ServerNode serverNode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (serverNode == null || serverNode.isDestroyed()) {
                    serverInfoView.setText("none");
                } else {
                    ServerSocket serverSocket = serverNode.getServerSocket();
                    serverInfoView.setText(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
                }
            }
        });
    }

    private void updateConnectedDeviceListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LayoutInflater inflater = LayoutInflater.from(TestFuncActivity3.this);
                connectedDevicesLayout.removeAllViews();
                Set<String> connectedPeersTagSet = new HashSet<>(ConnectedPeersManager.getConnectedPeersTagSet());
                Map<String, String> id_tagMap = PeerUtils.generateDeviceIDMapByTagSet(connectedPeersTagSet);

                List<IDeviceInfoManager.DeviceInfo> allEverDiscoveredDevices = Collections.unmodifiableList(TestFuncActivity3.this.allEverDiscoveredDevices);

                for (IDeviceInfoManager.DeviceInfo deviceInfo : allEverDiscoveredDevices) {
                    View deviceInfoView;
                    boolean connected = false;

                    //connected
                    if (id_tagMap.keySet().contains(deviceInfo.getId())) {
                        deviceInfoView = inflater.inflate(R.layout.item_device_connected, null);
                        deviceInfoView.setOnClickListener(null);
                        connected = true;
                    } else {
                        //not connected
                        deviceInfoView = inflater.inflate(R.layout.item_device_notconnected, null);
                        deviceInfoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gotoTryToConnectActivity(deviceInfo);
                            }
                        });
                    }

                    TextView idView = deviceInfoView.findViewById(R.id.id);
                    TextView nameView = deviceInfoView.findViewById(R.id.name);
                    TextView platformView = deviceInfoView.findViewById(R.id.platform);
                    TextView timeView = deviceInfoView.findViewById(R.id.connectTime);

                    idView.setText(deviceInfo.getId());
                    nameView.setText(deviceInfo.getName());
                    platformView.setText(deviceInfo.getPlatform());
                    if (connected) {
                        String tag = id_tagMap.get(deviceInfo.getId());
                        Peer peer = ConnectedPeersManager.getConnectedPeerByTag(tag);
                        if (peer != null) {
                            timeView.setText("" + DATE_FORMAT.format(new Date(peer.getConnectionEstablishedTime())));
                        }
                    }
                    connectedDevicesLayout.addView(deviceInfoView);
                }
            }
        });
    }

    private void gotoTryToConnectActivity(IDeviceInfoManager.DeviceInfo deviceInfo) {
        String s = deviceInfo.toJson();
        Intent intent = new Intent(this, PeerTryConnectActivity.class);
        intent.putExtra("device", s);
        startActivity(intent);
    }


    private TotalEverDiscoveredDeviceInfoManager.ITotalEverConnectedDeviceInfoListener deviceInfoListener = new TotalEverDiscoveredDeviceInfoManager.ITotalEverConnectedDeviceInfoListener() {
        @Override
        public void onUpdated(Map<String, IDeviceInfoManager.DeviceInfo> all) {
            afterTotalConnectedDevicesListUpdated(all);
        }
    };


    private synchronized void afterTotalConnectedDevicesListUpdated(Map<String, IDeviceInfoManager.DeviceInfo> all) {
        TotalEverDiscoveredDeviceInfoManager.removeSelfDevice(all);
        //diff checked
        if (!allEverDiscoveredDevices.equals(all.values())) {
            allEverDiscoveredDevices.clear();
            allEverDiscoveredDevices.addAll(all.values());
            Collections.sort(allEverDiscoveredDevices);

            notifyMsgAdapter();
            updateConnectedDeviceListView();
        }
    }


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

        @Override
        public void onNewMsgEntitySendedOrReceived(MsgEntity msgEntity) {

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
            sb.append("msgExtra: " + msgEntity.getExtra());
            sb.append("\n");

            sb.append("time: " + new Date(msgEntity.getMsgTime()).toString());

            msgView.setText(sb.toString());
            msgFromView.setText("fromDeviceID: " + msgEntity.getFromDeviceID() + "\n" + "fromDeviceName: " + TotalEverDiscoveredDeviceInfoManager.getDeviceNameById(msgEntity.getFromDeviceID()));

            Map<String, String> msgSendStates = msgEntity.getMsgSendStates();
            sendingStateLayout.removeAllViews();
            for (String toID : msgSendStates.keySet()) {
                TextView textView = new TextView(TestFuncActivity3.this);
                textView.setText(TotalEverDiscoveredDeviceInfoManager.getDeviceNameById(toID) + ", " + toReadableSendState(toID, msgSendStates.get(toID)));
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

    private static String toReadableSendState(String toID, String state) {
        switch (state) {
            case Constants.DB.MSG_SEND_STATE_IN_SENDING:
                if (toID.equals(PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID())) {
                    return "receiving...";
                } else {
                    return "sending...";
                }
            case Constants.DB.MSG_SEND_STATE_FAILED:
                return "failed";
            case Constants.DB.MSG_SEND_STATE_SUCCEEDED:
                return "succeeded";
        }
        return "unknown";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_func_3);

        serverInfoView = findViewById(R.id.server_info);
        nsdInfoView = findViewById(R.id.nsd_info);
        connectedDevicesLayout = findViewById(R.id.connectedDevicesLayout);
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
                if (!hasConnectedPeers()) {
                    Toast.makeText(TestFuncActivity3.this, "no connected peers", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
            }
        });

        ((CheckBox) findViewById(R.id.pingCheckbox)).setChecked(SettingsManager.getInstance().isPingCheckEnabled());
        ((CheckBox) findViewById(R.id.pingCheckbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ConnectedPeersManager.enablePingCheck();
                } else {
                    ConnectedPeersManager.disablePingCheck();
                }
            }
        });

        ((CheckBox) findViewById(R.id.useLastKnownNsdInfo)).setChecked(SettingsManager.getInstance().useLastKnownNsdInfo());
        ((CheckBox) findViewById(R.id.useLastKnownNsdInfo)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsManager.getInstance().setUseLastKnownNsdInfo(isChecked);
            }
        });

        ConnectedPeersManager.monitorConnectedPeersEvent(connectedPeerEventListener);
        TotalEverDiscoveredDeviceInfoManager.monitorTotalEverConnectedDeviceListUpdate(deviceInfoListener);
        MsgEntitiesManager.monitorMsgEntitiesUpdate(msgEntityListUpdateListener);

        loadMsgBatchOrFillMsgEntitiesList();
        afterTotalConnectedDevicesListUpdated(TotalEverDiscoveredDeviceInfoManager.getTotalKnownDevices());

        monitorServerNodeState();
        monitorNsdNodeState();

        updateServerNodeInfo(ServerNode.getActiveServerNode());
        updateNsdRegisterInfo(NsdNode.getActiveNsdNode(), NsdNode.getActiveRegisteredServiceText(), NsdNode.getActiveRegisterPort());
    }

    private void monitorServerNodeState() {
        ServerNode.monitorServerNodeState(serverListener);
    }

    private void unmonitorServerNodeState() {
        ServerNode.unmonitorServerNodeState(serverListener);
    }

    private void monitorNsdNodeState() {
        NsdNode.monitorListener(nsdEventListener);
    }

    private void unmonitorNsdNodeState() {
        NsdNode.unmonitorListener(nsdEventListener);
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
        if (!hasConnectedPeers()) {
            Toast.makeText(this, "No connected peers", Toast.LENGTH_SHORT).show();
        } else {
            MsgEntitiesManager.sendStringMsgToPeers(UUID.randomUUID().toString(), "this is a test msg, time: " + new Date().toLocaleString(), ConnectedPeersManager.getConnectedPeersTagSet());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectedPeersManager.unmonitorConnectedPeersEvent(connectedPeerEventListener);
        TotalEverDiscoveredDeviceInfoManager.unmonitorTotalEventConnectedDeviceListUpdate(deviceInfoListener);
        MsgEntitiesManager.unmonitorMsgEntitiesUpdate(msgEntityListUpdateListener);
        unmonitorServerNodeState();
        unmonitorNsdNodeState();
    }

    private boolean hasConnectedPeers() {
        return ConnectedPeersManager.getConnectedPeersTagSet().size() > 0;
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
        FileUtils.FileInfo fileInfo = FileUtils.retrieveFileInfoFromContentUri(getApplicationContext(), uri);

        if (fileInfo == null || !fileInfo.isValid()) return;

        if (!hasConnectedPeers()) {
            Toast.makeText(this, "has no connected peers", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Set<String> tagSet = new HashSet<>(ConnectedPeersManager.getConnectedPeersTagSet());
            for (String tag : tagSet) {
                streamSet.add(getContentResolver().openInputStream(uri));
            }
            MsgEntitiesManager.sendFileMsgToPeers(UUID.randomUUID().toString(), uri.toString(), fileInfo.getName(), fileInfo.getSize(), streamSet, tagSet);
        } catch (Exception e) {
            e.printStackTrace();
            for (InputStream inputStream : streamSet) {
                Closer.close(inputStream);
            }
        }
    }

    public void redo(View view) {
        NsdServiceStarter.redoIfSomethingWentWrong("redo called by user", null);
    }

}
