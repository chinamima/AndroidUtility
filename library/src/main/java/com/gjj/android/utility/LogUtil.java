package com.gjj.android.utility;

import android.util.Log;

/**
 * Created by guojinjun on 2018/07/03.
 */
public class LogUtil {

    private static final int SIZE_LOG = 3500;

    public static void log(int level, String format, Object... args) {
        String str = String.format(format, args);
        int index = 0;
        for (; index + SIZE_LOG < str.length(); index += SIZE_LOG) {
            log(level, str.substring(index, index + SIZE_LOG));
        }
        log(level, str.substring(index));
    }

    protected static void log(int level, String str) {
        switch (level) {
        case 0:
            Log.wtf(Global.TAG, str);
            break;
        case 1:
            Log.e(Global.TAG, str);
            break;
        case 2:
            Log.w(Global.TAG, str);
            break;
        case 3:
            Log.i(Global.TAG, str);
            break;
        case 4:
            Log.d(Global.TAG, str);
            break;
        case 5:
            Log.v(Global.TAG, str);
            break;
        }
    }

    public static void v(String format, Object... args) {
        log(5, format, args);
    }

    public static void d(String format, Object... args) {
        log(4, format, args);
    }

    public static void i(String format, Object... args) {
        log(3, format, args);
    }

    public static void w(String format, Object... args) {
        log(2, format, args);
    }

    public static void e(String format, Object... args) {
        log(1, format, args);
    }

    public static void e(Throwable throwable, String format, Object... args) {
        Log.e(Global.TAG, String.format(format, args), throwable);
    }

    public static void callStack(String msg) {
        Log.w(Global.TAG, msg, new Throwable("===== callStack"));
    }
}
