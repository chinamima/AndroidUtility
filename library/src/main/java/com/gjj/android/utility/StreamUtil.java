package com.gjj.android.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guojinjun on 2018/08/13.
 */
public class StreamUtil {

    public static String inputStreamToString(InputStream is) {
        StringBuffer sb = new StringBuffer();
        try {
            String line = "";
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "utf-8"));
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
