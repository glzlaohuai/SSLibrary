package com.imob.lib.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.imob.lib.app.utils.DialogUtils;
import com.imob.lib.sslib.client.ClientListener;
import com.imob.lib.sslib.client.ClientManager;
import com.imob.lib.sslib.client.ClientNode;
import com.imob.lib.sslib.msg.StringMsg;
import com.imob.lib.sslib.peer.Peer;
import com.imob.lib.sslib.peer.PeerListener;
import com.imob.lib.sslib.server.ServerListener;
import com.imob.lib.sslib.server.ServerManager;
import com.imob.lib.sslib.server.ServerNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            public void onMsgSendStart(Peer peer, String id) {

            }

            @Override
            public void onMsgSendSucceeded(Peer peer, String id) {

            }

            @Override
            public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {

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
            public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar) {

            }

            @Override
            public void onIncomingMsgReadSucceeded(Peer peer, String id) {

            }

            @Override
            public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {

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
        if (ServerManager.getManagedServerNode() != null) {
            boolean result = ServerManager.getManagedServerNode().broadcast(StringMsg.build(UUID.randomUUID().toString(), "this is a test msg send from server"));
            Log.i(TAG, "broadcastMsg, result: " + result);
        } else {
            Log.i(TAG, "broadcastMsg: has no server node instance");
        }
    }

    public void createClient(View view) {
        DialogUtils.createInputDialog(this, "ip", new DialogUtils.OnDialogInputListener() {
            @Override
            public void onInputContent(String content) {
                if (TextUtils.isEmpty(content)) {

                } else {
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
                                public void onMsgSendStart(Peer peer, String id) {

                                }

                                @Override
                                public void onMsgSendSucceeded(Peer peer, String id) {

                                }

                                @Override
                                public void onMsgSendFailed(Peer peer, String id, String msg, Exception exception) {

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
                                public void onIncomingMsgChunkReadSucceeded(Peer peer, String id, int chunkSize, int soFar) {

                                }

                                @Override
                                public void onIncomingMsgReadSucceeded(Peer peer, String id) {

                                }

                                @Override
                                public void onIncomingMsgReadFailed(Peer peer, String id, int total, int soFar) {

                                }
                            });
                        }
                    });
                }
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
        Map<String, Set<ClientNode>> inUsingClientMap = ClientManager.getInUsingClientMap();

        Set<String> keySet = inUsingClientMap.keySet();
        for (String key : keySet) {
            Set<ClientNode> clientNodes = inUsingClientMap.get(key);
            if (clientNodes != null) {
                for (ClientNode clientNode : clientNodes) {
                    clientNode.sendMsg(StringMsg.build(UUID.randomUUID().toString(), "a test msg from client to server"));
                }
            }
        }
    }
}

