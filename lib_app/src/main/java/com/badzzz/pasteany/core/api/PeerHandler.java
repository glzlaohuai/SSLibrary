package com.badzzz.pasteany.core.api;

import com.imob.lib.sslib.peer.Peer;

public class PeerHandler {

    public interface PeerAPIRequestListener {
        void start();

        void response(String msg);

        void error(String msg, Exception e);

        void after();
    }

    public static void requestAPI(Peer peer, String api, PeerAPIRequestListener listener) {
        if (peer != null && api != null) {

        }
    }


}
