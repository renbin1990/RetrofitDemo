package com.renbin.retrofitdemo.dongtaidaili;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * data:2021-08-12
 * Author:renbin
 */
public class DynamicProxy implements InvocationHandler {
    private Object mObject;

    public DynamicProxy(Object object) {
        mObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.invoke(mObject,args);
        return mObject;
    }
}
