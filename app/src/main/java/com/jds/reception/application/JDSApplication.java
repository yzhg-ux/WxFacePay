package com.jds.reception.application;

import android.app.Application;

import com.jds.reception.constant.Constant;
import com.jds.reception.http.tools.SSLSocketClient;
import com.jds.reception.utils.Tools;
import com.jds.reception.utils.toast.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.model.HttpHeaders;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 类 名: JDSApplication
 * 作 者: yzhg
 * 创 建: 2019/5/23 0023
 * 版 本: 2.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述: 启动程序
 * <p>
 * 微信人脸支付  开放文档地址：
 * https://pay.weixin.qq.com/wiki/doc/wxfacepay/develop/sdk-android.html#%E5%88%9D%E5%A7%8B%E5%8C%96-initwxpayface
 */
public class JDSApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);
        Tools.init(getApplicationContext());
        initOkGo();
    }


    /*
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:
     *      初始化OKGO
     */
    private void initOkGo() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(false);
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        builder.readTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.connectTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this))); //自动管理cookie

        HttpHeaders headers = new HttpHeaders();
        headers.put("content-type", "application/json");
        headers.toJSONString();

        try {
            // 其他统一的配置
            // 详细说明看GitHub文档：https://github.com/jeasonlzy/
            OkGo.getInstance().init(this)                           //必须调用初始化
                    .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                    .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,
                    .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                    .setRetryCount(3) //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                    .addCommonHeaders(headers); //设置全局公共头
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
