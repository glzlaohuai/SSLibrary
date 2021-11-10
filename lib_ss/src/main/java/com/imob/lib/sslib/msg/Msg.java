package com.imob.lib.sslib.msg;

import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.peer.Peer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Msg {
    private String id;
    private InputStream inputStream;

    private Set<Peer> holdedPeers = new HashSet<>();


    public Msg(String id, InputStream inputStream) {
        this.id = id;
        this.inputStream = inputStream;
    }

    public void addPeerHolder(Peer... peers) {
        if (peers != null) {
            Collections.addAll(holdedPeers, peers);
        }
    }


    public boolean isValid() {
        return id != null && !id.equals("") && inputStream != null;
    }


    public void destroy(Peer peer) {
        holdedPeers.remove(peer);

        if (holdedPeers.size() == 0) {
            Closer.close(inputStream);
        }
    }

    public String getId() {
        return id;
    }

    public InputStream getInputStream() {
        return inputStream;
    }


    public Chunk readChunk(byte[] bytes) {
        int readed = 0;
        Exception exception = null;

        if (holdedPeers.size() == 0) {
            throw new IllegalStateException("has no any holded peers currently, it maybe destroyed already or not be successfully setup.");
        }

        try {
            readed = inputStream.read(bytes);
        } catch (IOException e) {
            exception = e;
            Logger.e(e);
        }

        return new Chunk(bytes, readed,exception);
    }


    public int getAvailable() {

        try {
            return inputStream.available();
        } catch (IOException e) {
            Logger.e(e);
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "id='" + id + '\'' +
                ", inputStream=" + inputStream +
                '}';
    }
}
