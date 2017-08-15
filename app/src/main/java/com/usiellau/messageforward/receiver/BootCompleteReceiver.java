package com.usiellau.messageforward.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.usiellau.messageforward.service.MonitorService;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    private String TAG="BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"MessageForward接受到开启广播");
        Intent serviceIntent=new Intent(context, MonitorService.class);
        context.startService(serviceIntent);
    }
}
