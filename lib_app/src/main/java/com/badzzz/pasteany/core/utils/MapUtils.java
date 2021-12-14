package com.badzzz.pasteany.core.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {


    public static Map<String, String> buildMap(String[] keys, String[] values) {
        if (keys == null || values == null || keys.length != values.length) {
            return null;
        }

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }
}
