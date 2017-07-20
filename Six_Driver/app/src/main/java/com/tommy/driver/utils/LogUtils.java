package com.tommy.driver.utils;

import android.util.Log;

import com.tommy.driver.BuildConfig;

/**
 * Created on 20/7/15.
 *
 * @author PurpleTalk India Pvt. Ltd.
 */
public class LogUtils {

    private static final String LOG_TAG = "SixDriverLogs";

    private static boolean isLogEnabled = BuildConfig.PRINT_LOG;

    public static void i(String log){
        if (isLogEnabled){
            Log.i(LOG_TAG, log);
        }
    }

    public static void w(String log){
        if (isLogEnabled){
            Log.w(LOG_TAG, log);
        }
    }

    public static void e(String log){
        if (isLogEnabled){
            Log.e(LOG_TAG, log);
        }
    }

    public static void v(String log){
        if (isLogEnabled){
            Log.v(LOG_TAG, log);
        }
    }

    public static void d(String log){
        if (isLogEnabled){
            Log.d(LOG_TAG, log);
        }
    }
}