package com.jds.reception.ui.main;

import com.jds.reception.base.mvp.base.BasePresenter;
import com.jds.reception.base.mvp.base.BaseView;
import com.jds.reception.base.mvp.base.NetStateEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 类 名: MainContract
 * 作 者: yzhg
 * 创 建: 2019/5/23 0023
 * 版 本: 2.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述: main主页面  连接层
 */
public interface MainContract {
    interface MainView extends BaseView {

        /*初始化微信人脸支付成功*/
        void initWxPaySuccess();

        /*获取人脸识别  rawData 成功*/
        void wxPayFaceRawDataSuccess(String rawdata);

        /*获取人脸支付凭证成功*/
        void wxFacePayVoucherSuccess(String authinfo);

        /*人脸识别成功*/
        void getWxPayFaceCodeSuccess(String faceText, String faceCode, String openId, String sub_open_id);

        /*初始化微信人脸支付失败*/
        void initWxPayFailed(String failedText, NetStateEnum netStateEnum);

        /*支付失败*/
        void startWxPayFace(String successText);
    }

    interface MainPresenter extends BasePresenter<MainView> {
        /**
         * 版  本 : 1.0
         * 操作人 : yzhg
         * 描  述 : 第一步：初始化微信操作
         */
        void initWxPay();

        /**
         * 版  本 : 1.0
         * 操作人 : yzhg
         * 描  述 : 第二步 ：获取微信人脸支付RawData
         */
        void getWxPayFaceRawData();

        /**
         * 版  本 : 1.0
         * 操作人 : yzhg
         * 描  述 : 第三步 ： 获取微信支付凭证
         */
        void getWxFacePayVoucher(String rawdata);

        /**
         * 版  本 : 1.0
         * 操作人 : yzhg
         * 描  述 : 第四步： 开始刷脸
         */
        void getWxPayFaceCode(HashMap<String, String> map);

        /**
         * 版  本 : 1.0
         * 操作人 : yzhg
         * 描  述 : 第五步开始支付
         */
        void startWxPayFace(String toXml) throws Exception;
    }
}
