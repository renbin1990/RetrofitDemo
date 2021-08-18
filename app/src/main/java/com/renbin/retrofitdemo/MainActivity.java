package com.renbin.retrofitdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.renbin.retrofitdemo.dongtaidaili.DynamicProxy;
import com.renbin.retrofitdemo.dongtaidaili.ProxyInterface;
import com.renbin.retrofitdemo.dongtaidaili.Xiaobin;

import java.lang.reflect.Proxy;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imageView);
        mTextView = findViewById(R.id.textView);
    }

    public void dynamic(View view) {
        Xiaobin xiaobin = new Xiaobin();
        ProxyInterface proxyInterface= (ProxyInterface) Proxy.newProxyInstance(xiaobin.getClass().getClassLoader(), xiaobin.getClass().getInterfaces(),new DynamicProxy(xiaobin));
        proxyInterface.choiceyan();
        proxyInterface.buyyan(1);
    }

    public void retrofitRespose(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hb.xyg12.cn/")
                .build();

        HttpApi httpApi = retrofit.create(HttpApi.class);
        Call<ResponseBody> personInfo = httpApi.getPersonInfo();
        personInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                try {
                    //这里就是主线程了，可以直接渲染数据
                    //如果是okhttp请求数据，这里是不可以的
                    String content = body.string();
                    PersonInfo personInfo = new Gson().fromJson(content,PersonInfo.class);
                    Glide.with(MainActivity.this).load(personInfo.getHeadUrl()).into(mImageView);
                    mTextView.setText("姓名："+personInfo.getName()+" 年龄："+personInfo.getAge()+" 职业："+personInfo.getJob()+" 简介："+personInfo.getDesc()+" 是否喜欢："+personInfo.getIsLove());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void retrofitRespose2(View view) {
        TestRetroiftUtil.testNet(new TestRetroiftUtil.Callback() {
            @Override
            public void onSuccess(PersonInfo personInfo) {
                Glide.with(MainActivity.this).load(personInfo.getHeadUrl()).into(mImageView);
                mTextView.setText("姓名："+personInfo.getName()+" 年龄："+personInfo.getAge()+" 职业："+personInfo.getJob()+" 简介："+personInfo.getDesc()+" 是否喜欢："+personInfo.getIsLove());
            }
            @Override
            public void onFail() {

            }
        });
    }
}