package com.renbin.retrofitdemo.my;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 最终的请求拼装类
 */
public class RequestBuilder {

    //方法的请求方式
    private final String method;
    //接口的请求地址
    private final HttpUrl baseUrl;
    private String relativeUrl;   //方法注解的值  ("/ip/newIp")
    //请求url完整构建者
    private HttpUrl.Builder urlBuilder;
    //form表单构建者
    private FormBody.Builder formBuilder;
    //构建完整请求，包含url method  body
    private final Request.Builder requestBuilder;

    public RequestBuilder(String method, HttpUrl baseUrl, String relativeUrl, boolean hasBody) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;
        this.requestBuilder = new Request.Builder();
        if (hasBody) formBuilder = new FormBody.Builder();
    }

    public RequestBuilder(String method, HttpUrl httpUrl, String relativeUrl, HttpUrl.Builder urlBuilder, FormBody.Builder formBuilder, Request.Builder requestBuilder) {
        this.method = method;
        this.baseUrl = httpUrl;
        this.relativeUrl = relativeUrl;
        this.urlBuilder = urlBuilder;
        this.formBuilder = formBuilder;
        this.requestBuilder = requestBuilder;
    }

    public void addQueryParam(String name, String value) {
        if (relativeUrl != null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
            if (urlBuilder == null) {
                throw new IllegalArgumentException("url 为空");
            }
            //每次请求都会实例化 需要重置
            relativeUrl = null;
        }
        urlBuilder.addQueryParameter(name, value);
    }

    public void addFieldForm(String name, String value) {
        formBuilder.add(name, value);
    }

    Request build() {
        //定义局部变量   保证每次值都不一样， 易回收
        HttpUrl url;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = baseUrl.resolve(relativeUrl);
            if (url == null) {
                throw new IllegalArgumentException("build 时url 为空");
            }
        }

        //如果请求中有表单，构建方法中会初始化formBuilder 然后在实例化请求体
        RequestBody body = null;
        if (formBuilder != null) {
            body = formBuilder.build();
        }

        return requestBuilder
                .url(url)
                .method(method, body)
                .build();
    }
}
