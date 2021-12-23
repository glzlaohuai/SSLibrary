package com.imob.app.pasteew;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.manager.MsgEntitiesManager;
import com.badzzz.pasteany.core.manager.TotalEverConnectedDeviceInfoManager;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListener;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.imob.lib.sslib.peer.Peer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;

public class TestService extends Service {
    public static final int NOTIFYCATION_REQUEST_CODE = 0x01;
    public static final int ONGOING_NOTIFICATION_ID = 0x2;
    public static final int NEW_MSG_NOTIFICATION_ID = 0x3;
    private static final String CHANNEL_PASTE_ANY = "paste_any";

    private ExecutorService singleThreadExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        afterStartCommand();
        return super.onStartCommand(intent, flags, startId);
    }

    private String getConnectedPeersInfo() {
        Set<String> tagSet = new HashSet<>(ConnectedPeersManager.getConnectedPeersTagSet());
        if (tagSet.size() == 0) {
            return "无连接";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String tag : tagSet) {
                sb.append(PeerUtils.getDeviceNameFromPeerTag(tag));
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    private Notification buildConnectedNotification() {
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_PASTE_ANY);
        } else {
            builder = new Notification.Builder(this);
        }

        Intent notificationIntent = new Intent(this, TestFuncActivity3.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFYCATION_REQUEST_CODE, notificationIntent, 0);

        return builder.setContentTitle(getText(R.string.app_name)).setContentText(getConnectedPeersInfo()).setSmallIcon(R.mipmap.ic_launcher).setPriority(Notification.PRIORITY_HIGH).setContentIntent(pendingIntent).build();
    }


    private Notification buildMsgNotification(MsgEntity msgEntity) {
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_PASTE_ANY);
        } else {
            builder = new Notification.Builder(this);
        }

        Intent notificationIntent = new Intent(this, TestFuncActivity3.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFYCATION_REQUEST_CODE, notificationIntent, 0);

        String content = "from: " + TotalEverConnectedDeviceInfoManager.getDeviceNameById(msgEntity.getFromDeviceID()) + "\n" + "data: " + msgEntity.getExtra() + "\n";

        StringBuilder sb = new StringBuilder();
        Map<String, String> msgSendStates = msgEntity.getMsgSendStates();
        for (Map.Entry<String, String> entry : msgSendStates.entrySet()) {
            sb.append(TotalEverConnectedDeviceInfoManager.getDeviceNameById(entry.getKey()) + ", " + Constants.DB.toReadableSendState(entry.getValue()));
            if (entry.getValue().equals(Constants.DB.MSG_SEND_STATE_IN_SENDING)) {
                sb.append("%" + msgEntity.getProgressByDeviceID(entry.getKey()));
            }
            sb.append("\n");
        }
        content = content + sb.toString();

        return builder.setContentTitle(msgEntity.getMsgID()).setContentText(content).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_HIGH).build();
    }

    private void afterStartCommand() {
        singleThreadExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Notification notification = buildConnectedNotification();
                startForeground(ONGOING_NOTIFICATION_ID, notification);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        afterStartCommand();
        setupMonitors();
    }

    private ConnectedPeerEventListener connectedPeerEventListener = new ConnectedPeerEventListenerAdapter() {
        @Override
        public void onIncomingPeer(Peer peer) {
            super.onIncomingPeer(peer);

            updateConnectedPeersNotification();
        }

        @Override
        public void onPeerLost(Peer peer) {
            super.onPeerLost(peer);

            updateConnectedPeersNotification();
        }
    };


    MsgEntitiesManager.IMsgEntityListUpdateListener updateListener = new MsgEntitiesManager.IMsgEntityListUpdateListener() {
        @Override
        public void onGotNewMsgEntities(List<MsgEntity> msgEntityList) {

        }

        @Override
        public void onMsgEntitySendStateUpdated(MsgEntity msgEntity) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(msgEntity.getMsgID(), NEW_MSG_NOTIFICATION_ID, buildMsgNotification(msgEntity));
        }

        @Override
        public void onNewMsgEntitySendedOrReceived(MsgEntity msgEntity) {

        }
    };


    private void updateConnectedPeersNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ONGOING_NOTIFICATION_ID, buildConnectedNotification());
    }

    private void setupMonitors() {
        ConnectedPeersManager.monitorConnectedPeersEvent(connectedPeerEventListener);
        MsgEntitiesManager.monitorMsgEntitiesUpdate(updateListener);
    }

    private void undoMonitors() {
        ConnectedPeersManager.unmonitorConnectedPeersEvent(connectedPeerEventListener);
        MsgEntitiesManager.unmonitorMsgEntitiesUpdate(updateListener);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_PASTE_ANY;
            String description = CHANNEL_PASTE_ANY;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_PASTE_ANY, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        undoMonitors();
    }
}
