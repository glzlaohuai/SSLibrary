package com.imob.lib.sslib;

import com.imob.lib.sslib.send.exc.SenderPeerEOFException;
import com.imob.lib.sslib.send.exc.SenderPeerFetchBytesFailedException;
import com.imob.lib.sslib.send.msg.IMsg;
import com.imob.lib.sslib.utils.IOUtils;
import com.imob.lib.sslib.utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class Peer {

    public static final byte TYPE_CHUNK_HAS_NEXT = 0x0;
    public static final byte TYPE_CHUNK_READ_FINISHED_WITH_BYTES_DELIVERED = 0x1;
    public static final byte TYPE_CHUNK_READ_FINISHED_WITHOUT_BYTES_DELIVERED = 0x2;
    public static final byte TYPE_CHUNK_READ_PROCESS_ERROR_OCCURED = 0x3;

    private Socket socket;

    private DataInputStream dis;
    private DataOutputStream dos;

    private OnPeerListener listener;

    private ExecutorService connectAndReadService = Executors.newSingleThreadExecutor();
    private ExecutorService sendService = Executors.newSingleThreadExecutor();

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public interface OnPeerListener {
        void onConnected(String ip, int port);

        void onConnectFailed(String msg, Exception e);

        void onConnectCorrupted(String msg, Exception e);

        void onConnectDestroyed();

        void onSendMessageToQueue(IMsg msg);

        void onSendMessageStarted(IMsg msg);

        void onSendMessageFailed(IMsg msg, String error, Exception e);

        void onSendMessageChunk(IMsg msg, byte chunkType, int chunkLen);

        void onSendMessageCompleted(IMsg msg);

        void onIncomingMessage(String msgID, byte msgType, byte userDefiniedType);

        void onIncomingMessageChunk(String msgID, byte msgType, byte userDefiniedType, byte[] bytes);

        void onIncomingMessageCompleted(String msgID, byte msgType, byte userDefiniedType);

        void onIncomingMessageInteruppted(String msgID, byte msgType, byte userDefiniedType);


    }

    private void writeSegment(IMsg msg, byte type, byte[] bytes, DataOutputStream dos) throws IOException {
        dos.writeByte(type);
        if (bytes != null && bytes.length > 0) {
            dos.writeInt(bytes.length);
        }
        if (bytes != null) {
            dos.write(bytes);
        }

        listener.onSendMessageChunk(msg, type, bytes != null ? bytes.length : 0);
    }


    public void send(IMsg msg) {
        if (msg.isValid()) {
            listener.onSendMessageToQueue(msg);
            sendService.execute(new Runnable() {
                @Override
                public void run() {
                    listener.onSendMessageStarted(msg);

                    if (dos == null) {
                        listener.onSendMessageFailed(msg, "output stream is already closed", null);
                    } else {
                        try {
                            //msgType
                            dos.writeByte(msg.getType());
                            dos.writeByte(msg.getUserDefiniedType());
                            dos.writeUTF(msg.id());

                            dos.flush();

                            boolean hitEnd = false;
                            Exception fetchFailedException = null;
                            while (!hitEnd) {

                                //next: readChunkHasNext、readChunkFinishedWithBytes、readChunkFinishedWithoutBytes、readProcessErrorOccured
                                byte readChunkResult;
                                byte[] bytes = null;

                                try {
                                    bytes = msg.readChunk();
                                    if (bytes != null && bytes.length > 0) {
                                        readChunkResult = TYPE_CHUNK_HAS_NEXT;
                                    } else {
                                        readChunkResult = TYPE_CHUNK_READ_FINISHED_WITHOUT_BYTES_DELIVERED;
                                    }
                                } catch (SenderPeerFetchBytesFailedException e) {
                                    fetchFailedException = e;
                                    Logger.e(e);

                                    readChunkResult = TYPE_CHUNK_READ_PROCESS_ERROR_OCCURED;
                                } catch (SenderPeerEOFException e) {
                                    Logger.e(e);

                                    if (e == null || e.getTailBytes() == null || e.getSize() <= 0) {
                                        readChunkResult = TYPE_CHUNK_READ_FINISHED_WITHOUT_BYTES_DELIVERED;
                                    } else {
                                        readChunkResult = TYPE_CHUNK_READ_FINISHED_WITH_BYTES_DELIVERED;
                                        bytes = Arrays.copyOf(e.getTailBytes(), e.getSize());
                                    }
                                }

                                writeSegment(msg, readChunkResult, bytes, dos);
                                dos.flush();

                                switch (readChunkResult) {
                                    case TYPE_CHUNK_READ_FINISHED_WITHOUT_BYTES_DELIVERED:
                                    case TYPE_CHUNK_READ_FINISHED_WITH_BYTES_DELIVERED:
                                        hitEnd = true;
                                        listener.onSendMessageCompleted(msg);
                                        break;

                                    case TYPE_CHUNK_READ_PROCESS_ERROR_OCCURED:
                                        hitEnd = true;
                                        listener.onSendMessageFailed(msg, "error occured fetch bytes from sender side", fetchFailedException);
                                        break;
                                }
                            }
                        } catch (IOException e) {
                            Logger.e(e);
                            listener.onSendMessageFailed(msg, "io exception occured", e);
                        }
                    }
                }
            });
        }
    }

    public void destroy() {
        cleanup();
        listener.onConnectDestroyed();
    }

    private void cleanup() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                Logger.e(e);
            }
            IOUtils.close(dis);
            IOUtils.close(dos);
        }
    }

    public Peer(@NonNull Socket socket, @NonNull OnPeerListener listener) {
        this.socket = socket;
        this.listener = listener;

        connectAndReadService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    Logger.e(e);
                    listener.onConnectFailed("error occured during io stream opening process", e);
                    cleanup();
                }

                if (dis != null && dos != null) {
                    listener.onConnected(socket.getRemoteSocketAddress().toString(), socket.getLocalPort());

                    startMonitorIncomingMsg();
                }
            }
        });
    }


    private void readIncomingMsg(byte msgType, byte userDefiniedType, String msgID) throws IOException {

        boolean readCompleted = false;
        boolean readInterupted = false;

        while (!readCompleted && !readInterupted) {
            byte chunkType = dis.readByte();
            byte[] chunkBytes;
            int len;

            switch (chunkType) {
                case TYPE_CHUNK_HAS_NEXT:
                case TYPE_CHUNK_READ_FINISHED_WITH_BYTES_DELIVERED:
                    len = dis.readInt();
                    chunkBytes = new byte[len];
                    dis.readFully(chunkBytes);
                    listener.onIncomingMessageChunk(msgID, msgType, userDefiniedType, chunkBytes);
                    if (chunkType == TYPE_CHUNK_READ_FINISHED_WITH_BYTES_DELIVERED) {
                        readCompleted = true;
                    }
                    break;
                case TYPE_CHUNK_READ_FINISHED_WITHOUT_BYTES_DELIVERED:
                    readCompleted = true;
                    break;
                case TYPE_CHUNK_READ_PROCESS_ERROR_OCCURED:
                    readInterupted = true;
                    break;
            }

            if (readCompleted) {
                listener.onIncomingMessageCompleted(msgID, msgType, userDefiniedType);
            } else if (readInterupted) {
                listener.onIncomingMessageInteruppted(msgID, msgType, userDefiniedType);
            }
        }
    }

    private void startMonitorIncomingMsg() {
        try {
            while (true) {

                byte msgType = dis.readByte();
                byte userDefiniedType = dis.readByte();
                String msgID = dis.readUTF();

                listener.onIncomingMessage(msgID, msgType, userDefiniedType);

                readIncomingMsg(msgType, userDefiniedType, msgID);
            }
        } catch (IOException e) {
            Logger.e(e);
            listener.onConnectCorrupted("error occured during incoming bytes monitor process", e);
        } finally {
            cleanup();
        }
    }
}
