package com.imob.lib.sslib.msg;

import java.io.IOException;
import java.io.InputStream;

public class ConfirmMsg extends Msg {

    public static final int AVAILABLE_SIZE_CONFIRM = -0xff;

    private static class ConfirmInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            return 0;
        }

        @Override
        public int available() throws IOException {
            return AVAILABLE_SIZE_CONFIRM;
        }
    }

    private final static ConfirmInputStream CONFIRM_INPUT_STREAM = new ConfirmInputStream();

    private int soFar;
    private int total;

    private ConfirmMsg(String id, int soFar, int total) {
        super(id, CONFIRM_INPUT_STREAM);

        this.soFar = soFar;
        this.total = total;
    }

    public int getSoFar() {
        return soFar;
    }

    public int getTotal() {
        return total;
    }


    public static ConfirmMsg build(String id, int soFar, int total) {
        return new ConfirmMsg(id, soFar, total);
    }

}
