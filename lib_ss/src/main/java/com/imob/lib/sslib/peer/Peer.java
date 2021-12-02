package com.imob.lib.sslib.peer;

import com.imob.lib.lib_common.Closer;
import com.imob.lib.lib_common.Logger;
import com.imob.lib.sslib.INode;
import com.imob.lib.sslib.msg.Chunk;
import com.imob.lib.sslib.msg.ConfirmMsg;
import com.imob.lib.sslib.msg.Msg;
import com.imob.lib.sslib.msg.MsgQueue;
import com.imob.lib.sslib.utils.SSThreadFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {

    private static final String S_TAG = "Peer";

    public static final int CHUNK_BYTE_LEN = 1024 * 512;

    private static final String MSG_SEND_ERROR_PEER_IS_DESTROIED = "send failed, peer is already destroied";
    private static final String MSG_SEND_ERROR_NO_AVAILABLE_BYTES_INPUT = "no available bytes found";
    private static final String MSG_SEND_ERROR_READ_CHUNK_FROM_INPUT = "error occured while reading byte chunk from input stream";
    private static final String MSG_SEND_ERROR_CANCELED = "send failed, msg send was canceled during sending progress";
    private static final String MSG_SEND_ERROR_EOF = "send failed, eof occured";
    private static final String MSG_SEND_ERROR_CONNECTION_LOST = "connection lost";

    private static final String ERROR_MSG_CHUNK_READ_FAILED_EOF = "read msg chunk failed due to peer send error occured, eof";
    private static final String ERROR_MSG_CHUNK_READ_FAILED_CANCELED = "read msg chunk failed due to peer send error occured, canceled";
    private static final String ERROR_MSG_CHUNK_READ_FAILED_IO = "read msg chunk failed due to peer send error occured, io error";

    private static PeerListenerGroup globalPeerListener = new PeerListenerGroup();
    private static PeerListenerGroup monitoredListener = new PeerListenerGroup();

    private Socket socket;
    private PeerListenerGroup listener = new PeerListenerGroup();

    private DataInputStream dis;
    private DataOutputStream dos;

    private MsgQueue msgQueue = new MsgQueue();
    private Set<IncomingMsgInfo> inProcessingIncomingMsgSet = new HashSet<>();

    private boolean isDestroyed = false;

    private ExecutorService msgSendService = Executors.newSingleThreadExecutor(SSThreadFactory.build("peer-send"));
    private ExecutorService monitorIncomingMsgService = Executors.newSingleThreadExecutor(SSThreadFactory.build("peer-moni"));
    private ExecutorService msgInQueueService = Executors.newSingleThreadExecutor(SSThreadFactory.build("peer-queue"));
    private ExecutorService timeoutCheckService = Executors.newSingleThreadExecutor(SSThreadFactory.build("peer-time"));

    private Byte timeoutLock = 0x0;

    private boolean destroyCallbacked = false;
    private boolean corruptedCallbacked = false;
    private boolean isTimeoutCheckRunning = false;

    private Map<String, Long> chunkSendingTime = new HashMap<>();
    private INode localNode;
    private String tag;
    private long timeout;

    class IncomingMsgInfo {
        private String msgID;
        private int soFar;
        private int total;

        public IncomingMsgInfo(String msgID, int soFar, int total) {
            this.msgID = msgID;
            this.soFar = soFar;
            this.total = total;
        }

        public void setSoFar(int soFar) {
            this.soFar = soFar;
        }


        public String getMsgID() {
            return msgID;
        }

        public int getSoFar() {
            return soFar;
        }

        public int getTotal() {
            return total;
        }
    }


    /**
     * @param tag a none empty string, or else no effects
     */
    public void setTag(String tag) {
        if (tag == null || tag.isEmpty()) return;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static PeerListenerGroup getGlobalPeerListener() {
        return globalPeerListener;
    }

    public static void setGlobalPeerListener(PeerListener globalPeerListener) {
        Peer.globalPeerListener.clear();
        Peer.globalPeerListener.add(globalPeerListener);
    }

    public static void clearGlobalPeerListener() {
        Peer.globalPeerListener.clear();
    }

    public static void monitorPeerState(PeerListener listener) {
        monitoredListener.add(listener);
    }

    public static void unmonitorPeerState(PeerListener listener) {
        monitoredListener.remove(listener);
    }

    public Peer(Socket socket, INode localNode, PeerListener listener) {
        this.socket = socket;
        this.listener.add(listener);
        this.listener.add(globalPeerListener);
        this.listener.add(monitoredListener);

        this.localNode = localNode;

        init();

        tag = S_TAG + " - " + (localNode.isServerNode() ? "server" : "client") + ", remote: " + socket.getRemoteSocketAddress() + ", local: " + socket.getLocalSocketAddress();
    }


    public synchronized void registerListener(PeerListener peerListener) {
        this.listener.add(peerListener);
    }

    public synchronized void unregisterListener(PeerListener peerListener) {
        this.listener.remove(peerListener);
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

    public synchronized void destroy() {
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


    private void clearAllProcessingIncomingMsgSetAndCallbackFailed() {
        for (IncomingMsgInfo msgInfo : inProcessingIncomingMsgSet) {
            listener.onIncomingMsgReadFailed(this, msgInfo.getMsgID(), msgInfo.getTotal(), msgInfo.getSoFar());
        }
        inProcessingIncomingMsgSet.clear();
    }

    private void clearAllNonePendingMsgAndCallbackFailed() {
        while (!msgQueue.isEmpty()) {
            Msg msg = msgQueue.poll();

            callbackMsgSendStart(msg);
            callbackMsgSendFailed(msg, MSG_SEND_ERROR_PEER_IS_DESTROIED, null);

            msg.destroy(this);
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
        if (!isDestroyed()) {
            listener.onTimeoutOccured(this);
        }
        destroy();
    }

    private void kickOffTimeoutCheck() {
        while (!isDestroyed()) {
            synchronized (timeoutLock) {
                if (chunkSendingTime.isEmpty()) {
                    try {
                        timeoutLock.wait();
                        Logger.i(tag, "msg chunk sending msg map is empty now, just wait until it's not empty to resume the timeout check process.");
                        continue;
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

    private synchronized void doDestroyStuff() {
        closeIODropConnection();
        clearAllNonePendingMsgAndCallbackFailed();
        clearAllProcessingIncomingMsgSetAndCallbackFailed();
        stopAllExecutorService();
    }


    private void stopAllExecutorService() {
        msgSendService.shutdown();
        monitorIncomingMsgService.shutdown();
        msgInQueueService.shutdown();
        timeoutCheckService.shutdown();
    }


    private void closeIODropConnection() {
        Closer.close(dis);
        Closer.close(dos);

        Closer.close(socket);
    }

    public synchronized void sendMessage(final Msg msg) {
        if (msg == null) return;
        if (isDestroyed) {
            callbackMsgSendFailed(msg, MSG_SEND_ERROR_PEER_IS_DESTROIED, null);
            return;
        }

        msg.addPeerHolder(this);
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
            msg.destroy(this);
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
            int msgType = msg.getMsgType();

            if (available <= 0 && msgType == Msg.TYPE_NORMAL) {
                callbackMsgSendFailed(msg, MSG_SEND_ERROR_NO_AVAILABLE_BYTES_INPUT, null);
                msg.destroy(this);
                continue;
            }

            try {
                //add a prefix, so peer can check if is a valid msgID after readed from io stream
                dos.writeUTF(Msg.MSG_ID_PREFIX + msg.getId());
                dos.writeInt(msg.getMsgType());

                switch (msgType) {
                    case Msg.TYPE_NORMAL:
                        dos.writeInt(available);

                        int readed = 0;
                        int round = 1;

                        while (readed < available) {
                            Chunk chunk = msg.readChunk(bytes);
                            int state = chunk.getState();
                            dos.writeInt(state);
                            Logger.i(tag, "write normal msg to dos, round: " + round + ", state: " + state + " , chunkSize: " + chunk.getSize() + ", total: " + available + ", e: " + chunk.getException());

                            switch (state) {
                                case Chunk.STATE_OK:
                                    readed += chunk.getSize();
                                    dos.writeInt(chunk.getSize());
                                    dos.write(chunk.getBytes(), 0, chunk.getSize());

                                    listener.onMsgChunkSendSucceeded(Peer.this, msg.getId(), chunk.getSize());

                                    //保存chunk发出的时间
                                    synchronized (timeoutLock) {
                                        addItemToChunkSendingTime(msg.getId(), readed);
                                        timeoutLock.notify();
                                    }
                                    break;
                                //should never happen
                                case Chunk.STATE_EOF:
                                    callbackMsgSendFailed(msg, MSG_SEND_ERROR_EOF, chunk.getException());
                                    break;
                                case Chunk.STATE_CANCELED:
                                    callbackMsgSendFailed(msg, MSG_SEND_ERROR_CANCELED, chunk.getException());
                                    break;
                                case Chunk.STATE_ERROR:
                                    callbackMsgSendFailed(msg, MSG_SEND_ERROR_READ_CHUNK_FROM_INPUT, chunk.getException());
                                    break;
                            }
                            round++;
                            if (state != Chunk.STATE_OK) {
                                break;
                            }
                        }
                        break;
                    case Msg.TYPE_CONFIRM:
                        Logger.i(S_TAG, "write confirmMsg to dos, " + msg.getId());
                        dos.writeInt(((ConfirmMsg) msg).getSoFar());
                        dos.writeInt(((ConfirmMsg) msg).getTotal());
                        break;
                }

                callbackMsgSendSucceeded(msg);
            } catch (IOException e) {
                Logger.e(e);
                //send msg failed, connection lost
                destroy();
                callbackMsgSendFailed(msg, MSG_SEND_ERROR_CONNECTION_LOST, e);
                callbackCorrupted("corrupted due to exception occured while sending msg to peer", e);

            } finally {
                msg.destroy(this);
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
                        int msgType = dis.readInt();

                        if (id == null || id.equals("") || !id.startsWith(Msg.MSG_ID_PREFIX)) {
                            throw new IOException("read msgID from stream failed, something went wrong, io connection might already corrupted");
                        }

                        id = id.substring(Msg.MSG_ID_PREFIX.length());

                        switch (msgType) {
                            case Msg.TYPE_NORMAL:
                                int available = dis.readInt();
                                if (available <= 0) {
                                    throwIOException("got a unexpected available value from peer, connection corrupted.");
                                }

                                listener.onIncomingMsg(Peer.this, id, available);
                                int readed = 0;
                                IncomingMsgInfo msgInfo = new IncomingMsgInfo(id, readed, available);
                                inProcessingIncomingMsgSet.add(msgInfo);

                                while (readed < available) {
                                    int state = dis.readInt();

                                    switch (state) {
                                        case Chunk.STATE_CANCELED:
                                            listener.onIncomingMsgChunkReadFailed(Peer.this, id, ERROR_MSG_CHUNK_READ_FAILED_CANCELED);
                                            break;
                                        case Chunk.STATE_EOF:
                                            listener.onIncomingMsgChunkReadFailed(Peer.this, id, ERROR_MSG_CHUNK_READ_FAILED_EOF);
                                            break;
                                        case Chunk.STATE_ERROR:
                                            listener.onIncomingMsgChunkReadFailed(Peer.this, id, ERROR_MSG_CHUNK_READ_FAILED_IO);
                                            break;
                                        case Chunk.STATE_OK:
                                            int size = dis.readInt();
                                            if (size <= 0) {
                                                throw new IOException("got a unexpected chunk size from peer, connection corrupted.");
                                            }
                                            readed += size;
                                            dis.readFully(buffer, 0, size);
                                            listener.onIncomingMsgChunkReadSucceeded(Peer.this, id, size, readed, available, buffer);
                                            //send confirm(ack) msg
                                            sendMessage(ConfirmMsg.build(id, readed, available));
                                            break;
                                        default:
                                            throw new IOException("got a unexpected chunk state from peer, connection coruppted");
                                    }
                                }

                                if (readed == available) {
                                    listener.onIncomingMsgReadSucceeded(Peer.this, id);
                                } else {
                                    listener.onIncomingMsgReadFailed(Peer.this, id, available, readed);
                                }
                                inProcessingIncomingMsgSet.remove(msgInfo);

                                break;
                            case Msg.TYPE_CONFIRM:
                                //incoming confirm msg
                                int soFar = dis.readInt();
                                int total = dis.readInt();

                                if (soFar <= 0 || total <= 0) {
                                    throwIOException("got unexpected confirm soFar or total value from peer, connection corrupted.");
                                }

                                listener.onIncomingConfirmMsg(Peer.this, id, soFar, total);
                                synchronized (timeoutLock) {
                                    removeItemFromChunkSendingTime(id, soFar);
                                    timeoutLock.notify();
                                }
                                break;

                            default:
                                throwIOException("got a unexpected msg type from peer, connection corrupted.");
                                break;
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


    private void throwIOException(String errorMsg) throws IOException {
        throw new IOException(errorMsg);
    }


    public Socket getSocket() {
        return socket;
    }


    @Override
    public String toString() {
        return String.format("Peer{%s, node: %s}", getTag(), getLocalNode() == null ? "null" : getLocalNode().toString());
    }
}
