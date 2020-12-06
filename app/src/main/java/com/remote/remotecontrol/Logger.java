package com.remote.remotecontrol;

import android.util.Log;

public class Logger {

    public void LoggerStart(String TAG, String mag){
        Log.d("========START=======>"+TAG+"::",mag);
    }
    public void LoggerEnd(String TAG, String mag){
        Log.d("========END=======>"+TAG+"::",mag);
    }
}
