package com.jds.reception.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jds.reception.R;
import com.jds.reception.base.mvp.activity.MVPBaseActivity;
import com.jds.reception.base.mvp.base.NetStateEnum;
import com.jds.reception.constant.WxConstant;
import com.jds.reception.ui.pay_result.PaySuccess;
import com.jds.reception.utils.LogUtils;
import com.jds.reception.utils.Tools;
import com.jds.reception.utils.toast.ToastUtils;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 作 者: yzhg
 * 历 史: (版本) 1.0
 * 描 述:  微信人脸支付Activity
 *
 */
public class MainActivity extends MVPBaseActivity<MainContract.MainView, MainPresenter> implements MainContract.MainView {

    /*输入的金额*/
    private EditText eTInputMoney;
    /*输入的手机号*/
    private EditText edPhoneView;
    /*开始支付按钮*/
    private Button butStartPay;
    /*复制*/
    private Button butCopyResult;
    /*初始化*/
    private Button butInitView;
    /*支付logo*/
    private TextView butPayResult;
    /*autoInfo*/
    private String authinfo = "";
    /*是否成功*/
    private boolean isInitSuccess = false;
    private StringBuffer sb = new StringBuffer();
    private long anInt = 0L;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *  版  本 : 1.0
         *  操作人 : yzhg
         *  描  述 : 微信人脸支付第一步  初始化微信操作
         */
        mPresenter.initWxPay();

        edPhoneView = findViewById(R.id.EdPhone);
        eTInputMoney = findViewById(R.id.ETInputMoney);
        butStartPay = findViewById(R.id.ButStartPay);
        butCopyResult = findViewById(R.id.ButCopyResult);
        butPayResult = findViewById(R.id.ButPayResult);
        butInitView = findViewById(R.id.butInitView);

        /**
         * 点击按钮进行手动初始化微信操作
         */
        butInitView.setOnClickListener(v -> {
            mPresenter.initWxPay();
        });

        /**
         *  版  本 : 1.0
         *  操作人 : yzhg
         *  描  述 : 人脸支付第二步
         *      获取RawData（从这里开始进行支付）
         */
        butStartPay.setOnClickListener(v -> {
            if (isInitSuccess) {
                anInt = System.currentTimeMillis();
                butPayResult.setText("");
                mPresenter.getWxPayFaceRawData();
            } else {
                ToastUtils.show("人脸支付初始化失败");
                mPresenter.initWxPay();
            }
        });


        butCopyResult.setOnClickListener(v -> {
            if ("".equals(butPayResult)) {
                ToastUtils.show("无复制内容");
                return;
            }
            Tools.copyToClipboard(this, butPayResult.getText().toString().trim());
        });
    }

    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:
     * 初始化微信人脸支付回调
     */
    @Override
    public void initWxPaySuccess() {
        sb.append("初始化成功\n");
        showResult();
        isInitSuccess = true;
    }


    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:
     * 微信获取RawData成功 ，开始进行第三步authinfo
     */
    @Override
    public void wxPayFaceRawDataSuccess(String rawdata) {

        /**
         *  版  本 : 1.0
         *  操作人 : yzhg
         *  描  述 : 微信人脸支付第三步 通过RawData获取人脸支付凭证authinfo
         */
        mPresenter.getWxFacePayVoucher(rawdata);
        sb.append("获取RawData成功\n");
        showResult();
    }


    /**
     * 版  本 : 1.0
     * 操作人 : yzhg
     * 描  述 : 获取到人脸支付凭证  调用微信刷脸支付程序进行刷脸动作
     */
    @Override
    public void wxFacePayVoucherSuccess(String authinfo) {
        this.authinfo = authinfo;
        sb.append("获取支付凭证成功\n");
        showResult();
        String money = eTInputMoney.getText().toString().trim();
        String edPhone = edPhoneView.getText().toString().trim();
        if ("".equals(money)) {
            ToastUtils.show("请输入金额");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", WxConstant.APP_ID);
        map.put("mch_id", WxConstant.MCH_ID);
        //    map.put("sub_appid", "");
        map.put("sub_mch_id", "1484267682");
        map.put("store_id", "055038");
        if (!edPhone.isEmpty()) {
            map.put("telephone", edPhone);
        }
        map.put("out_trade_no", anInt + "");
        map.put("total_fee", money);
        map.put("face_code_type", "0");
        map.put("ignore_update_pay_result", "0");
        map.put("face_authtype", "FACEPAY");
        map.put("authinfo", authinfo);
        map.put("ask_face_permit", "0");
        map.put("ask_ret_page", "1");
        LogUtils.d(authinfo);

        /**
         *  版  本 : 1.0
         *  操作人 : yzhg
         *  描  述 : 人脸支付第四步，调用摄像头进行刷脸
         */
        mPresenter.getWxPayFaceCode(map);
    }

    /**
     * 作 者: yzhg
     * 描 述:
     * 刷脸已经成功，人脸验证成功后进行支付操作
     */
    @Override
    public void getWxPayFaceCodeSuccess(String faceText, String faceCode, String openId, String sub_open_id) {
        sb.append("获取faceCode和openId成功---").append(faceText).append("\n\n");
        String money = eTInputMoney.getText().toString().trim();
        String edPhone = edPhoneView.getText().toString().trim();
        Map<String, String> map = new HashMap<>();
        map.put("appid", WxConstant.APP_ID);
        //  map.put("sub_appid", "");
        //     map.put("sub_openid", "");
        map.put("mch_id", WxConstant.MCH_ID);
        map.put("sub_mch_id", "1484267682");
        map.put("device_info", "" + (System.currentTimeMillis() / 100000));
        map.put("nonce_str", "" + (System.currentTimeMillis() / 100000));
        map.put("body", "小杨服饰专卖店");
        map.put("out_trade_no", anInt + "");
        map.put("total_fee", money);
        map.put("spbill_create_ip", "192.168.56.1");
        map.put("openid", openId);
        map.put("face_code", faceCode);
        //按字典顺序排序
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());
        Collections.sort(infoIds, (o1, o2) -> (o1.getKey()).compareTo(o2.getKey()));
        //使用&符号进行频率
        String sbR = Tools.getStringBuffer(infoIds) + "&key=" + WxConstant.MCH_KEY_ID;
        //进行MD5加密之后  转大写
        String sign = Tools.encode(sbR).toUpperCase();
        map.put("sign", sign);
        sb.append("加密后传参：").append(map.toString()).append("\n\n");
        showResult();
        try {
            String toXml = Tools.mapToXml(map);
            sb.append("传递的XML文件：").append(toXml).append("\n\n");

            /**
             *  版  本 : 1.0
             *  操作人 : yzhg
             *  描  述 :  第五步：人脸验证已经成功开始 调用支付接口进行扣款
             */
            mPresenter.startWxPayFace(toXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initWxPayFailed(String failedText, NetStateEnum netStateEnum) {
        if (netStateEnum == NetStateEnum.one) {
            isInitSuccess = false;
            LogUtils.d("初始化微信人脸支付失败" + failedText);
            ToastUtils.show("初始化微信人脸支付失败" + failedText);
            sb.append("获取faceCode和openId成功---").append(failedText).append("\n\n");
        } else if (netStateEnum == NetStateEnum.two) {
            ToastUtils.show("获取RawData失败" + failedText);
            LogUtils.d("获取RawData失败" + failedText);
            sb.append("获取RawData失败---").append(failedText).append("\n\n");
        } else if (netStateEnum == NetStateEnum.three) {
            ToastUtils.show("获取支付凭证失败" + failedText);
            LogUtils.d("获取支付凭证失败" + failedText);
            sb.append("获取支付凭证失败---").append(failedText).append("\n\n");
        }
        showResult();
    }


    /**
     * 版  本 : 1.0
     * 操作人 : yzhg
     * 描  述 : 扣款成功  支付完成
     */
    @Override
    public void startWxPayFace(String successText) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", WxConstant.APP_ID);
        //  map.put("sub_appid", "");
        //     map.put("sub_openid", "");
        map.put("mch_id", WxConstant.MCH_ID);
        map.put("store_id", "055038");
        map.put("authinfo", authinfo);
        map.put("payresult", "SUCCESS");
        sb.append(successText).append("\n");
        WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
            @Override
            public void response(Map map) throws RemoteException {
                String returnCode = map.get(WxConstant.RETURN_CODE).toString();
                if ("SUCCESS".equals(returnCode)) {
                    sb.append("——————————————————————————————支付成功——————————————————————————————").append("\n");
                    Intent intent = new Intent(MainActivity.this, PaySuccess.class);
                    startActivity(intent);
                } else {
                    ToastUtils.show("支付失败");
                }
            }
        });
        showResult();
    }

    private void showResult() {
        butPayResult.setText(sb.toString());
    }
}
