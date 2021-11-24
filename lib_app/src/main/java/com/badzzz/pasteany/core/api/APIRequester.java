package com.badzzz.pasteany.core.api;

import com.badzzz.pasteany.core.api.msg.MsgID;
import com.badzzz.pasteany.core.utils.Constants;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListenerAdapter;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class APIRequester {

    private static final String TAG = "APIRequester";

    public interface APIRequestListener {
        void start(Peer peer, String api);

        void response(Peer peer, String msg);

        void error(Peer peer, String msg, Exception e);

        void after(Peer peer);
    }

    public static void requestAPI(final Peer peer, String api, final APIRequestListener listener) {
        final String tag = TAG + " # " + UUID.randomUUID().hashCode();
        Logger.i(tag, "reqeust api, peer: " + peer.getTag() + ", api: " + api);

        if (peer != null && api != null) {
            final Msg apiMsg = MsgCreator.createAPIRequestMsg(api);
            peer.sendMessage(apiMsg);
            listener.start(peer, api);

            final String msgID = MsgID.buildWithJsonString(apiMsg.getId()).getId();

            peer.registerListener(new PeerListenerAdapter() {

                boolean isResultCallbacked = false;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();


                void unregisterListener() {
                    peer.unregisterListener(this);
                }

                void callbackFailed(String msg, Exception e) {
                    if (!isResultCallbacked) {
                        isResultCallbacked = true;
                        unregisterListener();
                        listener.error(peer, msg, e);
                        listener.after(peer);
                        Logger.i(tag, "request failed, msg: " + msg + ", exception: " + e);
                    }
                }

                void callbackSucceeded() {
                    if (!isResultCallbacked) {
                        isResultCallbacked = true;
                        unregisterListener();
                        listener.response(peer, new String(bos.toByteArray()));
                        Logger.i(tag, "request succeeded, response: " + new String(bos.toByteArray()));
                    }
                }


                private MsgID createMsgID(String id) {
                    return MsgID.buildWithJsonString(id);
                }

                @Override
                public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
                    super.onMsgSendFailed(peer, id, msg, exception);
                    MsgID tmp = createMsgID(id);
                    if (tmp != null && tmp.getId().equals(msgID)) {
                        unregisterListener();
                        listener.error(peer, msg, exception);
                    }
                }


                @Override
                public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
                    super.onIncomingMsgChunkReadFailed(peer, id, errorMsg);
                    MsgID tmp = createMsgID(id);
                    if (tmp != null && tmp.getId().equals(msgID)) {
                        callbackFailed("response read failed, " + errorMsg, null);
                    }
                }

                @Override
                public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
                    super.onIncomingMsgReadFailed(peer, id, total, soFar);
                    MsgID tmp = createMsgID(id);

                    if (tmp != null && tmp.getId().equals(msgID)) {
                        callbackFailed("response read failed", null);
                    }
                }

                @Override
                public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, int available, byte[] chunkBytes) {
                    super.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, available, chunkBytes);
                    MsgID tmp = createMsgID(id);

                    if (tmp != null && tmp.getId().equals(msgID) && tmp.getType().equals(Constants.PeerMsgType.TYPE_API_RESPONSE)) {
                        bos.write(chunkBytes, 0, chunkSize);
                    }
                }


                @Override
                public void onIncomingMsgReadSucceeded(Peer peer, String id) {
                    super.onIncomingMsgReadSucceeded(peer, id);

                    MsgID tmp = createMsgID(id);

                    if (tmp != null && tmp.getId().equals(msgID) && tmp.getType().equals(Constants.PeerMsgType.TYPE_API_RESPONSE)) {
                        callbackSucceeded();
                    }
                }

                @Override
                public void onTimeoutOccured(Peer peer) {
                    super.onTimeoutOccured(peer);
                    callbackFailed("timeout", null);
                }
            });
        }
    }


}
