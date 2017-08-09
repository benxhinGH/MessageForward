package com.usiellau.messageforward;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
