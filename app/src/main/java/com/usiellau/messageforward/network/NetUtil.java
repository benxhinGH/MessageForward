package com.usiellau.messageforward.network;

import com.usiellau.messageforward.network.api.SMSend;

import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class NetUtil {
    private static SMSend smSend;

    public static SMSend getSMSendApi(){
        if(smSend==null){
            Retrofit retrofit= new Retrofit.Builder()
                    .baseUrl("something/")
                    .build();
            smSend=retrofit.create(SMSend.class);
        }
        return smSend;
    }

}
