package com.dwman;

import android.app.Application;
import android.content.Context;

/**
 * Created by ldw on 2018/3/20.
 */

public class MyApp extends Application {


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getApplication(){
        return mContext;
    }
}
