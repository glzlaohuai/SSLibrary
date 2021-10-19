package com.imob.lib.sslib;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerNode {

    public interface OnServerListener {
        void onCreated();

        void onAlreadyCreated();
    }

    private ServerSocket serverSocket;
    private OnServerListener serverListener;

    public ServerNode(OnServerListener serverListener) {
        this.serverListener = serverListener;
    }


    public void create() {
        if (serverSocket != null) {
            try {
                serverSocket = new ServerSocket(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.serverListener.onAlreadyCreated();
        }
    }


}
