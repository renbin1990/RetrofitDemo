package com.renbin.retrofitdemo.my;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.HttpUrl;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * data:2021-08-18
 * Author:renbin
 */
public class ServiceMethod {
    Call.Factory callFactory;  //Okhttp的实现接口
    HttpUrl httpUrl;            //接口请求地址
    private String httpMethod;      //请求方式  get post
    private String relativeUrl;     //方法注解的值
    private boolean hasBody;        //是否有请求体
    private ParameterHandler[] parameterHandlers; //方法参数的数组，每个对象参数值 参数注解值

    private ServiceMethod(Builder builder){
        this.callFactory = builder.retrofit.getCallFactory();
        this.httpUrl = builder.retrofit.getBaseUrl();
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.parameterHandlers = builder.parameterHandlers;
        this.hasBody = hasBody;
    }

    //开始拼接url，请求网络
    public Call toCall(Object[] args) {
        //拼装
        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, httpUrl, relativeUrl, hasBody);
        ParameterHandler[] parameterHandlers = this.parameterHandlers;
        //要做一个检测，如果收集到的参数值数量长度和service里定义的不一样 那样肯定是不行的
        int argumentCount = args != null ? args.length : 0;
        if (argumentCount != parameterHandlers.length){
            throw new IllegalArgumentException("收集参数错误！");
        }
        for (int i = 0; i < argumentCount; i++) {
            parameterHandlers[i].apply(requestBuilder, (String) args[i]);
        }
        return callFactory.newCall(requestBuilder.build());
    }

    static final class Builder {
        private Retrofit2 retrofit;
        Method method;      //带注解的方法
        private Annotation[] methodAnnotations;       //注解路径   @GET("/webapp.php")
        private Annotation[][] paramsAnnotationArray;  //请求参数(@Query("page") String page, @Query("size")String size);
        private String httpMethod;      //请求方式  get post
        private String relativeUrl;     //方法注解的值
        private boolean hasBody;        //是否有请求体
        private ParameterHandler[] parameterHandlers; //方法参数的数组，每个对象参数值 参数注解值

        public Builder(Retrofit2 retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            //方法的所有注解，这里是拼接域名
            methodAnnotations = method.getAnnotations();
            //方法参数的所有注解，get   post
            paramsAnnotationArray = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            //专门对注解类进行解析，然后封装成ServiceMethod对象
            //对方法注解解析
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            //解析请求参数
            int paramsCount = paramsAnnotationArray.length;
            //初始化参数对象数组
            parameterHandlers = new ParameterHandler[paramsCount];
            for (int i = 0; i < paramsCount; i++) {
                Annotation[] paramsAnnotations = paramsAnnotationArray[i];
                if (paramsAnnotations == null) {
                    throw new IllegalArgumentException("参数注解不存在");
                }
                parameterHandlers[i] = parsePaeameter(paramsAnnotations);
            }

            return new ServiceMethod(this);
        }

        /**
         * 解析每个参数的所有注解
         *
         * @param paramsAnnotations 参数的注解数组
         * @return
         */
        private ParameterHandler parsePaeameter(Annotation[] paramsAnnotations) {
            ParameterHandler result = null;
            for (Annotation annotation : paramsAnnotations) {
                ParameterHandler parameterHandler =   parseParamsAnnotation(annotation);
                if (parameterHandler == null){
                    continue;
                }
                result = parameterHandler;
            }
            return result;
        }
        /**
         * @param annotation 而解析
         * @return
         */
        private ParameterHandler parseParamsAnnotation(Annotation annotation) {
            if (annotation instanceof Query){
                Query query = (Query) annotation;
                String name =  query.value();
                return new ParameterHandler.Query(name);
            }else if (annotation instanceof Field){
                Field field = (Field) annotation;
                String name =  field.value();
                return new ParameterHandler.Field(name);
            }
            return null;
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof GET) {
                //解析get方法。最后一个参数是看是否有请求体，get请求是没有请求体的
                parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
            } else if (annotation instanceof POST) {
                parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
            }
        }

        private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
            this.httpMethod = httpMethod;
            this.relativeUrl = value;
            this.hasBody = hasBody;
        }
    }

}
