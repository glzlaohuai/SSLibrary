package com.badzzz.pasteany.core.utils;

public class Constants {

    public final static class Platforms {
        public static final String MAC = "macos";
        public static final String WIN = "windows";
        public static final String ANDROID = "android";
        public static final String UNKNOWN = "unknown";
    }

    public static final class MsgKeys {
        public static final String id = "id";
        public static final String type = "type";
        public static final String api = "api";
    }


    public static final class APIS {
        public static final String PEER_DETAILS = "peerDetails";
    }


    public static final class NSD {
        public static final String NSD_SERVICE_NAME_DEFAULT = "hi_paste_anywhere";
        private static final String NSD_SERVICE_TYPE = "_pasteanywhere._tcp.local.";
    }


    public static final class Others {
        public static final long TIMEOUT = 10 * 1000;
    }

}
