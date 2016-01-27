package com.heaven7.third.core;

import android.content.Context;

import com.heaven7.third.pay.WeixinPayHelper;
import com.heaven7.third.pay.ZhifubaoHelper;
import com.heaven7.third.share.QQHelper;
import com.heaven7.third.share.WeixinHelper;

/**
 * Created by heaven7 on 2016/1/27.
 */
public class SdkFactory {

    public static QQHelper getQQHelper(Context context){
        return Creator.QQ_HELPER.init(context);
    }
    public static WeixinHelper getWeixinHelper(Context context){
        return Creator.WEIXIN_HELPER.initWXApi(context);
    }
    public static WeixinPayHelper getWeixinPayHelper(Context context){
        return Creator.WEIXIN_PAY_HELPER.initWXPayApi(context);
    }
    public static ZhifubaoHelper getZhifubaoHelper(){
        return new ZhifubaoHelper() ;
    }

    private static class Creator{
        public static final WeixinPayHelper WEIXIN_PAY_HELPER = new WeixinPayHelper();
        public static final QQHelper QQ_HELPER = new QQHelper();
        public static final WeixinHelper WEIXIN_HELPER = new WeixinHelper();
    }

    public static class PayConfig{
        public static String sWeixin_app_Id ;
        public static String sWeixin_merchant_id ;
        public static String sWeixin_pay_app_key ;

        public static String sZhifubao_pid ;
        public static String sZhifubao_account ;
        public static String sZhifubao_rsa_key ;
    }

    public static class ShareConfig{
        public static String sWeixin_app_Id ;
        public static String sWeixin_app_secret ;
        public static String sQq_app_id ;
    }

}
