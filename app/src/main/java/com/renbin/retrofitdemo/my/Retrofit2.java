package com.renbin.retrofitdemo.my;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * data:2021-08-18
 * Author:renbin
 * 建造者模式  动态代理
 */
public class Retrofit2 {
    //缓存集合，避免重复解析 方法性能
    private final Map<Method, ServiceMethod> serviceMethodCache = new LinkedHashMap<>();
    private Call.Factory callFactory;  //Okhttp的实现接口
    private HttpUrl httpUrl;            //接口请求地址

    public Retrofit2(Builder builder) {
        this.callFactory = builder.callFactory;
        this.httpUrl = builder.baseUrl;
    }

    public <T> T create(Class<T> server){
        return (T) Proxy.newProxyInstance(server.getClassLoader(), new Class[]{server}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                /**
                 * 前面解析后 交给okhttp去请求网络
                 */
                ServiceMethod serviceMethod =  loadServiceMethod(method);
                //调用okhttp辅助类Call
                return new OkHttpCall(serviceMethod,args);
            }
        });
    }

    //获取所有注解 1 方法注解 2 参数注解
    private ServiceMethod loadServiceMethod(Method method) {
        //如果缓存有 直接返回 没有就去遍历获取
        ServiceMethod result =  serviceMethodCache.get(method);
        if (result != null){
            return result;
        }

        //防止重复调用
        synchronized (this){
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this,method).build();
                serviceMethodCache.put(method,result);
            }
        }
        return result;
    }

    public static class Builder{
        private HttpUrl baseUrl;
        private Call.Factory callFactory;

        public Builder baseUrl(String baseUrl){
            this.baseUrl = HttpUrl.parse(baseUrl);
            return  this;
        }

        Builder callFactory(Call.Factory factory){
            if (factory == null){
                throw new NullPointerException("factory can not be null");
            }
            this.callFactory = factory;
            return this;
        }

        public Retrofit2 build(){
            if (baseUrl == null){
                throw new IllegalArgumentException("null ");
            }

            if (callFactory == null){
                callFactory = new OkHttpClient();
            }
            return new Retrofit2(this);
        }
    }

    public HttpUrl getBaseUrl() {
        return httpUrl;
    }

    public Call.Factory getCallFactory() {
        return callFactory;
    }
}
