package com.usiellau.messageforward.network.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public interface SMSend {
    @GET("/something")
    Call<ResponseBody> sendMessage(@Query("param1") String param1,
                                   @Query("param2") String param2);
}
