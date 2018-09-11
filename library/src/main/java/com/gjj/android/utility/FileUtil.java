package com.gjj.android.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guojinjun on 2018/06/05.
 */
public class FileUtil {

    public static String readFile(String path) {
        List<String> list = readFile2List(path);
        if (list == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append("\n");
        }
        return sb.toString();
    }

    public static List<String> readFile2List(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        int sum = 0;
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(file.length());
            sum = lnr.getLineNumber();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (lnr != null) {
                try {
                    lnr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            List<String> list = new ArrayList<>(sum);
            String line = null;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] readFile2Byte(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] result = new byte[fis.available()];
            fis.read(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static boolean writeFile(String path, String content) {
        return writeFile(path, content, false);
    }

    public static boolean writeFile(String path, String content, boolean append) {
        try {
            return writeFile(path, content.getBytes("utf-8"), append);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
//        File file = new File(path);
//        BufferedWriter bw = null;
//        try {
//            if (file.exists()) {
//                if (!append) {
//                    file.delete();
//                }
//            } else {
//                file.getParentFile().mkdirs();
//            }
//            file.createNewFile();
//            bw = new BufferedWriter(new FileWriter(file, append));
//            bw.write(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            if (bw != null) {
//                try {
//                    bw.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return true;
    }

    public static boolean copyFile(String src, String dst) {
        String content = readFile(src);
        if (content == null) {
            return false;
        }
        return writeFile(dst, content);
    }

    public static boolean writeFile(String path, byte[] content) {
        return writeFile(path, content, false);
    }

    public static boolean writeFile(String path, byte[] content, boolean append) {
        if (content == null || content.length < 1) {
            return false;
        }
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            if (file.exists()) {
                if (!append) {
                    file.delete();
                }
            } else {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            fos = new FileOutputStream(file, append);
            fos.write(content);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }



    public static List<String> getAllFilePath(File root) {
        List<String> result = new LinkedList<>();
        if (root.isFile()) {
            result.add(root.getAbsolutePath());
        } else {
            File[] children = root.listFiles();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    List<String> list = getAllFilePath(children[i]);
                    result.addAll(list);
                }
            }
        }
        return result;
    }
}
