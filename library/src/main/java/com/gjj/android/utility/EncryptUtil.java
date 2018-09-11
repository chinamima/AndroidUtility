package com.gjj.android.utility;

import java.security.MessageDigest;

/**
 * Created by guojinjun on 2018/08/21.
 */
public class EncryptUtil {

    public static String calcMD5(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            return ByteUtil.byte2Str(instance.digest());
        } catch (Exception e) {
            LogUtil.e(e, "calcMD5 failed! input: %s", str);
        }
        return null;
    }
}
