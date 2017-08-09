package com.usiellau.messageforward;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MonitorService extends Service {

    private String TAG="MonitorService";

    private Uri SMS_INBOX=Uri.parse("content://sms/");

    private SmsObserver smsObserver;
    private Handler handler=new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
        Notification notification= new NotificationCompat.Builder(this)
                .setContentTitle("MessageForward")
                .setContentText("短信转发服务已开启")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();
        startForeground(1,notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        smsObserver=new SmsObserver(handler);
        getContentResolver().registerContentObserver(SMS_INBOX,true,smsObserver);
        return super.onStartCommand(intent, flags, startId);
    }

    class SmsObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG,"onChange.............");
            super.onChange(selfChange);
            List<MyMessage> myMessages=getMonitorMsgFromPhone();
            if(myMessages==null)return;
            if(myMessages.size()!=0){
                forwardMessage(myMessages);
            }
        }

        public List<MyMessage> getMonitorMsgFromPhone(){
            List<MyMessage> res=new ArrayList<>();
            List<String> monitorNumber=DBUtil.queryMonitorNumber();
            ContentResolver contentResolver=getContentResolver();
            String[] projection=new String[]{"address","body"};
            Cursor cursor=contentResolver.query(SMS_INBOX,projection,"read=?",new String[]{"0"},"date desc");
            if(cursor==null)return null;
            if(cursor.moveToFirst()){
                String address;
                String body;
                do{
                    address=cursor.getString(cursor.getColumnIndex("address"));
                    if(monitorNumber.contains(address)){
                        body=cursor.getString(cursor.getColumnIndex("body"));
                        res.add(new MyMessage(address,body));
                    }
                }while (cursor.moveToNext());
                cursor.close();
            }
            return res;
        }

        public void forwardMessage(List<MyMessage> messages){
            List<String> forwardNumber=DBUtil.queryForwardNumber();
            for(String number:forwardNumber){
                sendMessage(number,addForwardText(messages.get(0).getAddress(),messages.get(0).getBody()));
            }
        }
        public void sendMessage(String number,String content){
            Log.d(TAG,"发送消息，号码："+number+"内容："+content);
            SmsManager smsManager=SmsManager.getDefault();
            Intent intent=new Intent(Util.SENT_SMS_ACTION);
            PendingIntent sentPi=PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            smsManager.sendTextMessage(number,null,content,sentPi,null);
        }
        public String addForwardText(String address,String body){
            String head="转发来自:"+address+"的信息\r\n--------------------\r\n";
            return head+body;
        }
    }
}
