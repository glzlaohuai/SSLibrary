package com.imob.lib.sslib.send.msg;

import com.imob.lib.sslib.send.exc.SenderPeerEOFException;
import com.imob.lib.sslib.send.exc.SenderPeerFetchBytesFailedException;
import com.imob.lib.sslib.utils.Logger;
import com.imob.lib.sslib.utils.SignUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileMsg implements IMsg {

    private File file;

    private RandomAccessFile randomAccessFile;

    public FileMsg(File file) {
        this.file = file;

        if (file != null && file.exists() && file.isFile()) {
            try {
                randomAccessFile = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException e) {
                Logger.e(e);
            }
        }
    }

    @Override
    public String id() {
        return SignUtils.md5(file.getAbsolutePath().hashCode() + String.valueOf(file.lastModified()));
    }

    @Override
    public void seekTo(long position) throws UnsupportedOperationException {
        try {
            randomAccessFile.seek(position);
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    @Override
    public byte[] readChunk() throws SenderPeerFetchBytesFailedException, SenderPeerEOFException {
        byte[] chunk = new byte[10240];

        try {
            int readed = randomAccessFile.read(chunk);
            if (readed == -1) {
                throw new SenderPeerEOFException();
            } else if (readed < chunk.length) {
                throw new SenderPeerEOFException(chunk, readed);
            } else {
                return chunk;
            }
        } catch (IOException e) {
            Logger.e(e);
            throw new SenderPeerFetchBytesFailedException(e);
        }
    }

    @Override
    public void close() {
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
    }

    @Override
    public boolean isValid() {
        return file.exists() && file.isFile() && file.canRead() && randomAccessFile != null;
    }

    @Override
    public byte getType() {
        return TYPE_FILE;
    }

    @Override
    public byte getUserDefiniedType() {
        return 0;
    }

    @Override
    public String getName() {
        return file.getName();
    }
}
