package com.usiellau.messageforward.other;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class Util {

    public static final String SENT_SMS_ACTION="com.usiellau.messageforward.SENT_MSG_ACTION";

    public static void listDataCopy(List<String> oldList,List<String> newList){
        oldList.clear();
        for(String s:newList){
            oldList.add(s);
        }
    }
    public static List<ActivityManager.RunningServiceInfo> getRunningServiceInfo(Context context){
        int defaultMaxNum=50;
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos=activityManager.getRunningServices(defaultMaxNum);
        for(ActivityManager.RunningServiceInfo runningServiceInfo:runningServiceInfos){
            ComponentName componentName=runningServiceInfo.service;
            Log.d("Utilities","服务包名："+componentName.getPackageName()+"服务类名："+componentName.getShortClassName());
        }
        return runningServiceInfos;
    }

    public static boolean isServiceRunning(Context context,String serviceName){
        String myPackageName=context.getPackageName();
        List<ActivityManager.RunningServiceInfo> runningServiceInfos=getRunningServiceInfo(context);

        ComponentName tempComponentName;
        String tempPackageName;
        String tempServiceName;
        for(ActivityManager.RunningServiceInfo runningServiceInfo:runningServiceInfos){
            tempComponentName=runningServiceInfo.service;
            tempPackageName=tempComponentName.getPackageName();
            tempServiceName=tempComponentName.getShortClassName();
            if(tempPackageName.equals(myPackageName)&&tempServiceName.equals(serviceName))return true;
        }
        return false;
    }
}
