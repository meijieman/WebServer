package com.major.webserver;

import android.util.Log;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2017/8/20 10:55
 */
public class SL{

    private static final String TAG = "ele_a";

    public static void i(String msg){
        StackTraceElement[] stackTrace = new Thread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        if(stackTrace.length >= 3){
            sb.append(stackTrace[3].toString().replace("com.major.webserver.", ""));
        } else if(stackTrace.length == 1){
            sb.append(stackTrace[0].toString().replace("com.major.webserver.", ""));
        }
        sb.append(msg);
        Log.i(TAG, sb.toString());
    }

    public static void e(String msg){
        StackTraceElement[] stackTrace = new Thread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        if(stackTrace.length >= 3){
            sb.append(stackTrace[3].toString().replace("com.major.webserver.", ""));
        } else if(stackTrace.length == 1){
            sb.append(stackTrace[0].toString().replace("com.major.webserver.", ""));
        }
        sb.append(msg);
        Log.e(TAG, sb.toString());
    }
}
