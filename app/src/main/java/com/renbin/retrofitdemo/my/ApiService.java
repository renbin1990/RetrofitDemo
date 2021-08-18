package com.renbin.retrofitdemo.my;




import okhttp3.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/getData.php")
    Call get(@Query("page") String page, @Query("size") String size);
    /**
     * 1、模仿造一个冰冰出来，身体画出来，再注入灵魂
     * 2、建一个这样的接口
     * 3、创建一个动态代理对象，专门用于解析成http的链接模式
     * 4、解析这个接口方法上的注解
     * 5、解析这个接口的参数注解
     * 6、切换到主线程（简单）
     *
     * 用了建造者，适配器，工厂，命令模式……
     *
     *
     */
}
