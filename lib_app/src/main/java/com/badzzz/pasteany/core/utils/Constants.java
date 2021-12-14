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
        public static final String TYPE_PING = "ping";
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
        public static final String AUTO_INCREAMENT_ID = "_id";

        public static final String TB_CONNECTED_DEVICES = "connected_devices";
        public static final String TB_MSGS = "msgs";
        public static final String TB_MSGS_SENDING = "msgs_sending";

        public static final int DEFAULT_QUERY_LIMIT = 50;

        public static final String SQL_CREATE_TABLE_DEVICES = String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s TEXT)", TB_CONNECTED_DEVICES, KEY.CONNECTED_DEVICES.DEVICE_ID, KEY.CONNECTED_DEVICES.DEVICE_NAME, KEY.CONNECTED_DEVICES.DEVICE_PLATFORM);
        public static final String SQL_CREATE_TABLE_MSGS = String.format("CREATE TABLE %s (%s INTEGER AUTOINCREMENT PRIMARY KEY, %s TEXT PRIMARY KEY, %s TEXT, %s TEXT,%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", TB_MSGS, AUTO_INCREAMENT_ID, KEY.MSGS.MSG_ID, KEY.MSGS.MSG_TYPE, KEY.MSGS.MSG_DATA, KEY.MSGS.MSG_FROM, KEY.MSGS.MSG_TO, KEY.MSGS.MSG_LEN, KEY.MSGS.MSG_STATE, KEY.MSGS.MSG_TIME);
        public static final String SQL_CREATE_TABLE_MSGS_SENDING = String.format("CREATE TABLE %s (%s INTEGER AUTOINCREMENT PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT)", TB_MSGS_SENDING, AUTO_INCREAMENT_ID, KEY.MSGS.MSG_ID, KEY.MSGS.MSG_FROM, KEY.MSGS.MSG_TO);

        public static final String SQL_QUERY_MSGS = "SELECT * FROM " + TB_MSGS + " WHERE " + AUTO_INCREAMENT_ID + " < %d ORDER BY " + AUTO_INCREAMENT_ID + " ASC LIMIT %d;";
        public static final String SQL_QUERY_DEVICE_RELATED_MSGS = "SELECT * FROM " + TB_MSGS + " WHERE " + AUTO_INCREAMENT_ID + " < %d AND ( " + KEY.MSGS.MSG_FROM + " == %s OR " + KEY.MSGS.MSG_TO + " LIKE %s ) ORDER BY " + AUTO_INCREAMENT_ID + " ASC LIMIT %d;";
        public static final String SQL_QUERY_MSG_DETAIL = "SELECT * FROM " + TB_MSGS + " WHERE " + AUTO_INCREAMENT_ID + " == %d";
        public static final String SQL_QUERY_ALL_SENDING_MSGS = "SELECT * FROM " + TB_MSGS_SENDING;

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
                public static final String MSG_TIME = "msg_time";
            }
        }

        public static final String MSG_TYPE_STATE_SENDING = "0";
        public static final String MSG_TYPE_STATE_SENDED = "1";
        public static final String MSG_TYPE_STATE_FAILED = "-1";

        public static final String MSG_CHAR_SPLIT = ",";
    }


    public static final class Others {
        public static final long TIMEOUT = 10 * 1000;
        public static final String APP_ROOT_DIR_NAME = "paste_any_where";

        public static final String BUGLY_APP_ID = "9a38940688";
    }

}
