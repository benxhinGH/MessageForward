package com.usiellau.messageforward.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.usiellau.messageforward.activity.MainActivity;
import com.usiellau.messageforward.controller.Forwarder;
import com.usiellau.messageforward.database.DBUtil;
import com.usiellau.messageforward.model.MyMessage;
import com.usiellau.messageforward.other.Util;
import com.usiellau.messageforward.service.MonitorService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class SmsReceiver extends BroadcastReceiver {

    private String TAG="SmsReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                final String address=msg.getOriginatingAddress();
                final String body=msg.getMessageBody();
                final long dateSent=msg.getTimestampMillis();
                Log.d(TAG,"广播监听器收到新短信："+"number:" + address
                        + "   body:" + body + "  time:"
                        + receiveTime);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(DBUtil.queryMonitorNumber().contains(address)){
                            Forwarder.getForwarder(context).forwardMessage(new MyMessage("",address,body,dateSent));
                        }
                    }
                }).start();
                keepMonitorServiceAlive(context);
            }
        }
    }

    private void keepMonitorServiceAlive(Context context){
        boolean isRunningInLog=context.getSharedPreferences("state",0).getBoolean("serviceRunning",false);
        boolean isRunningInMemory= Util.isServiceRunning(context,".service.MonitorService");
        if(!isRunningInMemory&&isRunningInLog){
            Intent intent=new Intent(context, MonitorService.class);
            context.startService(intent);
            Log.d(TAG,"重启MonitorService服务");
        }
    }

}
