package com.imob.lib.sslib.server;

import com.imob.lib.sslib.server.exception.AlreadyHasARunningServerException;
import com.imob.lib.sslib.server.exception.ServerIsCreatingException;
import com.imob.lib.sslib.server.exception.ServerIsDestroiedException;
import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNode {

    private final static ExecutorService createExecutorService = Executors.newSingleThreadExecutor();

    private final static ExecutorService monitorExecutorService = Executors.newSingleThreadExecutor();

    public interface OnServerListener {
        void onCreated();

        void onCreateFailed(Exception exception);

        void onDestroied();

        void onCorrupted();
    }

    private boolean isCreating = false;
    private boolean isRunning = false;
    private boolean isDestroied = false;

    private ServerSocket serverSocket;
    private OnServerListener listener;

    public ServerNode(OnServerListener serverListener) {
        this.listener = serverListener;
    }

    public boolean isCreating() {
        return isCreating;
    }

    public void create() throws AlreadyHasARunningServerException, ServerIsDestroiedException, ServerIsCreatingException {

        if (isCreating) {
            throw new ServerIsCreatingException();
        } else if (isRunning()) {
            throw new AlreadyHasARunningServerException();
        } else if (isDestroied()) {
            throw new ServerIsDestroiedException();
        }

        handleCreate();
    }

    private void handleCreate() {
        createExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(0);

                    isCreating = false;
                    isRunning = true;
                    isDestroied = false;

                    listener.onCreated();

                    startMonitorIncomingClients();

                } catch (IOException e) {
                    Logger.e(e);
                    listener.onCreateFailed(e);
                } finally {
                    isCreating = false;
                }
            }
        });
    }


    private void startMonitorIncomingClients() {
        monitorExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                handleMonitorIncomingClients();
            }
        });
    }


    private void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
    }

    public void destroy() {
        if (!isDestroied()) {
            isDestroied = true;
            listener.onDestroied();
            closeServerSocket();
            listener.onDestroied();
        }
    }


    private void handleMonitorIncomingClients() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                manageIncomingClient(socket);

            } catch (IOException e) {
                Logger.e(e);
                if (!isDestroied) {
                    isDestroied = true;
                    listener.onCorrupted();
                }
            }
        }
    }


    private void manageIncomingClient(Socket socket) {
        // TODO: 2021/10/25
    }

    public boolean isRunning() {
        return isRunning && serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed();
    }

    public boolean isDestroied() {
        return isDestroied;
    }


}
