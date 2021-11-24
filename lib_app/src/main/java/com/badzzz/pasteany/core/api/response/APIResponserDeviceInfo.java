package com.badzzz.pasteany.core.api.response;

import com.badzzz.pasteany.core.api.MsgCreator;
import com.badzzz.pasteany.core.wrap.PlatformManagerHolder;
import com.imob.lib.sslib.peer.Peer;

public class APIResponserDeviceInfo implements IAPIResponser {

    @Override
    public void response(Peer peer, String id) {
        if (peer != null) {
            peer.sendMessage(MsgCreator.createAPIResponseMsg(id, PlatformManagerHolder.get().getAppManager().getDeviceInfoManager().getDeviceDetailInfo()));
        }
    }
    
}
