package com.badzzz.pasteany.core.manager;

import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.dbentity.MsgEntity;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeerEventListenerAdapter;
import com.badzzz.pasteany.core.nsd.peer.ConnectedPeersManager;
import com.badzzz.pasteany.core.utils.PeerUtils;
import com.badzzz.pasteany.core.wrap.DBManagerWrapper;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.sslib.peer.Peer;

import java.io.File;

public class IncomingMsgDBManager {

    private static boolean inited = false;

    public synchronized static void init() {
        if (!inited) {
            inited = true;
            doInit();
        }
    }


    private static void doInit() {
        ConnectedPeersManager.monitorConnectedPeersEvent(new ConnectedPeerEventListenerAdapter() {

            @Override
            public void onIncomingFileChunkSaved(Peer peer, String id, int soFar, int chunkSize, File file) {
                super.onIncomingFileChunkSaved(peer, id, soFar, chunkSize, file);
            }

            @Override
            public void onIncomingFileChunk(Peer peer, String id, int soFar, int chunkSize, int available, byte[] bytes) {
                super.onIncomingFileChunk(peer, id, soFar, chunkSize, available, bytes);
            }

            @Override
            public void onIncomingFileChunkSaveFailed(Peer peer, String id, int soFar, int chunkSize) {
                super.onIncomingFileChunkSaveFailed(peer, id, soFar, chunkSize);
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

                MsgID msgID = MsgID.buildWithJsonString(id);
                MsgEntity msgEntity = MsgEntity.buildMsgEntity(msgID.getId(), msgID.getType(), msg, PeerUtils.getDeviceIDFromPeer(peer), msg.getBytes().length, PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceID());
                msgEntity.insertIntoMsgTable(new DBManagerWrapper.IDBActionListenerWrapper());
            }

            @Override
            public void onIncomingMsgReadSucceeded(Peer peer, String id) {
                super.onIncomingMsgReadSucceeded(peer, id);
            }

            @Override
            public void onIncomingMsgReadFailed(Peer peer, String id, int soFar, int total) {
                super.onIncomingMsgReadFailed(peer, id, soFar, total);
            }
        });
    }
}
