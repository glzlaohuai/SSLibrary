package com.imob.lib.sslib.server;

public interface ServerListener {

    void onCreated();

    void onCreateFailed(Exception exception);

    void onDestroyed();

    void onCorrupted(String msg, Exception e);


}
