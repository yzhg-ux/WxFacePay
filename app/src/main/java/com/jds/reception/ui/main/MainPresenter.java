package com.jds.reception.ui.main;

import android.os.RemoteException;
import android.text.TextUtils;

import com.jds.reception.R;
import com.jds.reception.base.mvp.base.BasePresenterImpl;
import com.jds.reception.base.mvp.base.NetStateEnum;
import com.jds.reception.constant.Constant;
import com.jds.reception.constant.WxConstant;
import com.jds.reception.http.api.Api;
import com.jds.reception.utils.LogUtils;
import com.jds.reception.utils.SPUtils;
import com.jds.reception.utils.Tools;
import com.jds.reception.utils.ThreadUtils;
import com.jds.reception.utils.toast.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * 类 名: MainPresenter
 * 作 者: yzhg
 * 创 建: 2019/5/23 0023
 * 版 本: 2.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述:  主页面   网络层
 */
public class MainPresenter extends BasePresenterImpl<MainContract.MainView> implements MainContract.MainPresenter {

    /**
     * 版  本 : 1.0
     * 操作人 : yzhg
     * 描  述 : 初始化微信人脸支付SDK操作
     * 初始化使用的map 用于设置商户代理 配置刷脸走商户内部代理 若不需要，则不用填写
     */
    @Override
    public void initWxPay() {
        Map<String, String> agencyMap = new HashMap<>();
        // agencyMap.put("ip","");  //HTTP代理IP  192.168.1.1
        // agencyMap.put("port","");  //HTTP代理端口  8888
        // agencyMap.put("user","");  //HTTP代理的用户名
        // agencyMap.put("passwd",""); //HTTP代理的密码
        WxPayFace.getInstance().initWxpayface(Tools.getContext(), agencyMap, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                /**
                 *  版  本 : 1.0
                 *  操作人 : yzhg
                 *  描  述 : 拿到初始化结果
                 *     对比code是否为SUCCESS  详细可以查看官方错误码
                 */
                if (map != null) {
                    String code = (String) map.get(WxConstant.RETURN_CODE);
                    String msg = (String) map.get(WxConstant.RETURN_MSG);
                    if (!(code != null && code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS))) {
                        mView.initWxPayFailed(Tools.getString(R.string.wx_face_init_failed), NetStateEnum.one);
                    } else {
                        mView.initWxPaySuccess();
                    }
                }
            }
        });
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:
     * 获取微信人脸支付RawData
     */
    @Override
    public void getWxPayFaceRawData() {
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                if (!Tools.isSuccessInfo(map)) {
                    mView.initWxPayFailed(Tools.getString(R.string.raw_data_failed), NetStateEnum.two);
                } else {
                    //获取到RAW_DATA
                    if (map.get(WxConstant.RAW_DATA) != null) {
                        String rawdata = map.get(WxConstant.RAW_DATA).toString();
                        mView.wxPayFaceRawDataSuccess(rawdata);
                    } else {
                        mView.initWxPayFailed(Tools.getString(R.string.raw_data_failed), NetStateEnum.two);
                    }
                }
            }
        });
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:
     * 获取微信人脸支付凭证
     */
    @Override
    public void getWxFacePayVoucher(String rawdata) {
        //储存RawData的时间。
        SPUtils.putLong(Tools.getContext(), Constant.SP_PAY_RAWDATA, System.currentTimeMillis());
        Map<String, String> map = new HashMap<>();
        map.put("appid", WxConstant.APP_ID);
        map.put("mch_id", WxConstant.MCH_ID);
        map.put("sub_mch_id", WxConstant.SUB_MCH_ID);
        map.put("sub_appid", WxConstant.SUB_APP_ID);
        map.put("now", "" + (System.currentTimeMillis() / 1000));
        map.put("version", "1");
        map.put("sign_type", "MD5");
        map.put("nonce_str", "" + (System.currentTimeMillis() / 100000));
        map.put("store_id", "055039");
        map.put("store_name", "小杨店铺");
        map.put("device_id", "" + (System.currentTimeMillis() / 100000));
        map.put("rawdata", rawdata);
        //     map.put("spbill_create_ip",  (null == ipAddress) ? "" : ipAddress);

        //按字典顺序排序
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());
        Collections.sort(infoIds, (o1, o2) -> (o1.getKey()).compareTo(o2.getKey()));
        //使用&符号进行频率
        String sbR = Tools.getStringBuffer(infoIds) + "&key=" + WxConstant.MCH_KEY_ID;
        //进行MD5加密之后  转大写
        String sign = Tools.encode(sbR).toUpperCase();
        map.put("sign", sign);
        LogUtils.d("认证参数" + map.toString());
        try {
            String toXml = Tools.mapToXml(map);
            LogUtils.d("认证参数XMl" + toXml);
            RequestBody body = RequestBody.create(null, toXml);
            OkGo.<String>post(Api.getFaceAuthInfo)
                    .tag(Api.getFaceAuthInfo)
                    .upRequestBody(body)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String payVoucher = response.body();
                            LogUtils.d("获取到的微信支付凭证" + payVoucher);
                            try {
                                String return_code = Tools.parseGetAuthInfoXML(payVoucher, WxConstant.RETURN_CODE);
                                if ("SUCCESS".equals(return_code)) {
                                    //获取微信凭证成功
                                    String authinfo = Tools.parseGetAuthInfoXML(payVoucher, "authinfo");
                                    /*获取到authInfo有效时间。在这个有效时间只能就可以不用重复获取autoInfo*/
                                    String expires_in = Tools.parseGetAuthInfoXML(payVoucher, "expires_in");
                                    //这里使用sp储存这个时间和autoInfo(这里储存在sp中方便，只做演示，如果考虑安全性可以放到加密数据库中)
                                    SPUtils.putString(Tools.getContext(), Constant.SP_PAY_AUTOINFO, authinfo);
                                    //储存过期时间.用来判断是否需要重新获取autoInfo
                                    SPUtils.putLong(Tools.getContext(), Constant.SP_PAY_EXPIRES_IN, (expires_in == null) ? 0L : Long.valueOf(expires_in));
                                    LogUtils.d("获取到的微信支付凭证infoXML" + authinfo);
                                    mView.wxFacePayVoucherSuccess(authinfo);
                                } else {
                                    String return_msg = Tools.parseGetAuthInfoXML(payVoucher, WxConstant.RETURN_MSG);
                                    mView.initWxPayFailed("获取微信凭证失败" + return_msg, NetStateEnum.three);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mView.initWxPayFailed("获取微信凭证失败" + e.getMessage(), NetStateEnum.three);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            mView.initWxPayFailed("获取微信凭证失败" + response.body().toString(), NetStateEnum.three);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 版  本 : 1.0
     * 操作人 : yzhg
     * 描  述 : 调用人脸识别
     */
    @Override
    public void getWxPayFaceCode(HashMap<String, String> map) {
        WxPayFace.getInstance().getWxpayfaceCode(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) {
                if (!Tools.isSuccessInfo(map)) {
                    ToastUtils.show("支付失败");
                    return;
                }
                final String code = (String) map.get(WxConstant.RETURN_CODE);
                ThreadUtils.runOnMainThread(() -> {
                    if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                        String faceCode = (String) map.get(WxConstant.FACE_CODE);
                        String openId = (String) map.get(WxConstant.OPEN_ID);
                        String sub_open_id = (String) map.get(WxConstant.SUB_OPEN_ID);
                        mView.getWxPayFaceCodeSuccess(map.toString(), faceCode, openId, sub_open_id);
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                        ToastUtils.show("用户取消");
                        mView.userCancelFacePay(WxConstant.USER_CANCEL_FACE_PAY);
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                        ToastUtils.show("扫码支付");
                        mView.userCancelFacePay(WxConstant.USER_BAR_FACE_PAY);
                    } else if (TextUtils.equals(code, "FACEPAY_NOT_AUTH")) {
                        ToastUtils.show("无即时支付无权限");
                        mView.userCancelFacePay(WxConstant.USER_NO_PERMISSION);
                    } else {
                        ToastUtils.show("失败");
                        mView.userCancelFacePay(WxConstant.USER_PAY_FAILED);
                    }
                });
            }
        });
    }

    /*
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:
     *      开始支付
     */
    @Override
    public void startWxPayFace(String toXml) throws Exception {
        RequestBody body = RequestBody.create(null, toXml);
        OkGo.<String>post(Api.getFacePay)
                .tag(Api.getFacePay)
                .upRequestBody(body)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String payVoucher = response.body();
                        LogUtils.d("微信支付流程完毕" + payVoucher);
                        String result = null;
                        try {
                            result = Tools.parseGetAuthInfoXML(payVoucher, WxConstant.RETURN_CODE);
                            if ("SUCCESS".equals(result)) {
                                mView.startWxPayFace(payVoucher);
                            } else {
                                mView.startWxPayFace(Tools.parseGetAuthInfoXML(payVoucher, WxConstant.RETURN_MSG));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.startWxPayFace(e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        mView.initWxPayFailed("微信支付流程完毕", NetStateEnum.four);
                    }
                });
    }

}
