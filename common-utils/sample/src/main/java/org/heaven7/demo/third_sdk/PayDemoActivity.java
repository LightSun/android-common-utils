package org.heaven7.demo.third_sdk;

import android.os.Bundle;

import com.heaven7.third.core.SdkFactory;
import com.heaven7.third.pay.WeixinPayHelper;
import com.heaven7.third.pay.ZhifubaoHelper;

import org.heaven7.core.util.Toaster;
import org.heaven7.demo.BaseActivity;
import org.heaven7.demo.R;

/**
 * 需要配置intentfilter
 * <intent-filter>
 <action android:name="android.intent.action.VIEW"/>
 <category android:name="android.intent.category.DEFAULT"/>
 <data android:scheme="xxxxxxxxxx"/> <!--微信id,支付必须配置-->
 </intent-filter>
 * Created by heaven7 on 2016/2/4.
 */
public class PayDemoActivity extends BaseActivity {

    static final int PAY_WITH_ZHIFUBAO  = 1;
    static final int PAY_WITH_WEIXIN    = 2;

    int mPayMode = PAY_WITH_ZHIFUBAO;

    private WeixinPayHelper mWeixinPayHelper;
    private ZhifubaoHelper mZhifubaoPayHelper;

    @Override
    protected int getlayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initView() {
          initPayConfig();
    }

    /** you need to init your-self config */
    private void initPayConfig() {
        SdkFactory.PayConfig.sWeixin_app_Id    ="xxx";
        SdkFactory.PayConfig.sWeixin_merchant_id ="xxx";
        SdkFactory.PayConfig.sWeixin_pay_app_key ="xxx";
        SdkFactory.PayConfig.sZhifubao_account ="xxx";
        SdkFactory.PayConfig.sZhifubao_pid ="xxx";
        SdkFactory.PayConfig.sZhifubao_rsa_key ="xxx";

        mWeixinPayHelper = SdkFactory.getWeixinPayHelper(this);
        mZhifubaoPayHelper = SdkFactory.getZhifubaoHelper();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    void doPay(int payMode){
        mPayMode = payMode;
        switch (payMode){
            case PAY_WITH_WEIXIN:
                payWithWeixin();
                break;
            case PAY_WITH_ZHIFUBAO:
                payWithZhifubao();
                break;

            default:
            throw new RuntimeException("");
        }
    }

    private void payWithZhifubao() {
        String orderId = "";          //订单号
        String productName = "";      //产品名称
        String productDesc = "";      //产品描述
        String price = "";            //支付宝元为单位,比如 1分钱 传 0.01
        String notifyUrl = "";        //回调地址
        SdkFactory.getZhifubaoHelper().pay(this, orderId, productName, productDesc,
                price, notifyUrl, new ZhifubaoHelper.IZhifubaoPayCallback() {
            @Override //支付失败
            public void onError(String msg) {

            }
            @Override //支付宝未安装
            public void onZhifubaoNotExist() {

            }

            @Override //支付成功,
            public void onPaySuccess(String resultInfo, String memo) {

            }

            @Override //支付的结果不确定
            public void onPayIndeterminate(String resultInfo, String memo) {

            }
        });
    }

    private void payWithWeixin() {
/**
 * //参数依次： activity, 订单号，产品
 * sendPay(Activity activity, String orderId,String productBody, String productDetail,
 * String amount, String extra, String notifyUrl, IWeiXinPayCallback callback)
 */
        String orderId = "xxxx";
        String productBody = "xxxx";
        String productDetail = "xxxx";
        String amount = "1";       //微信分为单位。 1分 传 1
        String extra  = "透传参数";
        String notifyUrl = "回调给你服务器的地址";

        SdkFactory.getWeixinPayHelper(this).sendPay(this, orderId, productBody, productDetail,
                amount, extra, notifyUrl, new WeixinPayCallbackImpl(getToaster(),orderId ) {
                    @Override
                    protected void onPayResult(String orderId) {
                             //这里应该调用接口访问服务器 ， 去获取支付结果
                    }

                    @Override
                    protected void onPayCallback() {
                           //一般是关闭dialog之类的
                    }
                });
    }

    @Override
    protected void onDestroy() {
        mZhifubaoPayHelper.reset();
        mWeixinPayHelper.reset();
        super.onDestroy();
    }

    public static abstract class WeixinPayCallbackImpl implements WeixinPayHelper.IWeiXinPayCallback {
        private static boolean sDebug = true;

        final String orderId;
        final Toaster toaster;

        public WeixinPayCallbackImpl(Toaster toaster, String orderId) {
            this.orderId = orderId;
            this.toaster = toaster;
        }

        @Override
        public void onError(String msg) {
            onPayCallback();
            System.out.println("payWithWeixin onError : " + msg);
            showToastIfDebug("payWithWeixin " + msg);
        }

        public void showToastIfDebug(String msg) {
            if (sDebug)
                toaster.show(msg);
        }
        public void showToast(int resId) {
            toaster.show(resId);
        }
        public void showToast(String msg) {
            toaster.show(msg);
        }

        @Override
        public void onResponse(int errCode, String
                errStr) { //errCode =0代表成功，-2 用户取消，-1 错误
            onPayCallback();
            String msg;

            boolean userCancled = false;
            switch (errCode) {
                case 0:
                    msg = "success";
                    break;
                case -2:
                    msg = "user cancel";
                    userCancled = true;
                    break;
                default:
                case -1:
                    msg = "error " + errStr;
                    break;
            }
            System.out.println("payWithWeixin onResponse : " + errStr);
            showToastIfDebug("Weixin pay : " + msg);
            if(!userCancled)
                onPayResult(orderId);
            else{
                showToast("支付取消");
            }
        }

        /**
         * on pay result of sdk,you need to get result from server
         */
        protected abstract void onPayResult(String orderId);

        /** 支付操作完成， 成功 or 失败 都会调用*/
        protected abstract void onPayCallback();

        @Override
        public void onWexinNotSupport() {
            onPayCallback();
            showToast("Wexin Not Support");
        }
    }

}
