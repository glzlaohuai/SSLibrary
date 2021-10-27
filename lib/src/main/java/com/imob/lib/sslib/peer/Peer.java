package com.imob.lib.sslib.peer;

import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.Chunk;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.msg.MsgQueue;
import com.imob.lib.sslib.utils.Closer;
import com.imob.lib.sslib.utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {

    private static final String S_TAG = "Peer";

    public static final int CHUNK_BYTE_LEN = 1024 * 1024;

    private static final String MSG_SEND_ERROR_PEER_IS_DESTROIED = "send failed, peer is already destroied";
    private static final String MSG_SEND_ERROR_NO_AVAILABLE_BYTES_INPUT = "no available bytes found";
    private static final String MSG_SEND_ERROR_READ_CHUNK_FROM_INPUT_ = "error occured while reading byte chunk from input stream";
    private static final String MSG_SEND_ERROR_CONNECTION_LOST = "connection lost";
    private Socket socket;
    private PeerListener listener;

    private DataInputStream dis;
    private DataOutputStream dos;

    private MsgQueue msgQueue = new MsgQueue();

    private boolean isDestroyed = false;

    private ExecutorService msgSendService = Executors.newSingleThreadExecutor();
    private ExecutorService monitorIncomingMsgService = Executors.newSingleThreadExecutor();

    private boolean destroyCallbacked = false;
    private boolean corruptedCallbacked = false;

    private INode localNode;

    private String tag;


    public Peer(Socket socket, INode localNode, PeerListener listener) {
        this.socket = socket;
        this.listener = listener;

        this.localNode = localNode;

        init();

        tag = S_TAG + " - " + (localNode.isServerNode() ? "server" : "client") + " # " + hashCode();
    }

    public INode getLocalNode() {
        return localNode;
    }

    private void init() {
        msgSendService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());

                    listener.onIOStreamOpened(Peer.this);

                    startMonitorIncomingMsg();
                    startHandleMsgSendStuff();

                } catch (IOException e) {
                    Logger.e(e);
                    listener.onIOStreamOpenFailed(Peer.this, null, e);

                    //something went wrong here, maybe connection is lost,
                    destroy();
                }
            }
        });
    }

    public void destroy() {
        if (!isDestroyed) {
            isDestroyed = true;

            doDestroyStuff();
            callbackDestroy();
        }
    }


    private void callbackDestroy() {
        if (!destroyCallbacked) {
            destroyCallbacked = true;
            listener.onDestroy(Peer.this);
        }
    }


    private void callbackCorrupted(String msg, Exception exception) {
        if (!corruptedCallbacked) {
            corruptedCallbacked = true;
            listener.onCorrupted(Peer.this, msg, exception);
        }
    }


    private void clearAllNonePendingMsgAndCallbackFailed() {
        while (!msgQueue.isEmpty()) {
            Msg msg = msgQueue.poll();
            listener.onMsgSendStart(Peer.this, msg.getId());
            listener.onMsgSendFailed(Peer.this, msg.getId(), MSG_SEND_ERROR_PEER_IS_DESTROIED, null);
            msg.destroy();
        }
    }

    private void doDestroyStuff() {
        closeIODropConnection();
        clearAllNonePendingMsgAndCallbackFailed();
    }


    private void closeIODropConnection() {
        Closer.close(dis);
        Closer.close(dos);

        Closer.close(socket);
    }

    public synchronized void sendMessage(Msg msg) {
        if (msg == null || !msg.isValid()) {
            listener.onMsgSendFailed(this, msg == null ? null : msg.getId(), "msg is null or invalid", null);
            return;
        }

        if (isDestroyed) {
            listener.onMsgSendFailed(this, msg.getId(), MSG_SEND_ERROR_PEER_IS_DESTROIED, null);
            msg.destroy();
            return;
        }

        msgQueue.add(msg);
        Logger.i(tag, "awake msg queue loop thread after adding msg into msg queue.");
        listener.onMsgIntoQueue(this, msg.getId());
        notify();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }


    private void startHandleMsgSendStuff() {
        msgSendService.execute(new Runnable() {
            @Override
            public void run() {
                loopMsgQueue();
            }
        });
    }

    private synchronized void loopMsgQueue() {
        final byte[] bytes = new byte[CHUNK_BYTE_LEN];
        while (!isDestroyed) {
            Msg msg = msgQueue.poll();
            Logger.i(tag, "poll msg: " + msg);
            if (msg == null) {
                Logger.i(tag, "has no msg in msg queue currently, halt loop thread.");
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            listener.onMsgSendStart(Peer.this, msg.getId());

            int available = msg.getAvailable();
            if (available <= 0) {
                listener.onMsgSendFailed(Peer.this, msg.getId(), MSG_SEND_ERROR_NO_AVAILABLE_BYTES_INPUT, null);
                msg.destroy();
                continue;
            }

            //available != 0
            try {
                dos.writeUTF(msg.getId());
                dos.writeInt(available);

                int readed = 0;

                while (readed < available) {
                    Chunk chunk = msg.readChunk(bytes);
                    //eof, should never go there
                    if (chunk.getSize() == -1) {

                    } else if (chunk.getSize() == 0) {
                        //exception occured
                        listener.onMsgSendFailed(Peer.this, msg.getId(), MSG_SEND_ERROR_READ_CHUNK_FROM_INPUT_, null);
                        dos.writeInt(0);
                    } else {
                        readed += chunk.getSize();

                        dos.writeInt(chunk.getSize());
                        dos.write(chunk.getBytes(), 0, chunk.getSize());

                        listener.onMsgChunkSendSucceeded(Peer.this, msg.getId(), chunk.getSize());
                    }
                }
                listener.onMsgSendSucceeded(Peer.this, msg.getId());
            } catch (IOException e) {
                Logger.e(e);
                //send msg failed, connection lost
                destroy();
                listener.onMsgSendFailed(Peer.this, msg.getId(), MSG_SEND_ERROR_CONNECTION_LOST, e);

                callbackCorrupted("corrupted due to exception occured while sending msg to peer", e);

            } finally {
                msg.destroy();
            }
        }

        Logger.i(tag, "msg queue loop end");

    }

    private void startMonitorIncomingMsg() {

        final byte[] buffer = new byte[CHUNK_BYTE_LEN];

        monitorIncomingMsgService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isDestroyed) {
                        String id = dis.readUTF();
                        int available = dis.readInt();
                        listener.onIncomingMsg(Peer.this, id, available);


                        int readed = 0;

                        while (readed < available) {
                            int chunkSize = dis.readInt();
                            readed += chunkSize;

                            if (chunkSize == 0) {
                                listener.onIncomingMsgChunkReadFailedDueToPeerIOFailed(Peer.this, id);
                                break;
                            } else {
                                dis.readFully(buffer, 0, chunkSize);
                                listener.onIncomingMsgChunkReadSucceeded(Peer.this, id, chunkSize, readed, buffer);
                            }
                        }

                        if (readed == available) {
                            listener.onIncomingMsgReadSucceeded(Peer.this, id);
                        } else {
                            listener.onIncomingMsgReadFailed(Peer.this, id, available, readed);
                        }
                    }

                } catch (IOException e) {
                    Logger.e(e);
                    destroy();
                    callbackCorrupted("corrupted due to exception occured while monitor incoming msg", e);
                }
            }
        });
    }


    public Socket getSocket() {
        return socket;
    }
}
