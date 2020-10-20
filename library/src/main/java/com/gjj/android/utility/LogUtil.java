package com.gjj.android.utility;

import android.util.Log;

/**
 * Created by guojinjun on 2018/07/03.
 */
public class LogUtil {

    public static String sTag = "gjj";

    private static final int SIZE_LOG = 3500;

    private static int sLevel = Log.VERBOSE;

    public static void setLevel(int lv) {
        sLevel = lv;
    }

    public static void log(int level, String format, Object... args) {
        if (sLevel > level){
            return;
        }
        String str = String.format(format, args);
        int index = 0;
        for (; index + SIZE_LOG < str.length(); index += SIZE_LOG) {
            log(level, str.substring(index, index + SIZE_LOG));
        }
        log(level, str.substring(index));
    }

    protected static void log(int level, String str) {
        switch (level) {
            case Log.ERROR:
                Log.e(sTag, str);
                break;
            case Log.WARN:
                Log.w(sTag, str);
                break;
            case Log.INFO:
                Log.i(sTag, str);
                break;
            case Log.DEBUG:
                Log.d(sTag, str);
                break;
            case Log.VERBOSE:
                Log.v(sTag, str);
                break;
            default:
                Log.wtf(sTag, str);
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
        Log.e(sTag, String.format(format, args), throwable);
    }

    public static void callStack(String msg) {
        Log.w(sTag, msg, new Throwable("===== callStack"));
    }
}
