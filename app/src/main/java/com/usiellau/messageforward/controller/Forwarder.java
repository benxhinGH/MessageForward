package com.usiellau.messageforward.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.usiellau.messageforward.database.DBUtil;
import com.usiellau.messageforward.model.MyMessage;
import com.usiellau.messageforward.network.EmailUtil;
import com.usiellau.messageforward.network.SmsUtil;
import com.usiellau.messageforward.other.MyApplication;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/16 0016.
 */

public class Forwarder {

    private String TAG="Forwarder";

    private final boolean DEBUG=false;

    private static String recordFileName="msgIdHaveForwardedRecord";

    private static FileOutputStream fos;

    private static FileInputStream fis;

    private static Set<Long> msgIdHaveForwardedSet;

    private Context context;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(context, "邮箱转发失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"邮箱转发失败");
                    break;
                case 1:
                    Toast.makeText(context, "短信转发成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"短信转发成功");
                    break;
                case 2:
                    Toast.makeText(context, "短信转发失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"短信转发失败");
                    break;
                default:
                    break;
            }
        }
    };


    private static Forwarder forwarder;
    private Forwarder(Context context){
        this.context=context;
    }
    public static Forwarder getForwarder(Context context){
        if(forwarder==null){
            forwarder=new Forwarder(context);
            try {
                File file=new File(MyApplication.getContext().getFilesDir()+"/"+recordFileName);
                if(!file.exists())file.createNewFile();
                fis=MyApplication.getContext().openFileInput(recordFileName);
                ObjectInputStream ois=new ObjectInputStream(fis);
                msgIdHaveForwardedSet=(Set<Long>)ois.readObject();
            }catch (EOFException e){
                e.printStackTrace();
                if(msgIdHaveForwardedSet==null){
                    msgIdHaveForwardedSet=new HashSet<>();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return forwarder;
    }

    public synchronized boolean sendMessage(List<String> forwardNumber, MyMessage myMessage){

        String tempMobiles="";
        for(String number:forwardNumber){
            tempMobiles+=number+",";
        }
        final String mobiles=tempMobiles.substring(0,tempMobiles.length()-1);

        //Log.d(TAG,"发送消息，号码："+mobiles+"内容："+myMessage.getBody());

        if(DEBUG) {
            msgIdHaveForwardedSet.add(myMessage.getDateSent());
            Log.d(TAG,"添加时间戳为："+myMessage.getDateSent()+"的短信到已转发集合中");
            return true;
        }

        boolean res= SmsUtil.sendSms(mobiles,SmsUtil.templateId,"pwd",myMessage.getBody());
        if(res){
            msgIdHaveForwardedSet.add(myMessage.getDateSent());
        }
        return res;
    }

    public synchronized void forwardMessage(MyMessage message){

        //Log.d(TAG,"进入forwardMesaage，待转发消息时间戳为："+message.getDateSent());

        if(msgIdHaveForwardedSet.contains(message.getDateSent())){
            //Log.d(TAG,"id为"+message.getId()+"的短信已转发过，取消转发");
            return;
        }

        SharedPreferences setting=context.getSharedPreferences("setting",0);
        if(setting.getBoolean("number_forward",false)){
            numberForward(message);
        }
        if(setting.getBoolean("email_forward",false)){
            emailForward(message);
        }
    }
    public void numberForward(MyMessage message){
        List<String> forwardNumber= DBUtil.queryForwardNumber();
        if(forwardNumber.size()==0)return;
        sendMessage(forwardNumber,message);

    }

    public void emailForward(final MyMessage message){
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
                    //Log.d(TAG,"转发id为："+msgId+"的短信");
                    EmailUtil.sendTextEmail(addressArr,"转发来自："+msgAddress+"的信息",msgBody);
                    msgIdHaveForwardedSet.add(message.getDateSent());
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message=new Message();
                    message.what=0;
                    handler.sendMessage(message);
                }
            }
        }).start();


    }

    public void exit(){
        try {
            fos=MyApplication.getContext().openFileOutput(recordFileName,Context.MODE_PRIVATE);
            new ObjectOutputStream(fos).writeObject(msgIdHaveForwardedSet);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
