package com.jds.reception.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 类 名: SPUtils
 * 作 者: yzhg
 * 创 建: 2018/8/27 0027
 * 版 本: 1.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述: 对Sp的封装
 */
public class SPUtils {

    private SPUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /*储存一些常规的数据,可以清理的数据*/
    private static String spPrefsConfig = "sp_config";

    private static SharedPreferences sp;

    private static SharedPreferences getSp(Context context, String tableName) {
        if (sp == null)
            sp = context.getSharedPreferences(tableName, Context.MODE_PRIVATE);
        return sp;
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 储存String 类型的数据
     */
    public static void putString(Context context, String key, String value) {
        getSp(context, spPrefsConfig).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getSp(context, spPrefsConfig).getString(key, defaultValue);
    }

    public static void putString(Context context, String key, String value, String tableName) {
        getSp(context, tableName).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue, String tableName) {
        return getSp(context, tableName).getString(key, defaultValue);
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 储存Int类型的数据
     */
    public static void putInt(Context context, String key, int value) {
        getSp(context, spPrefsConfig).edit().putInt(key, value).apply();
    }


    public static int getInt(Context context, String key, int defaultValue) {
        return getSp(context, spPrefsConfig).getInt(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value, String tableName) {
        getSp(context, tableName).edit().putInt(key, value).apply();
    }


    public static int getInt(Context context, String key, int defaultValue, String tableName) {
        return getSp(context, tableName).getInt(key, defaultValue);
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 储存Boolean类型的数据
     */
    public static void putBoolean(Context context, String key, boolean value) {
        getSp(context, spPrefsConfig).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSp(context, spPrefsConfig).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value, String tableName) {
        getSp(context, tableName).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue, String tableName) {
        return getSp(context, tableName).getBoolean(key, defaultValue);
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 储存Long类型的数据
     */
    public static void putLong(Context context, String key, Long value) {
        getSp(context, spPrefsConfig).edit().putLong(key, value).apply();
    }

    public static Long getLong(Context context, String key, Long defaultValue) {
        return getSp(context, spPrefsConfig).getLong(key, defaultValue);
    }

    public static void putLong(Context context, String key, Long value, String tableName) {
        getSp(context, tableName).edit().putLong(key, value).apply();
    }

    public static Long getLong(Context context, String key, Long defaultValue, String tableName) {
        return getSp(context, tableName).getLong(key, defaultValue);
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 保存对象
     */
    public static <T extends Serializable> void putObject(Context context, String key, T obj) {
        try {
            put(context, key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> void putObject(Context context, String key, T obj, String tableName) {
        try {
            put(context, key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 获取对象
     */
    public static <T extends Serializable> T getObject(Context context, String key, String tableName) {
        try {
            return (T) get(context, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Serializable> T getObject(Context context, String key) {
        try {
            return (T) get(context, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 存储List集合
     */
    public static void putList(Context context, String key, List<? extends Serializable> list) {
        try {
            put(context, key, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 获取List集合
     */
    public static <E extends Serializable> List<E> getList(Context context, String key) {
        try {
            return (List<E>) get(context, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 存储Map集合
     */
    public static <K extends Serializable, V extends Serializable> void putMap(Context context, String key, Map<K, V> map) {
        try {
            put(context, key, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 获取MAP集合
     */
    public static <K extends Serializable, V extends Serializable> Map<K, V> getMap(Context context, String key) {
        try {
            return (Map<K, V>) get(context, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 储存对象
     */
    private static void put(Context context, String key, Object obj) throws IOException {
        if (obj == null) {//判断对象是否为空
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        baos.close();
        oos.close();
        putString(context, key, objectStr);
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 储存对象
     */
    private static void put(Context context, String key, Object obj, String tableName) throws IOException {
        if (obj == null) {//判断对象是否为空
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        baos.close();
        oos.close();
        putString(context, key, objectStr, tableName);
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:获取对象
     */
    private static Object get(Context context, String key) throws IOException, ClassNotFoundException {
        String wordBase64 = getString(context, key, "");
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException
            return null;
        }
        byte[] objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        // 将byte数组转换成product对象
        Object obj = ois.readObject();
        bais.close();
        ois.close();
        return obj;
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:获取对象
     */
    private static Object get(Context context, String key, String tableName) throws IOException, ClassNotFoundException {
        String wordBase64 = getString(context, key, "", tableName);
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException
            return null;
        }
        byte[] objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        // 将byte数组转换成product对象
        Object obj = ois.readObject();
        bais.close();
        ois.close();
        return obj;
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 清除SP中的数据
     */
    public static void clearSp(Context context) {
       // SharedPreferences clearSp = context.getSharedPreferences(spPrefsConfig, Context.MODE_PRIVATE);
        //  SharedPreferences clearLoginSp = context.getSharedPreferences(sp_login_name, Context.MODE_PRIVATE);
        //   return clearSp != null && (clearSp.edit().clear().commit() && clearLoginSp.edit().clear().commit());
        SharedPreferences.Editor configEdit = getSp(context, spPrefsConfig).edit();
        //  edit.clear();
        //  edit.commit();
        configEdit.clear().apply();
       // return clearSp != null && (clearSp.edit().clear().apply());
    }

}
