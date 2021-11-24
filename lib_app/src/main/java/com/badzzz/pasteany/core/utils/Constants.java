package com.badzzz.pasteany.core.utils;

public class Constants {

    public final static class Platforms {
        public static final String MAC = "macos";
        public static final String WIN = "windows";
        public static final String ANDROID = "android";
        public static final String UNKNOWN = "unknown";
    }

    public static final class PeerMsgKey {
        public static final String id = "id";
        public static final String type = "type";
        public static final String data = "data";
        public static final String device = "device";
    }

    public static final class PeerMsgType {
        public static final String TYPE_FILE = "file";
        public static final String TYPE_STR = "string";
        public static final String TYPE_API_REQUEST = "api_request";
        public static final String TYPE_API_RESPONSE = "api_response";
    }

    public static final class PeerMsgAPI {
        public static final String PEER_DETAILS = "peerDetails";
    }

    public static final class NSD {
        public static final String NSD_SERVICE_NAME_DEFAULT = "hi_paste_anywhere";
        public static final String NSD_SERVICE_TYPE = "_pasteanywhere._tcp.local.";
        public static final String NSD_HOST_NAME = "badzzz.com";

        public static final class Key {
            public static final String DEVICE_ID = Device.KEY_DEVICEID;
            public static final String DEVICE_NAME = Device.KEY_DEVICE_NAME;
            public static final String SERVICE_NAME = "service_name";
        }
    }

    public static final class Preference {
        public static final String FILE_NAME = "pasteany";

        public static final String KEY_DEVICEID = Device.KEY_DEVICEID;
        public static final String KEY_DEVICE_NAME = Device.KEY_DEVICE_NAME;
        public static final String KEY_SERVICE_NAME = NSD.Key.SERVICE_NAME;
    }

    public static final class Device {
        public static final String KEY_DEVICEID = "device_id";
        public static final String KEY_DEVICE_NAME = "device_name";
        public static final String KEY_PLATFORM = "platform";
    }


    public static final class Others {
        public static final long TIMEOUT = 10 * 1000;
    }

}
