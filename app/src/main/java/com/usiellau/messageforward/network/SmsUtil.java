package com.usiellau.messageforward.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class SmsUtil {

    private static final String TAG="SmsUtil";

    private static final String templateSmsUrl="http://183.230.40.74:80/tempsmsSend";
    private static final String sicode="915eb2bdd0984a94aaa4336586240c4b";
    public static final String templateId="10909";


    public static boolean sendSms(String mobiles, String tempId, String tempKey, String tempValue) {
        Map<String, String> map = new HashMap<>();
        map.put(tempKey, tempValue);
        return sendSms(mobiles, tempId, map);
    }



    public static boolean sendSms(String mobiles, String tempId, Map<String,String> paramMap){
        HttpURLConnection connection;

        String formParam = "sicode=" + sicode + "&mobiles=" + mobiles + "&tempid=" + tempId;

        if (paramMap.size() > 0) {
            Set<String> keySet = paramMap.keySet();
            for (String key : keySet) {
                formParam += "&" + key + "=" + paramMap.get(key);
            }
        }

        StringBuffer sb = new StringBuffer();
        int code = 0;
        try{
            connection = (HttpURLConnection) new URL(templateSmsUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.getOutputStream().write(formParam.getBytes());
            connection.getOutputStream().close();
            code = connection.getResponseCode();
            InputStream stream = null;
            if (code == 200) {
                stream = connection.getInputStream();
            } else {
                stream = connection.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }

            br.close();
            connection.disconnect();


            if (code == 200 && sb.toString().contains("10701")) {
                Log.i(TAG,"调用发送短信接口" + "成功" + "返回结果：" + sb.toString() );
                return true;
            } else {
                Log.i(TAG,"调用发送短信接口" + "失败" + "返回结果：" + sb.toString() );
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
