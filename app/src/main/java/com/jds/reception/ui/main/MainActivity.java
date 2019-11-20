package com.jds.reception.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jds.reception.R;
import com.jds.reception.base.mvp.activity.MVPBaseActivity;
import com.jds.reception.base.mvp.base.NetStateEnum;
import com.jds.reception.constant.Constant;
import com.jds.reception.constant.WxConstant;
import com.jds.reception.ui.pay_result.PaySuccess;
import com.jds.reception.utils.LogUtils;
import com.jds.reception.utils.SPUtils;
import com.jds.reception.utils.TextWatcherListener;
import com.jds.reception.utils.ThreadUtils;
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
    private TextView scanBarCode;

    /*记录当前时间为订单号*/
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
         *      如果需要判断是否安装了微信刷脸付APP，这里提供微信刷脸付的包名：com.tencent.wxpayface
         *
         *   如果返回null则说明本地没有安装微信刷脸APP,可做相应处理（我们是直接预装软件，卸载不了）
         *   public static PackageInfo getWxFaceAppVersion(Context context) {
         *         List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
         *         for (int i = 0; i < packages.size(); i++) {
         *             PackageInfo packageInfo = packages.get(i);
         *             String packageName = packageInfo.packageName;
         *             if ("com.tencent.wxpayface".equals(packageName)) {
         *                 return packageInfo;
         *             }
         *         }
         *         return null;
         *     }
         */
        mPresenter.initWxPay();

        edPhoneView = findViewById(R.id.EdPhone);
        eTInputMoney = findViewById(R.id.ETInputMoney);
        butStartPay = findViewById(R.id.ButStartPay);
        scanBarCode = findViewById(R.id.tv_Scan_Bar_Code);

        /**
         *  版  本 : 1.0
         *  操作人 : yzhg
         *  描  述 : 人脸支付第二步
         *      获取RawData（从这里开始进行支付）
         */
        butStartPay.setOnClickListener(v -> {
            startFacePay();
        });


        //监听金额输入框
        eTInputMoney.addTextChangedListener(new TextWatcherListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String payMoney = s.toString();
                if (payMoney.isEmpty()) {
                    //关闭扫码支付
                    stopWxScanBar();
                } else {
                    //调起扫码支付
                    initWxScanBar();
                }
            }
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
        LogUtils.d("微信刷脸支付初始化成功");
    }


    /**
     * 操作人 : yzhg
     * 描  述 : 开启刷脸支付
     * <p>
     * 这里判断autoInfo是否过期。如果过期则重新获取rawData
     */
    private void startFacePay() {
        anInt = System.currentTimeMillis();
        String autoInfo = SPUtils.getString(Tools.getContext(), Constant.SP_PAY_AUTOINFO, "");
        if ("".equals(autoInfo)) {
            //本地储存的没有autoInfo直接去获取rawData
            getFaceRawData();
        } else {
            long getAutoInfoTime = SPUtils.getLong(Tools.getContext(), Constant.SP_PAY_RAWDATA, 0L);
            long expires_in = SPUtils.getLong(Tools.getContext(), Constant.SP_PAY_EXPIRES_IN, 0L);
            long time_difference = (int) (System.currentTimeMillis() - getAutoInfoTime);
            //expires_in  提供的单位是秒，这里需要转换为毫秒值
            if (time_difference > expires_in * 1000) {
                //autoInfo已经过期，重新获取rawData
                getFaceRawData();
            } else {
                //autoInfo没有过去。直接调用刷脸支付
                wxFacePayVoucherSuccess(autoInfo);
            }
        }
    }


    private void getFaceRawData() {
        /*开始获取RawData*/
        mPresenter.getWxPayFaceRawData();
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
    }


    /**
     * 版  本 : 1.0
     * 操作人 : yzhg
     * 描  述 : 获取到人脸支付凭证  调用微信刷脸支付程序进行刷脸动作
     */
    @Override
    public void wxFacePayVoucherSuccess(String authinfo) {
        String money = eTInputMoney.getText().toString().trim();
        String edPhone = edPhoneView.getText().toString().trim();
        if ("".equals(money)) {
            ToastUtils.show("请输入金额");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", WxConstant.APP_ID);
        map.put("mch_id", WxConstant.MCH_ID);
        map.put("sub_appid", WxConstant.SUB_APP_ID);
        map.put("sub_mch_id", WxConstant.SUB_MCH_ID);
        map.put("store_id", "055038");
        if (!edPhone.isEmpty()) {
            map.put("telephone", edPhone);
        }
        map.put("out_trade_no", anInt + "");
        map.put("total_fee", String.valueOf((int) (Double.valueOf(money) * 100)));
        map.put("face_code_type", "0");
        map.put("ignore_update_pay_result", "0");
        map.put("face_authtype", "FACEPAY");
        map.put("authinfo", authinfo);
        map.put("ask_face_permit", "0");
        map.put("ask_ret_page", "1");
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
        String money = eTInputMoney.getText().toString().trim();
        if (money.isEmpty()) {
            ToastUtils.show("请输入金额");
            return;
        }
        String edPhone = edPhoneView.getText().toString().trim();
        Map<String, String> map = new HashMap<>();
        map.put("appid", WxConstant.APP_ID);
        map.put("sub_appid", WxConstant.SUB_APP_ID);
        map.put("sub_openid", "");
        map.put("mch_id", WxConstant.MCH_ID);
        map.put("sub_mch_id", WxConstant.SUB_MCH_ID);
        map.put("device_info", "" + (System.currentTimeMillis() / 100000));
        map.put("nonce_str", "" + (System.currentTimeMillis() / 100000));
        map.put("body", "小杨服饰专卖店");
        map.put("out_trade_no", String.valueOf(anInt));
        map.put("total_fee", String.valueOf((int) (Double.valueOf(money) * 100)));
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
        try {
            String toXml = Tools.mapToXml(map);
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

    /**
     * 操作人 : yzhg
     * 描  述 : 用户支付失败
     */
    @Override
    public void userCancelFacePay(int cancelType) {
        if (cancelType == WxConstant.USER_CANCEL_FACE_PAY) {
            //用户取消支付
            LogUtils.d("用户取消了支付");
            ToastUtils.show("用户取消了支付");
        } else if (cancelType == WxConstant.USER_BAR_FACE_PAY) {
            //用户使用扫码支付
            LogUtils.d("用户点击了扫码支付");
            ToastUtils.show("用户点击了扫码支付");
            new Handler().postDelayed(MainActivity.this::initWxScanBar, 500);
        } else if (cancelType == WxConstant.USER_NO_PERMISSION) {
            //用户没有权限
            LogUtils.d("没有权限");
            ToastUtils.show("没有权限");
        } else if (cancelType == WxConstant.USER_PAY_FAILED) {
            //用户支付失败
            LogUtils.d("支付失败");
            ToastUtils.show("支付失败");
        }
    }


    @Override
    public void initWxPayFailed(String failedText, NetStateEnum netStateEnum) {
        if (netStateEnum == NetStateEnum.one) {
            LogUtils.d("初始化微信人脸支付失败" + failedText);
            ToastUtils.show("初始化微信人脸支付失败" + failedText);
        } else if (netStateEnum == NetStateEnum.two) {
            ToastUtils.show("获取RawData失败" + failedText);
            LogUtils.d("获取RawData失败" + failedText);
        } else if (netStateEnum == NetStateEnum.three) {
            ToastUtils.show("获取支付凭证失败" + failedText);
            LogUtils.d("获取支付凭证失败" + failedText);
        }
    }


    /**
     * 版  本 : 1.0
     * 操作人 : yzhg
     * 描  述 : 扣款成功  支付完成
     */
    @Override
    public void startWxPayFace(String successText) {
        Intent intent = new Intent(MainActivity.this, PaySuccess.class);
        startActivity(intent);
        //  Map<String, String> map = new HashMap<>();
        //  map.put("appid", WxConstant.APP_ID);
        //  map.put("sub_appid", "");
        //  map.put("sub_openid", "");
        //  map.put("mch_id", WxConstant.MCH_ID);
        //  map.put("store_id", "055038");
        //  map.put("authinfo", autoInfo);
        //  map.put("payresult", "SUCCESS");
        //  WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
        //      @Override
        //      public void response(Map map) throws RemoteException {
        //          String returnCode = map.get(WxConstant.RETURN_CODE).toString();
        //          if ("SUCCESS".equals(returnCode)) {
        //              Intent intent = new Intent(MainActivity.this, PaySuccess.class);
        //              startActivity(intent);
        //          } else {
        //              ToastUtils.show("支付失败");
        //          }
        //      }
        //  });
    }


    /**
     * 操作人 : yzhg
     * 描  述 : 开启扫码支付
     */
    private void initWxScanBar() {
        WxPayFace.getInstance().startCodeScanner(new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (Tools.isSuccessInfo(info)) {
                    String code_msg = (String) info.get(WxConstant.CODE_MSG);
                    ToastUtils.show("获取到二维码信息" + code_msg);
                    LogUtils.d("获取到二维码信息" + code_msg);
                    /**
                     * 操作人 : yzhg
                     * 描  述 : 拿到二维码信息之后，去做支付操作
                     */
                    ThreadUtils.runOnMainThread(() -> scanBarCode.setText("获取到二维码信息" + code_msg));
                } else {
                    ToastUtils.show("扫码失败");
                    LogUtils.d("扫码失败");
                }
            }
        });
    }

    //关闭扫码
    private void stopWxScanBar() {
        WxPayFace.getInstance().stopCodeScanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWxScanBar();
        //释放掉内存
    }
}
