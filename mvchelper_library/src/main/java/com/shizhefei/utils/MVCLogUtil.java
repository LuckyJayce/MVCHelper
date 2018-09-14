package com.shizhefei.utils;

import android.util.Log;

/**
 * Created by luckyjayce on 2017/7/21.
 */

public class MVCLogUtil {
    public static boolean debug = true;
    private static final String matchText = "{}";
    private static final String TAG = "MVCHelper";

    public static void d(String text, Object... args) {
        if (debug) {
            StringBuilder stringBuilder = replace(text, args);
            Log.d(TAG, stringBuilder.toString());
        }
    }

    private static StringBuilder replace(String text, Object... args) {
        StringBuilder stringBuilder = new StringBuilder(text);
        int index = 0;
        int fix = 0;
        for (Object arg : args) {
            index = text.indexOf(matchText, index);
            if (index >= 0) {
                String argString = "【 " + arg + " 】";
                int replaceIndex = index + fix;
                stringBuilder.replace(replaceIndex, replaceIndex + matchText.length(), argString);
                index += matchText.length();
                fix += argString.length() - matchText.length();
            }
        }
        return stringBuilder;
    }

    public static void e(Throwable throwable, String text, Object... args) {
        if (debug) {
            StringBuilder stringBuilder = replace(text, args);
            if (throwable == null) {
                Log.e(TAG, stringBuilder.toString());
            } else {
                Log.e(TAG, stringBuilder.toString(), throwable);
            }
        }
    }

    public static void e(String text, Object... args) {
        if (debug) {
            StringBuilder stringBuilder = replace(text, args);
            Log.e(TAG, stringBuilder.toString());
        }
    }

    public static void d(Throwable throwable, String text, Object... args) {
        if (debug) {
            StringBuilder stringBuilder = replace(text, args);
            if (throwable == null) {
                Log.d(TAG, stringBuilder.toString());
            } else {
                Log.d(TAG, stringBuilder.toString(), throwable);
            }
        }
    }
}
