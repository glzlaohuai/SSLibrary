package com.imob.lib.sslib.msg;

public class ConfirmMsg extends Msg {

    public static final int AVAILABLE_SIZE_CONFIRM = -0xff;

    @Override
    public byte getMsgType() {
        return Msg.TYPE_CONFIRM;
    }

    private int soFar;
    private int total;

    private ConfirmMsg(String id, int soFar, int total) {
        super(id, null);

        this.soFar = soFar;
        this.total = total;
    }

    public int getSoFar() {
        return soFar;
    }

    public int getTotal() {
        return total;
    }


    @Override
    public void cancel() {
        throw new UnsupportedOperationException("unable to cancel confirm msg");
    }

    public static ConfirmMsg build(String id, int soFar, int total) {
        return new ConfirmMsg(id, soFar, total);
    }

}
