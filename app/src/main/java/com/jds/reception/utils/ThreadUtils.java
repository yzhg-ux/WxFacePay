package com.jds.reception.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 线程的封装类,
 * 维护一个线程池，当需要在子线程中运行某些代码时，只需要调用这个ThreadUtils的方法即可。
 */
public class ThreadUtils {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private static ExecutorService sExecutors = Executors.newSingleThreadExecutor();

    /**
     * 在子线程中运行一段逻辑
     * @param runnable
     */
    public static void runOnSubThread(Runnable runnable){
        sExecutors.execute(runnable);
    }

    /**
     * 在主线程中运行一段逻辑
     * @param runnable
     */
    public static void runOnMainThread(Runnable runnable){
        sHandler.post(runnable);
    }

}