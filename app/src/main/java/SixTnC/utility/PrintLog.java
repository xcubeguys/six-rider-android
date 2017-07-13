package SixTnC.utility;

import android.util.Log;

import SixTnC.BuildConfig;

/**
 * Helper class to print Application logs
 */

public class PrintLog {

    /*Information logger
     * */
    public static void printInfoLog(String tag, String message) {
        if (BuildConfig.PRINT_LOG) {
            Log.i(tag, message);
        }

    }

    /*Debug logger
     * */
    public static void printDebugLog(String tag, String message) {
        if (BuildConfig.PRINT_LOG) {
            Log.d(tag, message);
        }

    }

    /*Error logger
     * */
    public static void printErrorLog(String tag, String message) {
        if (BuildConfig.PRINT_LOG) {
            Log.e(tag, message);
        }

    }

}
