package com.renbin.retrofitdemo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * data:2021-08-12
 * Author:renbin
 */
public interface HttpApi {

    @GET("webapp.php")
    Call<ResponseBody> getPersonInfo();

    @GET("webapp.php")
    Call<ResponseBody> getinfo(@Query("page") String page,@Query("pageszie") String pageszie);
}
