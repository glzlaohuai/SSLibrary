package com.imob.lib.app;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.imob.lib.app.utils.DialogUtils;
import com.imob.lib.sslib.client.ClientListener;
import com.imob.lib.sslib.client.ClientManager;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.msg.StringMsg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerManager;
import com.imob.lib.sslib.server.ServerNode;
import com.imob.lib.sslib.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Demo";
    private TextView logView;

    public void clearLog(View view) {
        logView.setText("");
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

        Logger.setLogWatcher(new Logger.LogWatcher() {
            @Override
            public void log(String log) {
                appendLog(log);
            }
        });
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
            public void onIncomingMsg(Peer peer, String id, int available) {

            }

            @Override
            public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {

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
        });
    }

    public void printServerInfo(View view) {
        if (ServerManager.getManagedServerNode() != null) {

            ServerNode serverNode = ServerManager.getManagedServerNode();

            String serverSocketInfo = serverNode.getServerSocketInfo();
            List<Peer> connectedPeers = serverNode.getConnectedPeers();

            Log.i(TAG, "serverInfo: socketInfo: " + serverSocketInfo + ", connectedPeers: " + connectedPeers.size());

        } else {
            Log.i(TAG, "serverInfo: has no server instance currently");
        }
    }

    public void broadcastMsg(View view) {
        Msg msg = StringMsg.build(UUID.randomUUID().toString(), "this is a test msg send from server");
        doBroadcastMsg(msg);
    }


    private Msg createTestFileMsg() throws IOException {
        Msg msg = new Msg(UUID.randomUUID().toString(), getAssets().open("test.apk"));
        return msg;
    }

    public void broadcastFileMsg(View view) {
        try {
            Msg msg = createTestFileMsg();
            doBroadcastMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "create msg failed");
        }
    }

    private void doBroadcastMsg(Msg msg) {
        if (ServerManager.getManagedServerNode() != null) {
            boolean result = ServerManager.getManagedServerNode().broadcast(msg);
            Log.i(TAG, "broadcastMsg, result: " + result);
        } else {
            Log.i(TAG, "broadcastMsg: has no server node instance");
        }
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

                        Log.i(TAG, "create client: " + ip + ", port: " + port.get());

                        ClientManager.createClient(ip, port.get(), new ClientListener() {
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
                            public void onIncomingMsg(Peer peer, String id, int available) {

                            }

                            @Override
                            public void onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer peer, String id) {

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
                        });
                    }
                });
            }
        });
    }

    public void printConnectedClientsInfo(View view) {
        Map<String, Set<ClientNode>> inUsingClientMap = ClientManager.getInUsingClientMap();
        Set<String> keySet = inUsingClientMap.keySet();

        Log.i(TAG, "connected clients key in map: " + keySet);
        for (String key : keySet) {
            Set<ClientNode> clientNodes = inUsingClientMap.get(key);
            Log.i(TAG, "connected clients for key: " + key + ", num: " + (clientNodes == null ? 0 : clientNodes.size()));
        }
    }

    public void sendMsgToServer(View view) {
        boolean result = ClientManager.sendMsgByAllClients(StringMsg.build(UUID.randomUUID().toString(), "a test msg send to connected server"));
        Log.i(TAG, "send msg to server: " + result);
    }

    public void sendLargeMsgToServer(View view) {
        try {
            boolean result = ClientManager.sendMsgByAllClients(createTestFileMsg());
            Log.i(TAG, "send msg to server: " + result);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "create file msg failed: " + e);
        }
    }


    public void destroyServer(View view) {
        ServerNode managedServerNode = ServerManager.getManagedServerNode();
        if (managedServerNode != null) {
            managedServerNode.destroy();
        }
    }


    public void destroyClient(View view) {
        if (ClientManager.getInUsingClientMap().isEmpty()) {
            Log.i(TAG, "destroyClient, found no clients");
        } else {
            Map<String, Set<ClientNode>> inUsingClientMap = ClientManager.getInUsingClientMap();
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

