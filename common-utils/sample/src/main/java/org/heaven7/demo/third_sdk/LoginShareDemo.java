package org.heaven7.demo.third_sdk;

import android.content.Intent;
import android.os.Bundle;

import com.heaven7.third.core.SdkFactory;
import com.heaven7.third.share.QQHelper;
import com.heaven7.third.share.WeixinHelper;
import com.heaven7.third.share.weixin.UserInfoResult;

import org.heaven7.demo.BaseActivity;
import org.heaven7.demo.R;
import org.json.JSONObject;

/**
 * Created by heaven7 on 2016/2/4.
 */
public class LoginShareDemo extends BaseActivity {

    private QQHelper mQQHelper;
    private WeixinHelper mWeixinHelper;

    @Override
    protected int getlayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initView() {

        initConfig();
    }

    /*
     *  关于qq登陆必须配置的activity. 只能自己配置了.
     *  <activity
     android:name="com.tencent.tauth.AuthActivity"
     android:launchMode="singleTask"
     android:noHistory="true" >
     <intent-filter>
     <action android:name="android.intent.action.VIEW" />
     <category android:name="android.intent.category.DEFAULT" />
     <category android:name="android.intent.category.BROWSABLE" />
     <data android:scheme="tencent1104668213" /> //  每个app都不一样
     </intent-filter>
     </activity>
     */
    private void initConfig() {
        SdkFactory.ShareConfig.sQq_app_id = "xxx";
        SdkFactory.ShareConfig.sWeixin_app_Id = "xxx";
        SdkFactory.ShareConfig.sWeixin_app_secret = "xxx";

        mQQHelper = SdkFactory.getQQHelper(this);
        mWeixinHelper = SdkFactory.getWeixinHelper(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {


    }

    private void loginWithWeixin(){
       mWeixinHelper.sendAuthRequest(new WeixinHelper.IWXAuthCallback() {
           @Override //登陆ok
           public void onHandleResponse(UserInfoResult userInfo) {

           }

           @Override //登陆失败
           public void onRequestFailed(String url, String msg) {

           }
       });
    }
    private void shareWithWeixin(){
        //参数： 分享小图标图片的地址, 分享点击跳转的web地址, 标题，描述,是否分享到朋友圈，回调
     /*   mWeixinHelper.shareWebUrl(final String imgUrl,final String shareUrl,
        final String title,final String desc,
        final boolean shareToFriendCircle,final IWXShareCallback callback)*/
    }

    private void shareWithQQ(){
       // 参数看方法注释
        //activity,app名称，分享点击的跳转地址，标题，概要，小图标地址，回调
        /**
         * share(Activity activity,String appName,String jumpUrl,String title,String summary,
         String imageUrl,IQQCallback callbak)
         */
      /*  mQQHelper.share(Activity activity,String appName,String jumpUrl,String title,String summary,
                String imageUrl,IQQCallback callbak)*/
    }

    private  void loginWithQQ(){
        mQQHelper.login(this, new QQHelper.IQQCallback() {
            @Override //登陆失败
            public void onFailed(String msg) {

            }

            @Override  //登陆成功
            public void onSuccess(JSONObject response) {

            }

            @Override //用户取消
            public void onUserCancel() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //qq登陆必要的
        mQQHelper.onActivityResultByLogin(this, requestCode, resultCode, data);

        //qq分享必要的
        mQQHelper.onActivityResultByShare(this,requestCode,resultCode,data);
    }

    //微信分享回调
    private final WeixinHelper.IWXShareCallback mWeixinShareCallback = new WeixinHelper.IWXShareCallback() {
        @Override
        public void onShareSuccess() {
            // showToast("weixin_share_ success !!");
        }

        @Override
        public void onShareFailed(String msg) {
          //  Logger.i(TAG, "weixin_share_failed", msg);
        }
    };

    //qq回调, 登陆分享回调的接口
    private final QQHelper.IQQCallback mQQCallback = new QQHelper.IQQCallback() {
        @Override
        public void onFailed(String msg) {
            // showToast("qq_share:  onFailed(), msg = " + msg);
        }

        @Override
        public void onSuccess(JSONObject response) {
            // showToast("qq_share:  onSuccess(), msg = " + response.toString());
        }

        @Override
        public void onUserCancel() {
            // showToast("qq_share:  onUserCancel()");
        }
    };

}
