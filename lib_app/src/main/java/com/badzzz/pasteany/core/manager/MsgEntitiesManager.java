package com.badzzz.pasteany.core.manager;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.Constants;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MsgEntitiesManager {
    private static final String TAG = "MsgEntitiesManager";

    private static boolean inited = false;
    private static boolean everLoaded = false;
    private static boolean isBatchLoading = false;

    private static final String selfDeviceID = PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID();

    private static LinkedList<MsgEntity> msgEntities = new LinkedList<>();
    private static Map<String, MsgEntity> processingMsgEntities = new HashMap<>();

    private static IMsgEntityListUpdateListenerGroup msgEntitiesUpdateMonitorListenerGroup = new IMsgEntityListUpdateListenerGroup();
    private static IMsgEntityListUpdateListener globalMsgEntitiesUpdateListener;

    private static Set<IMsgEntityBatchLoadListener> listenerSet = new HashSet<>();
    private static IMsgEntityBatchLoadListener noActionListener = new IMsgEntityBatchLoadListener() {
        @Override
        public void onFinished() {

        }
    };


    private static Comparator<MsgEntity> msgEntityComparator = new Comparator<MsgEntity>() {
        @Override
        public int compare(MsgEntity o1, MsgEntity o2) {
            return o1.getAutoID() - o2.getAutoID();
        }
    };


    private static final int MSG_BATCH_LOAD_SIZE = 10;

    public interface IMsgEntityBatchLoadListener {
        void onFinished();
    }

    public interface IMsgEntityListUpdateListener {
        void onGotNewMsgEntities(List<MsgEntity> msgEntityList);

        void onMsgEntitySendStateUpdated(MsgEntity msgEntity);
    }

    private static class IMsgEntityListUpdateListenerGroup implements IMsgEntityListUpdateListener {
        private Set<IMsgEntityListUpdateListener> set = new HashSet<>();

        public void add(IMsgEntityListUpdateListener listener) {
            if (listener != null) {
                set.add(listener);
            }
        }

        public void remove(IMsgEntityListUpdateListener listener) {
            if (listener != null) {
                set.remove(listener);
            }
        }

        @Override
        public void onGotNewMsgEntities(List<MsgEntity> msgEntityList) {
            for (IMsgEntityListUpdateListener listener : set) {
                listener.onGotNewMsgEntities(msgEntityList);
            }
        }

        @Override
        public void onMsgEntitySendStateUpdated(MsgEntity msgEntity) {
            for (IMsgEntityListUpdateListener listener : set) {
                listener.onMsgEntitySendStateUpdated(msgEntity);
            }
        }
    }

    public synchronized final static void init() {
        if (!inited) {
            inited = true;
            doInit();
        }
    }

    private final static void doInit() {
        DBManagerWrapper.getInstance().queryAllSendingMsgsAndMarkThemAsFailed(new DBManagerWrapper.IDBActionFinishListener() {
            @Override
            public void onFinished() {
                //query first batch msgs
                loadNextBatch(noActionListener);
            }
        });

        ConnectedPeersManager.monitorConnectedPeersEvent(new ConnectedPeerEventListenerAdapter() {

            @Override
            public void onIncomingFileChunk(Peer peer, String id, int soFar, int chunkSize, int available, byte[] bytes) {
                super.onIncomingFileChunk(peer, id, soFar, chunkSize, available, bytes);

                //incoming first, generate msgEntity and add to list
                if (soFar == chunkSize) {
                    Logger.i(TAG, "incoming first file chunk, generate msgEntity and callback, id: " + id + ", peer: " + peer);
                    File finalFile = PeerUtils.getReceivedFileInLocalFromFileTypeMsgSendedByPeer(peer, id);
                    MsgID msgID = MsgID.buildWithJsonString(id);
                    MsgEntity msgEntity = handleNewMsgEntity(msgID.getId(), msgID.getType(), finalFile.getAbsolutePath(), available, PeerUtils.getDeviceIDFromPeer(peer), selfDeviceID);

                    if (msgEntity != null) {
                        msgEntity.setProgressForDeviceID(selfDeviceID, (int) (soFar * 100.0f / available));
                        msgEntitiesUpdateMonitorListenerGroup.onMsgEntitySendStateUpdated(msgEntity);
                    }
                }
            }


            @Override
            public void onIncomingFileChunkMergeFailed(Peer peer, String id) {
                super.onIncomingFileChunkMergeFailed(peer, id);
            }

            @Override
            public void onIncomingFileChunkMerged(Peer peer, String id, File finalFile) {
                super.onIncomingFileChunkMerged(peer, id, finalFile);
            }

            @Override
            public void onIncomingStringMsg(Peer peer, String id, String msg) {
                super.onIncomingStringMsg(peer, id, msg);

                Logger.i(TAG, "incoming string msg, generate msgEntity and handle it, msgID: " + id + ", msg: " + msg + ", peer: " + peer);

                String msgID = MsgID.buildWithJsonString(id).getId();

                handleNewMsgEntity(msgID, Constants.PeerMsgType.TYPE_STR, msg, msg.getBytes().length, PeerUtils.getDeviceIDFromPeer(peer), selfDeviceID);
                markMsgSendStateAndCallback(selfDeviceID, msgID, Constants.DB.MSG_SEND_STATE_SUCCEEDED);
            }

            @Override
            public void onIncomingMsgReadFailed(Peer peer, String id, int soFar, int total) {
                super.onIncomingMsgReadFailed(peer, id, soFar, total);

                markMsgSendStateAndCallback(selfDeviceID, MsgID.buildWithJsonString(id).getId(), Constants.DB.MSG_SEND_STATE_FAILED);
            }

            @Override
            public void onMsgSendFailed(Peer peer, String id) {
                super.onMsgSendFailed(peer, id);

                markMsgSendStateAndCallback(PeerUtils.getDeviceIDFromPeer(peer), MsgID.buildWithJsonString(id).getId(), Constants.DB.MSG_SEND_STATE_FAILED);
            }

            @Override
            public void onMsgSendStarted(Peer peer, String id) {
                super.onMsgSendStarted(peer, id);
            }

            @Override
            public void onNotAllMsgChunkSendedConfirmed(Peer peer, String id) {
                super.onNotAllMsgChunkSendedConfirmed(peer, id);

                markMsgSendStateAndCallback(PeerUtils.getDeviceIDFromPeer(peer), MsgID.buildWithJsonString(id).getId(), Constants.DB.MSG_SEND_STATE_FAILED);
            }

            @Override
            public void onSendedMsgChunkConfirmed(Peer peer, String id, int soFar, int total) {
                super.onSendedMsgChunkConfirmed(peer, id, soFar, total);

                MsgEntity msgEntity = getSendingMsgEntityByMsgAndDeviceID(MsgID.buildWithJsonString(id).getId(), PeerUtils.getDeviceIDFromPeer(peer));
                if (msgEntity != null) {
                    msgEntity.setProgressForDeviceID(PeerUtils.getDeviceIDFromPeer(peer), (int) (soFar * 100.0f / total));
                    if (soFar == total) {
                        markMsgSendStateAndCallback(PeerUtils.getDeviceIDFromPeer(peer), msgEntity.getMsgID(), Constants.DB.MSG_SEND_STATE_SUCCEEDED);
                    } else {
                        msgEntitiesUpdateMonitorListenerGroup.onMsgEntitySendStateUpdated(msgEntity);
                    }
                }
            }
        });
    }

    private static void markMsgSendStateAndCallback(String toID, String msgID, String state) {
        MsgEntity msgEntity = getSendingMsgEntityByMsgAndDeviceID(msgID, toID);
        if (msgEntity != null) {
            removeItemFromSendingMsgEntityMap(msgID, toID);
            msgEntity.removeItemFromMsgSendingTableAfterSendDone(toID, new DBManagerWrapper.IDBActionListenerAdapter());
            msgEntity.markMsgSendStateAndUpdateDB(toID, state, new DBManagerWrapper.IDBActionListenerAdapter());
            msgEntitiesUpdateMonitorListenerGroup.onMsgEntitySendStateUpdated(msgEntity);
        }
    }

    private synchronized static MsgEntity handleNewMsgEntity(String msgID, String type, String content, int available, String fromID, String... toIDs) {
        MsgEntity msgEntity = MsgEntity.buildMsgEntity(msgID, type, content, fromID, available, toIDs);
        if (msgEntity.isValid()) {
            msgEntities.addLast(msgEntity);
            addToSendingMsgEntityMap(msgID, msgEntity, toIDs);
            msgEntity.insertIntoMsgSendingTable(new DBManagerWrapper.IDBActionListenerAdapter());
            msgEntity.insertIntoMsgTable(new DBManagerWrapper.IDBActionListenerAdapter());
            msgEntitiesUpdateMonitorListenerGroup.onGotNewMsgEntities(Arrays.asList(msgEntity));
            return msgEntity;
        }
        return null;
    }

    public static boolean hasEverLoaded() {
        return everLoaded;
    }


    public static List<MsgEntity> getAllMsgEntities() {
        return msgEntities;
    }


    public static void loadNextBatch(IMsgEntityBatchLoadListener listener) {
        synchronized (MsgEntitiesManager.class) {
            listenerSet.add(listener);
            if (!isBatchLoading) {
                isBatchLoading = true;

                int firstID = msgEntities.size() == 0 ? Integer.MAX_VALUE : msgEntities.get(0).getAutoID();
                int limit = MSG_BATCH_LOAD_SIZE;

                DBManagerWrapper.getInstance().queryAllMsgs(firstID, limit, new DBManagerWrapper.IDBActionFinishListener() {
                    @Override
                    public void onFinished() {
                        everLoaded = true;
                        List<MsgEntity> queryedList = MsgEntity.buildWithDBQueryList(getResultList());
                        if (queryedList != null) {
                            Collections.sort(queryedList, msgEntityComparator);
                            msgEntities.addAll(0, queryedList);
                            msgEntitiesUpdateMonitorListenerGroup.onGotNewMsgEntities(queryedList);
                        }
                        Set<IMsgEntityBatchLoadListener> tmpListenerSet = new HashSet<>(listenerSet);
                        for (IMsgEntityBatchLoadListener each : tmpListenerSet) {
                            each.onFinished();
                        }
                        listenerSet.removeAll(tmpListenerSet);
                        isBatchLoading = false;
                    }
                });
                Logger.i(TAG, "load next batch from id: " + firstID + ", limit: " + limit);
            } else {
                Logger.i(TAG, "load next batch, has load stuff in progress, wait for previous load action done.");
            }
        }
    }


    private static String[] peerTagSetToIDArray(Set<String> tagSet) {
        if (tagSet == null || tagSet.size() == 0) {
            return new String[]{};
        } else {
            Set<String> idSet = new HashSet<>();
            for (String tag : tagSet) {
                idSet.add(PeerUtils.getDeviceIDFromPeerTag(tag));
            }
            return idSet.toArray(new String[0]);
        }
    }


    public static void monitorMsgEntitiesUpdate(IMsgEntityListUpdateListener listener) {
        msgEntitiesUpdateMonitorListenerGroup.add(listener);
    }

    public static void unmonitorMsgEntitiesUpdate(IMsgEntityListUpdateListener listener) {
        msgEntitiesUpdateMonitorListenerGroup.remove(listener);
    }


    public static void setGlobalMsgEntityUpdateListener(IMsgEntityListUpdateListener listener) {
        msgEntitiesUpdateMonitorListenerGroup.remove(globalMsgEntitiesUpdateListener);
        msgEntitiesUpdateMonitorListenerGroup.add(listener);
        globalMsgEntitiesUpdateListener = listener;
    }


    private synchronized static void addToSendingMsgEntityMap(String msgID, MsgEntity msgEntity, String... toDeviceIDs) {
        if (msgID == null || msgID.isEmpty() || msgEntity == null || !msgEntity.isValid() || toDeviceIDs == null || toDeviceIDs.length == 0) {
            return;
        } else {
            for (String toID : toDeviceIDs) {
                processingMsgEntities.put(msgID + "#" + toID, msgEntity);
            }
        }
    }

    private static MsgEntity getSendingMsgEntityByMsgAndDeviceID(String msgID, String toDeviceID) {
        if (msgID == null || msgID.isEmpty() || toDeviceID == null || toDeviceID.isEmpty()) {
            return null;
        } else {
            return processingMsgEntities.get(msgID + "#" + toDeviceID);
        }
    }


    private synchronized static void removeItemFromSendingMsgEntityMap(String msgID, String toDeviceID) {
        if (msgID == null || msgID.isEmpty() || toDeviceID == null || toDeviceID.isEmpty()) {
            return;
        } else {
            processingMsgEntities.remove(msgID + "#" + toDeviceID);
        }
    }

    public static void sendStringMsgToPeers(String msgID, String content, Set<String> tagSet) {
        Logger.i(TAG, "send string msg to peers, msgID: " + msgID + ", content: " + content + ", tagSet: " + tagSet);
        if (msgID == null || msgID.isEmpty() || content == null || content.isEmpty() || tagSet == null || tagSet.size() == 0) {
            Logger.i(TAG, "abort, invalid arguments");
            return;
        }

        String[] toDeviceIDs = peerTagSetToIDArray(tagSet);
        handleNewMsgEntity(msgID, Constants.PeerMsgType.TYPE_STR, content, content.getBytes().length, selfDeviceID, toDeviceIDs);

        for (String tag : tagSet) {
            Peer peer = ConnectedPeersManager.getConnectedPeerByTag(tag);
            if (peer == null) {
                //send msg failed immediately, currently peer is alread lost.
                markMsgSendStateAndCallback(tag, msgID, Constants.DB.MSG_SEND_STATE_FAILED);
            } else {
                peer.sendMessage(MsgCreator.createNormalStringMsg(msgID, content));
            }
        }
    }

    public static void sendFileMsgToPeers(String msgID, File file, Set<String> tagSet) {
        Logger.i(TAG, "send file msg to peers, msgID: " + msgID + ", file: " + file + ", tagSet: " + tagSet);


    }


}
