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
            public static final String SERVICE_NAME = "s_name";
        }
    }

    public static final class Preference {
        public static final String FILE_NAME = "paste_any_where";

        public static final String KEY_DEVICEID = Device.KEY_DEVICEID;
        public static final String KEY_DEVICE_NAME = Device.KEY_DEVICE_NAME;
        public static final String KEY_SERVICE_NAME = NSD.Key.SERVICE_NAME;
    }

    public static final class Device {
        public static final String KEY_DEVICEID = "d_id";
        public static final String KEY_DEVICE_NAME = "d_name";
        public static final String KEY_PLATFORM = "platform";
    }


    public final static class DB {
        public static final String DB_NAME = "paste_any_where";

        public static final String TB_CONNECTED_DEVICES = "connected_devices";
        public static final String TB_MSGS = "msgs";

        public static final class KEY {
            public static final class CONNECTED_DEVICES {
                public static final String DEVICE_ID = Device.KEY_DEVICEID;
                public static final String DEVICE_NAME = Device.KEY_DEVICE_NAME;
                public static final String DEVICE_PLATFORM = Device.KEY_PLATFORM;
            }

            public static final class MSGS {
                public static final String MSG_ID = "msg_id";
                public static final String MSG_TYPE = "msg_type";
                public static final String MSG_DATA = "msg_data";
                public static final String MSG_FROM = "msg_from";
                public static final String MSG_TO = "msg_to";
                public static final String MSG_LEN = "msg_len";
                public static final String MSG_STATE = "msg_state";
                public static final String MSG_TIME_RECEIVE = "msg_time_receive";
                public static final String MSG_TIME_SEND = "msg_time_send";
            }
        }
    }


    public static final class Others {
        public static final long TIMEOUT = 10 * 1000;
        public static final String APP_ROOT_DIR_NAME = "paste_any_where";
    }

}
