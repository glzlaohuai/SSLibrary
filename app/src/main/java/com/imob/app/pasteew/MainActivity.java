package com.imob.app.pasteew;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.imob.app.pasteew.utils.DialogUtils;
import com.imob.app.pasteew.utils.FileUtils;
import com.imob.app.pasteew.utils.ServiceRegister;
import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.client.ClientListener;
import com.imob.lib.sslib.client.ClientManager;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerManager;
import com.imob.lib.sslib.server.ServerNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Demo";
    private TextView logView;

    private File testFile;

    public void clearLog(View view) {
        logView.setText("");
    }

    public void testIt(View view) throws IOException {
        doTest();
    }


    private void doTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                byte[] bytes = new byte[1024 * 512];
                for (int i = 0; i < 3; i++) {

                    try {
                        InputStream inputStream = getAssets().open("test.apk");

                        int available = inputStream.available();
                        int readed = 0;

                        while (readed < available) {
                            try {
                                readed += inputStream.read(bytes);
                                System.out.println("readed - " + i + ": " + readed);
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        }).start();
    }

    public void createAndRegisterService(View view) {
        ServiceRegister.startServiceRegisterStuff();
    }

    static class Log {
        public static void i(String tag, String msg) {
            String info = tag + " - " + msg;
            Logger.LogWatcher logWatcher = Logger.getLogWatcher();
            if (logWatcher != null) {
                logWatcher.log(info);
            }

            android.util.Log.i(tag, msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logView = findViewById(R.id.logView);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());

        testFile = new File(getCacheDir(), "a_test_file_name");
        copyTestFileToAppSandboxDirectory();
    }


    private void copyTestFileToAppSandboxDirectory() {

        if (!testFile.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = null;
                    FileOutputStream fos = null;

                    try {
                        inputStream = getAssets().open("test.apk");
                        if (!testFile.exists()) {
                            testFile.createNewFile();
                        }

                        fos = new FileOutputStream(testFile);
                        FileUtils.inputToOutput(inputStream, fos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Closer.close(fos);
                        Closer.close(inputStream);
                    }
                }
            }).start();

        }
    }

    private void appendLog(String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logView.append(log + "\n");
            }
        });
    }


    public void createServer(View view) {
        ServerManager.createServerNode(new ServerListener() {
            @Override
            public void onCreated() {

            }

            @Override
            public void onCreateFailed(Exception exception) {

            }

            @Override
            public void onDestroyed() {

            }

            @Override
            public void onCorrupted(String msg, Exception e) {

            }

            @Override
            public void onIncomingClient(Peer peer) {

            }
        }, new PeerListener() {
            @Override
            public void onMsgIntoQueue(Peer peer, String id) {

            }

            @Override
            public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendStart(Peer peer, String id) {

            }

            @Override
            public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendSucceeded(Peer peer, String id) {

            }

            @Override
            public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {

            }

            @Override
            public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {

            }

            @Override
            public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {

            }

            @Override
            public void onIOStreamOpened(Peer peer) {

            }

            @Override
            public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {

            }

            @Override
            public void onCorrupted(Peer peer, String msg, Exception e) {

            }

            @Override
            public void onDestroy(Peer peer) {

            }

            @Override
            public void onTimeoutOccured(Peer peer) {

            }

            @Override
            public void onIncomingMsg(Peer peer, String id, int available) {

            }

            @Override
            public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {

            }

            @Override
            public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {

            }

            @Override
            public void onIncomingMsgReadSucceeded(Peer peer, String id) {

            }

            @Override
            public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {

            }

            @Override
            public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {

            }

            @Override
            public void onMsgSendPending(Peer peer, String id) {

            }
        }, 10 * 1000);
    }

    public void printServerInfo(View view) {
        if (ServerManager.getManagedServerNode() != null) {

            ServerNode serverNode = ServerManager.getManagedServerNode();

            String serverSocketInfo = serverNode.getServerSocketInfo();
            Queue<Peer> connectedPeers = serverNode.getConnectedPeers();

            Log.i(TAG, "serverInfo: socketInfo: " + serverSocketInfo + ", connectedPeers: " + connectedPeers.size());

        } else {
            Log.i(TAG, "serverInfo: has no server instance currently");
        }
    }

    public void broadcastStringMsg(View view) {
        boolean result = ServerManager.broadcastStringMsg(UUID.randomUUID().toString(), "a test msg from server");
        Log.i(TAG, "broadcast string msg to all connected clients: " + result);
    }


    public void broadcastFileMsg(View view) {
        boolean result = ServerManager.broadcastFileMsg(UUID.randomUUID().toString(), testFile.getAbsolutePath());
        Log.i(TAG, "broadcast file msg to all connected clients: " + result);
    }

    public void createClient(View view) {
        DialogUtils.createInputDialog(this, "ip", new DialogUtils.OnDialogInputListener() {
            @Override
            public void onInputContent(String content) {
                if (content == null || content.equals("")) {
                    content = "127.0.0.1";
                }

                String ip = content;
                AtomicInteger port = new AtomicInteger(0);

                DialogUtils.createInputDialog(MainActivity.this, "port", new DialogUtils.OnDialogInputListener() {
                    @Override
                    public void onInputContent(String content) {
                        try {
                            port.set(Integer.parseInt(content));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        if (port.get() == 0 && ServerManager.getManagedServerNode() != null && ServerManager.getManagedServerNode().isRunning()) {
                            port.set(ServerManager.getManagedServerNode().getPort());
                        }

                        boolean result = ClientManager.createClient(ip, port.get(), new ClientListener() {
                            @Override
                            public void onClientCreated(ClientNode clientNode) {

                            }

                            @Override
                            public void onClientCreateFailed(ClientNode clientNode, String msg, Exception exception) {

                            }

                            @Override
                            public void onMsgIntoQueue(Peer peer, String id) {

                            }

                            @Override
                            public void onConfirmMsgIntoQueue(Peer peer, String id, int soFar, int total) {

                            }

                            @Override
                            public void onMsgSendStart(Peer peer, String id) {

                            }

                            @Override
                            public void onConfirmMsgSendStart(Peer peer, String id, int soFar, int total) {

                            }

                            @Override
                            public void onMsgSendSucceeded(Peer peer, String id) {

                            }

                            @Override
                            public void onConfirmMsgSendSucceeded(Peer peer, String id, int soFar, int total) {

                            }

                            @Override
                            public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {

                            }

                            @Override
                            public void onConfirmMsgSendFailed(Peer peer, String id, int soFar, int total, String msg, Exception exception) {

                            }

                            @Override
                            public void onMsgChunkSendSucceeded(Peer peer, String id, int chunkSize) {

                            }

                            @Override
                            public void onIOStreamOpened(Peer peer) {

                            }

                            @Override
                            public void onIOStreamOpenFailed(Peer peer, String errorMsg, Exception exception) {

                            }

                            @Override
                            public void onCorrupted(Peer peer, String msg, Exception e) {

                            }

                            @Override
                            public void onDestroy(Peer peer) {

                            }

                            @Override
                            public void onTimeoutOccured(Peer peer) {

                            }

                            @Override
                            public void onIncomingMsg(Peer peer, String id, int available) {

                            }

                            @Override
                            public void onIncomingMsgChunkReadFailed(Peer peer, String id, String errorMsg) {

                            }

                            @Override
                            public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar, byte[] chunkBytes) {

                            }

                            @Override
                            public void onIncomingMsgReadSucceeded(Peer peer, String id) {

                            }

                            @Override
                            public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {

                            }

                            @Override
                            public void onIncomingConfirmMsg(Peer peer, String id, int soFar, int total) {

                            }

                            @Override
                            public void onConfirmMsgSendPending(Peer peer, String id, int soFar, int total) {

                            }

                            @Override
                            public void onMsgSendPending(Peer peer, String id) {

                            }
                        }, 10 * 1000);

                        Log.i(TAG, "create client: " + ip + ", port: " + port.get() + ", result: " + result);
                    }
                });
            }
        });
    }

    public void printConnectedClientsInfo(View view) {
        Map<String, Set<ClientNode>> inUsingClientMap = ClientManager.getConnectedClientMap();
        Set<String> keySet = inUsingClientMap.keySet();

        Log.i(TAG, "connected clients key in map: " + keySet);
        for (String key : keySet) {
            Set<ClientNode> clientNodes = inUsingClientMap.get(key);
            Log.i(TAG, "connected clients for key: " + key + ", num: " + (clientNodes == null ? 0 : clientNodes.size()));
        }
    }

    public void sendMsgToServer(View view) {
        boolean result = ClientManager.sendOutStringMsgByAllConnectedClients(UUID.randomUUID().toString(), "a test msg from client");
        Log.i(TAG, "send string msg to server: " + result);
    }

    public void sendLargeMsgToServer(View view) {
        boolean result = ClientManager.sendOutFileMsgByAllConnectedClients(UUID.randomUUID().toString(), testFile.getAbsolutePath());
        Log.i(TAG, "send file msg to server: " + result);
    }


    public void destroyServer(View view) {
        ServerNode managedServerNode = ServerManager.getManagedServerNode();
        if (managedServerNode != null) {
            managedServerNode.destroy();
        }
    }


    public void destroyClient(View view) {
        if (ClientManager.getConnectedClientMap().isEmpty()) {
            Log.i(TAG, "destroyClient, found no clients");
        } else {
            Map<String, Set<ClientNode>> inUsingClientMap = ClientManager.getConnectedClientMap();
            Set<String> keySet = inUsingClientMap.keySet();

            List<String> items = new ArrayList<>();

            for (String key : keySet) {
                Set<ClientNode> clientNodes = inUsingClientMap.get(key);
                for (int i = 0; i < clientNodes.size(); i++) {
                    items.add(key + " - " + i);
                }
            }
            String[] itemsArray = new String[items.size()];
            items.toArray(itemsArray);

            DialogUtils.createListDialog(this, itemsArray, new DialogUtils.OnListDialogSelectListener() {
                @Override
                public void onSelected(int index) {
                    Log.i(TAG, "destry client: " + itemsArray[index]);

                    String[] splits = itemsArray[index].split("-");

                    Set<ClientNode> clientNodes = inUsingClientMap.get(splits[0].trim());
                    ClientNode clientNode = clientNodes.iterator().next();
                    clientNode.destroy();
                }
            });
        }
    }
}

