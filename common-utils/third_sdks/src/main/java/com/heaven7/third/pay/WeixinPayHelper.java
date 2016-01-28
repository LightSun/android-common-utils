package com.heaven7.third.pay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.heaven7.third.core.AsyncTask2;
import com.heaven7.third.core.IResetable;
import com.heaven7.third.core.ITaskManager;
import com.heaven7.third.core.SdkFactory;
import com.heaven7.third.core.internal.SdkConfig;
import com.heaven7.third.core.internal.TaskManagerImpl;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 微信支付: 必须配置
 *   <activity android:name=".activity.PayActivity"
 android:screenOrientation="portrait"
 android:theme="@style/Theme.common.background"
 >
 <intent-filter>
 <action android:name="android.intent.action.VIEW"/>
 <category android:name="android.intent.category.DEFAULT"/>
 <data android:scheme="wx04b6a1a9282dc734"/> <!--微信id,支付必须配置-->
 </intent-filter>
 </activity>
 * Created by heaven7 on 2015/9/20.
 * */
public class WeixinPayHelper implements IResetable {
    private static final String TAG = "WeixinPay";
    private static final boolean sDebug = true;

    private IWXAPI mWxApi;
    private final IWXAPIEventHandler mWeixinHandler = new IWXAPIEventHandler() {
        @Override
        public void onReq(BaseReq baseReq) {
        }

        @Override
        public void onResp(BaseResp resp) {
            if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                if (mPayCallback != null) {
                    mPayCallback.onResponse(resp.errCode, resp.errStr);
                    mPayCallback = null;
                }
            }
            mProcessing.set(false);
        }
    };
    private IWeiXinPayCallback mPayCallback;
    private WeakReference<Activity> mWeakActivity;
    private final AtomicBoolean mProcessing = new AtomicBoolean(false);

    private final ITaskManager mTaskManager = new TaskManagerImpl();

    public WeixinPayHelper() {
    }

    public WeixinPayHelper initWXPayApi(Context context) {
        //支付。对应的activity 需要配置scheme  SdkConfig.WEIXIN_PAY_APP_ID
        mWxApi = WXAPIFactory.createWXAPI(context.getApplicationContext(),
                SdkFactory.PayConfig.sWeixin_app_Id, false);
        mWxApi.registerApp(SdkFactory.PayConfig.sWeixin_app_Id);
        return this;
    }

    public void reset(){
        mTaskManager.reset();
        mProcessing.set(false);
        mPayCallback = null;
    }

    public boolean onHandleIntent(Intent intent) {
        return mWxApi.handleIntent(intent, mWeixinHandler);
    }

    public boolean isProcessing() {
        return mProcessing.get();
    }

    /**
     * 支付-》取消后-》再去订单详情支付--提示订单号重复，即使关闭订单也无效
     * 原因： 微信支付，不管你有没支付成功，一个orderId只能发起一次请求
     **/

    public void sendCloseOrder(String orderId) {

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new NameValuePair("appid", SdkFactory.PayConfig.sWeixin_app_Id));
        pairs.add(new NameValuePair("mch_id", SdkFactory.PayConfig.sWeixin_merchant_id));
        pairs.add(new NameValuePair("out_trade_no", orderId));
        pairs.add(new NameValuePair("nonce_str", String.valueOf(System.currentTimeMillis())));

        String sign = WeixinUtil.sign(pairs, true);
        logIfNeed("sendCloseOrder_sign = " + sign);
        pairs.add(new NameValuePair("sign", sign));

        String entity = WeixinUtil.toXml(pairs);
        new CloseOrderTask(mTaskManager).execute(entity);
    }
    /**
     * @param activity
     * @param orderId
     * @param productBody   商品描述
     * @param productDetail 商品详情
     * @param amount        订单金额,int类型(分为单位)
     * @param extra         透传参数
     * @param notifyUrl     server通知回调
     * @param callback
     */

    public void sendPay(Activity activity, String orderId,
                        String productBody, String productDetail, String amount,
                        String extra, String notifyUrl, IWeiXinPayCallback callback) {
     /*   if (isProcessing()) {
            callback.onError("is processing");
            return;
        }*/

        if (!mWxApi.isWXAppSupportAPI()) {
            callback.onWexinNotSupport();
            return;
        }
        mProcessing.set(true);
        this.mPayCallback = callback;
        this.mWeakActivity = new WeakReference<>(activity);
        List<NameValuePair> pairs = new ArrayList<>();
        buildParams(pairs, orderId, productBody, productDetail, amount, extra, notifyUrl);
        //生成xml
        String entity = WeixinUtil.toXml(pairs);

        //微信支付， 生成的xml最好ISO8859-1编码，否则当有汉字时，签名不通过.
        // 这一步最关键 我们把字符转为 字节后,再使用“ISO8859-1”进行编码，得到“ISO8859-1”的字符串
        try {
            entity = new String(entity.getBytes(), "ISO8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GetPrepayIdTask(mTaskManager,"", "加载中...请稍后").execute(entity);
    }

    private static void buildParams(List<NameValuePair> pairs, String orderId,
                                    String productBody, String productDetail, String amount,
                                    String extra, String notifyUrl) {
        if (!TextUtils.isEmpty(extra))
            pairs.add(new NameValuePair("attach", extra));         //透传参数
        if (!TextUtils.isEmpty(productDetail))
            pairs.add(new NameValuePair("detail", productDetail)); //商品详情
        pairs.add(new NameValuePair("appid", SdkFactory.PayConfig.sWeixin_app_Id));

        pairs.add(new NameValuePair("body", productBody));
        pairs.add(new NameValuePair("mch_id", SdkFactory.PayConfig.sWeixin_merchant_id));
        pairs.add(new NameValuePair("nonce_str", String.valueOf(System.currentTimeMillis())));
        pairs.add(new NameValuePair("notify_url", notifyUrl));
        pairs.add(new NameValuePair("out_trade_no", orderId));
        pairs.add(new NameValuePair("spbill_create_ip", "127.0.0.1"));
        pairs.add(new NameValuePair("total_fee", amount));
        pairs.add(new NameValuePair("trade_type", "APP"));
        //openid  用户标示

        String sign = WeixinUtil.sign(pairs, true);
        logIfNeed("buildParams_sign = " + sign);
        pairs.add(new NameValuePair("sign", sign));
    }

    private Activity getActivity() {
        return mWeakActivity != null ? mWeakActivity.get() : null;
    }

    private static void logIfNeed(String msg) {
        if (sDebug)
            Log.i(TAG, msg);
    }

    public interface IWeiXinPayCallback {

        void onError(String msg);

        void onResponse(int errCode, String errStr); //from weixin BaseResp

        //一般是没安装微信
        void onWexinNotSupport();
    }

    private class CloseOrderTask extends AsyncTask2<String, Void, Map<String, String>> {

        public CloseOrderTask(ITaskManager manager) {
            super(manager);
        }

        @Override
        protected Map<String, String> doInBackground(String... params) {
            String entity = params[0];
            String url = SdkConfig.URL_WEIXIN_PAY_CLOSE_ORDER;
            logIfNeed("entity = " + entity);

            byte[] buf = WeixinUtil.httpPost(url, entity);
            if (buf == null)
                return null;
            else {
                String content = new String(buf);
                logIfNeed("CloseOrderTask----> content = " + content);
                return WeixinUtil.decodeXml(content);
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> map) {
            if (map == null) {
                logIfNeed("error:  weixin close order failed !");
            } else {
                //TODO
            }
            super.onPostExecute(map);
        }
    }

    private class GetPrepayIdTask extends AsyncTask2<String, Void, Map<String, String>> {

        private ProgressDialog dialog;
        private String title;
        private String message;

        public GetPrepayIdTask(ITaskManager manager,String title, String message) {
            super(manager);
            this.message = message;
            this.title = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity activity = getActivity();
            if (activity == null) return;
            if (message != null)
                dialog = WeixinUtil.showDialog(activity, title, message);
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result == null) {
                onPayCallbackNull("get prepare_id return null");
            } else {
                // String prepare_id = result.get("prepay_id");
                String prepay_id = result.get("prepay_id");
                if (prepay_id == null) {
                    onPayCallbackNull("get prepare_id return data ,but prepare_id = null");
                } else
                    sendToWeixinPay(prepay_id);
            }
            super.onPostExecute(result);
        }

        @Override
        protected Map<String, String> doInBackground(String... params) {

            String url = SdkConfig.URL_WEIXIN_PAY_GET_PREPARED_ID;
            String entity = params[0];

            logIfNeed("entity = " + entity);

            byte[] buf = WeixinUtil.httpPost(url, entity);
            if (buf == null)
                return null;
            else {
                String content = new String(buf);
                logIfNeed("get prepare_id return content = " + content);
                return WeixinUtil.decodeXml(content);
            }
        }
    }

    private void sendToWeixinPay(String prepay_id) {
        String randomStr = UUID.randomUUID().toString();
        if (randomStr.length() > 32) {
            randomStr = randomStr.substring(0, 32);
        }
        PayReq req = new PayReq();
        req.appId = SdkFactory.PayConfig.sWeixin_app_Id;
        req.partnerId =  SdkFactory.PayConfig.sWeixin_merchant_id;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = randomStr;
        req.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);//秒

        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("appid", req.appId));
        params.add(new NameValuePair("noncestr", req.nonceStr));
        params.add(new NameValuePair("package", req.packageValue));
        params.add(new NameValuePair("partnerid", req.partnerId));
        params.add(new NameValuePair("prepayid", req.prepayId));
        params.add(new NameValuePair("timestamp", req.timeStamp));
        req.sign = WeixinUtil.sign(params, false);
        logIfNeed("PayReq_sign = " + req.sign);

        mWxApi.sendReq(req);
    }

    private void onPayCallbackNull(String msg) {
        if (mPayCallback != null) {
            mPayCallback.onError(msg);
            mPayCallback = null;
        }
        mProcessing.set(false);
    }

    public static class NameValuePair {
        public String key;
        public String value;

        public NameValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
