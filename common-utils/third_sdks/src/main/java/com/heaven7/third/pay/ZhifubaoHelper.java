package com.heaven7.third.pay;

import android.app.Activity;
import android.os.Build;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.heaven7.third.core.AsyncTask2;
import com.heaven7.third.core.IResetable;
import com.heaven7.third.core.ITaskManager;
import com.heaven7.third.core.SdkFactory;
import com.heaven7.third.core.internal.TaskManagerImpl;

import java.lang.ref.WeakReference;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 支付宝支付
 * Created by heaven7 on 2015/9/21.
 */
public class ZhifubaoHelper implements IResetable {

    private IZhifubaoPayCallback mCallback ;
    private WeakReference<Activity> mWeakActivity;
    private final AtomicBoolean mProcessing = new AtomicBoolean(false);
    private final ITaskManager mTaskManager = new TaskManagerImpl();

    /** called with the lifecycle of activity */
    public void reset(){
        mTaskManager.reset();
        //reset tag
        mProcessing.set(false);
        mCallback = null;
    }

    public void pay(Activity activity,final String orderId, final String productName ,final String productDesc,
                    final String price, final String notifyUrl ,IZhifubaoPayCallback callback){
        if(mProcessing.get()){
            callback.onError("zhifubao pay is processing.");
            return ;
        }
        mProcessing.set(true);
        this.mWeakActivity = new WeakReference<>(activity);
        this.mCallback = callback;
        CheckTask task = new CheckTask(mTaskManager) {
            @Override
            protected void onZhifubaoExist() {
                doPay(orderId, productName, productDesc, price, notifyUrl);
            }
        };
        AsyncTaskCompat.executeParallel(task,activity);
    }

    private Activity getActivity(){
        return mWeakActivity!=null? mWeakActivity.get() :null;
    }

    private void doPay(String orderId,String productName, String productDesc, String price,String notifyUrl) {
        String orderInfo = getOrderInfo(orderId,productName,productDesc,price,notifyUrl);
        // 对订单做RSA 签名
        String sign = SignUtil.sign(orderInfo, SdkFactory.PayConfig.sZhifubao_rsa_key);
        sign = Util.urlEncode(sign);
        if(sign == null){
            onPayFailed("sign urlencode failed");
            return;
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        AsyncTaskCompat.executeParallel(new InternalPayTask(mTaskManager), payInfo);
    }
    private static String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private void onPayFailed(String msg) {
        if(mCallback!=null){
            mCallback.onError(msg);
            mCallback = null;
        }
        mProcessing.set(false);
    }

    /**
     * @param orderId
     * @param subject      商品名称
     * @param body         商品描述
     * @param price        价格。比如"0.01"
     * @return
     */
    public static String getOrderInfo(String orderId,String subject, String body, String price,String notifyUrl) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + SdkFactory.PayConfig.sZhifubao_pid + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SdkFactory.PayConfig.sZhifubao_account + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notifyUrl
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"http://m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }
    private class InternalPayTask extends AsyncTask2<String,Void,String> {

        public InternalPayTask(ITaskManager manager) {
            super(manager);
        }

        @Override
        protected String doInBackground(String... params) {
            Activity activity = getActivity();
            if(activity == null || activity.isFinishing()){
                return "activity is null";
            }
            return new PayTask(activity).pay(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if("activity is null".equals(result)) {
                onPayFailed(result);
                return;
            }
            PayResult payResult = new PayResult(result);
            // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
            String resultInfo = payResult.getResult();

            String resultStatus = payResult.getResultStatus();

            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                onPaySuccess(resultInfo,payResult.getMemo());
            } else {
                // 判断resultStatus 为非“9000”则代表可能支付失败
                // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    onPayIndeterminate(resultInfo,payResult.getMemo());
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    onPayFailed(payResult.getMemo());
                }
            }
            super.onPostExecute(result);
        }
    }

    private void onPaySuccess(String resultInfo,String memo) {
        if(mCallback!=null){
            mCallback.onPaySuccess(resultInfo,memo);
            mCallback = null;
        }
        mProcessing.set(false);
    }
    /** 支付结果确认中,需从后台查询 */
    private void onPayIndeterminate(String resultInfo,String memo) {
        if(mCallback!=null){
            mCallback.onPayIndeterminate(resultInfo, memo);
            mCallback = null;
        }
        mProcessing.set(false);
    }

    private abstract class CheckTask extends AsyncTask2<Activity,Void,Boolean>{

        public CheckTask(ITaskManager manager) {
            super(manager);
        }

        @Override
        protected Boolean doInBackground(Activity... params) {
            return new com.alipay.sdk.app.PayTask(params[0]).checkAccountIfExist();
        }

        @Override
        protected void onPostExecute(Boolean exist) {
            if(!exist) onZhifubaoNotExist();
            onZhifubaoExist();
            super.onPostExecute(exist);
        }
        protected abstract void onZhifubaoExist();
    }

    private void onZhifubaoNotExist() {
        if(mCallback!=null){
            mCallback.onZhifubaoNotExist();
            mCallback = null;
        }
        mProcessing.set(false);
    }

    public class PayResult {
        private String resultStatus;
        private String result;
        private String memo;

        public PayResult(String rawResult) {

            if (TextUtils.isEmpty(rawResult))
                return;

            String[] resultParams = rawResult.split(";");
            for (String resultParam : resultParams) {
                if (resultParam.startsWith("resultStatus")) {
                    resultStatus = gatValue(resultParam, "resultStatus");
                }
                if (resultParam.startsWith("result")) {
                    result = gatValue(resultParam, "result");
                }
                if (resultParam.startsWith("memo")) {
                    memo = gatValue(resultParam, "memo");
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }

        private String gatValue(String content, String key) {
            String prefix = key + "={";
            return content.substring(content.indexOf(prefix) + prefix.length(),
                    content.lastIndexOf("}"));
        }

        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }

        /**
         * @return the memo
         */
        public String getMemo() {
            return memo;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }

    public interface IZhifubaoPayCallback{

        void onError(String msg);

        void onZhifubaoNotExist();

        void onPaySuccess(String resultInfo, String memo);

        void onPayIndeterminate(String resultInfo, String memo);
    }

    public static class SignUtil{

        private static final String ALGORITHM = "RSA";

        private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

        private static final String DEFAULT_CHARSET = "UTF-8";

        /**
         * 08-13 16:50:39.352: W/System.err(26987):
         * java.security.spec.InvalidKeySpecException: java.lang.RuntimeException:
         * error:0D0680A8:asn1 encoding routines:ASN1_CHECK_TLEN:wrong tag
         * @param content
         * @param privateKey
         * @return
         */
        public static String sign(String content, String privateKey) {
            try {
                PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                        Base64.decode(privateKey));
                KeyFactory keyf;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    keyf = KeyFactory.getInstance("RSA", "BC");
                }else {
                    keyf = KeyFactory.getInstance(ALGORITHM);//rsa
                }
                PrivateKey priKey = keyf.generatePrivate(priPKCS8);

                java.security.Signature signature = java.security.Signature
                        .getInstance(SIGN_ALGORITHMS);

                signature.initSign(priKey);
                signature.update(content.getBytes(DEFAULT_CHARSET));

                byte[] signed = signature.sign();

                return Base64.encode(signed);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
