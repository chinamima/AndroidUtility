package com.gjj.android.utility;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by guojinjun on 2018/06/27.
 */
public class PrintObject {

    public static String sTag = "gjj";

    public static void field(Object obj) {
        field(obj, "");
    }

    public static void field(Object obj, String name) {
        field(obj, name, 1, 1);
    }

    /**
     * @param breadth 广度，即解析几层的成员变量
     * @param depth 深度，即几层的父类
     */
    public static void field(Object obj, String name, int breadth, int depth) {
        if (obj == null) {
            Log.d(sTag, "===== PrintObject is null!!!");
            return;
        }

        LogUtil.v("===== PrintObject begin %s: %s", name, obj.toString());
        LogUtil.d("===== PrintObject %s: %s", name, parse(obj, breadth, depth).toString());
//        LogUtil.v("===== PrintObject finish %s: %s", name, obj.toString());
    }

    private static JSONObject parse(Object obj, int breadth, int depth) {
        Class<?> clazz = obj.getClass();
        return parse(obj, clazz, breadth, depth);
    }

    /**
     * @param breadth 广度，即解析几层的成员变量
     * @param depth 深度，即几层的父类
     */
    private static JSONObject parse(Object obj, Class<?> clazz, int breadth, int depth) {
        JSONObject rootObj = new JSONObject();
        if (clazz.getName().contains("java.lang.Object")) {
            return rootObj;
        }

        try {
            rootObj.put("class", clazz.getName());
            rootObj.put("field", parseField(obj, clazz, breadth, depth));
            if (depth > 1) {
                Class<?> clazzSuper = obj.getClass().getSuperclass();
                rootObj.put("parent", parse(obj, clazzSuper, breadth, depth - 1));
            }
        } catch (JSONException e) {
            LogUtil.e(e, "PrintObject parse failed! obj: %s", obj.toString());
        }

        return rootObj;
    }

    private static JSONArray parseField(Object obj, Class<?> clazz, int breadth, int depth) {
        JSONArray jsonArray = new JSONArray();
        Field[] arrField = clazz.getDeclaredFields();
        jsonArray.put(String.format("field size is %d", arrField.length));
        for (int i = 0; i < arrField.length; i++) {
            JSONObject fieldObj = new JSONObject();
            try {
                String name = arrField[i].getName();
                fieldObj.put("name", name);
                String type = arrField[i].getType().getName();
                fieldObj.put("type", type);
                arrField[i].setAccessible(true);
                Object value = arrField[i].get(obj);
                if (value == null) {
                    fieldObj.put("value", null);
                    continue;
                }

                if (type.equals("java.lang.String")) {
                    fieldObj.put("value", value.toString());
                } else if (type.equals("int") || type.equals("java.lang.Integer")) {
                    fieldObj.put("value", (int) value);
                } else if (type.equals("long") || type.equals("java.lang.Long")) {
                    fieldObj.put("value", (long) value);
                } else if (type.equals("double") || type.equals("java.lang.Double")) {
                    fieldObj.put("value", (double) value);
                } else if (type.equals("float") || type.equals("java.lang.Float")) {
                    fieldObj.put("value", (float) value);
                } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
                    fieldObj.put("value", (boolean) value);
                } else if (type.equals("char") || type.equals("java.lang.Char")) {
                    fieldObj.put("value", Character.toString((char) value));
                } else if (type.startsWith("java.util.") && (type.contains("Map") || type.contains("List"))) {
                    fieldObj.put("value", JSONObject.wrap(value));
                    fieldObj.put("value2", value.toString());
                    if (type.contains("List") && ((List) value).size() > 0) {
                        List list = (List) value;
                        JSONArray arrTemp = new JSONArray();
                        for (int j = 0; j < list.size(); j++) {
                            JSONObject item = parse(list.get(j), 0, 0);
                            arrTemp.put(item);
                        }
                        fieldObj.put("value3", arrTemp);
                    }
                } else if (type.contains("java.lang.ref.WeakReference")) {
                    Object ref = ((WeakReference) value).get();
                    if (ref != null) {
                        JSONObject v = parse(ref, 0, 0);
                        fieldObj.put("value", v);
                    } else {
                        fieldObj.put("value", "WeakReference get null.");
                    }
                } else if (type.startsWith("[")) {
                    fieldObj.put("value", String.format("value is an array, size is %d", Array.getLength(value)));
                } else if (type.contains("java")) {
                    fieldObj.put("value", value.toString());
                } else if (breadth > 0) {
                    JSONObject v = parse(value, breadth - 1, 0);
                    fieldObj.put("value", v);
                } else {
                    fieldObj.put("value", value.toString());
                }
            } catch (Exception e) {
                LogUtil.e(e, "PrintObject parseField failed! class: %s", clazz.getName());
            }
            jsonArray.put(fieldObj);
        }
        return jsonArray;
    }

//    private static String parse(Object obj, Class<?> clazz, int depth, int breadth) {
//        if (clazz.getName().contains("java.lang.Object")) {
//            return "";
//        }
//        StringBuffer sb = new StringBuffer();
//        sb.append("{");
//        sb.append("\"class\":\"").append(clazz.getName()).append("\", \"field\":[");
//        Field[] arrField = clazz.getDeclaredFields();
//        for (int i = 0; i < arrField.length; i++) {
//            sb.append("{");
//            try {
//                String name = arrField[i].getName();
//                sb.append("\"name\":\"").append(name).append("\",");
//                String type = arrField[i].getType().getName();
//                sb.append("\"type\":\"").append(type).append("\",");
//                arrField[i].setAccessible(true);
//                Object value = arrField[i].get(obj);
//                sb.append("\"value\":");
//                if (value == null) {
//                    sb.append("\"").append("null").append("\"");
//                    continue;
//                }
////                Log.v(sTag, String.format("===== PrintObject value: %s", value.toString()));
//
//                if (type.equals("java.lang.String")) {
//                    sb.append("\"").append(value.toString()).append("\"");
//                } else if (type.equals("int") || type.equals("java.lang.Integer")) {
//                    sb.append((int) value);
//                } else if (type.equals("long") || type.equals("java.lang.Long")) {
//                    sb.append((long) value);
//                } else if (type.equals("double") || type.equals("java.lang.Double")) {
//                    sb.append((double) value);
//                } else if (type.equals("float") || type.equals("java.lang.Float")) {
//                    sb.append((float) value);
//                } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
//                    sb.append((boolean) value);
//                } else if (type.equals("char") || type.equals("java.lang.Char")) {
//                    sb.append("\"").append((char) value).append("\"");
//                } else if (type.startsWith("java.util") && (type.contains("Map") || type.contains("List"))) {
//                    sb.append("\"").append(value.toString()).append("\"");
//                } else if (type.startsWith("[")) {
//                    sb.append("\"").append("value is an array").append("\"");
//                } else if (type.contains("java")) {
//                    sb.append("\"").append(value.toString()).append("\"");
//                } else if (depth > 1) {
//                    String v = parse(value, depth - 1, 0);
//                    sb.append(v);
//                } else {
//                    sb.append("\"").append(value.toString()).append("\"");
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } finally {
//                sb.append("},");
//            }
//        }
//        sb.append("]");
//        if (breadth > 1) {
//            sb.append(", \"parent\":");
//            Class<?> clazzSuper = obj.getClass().getSuperclass();
//            sb.append(parse(obj, clazzSuper, depth, breadth - 1));
//        }
//        sb.append("}");
//        return sb.toString();
//    }
}
