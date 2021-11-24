package com.badzzz.pasteany.core.api.response;

import com.badzzz.pasteany.core.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class APIResponserManager {
    private final static Map<String, IAPIResponser> map = new HashMap<>();


    static {
        map.put(Constants.PeerMsgAPI.PEER_DETAILS, new APIResponserDeviceInfo());
    }

    public static IAPIResponser getResponser(String api) {
        return map.get(api);
    }


}
