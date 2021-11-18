package com.badzzz.pasteany.core.api;

import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListenerAdapter;

import java.io.ByteArrayOutputStream;

public class APIHandler {

    public interface APIRequestListener {
        void start(Peer peer, String api);

        void response(Peer peer, String msg);

        void error(Peer peer, String msg, Exception e);

        void after(Peer peer);
    }

    public static void requestAPI(final Peer peer, String api, final APIRequestListener listener) {
        if (peer != null && api != null) {
            final Msg apiMsg = MsgCreator.createAPIMsg(api);
            peer.sendMessage(apiMsg);
            listener.start(peer, api);

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
                    }
                }

                void callbackSucceeded() {
                    if (!isResultCallbacked) {
                        isResultCallbacked = true;
                        unregisterListener();
                        listener.response(peer, new String(bos.toByteArray()));
                    }
                }


                @Override
                public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {
                    super.onMsgSendFailed(peer, id, msg, exception);
                    if (id.equals(apiMsg.getId())) {
                        unregisterListener();
                        listener.error(peer, msg, exception);
                    }
                }


                @Override
                public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {
                    super.onIncomingMsgChunkReadFailed(peer, id, errorMsg);
                    if (id.equals(apiMsg.getId())) {
                        callbackFailed("response read failed, " + errorMsg, null);
                    }
                }

                @Override
                public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {
                    super.onIncomingMsgReadFailed(peer, id, total, soFar);
                    if (id.equals(apiMsg.getId())) {
                        callbackFailed("response read failed", null);
                    }
                }

                @Override
                public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {
                    super.onIncomingMsgChunkReadSucceeded(peer, id, chunkSize, soFar, chunkBytes);
                    if (id.equals(apiMsg.getId())) {
                        bos.write(chunkBytes, 0, chunkSize);
                    }
                }


                @Override
                public void onIncomingMsgReadSucceeded(Peer peer, String id) {
                    super.onIncomingMsgReadSucceeded(peer, id);

                    if (id.equals(apiMsg.getId())) {
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
