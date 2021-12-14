package com.badzzz.pasteany.core.utils;

import java.util.Arrays;

public class ArrayUtils {

    public static String[] createAndFill(int len, String value) {
        String[] array = new String[len];
        Arrays.fill(array, value);
        return array;
    }
}
