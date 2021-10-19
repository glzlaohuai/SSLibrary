package com.imob.lib.sslib;

import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerManager {

    private static final Byte lock = 0x1;
    private static ServerSocket serverSocket;
    private final static ExecutorService acceptClientExecutorService = Executors.newSingleThreadExecutor();

    public interface OnServerListener {
        void onCreateSucceed(ServerSocket serverSocket);

        void onCreateFailed(ServerSocket serverSocket, Exception exception);


        void onAlreadyCreated(ServerSocket serverSocket);
    }


    public static void createServer(OnServerListener serverListener) {
        if (serverSocket == null) {
            synchronized (lock) {
                if (serverSocket == null) {

                    boolean isCreated = false;
                    Exception exception = null;

                    try {
                        serverSocket = new ServerSocket(0);
                        isCreated = true;
                    } catch (IOException e) {
                        Logger.print(e);
                        exception = e;
                    }

                    if (isCreated) {
                        serverListener.onCreateSucceed(serverSocket);
                        startAccept(serverListener);
                    } else {
                        serverListener.onCreateFailed(serverSocket, exception);
                    }
                }
            }
        } else {
            serverListener.onAlreadyCreated(serverSocket);
        }
    }


    private static void startAccept(OnServerListener serverListener) {
        acceptClientExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();


                    }
                } catch (IOException e) {
                    Logger.i("error occured during client incoming monitoring, " + e);
                }

            }
        });
    }


    public static void printServerInfo() {
        Logger.i("created server info: " + serverSocket);
    }


}
