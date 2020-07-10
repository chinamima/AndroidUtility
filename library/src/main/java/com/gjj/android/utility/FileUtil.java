package com.gjj.android.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guojinjun on 2018/06/05.
 */
public class FileUtil {

    private static final int IO_BUFFER_SIZE = 4096;

    public static String cleanPath(String absPath) {
        if (StringUtil.isEmpty(absPath)) {
            return absPath;
        }
        try {
            File file = new File(absPath);
            absPath = file.getCanonicalPath();
        } catch (Exception e) {

        }
        return absPath;
    }

    public static boolean copy(String srcPath, String dstPath) {
        return copy(srcPath, dstPath, false);
    }

    public static boolean copy(String srcPath, String dstPath, boolean force) {
        if (StringUtil.isEmpty(srcPath) || StringUtil.isEmpty(dstPath)) {
            return false;
        }

        // check if copy source equals destination
        if (srcPath.equals(dstPath)) {
            return true;
        }

        // check if source file exists or is a directory
        if (!exists(srcPath) || !isFile(srcPath)) {
            return false;
        }

        // delete old content
        if (exists(dstPath)) {
            if (!force) {
                return false;
            } else {
                delete(dstPath);
            }
        }
        if (!create(dstPath)) {
            return false;
        }

        FileInputStream in = null;
        FileOutputStream out = null;

        // get streams
        try {
            in = new FileInputStream(srcPath);
            out = new FileOutputStream(dstPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return copy(in, out);
    }

    public static boolean copy(InputStream is, OutputStream os) {
        if (is == null || os == null) {
            return false;
        }

        try {
            byte[] buffer = new byte[IO_BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (Exception ignore) {
            }
            try {
                os.close();
            } catch (Exception ignore) {

            }
        }
        return true;
    }

    public static boolean copyFile(String src, String dst) {
        String content = readFile(src);
        if (content == null) {
            return false;
        }
        return writeFile(dst, content);
    }

    public static boolean create(String absPath) {
        return create(absPath, false);
    }

    public static boolean create(String absPath, boolean force) {
        if (StringUtil.isEmpty(absPath)) {
            return false;
        }

        if (exists(absPath)) {
            return true;
        }

        String parentPath = getParent(absPath);
        mkdirs(parentPath, force);

        try {
            File file = new File(absPath);
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(String absPath) {
        if (StringUtil.isEmpty(absPath)) {
            return false;
        }

        File file = new File(absPath);
        return delete(file);
    }

    public static boolean delete(File file) {
        if (!exists(file)) {
            return true;
        }

        if (file.isFile()) {
            return file.delete();
        }

        boolean result = true;
        File files[] = file.listFiles();
        for (int index = 0; index < files.length; index++) {
            result |= delete(files[index]);
        }
        result |= file.delete();

        return result;
    }

    public static boolean exists(String absPath) {
        if (StringUtil.isEmpty(absPath)) {
            return false;
        }
        File file = new File(absPath);
        return exists(file);
    }

    public static boolean exists(File file) {
        return file == null ? false : file.exists();
    }



    public static String[] listChildren(File dir, FilenameFilter filter) {
        if (dir == null) {
            return null;
        }
        if (filter == null) {
            return dir.list();
        } else {
            return dir.list(filter);
        }
    }

    public static String[] listChildren(File dir) {
        return listChildren(dir, null);
    }

    public static String[] listChildren(String strDir, FilenameFilter filter) {
        if (strDir == null) {
            return null;
        }
        return listChildren(new File(strDir), filter);
    }

    public static String[] listChildren(String strDir) {
        return listChildren(strDir, null);
    }

    public static String[] listChildrenRegex(String strDir, String regex) {
        final Pattern pattern = Pattern.compile(regex);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Matcher matcher = pattern.matcher(name);
                if (matcher.find()) {
                    return true;
                }
                return false;
            }
        };
        return listChildren(strDir, filter);
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

    public static String getParent(File file) {
        return file == null ? null : file.getParent();
    }

    public static String getParent(String absPath) {
        if (StringUtil.isEmpty(absPath)) {
            return null;
        }
        absPath = cleanPath(absPath);
        File file = new File(absPath);
        return getParent(file);
    }

    public static boolean isFile(String absPath) {
        boolean exists = exists(absPath);
        if (!exists) {
            return false;
        }

        File file = new File(absPath);
        return isFile(file);
    }

    public static boolean isFile(File file) {
        return file != null && file.isFile();
    }

    public static boolean isFolder(String absPath) {
        boolean exists = exists(absPath);
        if (!exists) {
            return false;
        }

        File file = new File(absPath);
        return file.isDirectory();
    }

    public static boolean mkdirs(String absPath) {
        return mkdirs(absPath, false);
    }

    public static boolean mkdirs(String absPath, boolean force) {
        File file = new File(absPath);
        if (exists(absPath) && !isFolder(absPath)) {
            if (!force) {
                return false;
            } else {
                delete(file);
            }
        }
        try {
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists(file);
    }

    public static boolean move(String srcPath, String dstPath, boolean force) {
        if (StringUtil.isEmpty(srcPath) || StringUtil.isEmpty(dstPath)) {
            return false;
        }

        if (!exists(srcPath)) {
            return false;
        }

        if (exists(dstPath)) {
            if (!force) {
                return false;
            } else {
                delete(dstPath);
            }
        }

        try {
            File srcFile = new File(srcPath);
            File dstFile = new File(dstPath);
            return srcFile.renameTo(dstFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

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

    public static boolean writeFile(String path, byte[] content) {
        return writeFile(path, content, false);
    }

    public static boolean writeFile(String path, List<String> listContent) {
        if (listContent == null || listContent.isEmpty()) {
            return false;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < listContent.size(); i++) {
            sb.append(listContent.get(i)).append("\n");
        }
        return writeFile(path, sb.toString());
    }
}
