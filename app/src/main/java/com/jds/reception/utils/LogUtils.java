package com.jds.reception.utils;

import android.util.Log;

import com.jds.reception.constant.Constant;


/**
 * author ${水木科技 - yzhg} on 2018/3/15.
 * <p>
 * describe()
 */

public class LogUtils {

    /**
     * DeBug开关
     */
    private static boolean DEBUG = Constant.DEBUG;

    /**
     * 获取当前类名
     *
     * @return
     */
    private static String getClassName() {
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        String result = thisMethodStack.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result = result.substring(lastIndex + 1, result.length());
        return "--------------------     " + result + "     --------------------";
    }

    /**
     * 统一说明:  以下日志打印  都是成对出现的 ，
     * 一个参数的，不需要传入名称，直接获取当前类名
     * 两个参数的需要手动传入名称
     *
     * @param msg
     */
    public static void w(String msg) {
        if (DEBUG) {
            Log.w(getClassName(), msg);
        }
    }

    public static void w(String name, String msg) {
        if (DEBUG) {
            Log.w(name, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            int segmentSize = 3 * 1024;
            long length = msg.length();
            if (length <= segmentSize) {// 长度小于等于限制直接打印
                Log.d(getClassName(), msg);
            } else {
                while (msg.length() > segmentSize) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize);
                    msg = msg.replace(logContent, "");
                    Log.d(getClassName(), msg);
                }
                Log.d(getClassName(), msg);
            }
        }
    }

    public static void d(String name, String msg) {
        if (DEBUG) {
            Log.d(name, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(getClassName(), msg);
        }
    }

    public static void e(String name, String msg) {
        if (DEBUG) {
            Log.e(name, msg);
        }
    }


    public static void i(String msg) {
        if (DEBUG) {
            Log.i(getClassName(), msg);
        }
    }

    public static void i(String name, String msg) {
        if (DEBUG) {
            if (msg.length() <= 1000) {
                Log.i(name, msg);
            } else {
                while (msg.length() > 1000) {
                    Log.i(name, msg.substring(0, 1000));
                    msg = msg.substring(1000, msg.length());
                }
                Log.i(name, msg);
            }
        }
    }


    public static void v(String msg) {
        if (DEBUG) {
            Log.v(getClassName(), msg);
        }
    }

    public static void v(String name, String msg) {
        if (DEBUG) {
            Log.v(name, msg);
        }
    }

    ///////////////打印重大BUG//////////////////

    public static void wtf(String msg) {
        if (DEBUG) {
            Log.wtf(getClassName(), msg);
        }
    }

    public static void wtf(String name, String msg) {
        if (DEBUG) {
            Log.wtf(name, msg);
        }
    }

}















