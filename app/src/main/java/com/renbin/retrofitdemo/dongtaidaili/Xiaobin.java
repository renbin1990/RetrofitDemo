package com.renbin.retrofitdemo.dongtaidaili;

import android.util.Log;

/**
 * data:2021-08-12
 * Author:renbin
 * 动态代理
 */
public class Xiaobin implements ProxyInterface {
    /**
     * jdk:有一套规则，中介者，运行时在内存中生成一个匿名代理对象java类，我们遵守一套规则
     * 1.实现一个InvocationHandler接口
     * 2.proxy.newProxyInstance(InvocationHandler)生成代理对象
     */
    @Override
    public void choiceyan() {
        Log.e("xiaobin","xiaobin委托外卖小哥买烟");
    }

    @Override
    public void buyyan(int params) {
        if (params == 0){
            Log.e("外卖小哥","帮xiaobin买到了中华");
        }else {
            Log.e("外卖小哥","帮xiaobin买到了云烟");
        }
    }
}
