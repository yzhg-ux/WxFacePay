package com.jds.reception.http.api;

/**
 * 类 名: Api
 * 作 者: yzhg
 * 创 建: 2019/5/23 0023
 * 版 本: 2.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述:
 */
public class Api {
    /*获取微信人脸识别凭证*/
   public static final String getFaceAuthInfo = "https://payapp.weixin.qq.com/face/get_wxpayface_authinfo";
    public static final String getFacePay = "https://api.mch.weixin.qq.com/pay/facepay";
}
