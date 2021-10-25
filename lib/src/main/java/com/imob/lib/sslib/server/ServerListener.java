package com.imob.lib.sslib.server;

public interface ServerListener {

    void onCreated();

    void onCreateFailed(String errorMsg);


}
