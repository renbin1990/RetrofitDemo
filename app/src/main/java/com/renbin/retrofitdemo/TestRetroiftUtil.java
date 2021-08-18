package com.renbin.retrofitdemo;

import com.google.gson.Gson;
import com.renbin.retrofitdemo.my.ApiService;
import com.renbin.retrofitdemo.my.MainThreadExecutor;
import com.renbin.retrofitdemo.my.Retrofit2;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class TestRetroiftUtil {

    public static void testNet(final Callback callback){
        Retrofit2 retrofit2 = new Retrofit2.Builder().baseUrl("https://hb.yxg12.cn/").build();
        ApiService personInterface2 = retrofit2.create(ApiService.class);
        Call call2 = personInterface2.get("111","2222");
        call2.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                ResponseBody body = response.body();
                try {
                    //这里就是主线程了，可以直接渲染数据
                    //如果是okhttp请求数据，这里是不可以的
                    String content = body.string();
                    final PersonInfo personInfo = new Gson().fromJson(content,PersonInfo.class);
                    Executor executor = new MainThreadExecutor();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(personInfo);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface Callback{
        void onSuccess(PersonInfo personInfo);
        void onFail();
    }
}