package com.usiellau.messageforward;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MessageSendResReceiver extends BroadcastReceiver {

    private String TAG="MessageSendResReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive....................");
        if(intent.getAction().equals(Util.SENT_SMS_ACTION)){
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "短信转发成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"短信转发成功");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                default:
                    Toast.makeText(context, "短信转发失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"短信转发失败");
                    break;
            }
        }
    }
}
