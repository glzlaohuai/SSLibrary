package com.imob.lib.sslib.peer;

import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.Chunk;
import com.imob.lib.sslib.msg.ConfirmMsg;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.msg.MsgQueue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {

    private static final String S_TAG = "Peer";

    private final static Map<String, Long> chunkSendingTime = new ConcurrentHashMap<>();

    public static final int CHUNK_BYTE_LEN = 1024 * 1024;

    private static final String MSG_SEND_ERROR_PEER_IS_DESTROIED = "send failed, peer is already destroied";
    private static final String MSG_SEND_ERROR_NO_AVAILABLE_BYTES_INPUT = "no available bytes found";
    private static final String MSG_SEND_ERROR_READ_CHUNK_FROM_INPUT = "error occured while reading byte chunk from input stream";
    private static final String MSG_SEND_ERROR_CONNECTION_LOST = "connection lost";
    private Socket socket;
    private PeerListener listener;

    private DataInputStream dis;
    private DataOutputStream dos;

    private MsgQueue msgQueue = new MsgQueue();

    private boolean isDestroyed = false;

    private ExecutorService msgSendService = Executors.newSingleThreadExecutor();
    private ExecutorService monitorIncomingMsgService = Executors.newSingleThreadExecutor();
    private ExecutorService msgInQueueService = Executors.newSingleThreadExecutor();
    private ExecutorService timeoutCheckService = Executors.newSingleThreadExecutor();

    private Byte timeoutLock = 0x0;

    private boolean destroyCallbacked = false;
    private boolean corruptedCallbacked = false;
    private boolean isTimeoutCheckRunning = false;

    private INode localNode;

    private String tag;

    private long timeout;

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

            callbackMsgSendStart(msg);
            callbackMsgSendFailed(msg, MSG_SEND_ERROR_PEER_IS_DESTROIED, null);

            msg.destroy();
        }
    }

    /**
     * @param timeout timeout in milliseconds, none positive value will be dropped and will not take effect
     */
    public void setTimeout(long timeout) {
        if (timeout <= 0) return;
        this.timeout = timeout;
        if (!isDestroyed() && !isTimeoutCheckRunning) {
            synchronized (timeoutLock) {
                if (!isDestroyed() && !isTimeoutCheckRunning) {
                    isTimeoutCheckRunning = true;
                    timeoutCheckService.execute(new Runnable() {
                        @Override
                        public void run() {
                            kickOffTimeoutCheck();
                        }
                    });
                }
            }
        }
    }


    private long findFirstAddedChunkSendTime() {
        long minTime = Long.MAX_VALUE;

        Set<String> keys = chunkSendingTime.keySet();
        for (String msgKey : keys) {

            Long aLong = chunkSendingTime.get(msgKey);
            if (aLong != null) {
                minTime = Math.min(aLong, minTime);
            }
        }
        return minTime;
    }

    private void callbackTimeout() {
        listener.onTimeoutOccured(this);
        destroy();
    }

    private void kickOffTimeoutCheck() {
        while (!isDestroyed()) {
            synchronized (timeoutLock) {
                if (chunkSendingTime.isEmpty()) {
                    try {
                        timeoutLock.wait();
                        Logger.i(tag, "msg chunk sending msg map is empty now, just wait until it's not empty to resume the timeout check process.");
                    } catch (InterruptedException e) {
                        Logger.e(e);
                    }
                } else {
                    long currentTime = System.currentTimeMillis();
                    //find the minimum time in chunkSendingTimeMap
                    long minimumTime = findFirstAddedChunkSendTime();

                    if (currentTime < minimumTime || (currentTime - minimumTime) >= timeout) {
                        //timeout occur
                        callbackTimeout();
                    } else {
                        try {
                            //wait at least this time long, or be notified due to a new msg chunk send time was put into chunkSendingTimeMap
                            timeoutLock.wait(currentTime - minimumTime);
                        } catch (InterruptedException e) {
                            Logger.e(e);
                        }
                    }
                }
            }
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

    public void sendMessage(final Msg msg) {
        msgQueue.add(msg);
        callbackMsgSendPending(msg);
        msgInQueueService.execute(new Runnable() {
            @Override
            public void run() {
                doSendMessage(msg);
            }
        });
    }

    private void callbackMsgSendPending(Msg msg) {
        if (msg instanceof ConfirmMsg) {
            listener.onConfirmMsgSendPending(this, msg.getId(), ((ConfirmMsg) msg).getSoFar(), ((ConfirmMsg) msg).getTotal());
        } else {
            listener.onMsgSendPending(this, msg.getId());
        }
    }

    private synchronized void doSendMessage(Msg msg) {
        if (msg == null || !msg.isValid()) {
            callbackMsgSendFailed(msg, "msg is null or invalid", null);
            return;
        }

        if (isDestroyed()) {
            callbackMsgSendFailed(msg, MSG_SEND_ERROR_PEER_IS_DESTROIED, null);
            msg.destroy();
            return;
        }

        Logger.i(tag, "awake msg queue loop thread after adding msg into msg queue.");
        callbackMsgIntoQueue(msg);
        notify();
    }

    private void callbackMsgIntoQueue(Msg msg) {
        if (msg instanceof ConfirmMsg) {
            listener.onConfirmMsgIntoQueue(this, msg.getId(), ((ConfirmMsg) msg).getSoFar(), ((ConfirmMsg) msg).getTotal());
        } else {
            listener.onMsgIntoQueue(this, msg.getId());
        }
    }


    public boolean isDestroyed() {
        return isDestroyed;
    }


    private void callbackMsgSendStart(Msg msg) {
        if (msg instanceof ConfirmMsg) {
            listener.onConfirmMsgSendStart(this, msg.getId(), ((ConfirmMsg) msg).getSoFar(), ((ConfirmMsg) msg).getTotal());
        } else {
            listener.onMsgSendStart(this, msg.getId());
        }
    }

    private void callbackMsgSendSucceeded(Msg msg) {
        if (msg instanceof ConfirmMsg) {
            listener.onConfirmMsgSendSucceeded(this, msg.getId(), ((ConfirmMsg) msg).getSoFar(), ((ConfirmMsg) msg).getTotal());
        } else {
            listener.onMsgSendSucceeded(this, msg.getId());
        }
    }

    private void callbackMsgSendFailed(Msg msg, String errorMsg, Exception e) {
        if (msg instanceof ConfirmMsg) {
            listener.onConfirmMsgSendFailed(this, msg.getId(), ((ConfirmMsg) msg).getSoFar(), ((ConfirmMsg) msg).getTotal(), errorMsg, e);
        } else {
            listener.onMsgSendFailed(Peer.this, msg == null ? "" : msg.getId(), errorMsg, e);
        }
    }


    private void startHandleMsgSendStuff() {
        msgSendService.execute(new Runnable() {
            @Override
            public void run() {
                loopMsgQueue();
            }
        });
    }


    private void addItemToChunkSendingTime(String id, int soFar) {
        chunkSendingTime.put(id + " # " + soFar, System.currentTimeMillis());
    }

    private void removeItemFromChunkSendingTime(String id, int soFar) {
        chunkSendingTime.remove(id + " # " + soFar);
    }

    private synchronized void loopMsgQueue() {
        final byte[] bytes = new byte[CHUNK_BYTE_LEN];
        while (!isDestroyed()) {
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
            callbackMsgSendStart(msg);

            //if msg instance confirMsg, then available is a negotiated negative number
            int available = msg.getAvailable();

            if (available <= 0 && !(msg instanceof ConfirmMsg)) {
                callbackMsgSendFailed(msg, MSG_SEND_ERROR_NO_AVAILABLE_BYTES_INPUT, null);
                msg.destroy();
                continue;
            }

            //go there only if available > 0
            try {
                dos.writeUTF(msg.getId());
                dos.writeInt(available);

                if (msg instanceof ConfirmMsg) {
                    Logger.i(S_TAG, "write confirmMsg to dos, " + msg.getId());
                    dos.writeInt(((ConfirmMsg) msg).getSoFar());
                    dos.writeInt(((ConfirmMsg) msg).getTotal());
                } else {
                    int readed = 0;
                    int round = 1;
                    while (readed < available) {
                        Chunk chunk = msg.readChunk(bytes);

                        Logger.i(S_TAG, "write normal msg to dos, round: " + round + ", chunkSize: " + chunk.getSize() + ", total: " + available);

                        //eof, should never go there
                        if (chunk.getSize() == -1) {

                        } else if (chunk.getSize() == 0) {
                            //exception occured
                            callbackMsgSendFailed(msg, MSG_SEND_ERROR_READ_CHUNK_FROM_INPUT, null);
                            dos.writeInt(0);
                        } else {
                            readed += chunk.getSize();

                            dos.writeInt(chunk.getSize());
                            dos.write(chunk.getBytes(), 0, chunk.getSize());

                            listener.onMsgChunkSendSucceeded(Peer.this, msg.getId(), chunk.getSize());

                            //保存chunk发出的时间
                            synchronized (timeoutLock) {
                                addItemToChunkSendingTime(msg.getId(), readed);
                                timeoutLock.notify();
                            }
                        }
                        round++;
                    }
                }
                callbackMsgSendSucceeded(msg);
            } catch (IOException e) {
                Logger.e(e);
                //send msg failed, connection lost
                destroy();
                callbackMsgSendFailed(msg, MSG_SEND_ERROR_CONNECTION_LOST, e);
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
                    while (!isDestroyed()) {
                        String id = dis.readUTF();
                        int available = dis.readInt();

                        if (available > 0) {
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
                                    //send confirm(ack) msg
                                    sendMessage(ConfirmMsg.build(id, readed, available));
                                }
                            }

                            if (readed == available) {
                                listener.onIncomingMsgReadSucceeded(Peer.this, id);
                            } else {
                                listener.onIncomingMsgReadFailed(Peer.this, id, available, readed);
                            }
                        } else if (available == ConfirmMsg.AVAILABLE_SIZE_CONFIRM) {
                            //incoming confirm msg
                            int soFar = dis.readInt();
                            int total = dis.readInt();

                            listener.onIncomingConfirmMsg(Peer.this, id, soFar, total);
                            removeItemFromChunkSendingTime(id, soFar);
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
