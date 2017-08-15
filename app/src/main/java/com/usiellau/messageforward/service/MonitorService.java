package com.usiellau.messageforward.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.usiellau.messageforward.R;
import com.usiellau.messageforward.network.EmailUtil;
import com.usiellau.messageforward.network.SmsUtil;
import com.usiellau.messageforward.activity.MainActivity;
import com.usiellau.messageforward.database.DBUtil;
import com.usiellau.messageforward.model.MyMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MonitorService extends Service {

    private boolean DEBUG=false;

    private String TAG="MonitorService";

    private Uri SMS_INBOX=Uri.parse("content://sms/");

    private SmsObserver smsObserver;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(MonitorService.this, "邮箱转发失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"邮箱转发失败");
                    break;
                case 1:
                    Toast.makeText(MonitorService.this, "短信转发成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"短信转发成功");
                    break;
                case 2:
                    Toast.makeText(MonitorService.this, "短信转发失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"短信转发失败");
                    break;
                default:
                    break;
            }
        }
    };


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

        private List<String> msgIdHaveForwarded=new ArrayList(){
            @Override
            public boolean add(Object o) {
                Log.d(TAG,"添加"+o.toString()+"为已转发短信id");
                return super.add(o);
            }
        };

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
            Log.d(TAG,"onChange.............selfChange:"+selfChange);
            super.onChange(selfChange);
            MyMessage myMessage=getMonitorMsgFromPhone();
            if(myMessage==null)return;
            Log.d(TAG,"查出符合转发条件的短信id为"+myMessage.getId()+"\n内容为："+myMessage.getBody());

            forwardMessage(myMessage);
        }

        public MyMessage getMonitorMsgFromPhone(){
            List<String> monitorNumber= DBUtil.queryMonitorNumber();
            ContentResolver contentResolver=getContentResolver();
            String[] projection=new String[]{"_id","address","body","read"};
            Cursor cursor=contentResolver.query(SMS_INBOX,projection,null,null,"date desc limit 1");
            if(cursor==null)return null;
            if(cursor.moveToFirst()){
                String id=cursor.getString(cursor.getColumnIndex("_id"));
                String address=cursor.getString(cursor.getColumnIndex("address"));
                String body=cursor.getString(cursor.getColumnIndex("body"));
                String read=cursor.getString(cursor.getColumnIndex("read"));
                if(monitorNumber.contains(address)&&!msgIdHaveForwarded.contains(id)&&read.equals("0")){
                    return new MyMessage(id,address,body);
                }
                cursor.close();
            }
            return null;
        }

        public void forwardMessage(MyMessage message){

            SharedPreferences setting=getSharedPreferences("setting",0);
            if(setting.getBoolean("number_forward",false)){
                numberForward(message);
            }
            if(setting.getBoolean("email_forward",false)){
                emailForward(message);
            }
        }

        public void numberForward(MyMessage message){
            List<String> forwardNumber=DBUtil.queryForwardNumber();
            if(forwardNumber.size()==0)return;
            sendMessage(forwardNumber,message);

        }

        public void emailForward(MyMessage message){
            List<String> addresses=DBUtil.queryForwardEmail();
            if(addresses.size()==0)return;
            final String msgId=message.getId();
            final String msgAddress=message.getAddress();
            final String msgBody=message.getBody();
            final String[] addressArr=addresses.toArray(new String[addresses.size()]);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG,"转发id为："+msgId+"的短信");
                        EmailUtil.sendTextEmail(addressArr,"转发来自："+msgAddress+"的信息",msgBody);
                        msgIdHaveForwarded.add(msgId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message message=new Message();
                        message.what=0;
                        handler.sendMessage(message);
                    }
                }
            }).start();


        }

        public void sendMessage(List<String> forwardNumber, final MyMessage myMessage){
            if(DEBUG){
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
                msgIdHaveForwarded.add(myMessage.getId());
                return;
            }
            String tempMobiles="";
            for(String number:forwardNumber){
                tempMobiles+=number+",";
            }
            final String mobiles=tempMobiles.substring(0,tempMobiles.length()-1);

            Log.d(TAG,"发送消息，号码："+mobiles+"内容："+myMessage.getBody());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean res=SmsUtil.sendSms(mobiles,SmsUtil.templateId,"pwd",myMessage.getBody());
                    Message message=new Message();
                    if(res){
                        message.what=1;
                        msgIdHaveForwarded.add(myMessage.getId());
                    }else {
                        message.what=2;
                    }
                    handler.sendMessage(message);
                }
            }).start();
        }


    }
}
