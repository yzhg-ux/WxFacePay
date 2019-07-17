package com.jds.reception.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;

import com.jds.reception.constant.WxConstant;
import com.jds.reception.utils.toast.ToastUtils;
import com.tencent.wxpayface.WxfacePayCommonCode;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 类 名: Tools
 * 作 者: yzhg
 * 创 建: 2019/5/23 0023
 * 版 本: 2.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述:  全局工具类
 */
public class Tools {

    public static final String TAG = "人脸支付";

    private static Context context;

    /*
     * 作 者: yzhg
     * 描 述: 初始化工具类
     */
    public static void init(Context context) {
        Tools.context = context.getApplicationContext();
    }

    /*
     * 作 者: yzhg
     * 描 述: 获取全局上下文
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("请前往JDSApplication中初始化Tools工具类");
    }


    /*
     * 作 者: yzhg
     * 描 述: 获取字符串
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /*
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述:
     *     判断微信人脸识别操作是否成功
     */
    public static boolean isSuccessInfo(Map info) {
        if (info == null) return false;
        String code = (String) info.get(WxConstant.RETURN_CODE);
        String msg = (String) info.get(WxConstant.RETURN_MSG);
        LogUtils.d(TAG, "失败信息---" + msg + "---code码---" + code);
        return code != null && code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS);
    }


    /**
     *      * 将Map转换为XML格式的字符串
     *      *
     *      * @param data Map类型数据
     *      * @return XML格式的字符串
     *      * @throws Exception
     *      
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key : data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        } catch (Exception ex) {
        }
        return output;
    }

    public static String parseGetAuthInfoXML(String resultText, String indexText) throws Exception {
        InputStream is = new ByteArrayInputStream(resultText.getBytes());
        String result = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals(indexText)) {
                        eventType = parser.next();
                        result = parser.getText();
                    }
            }
            eventType = parser.next();
        }

        return result;
    }


    /**
     * 拼接
     *
     * @param infoIds
     * @return
     */
    @NonNull
    public static String getStringBuffer(List<Map.Entry<String, String>> infoIds) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, String> stringStringEntry = infoIds.get(i);
            if (stringStringEntry.getKey() == null) {
                stringStringEntry.getKey();
            }
            String key = stringStringEntry.getKey();
            Object val = stringStringEntry.getValue();
            if (i != infoIds.size() - 1) {
                if (val != null && !TextUtils.equals("", val.toString())) {
                    sb.append(key).append("=").append(val).append("&");
                }
            } else {
                if (val != null && !TextUtils.equals("", val.toString())) {
                    sb.append(key).append("=").append(val);
                }
            }
        }
        return sb.toString();
    }


    /**
     * 作 者: yzhg
     * 历 史: (版本) 1.0
     * 描 述: 密码的md5加密
     */
    public static String encode(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("text", text));
        ToastUtils.show("复制成功");
    }
}
