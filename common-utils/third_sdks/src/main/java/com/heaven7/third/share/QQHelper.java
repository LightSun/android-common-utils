package com.heaven7.third.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.heaven7.third.core.SdkFactory;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * qq 登陆,分享:本app只支持分享图片文字。，声音暂时不用
 * Created by heaven7 on 2015/9/23.
 */
public class QQHelper {

    private static final String TAG          = "QQHelper";
    public static final String OPEN_ID       = "openid";
    public static final String NICK_NAME     = "nickname";
    public static final String HEAD_ICON     = "figureurl_qq_2";
    public static final String ACCESS_TOKEN  = "access_token";
    private Tencent mTencent;
    private static String sUserInfo;

    private IQQCallback mCallback;
    private WeakReference<Activity> mWeakActivity;

    private final UiListenerImpl mLoginListener = new UiListenerImpl() {
        @Override
        public void onComplete(Object response) {
            Log.w(TAG,"login response = " +response);
            if (null == response) {
                onLoginFailed("response == null");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (jsonResponse.length() == 0) {
                onLoginFailed("response == null");
                return;
            }
            //  mTencent.onActivityResultData(requestCode,resultCode,data,loginListener);
            if (initOpenIdAndToken(jsonResponse)) {
                getUserInfo(jsonResponse);
            } else {
                onLoginFailed("JSONException occoured while login.");
            }
        }
    };
    private  IQQCallback mShareCallback;
    private  final IUiListener mShareListener  = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            onShareSuccess(o);
        }

        @Override
        public void onError(UiError uiError) {
            onShareFailed("massage: " + uiError.errorMessage + " and detail: " + uiError.errorDetail);
        }

        @Override
        public void onCancel() {
            onShareUserCancle();
        }
    };

    public QQHelper(){}
    public QQHelper init(Context context){
        if(mTencent == null)
            mTencent = Tencent.createInstance(SdkFactory.ShareConfig.sQq_app_id,
                    context.getApplicationContext());
        return this;
    }

    private Activity getActivity(){
        return mWeakActivity!=null ? mWeakActivity.get() :null;
    }

    /** 纯图片分享，只支持本地 */
    public void shareOnlyImage(Activity activity,String appName,String fullPath,IQQCallback callbak){
        this.mShareCallback = callbak;
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,fullPath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        // showToast("在好友选择列表会自动打开分享到qzone的弹窗~~~");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

        mTencent.shareToQQ(activity, params, mShareListener);
    }

    /**
     *  分享，
     *  <li>jumpUrl , title , summary 不能同时为空  </li>
     * @param jumpUrl  消息被点击时跳转的url
     * @param title    分享标题
     * @param summary  分享消息摘要
     */
    public void share(Activity activity,String appName,String jumpUrl,String title,String summary,
                      String imageUrl,IQQCallback callbak){
        this.mShareCallback = callbak;
        Bundle bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, jumpUrl);
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        //分享的图片URL
         bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        //分享的消息摘要，最长50个字
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);

        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        //声音分享
       /* if (shareType == QQShare.SHARE_TO_QQ_TYPE_AUDIO) {
            params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, mEditTextAudioUrl.getText().toString());
        }*/
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        // showToast("在好友选择列表会自动打开分享到qzone的弹窗~~~");
        // bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(activity, bundle , mShareListener);
    }

    private void onShareFailed(String msg) {
        if(mShareCallback!=null){
            mShareCallback.onFailed(msg);
            mShareCallback = null;
        }
    }
    private void onShareSuccess(Object object) {
        if(mShareCallback != null){
            mShareCallback.onSuccess((JSONObject) object);
            mShareCallback = null;
        }
    }
    private void onShareUserCancle() {
        if(mShareCallback != null){
            mShareCallback.onUserCancel();
            mShareCallback = null;
        }
    }

    public void onActivityResultByLogin(Activity activity,int requestCode, int resultCode, Intent data){
        Tencent.onActivityResultData(requestCode,resultCode,data,mLoginListener);
        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.RESULT_LOGIN) {
                Tencent.handleResultData(data, mLoginListener);
                Log.i(TAG, "-->onActivityResult handle logindata");
            }
        }
    }

    public void onActivityResultByShare(Activity activity,int requestCode, int resultCode, Intent data){
        Tencent.onActivityResultData(requestCode, resultCode, data, mShareListener);
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            if (resultCode == Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, mShareListener);
            }
        }else{
            String path = null;
            if (resultCode == Activity.RESULT_OK && requestCode == 0) {
                if (data != null && data.getData() != null) {
                    // 根据返回的URI获取对应的SQLite信息
                    Uri uri = data.getData();
                    path = QQUtil.getPath(activity, uri);
                }
            }
            Log.d(TAG," user selected path =  " + path );
            //path may be null;
        }
    }

    public void logout(Context activity){
        mTencent.logout(activity);
    }

    /** activity 需要复写  mTencent.onActivityResultData(requestCode,resultCode,data,loginListener); */
    public void login(Activity activity,IQQCallback callback){
        this.mCallback = callback;
        this.mWeakActivity = new WeakReference<Activity>(activity);
        if(mTencent == null){
            onLoginFailed("mTencent == null");
        } else if(mTencent.isSessionValid() && TextUtils.isEmpty(sUserInfo)){
            try {
                onLoginSuccess(new JSONObject(sUserInfo));
            } catch (JSONException e) {
                //ignore
                e.printStackTrace();
            }
        }else
           mTencent.login(activity, "all", mLoginListener);
    }

    /** return is success */
    private boolean  initOpenIdAndToken(JSONObject jsonResponse) {
        try {
            String token = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonResponse.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getUserInfo(JSONObject response) {

        Activity activity = getActivity();
        if(activity == null ){
            onLoginFailed("activity = null");
            return ;
        }
        UserInfo info = new UserInfo(activity, mTencent.getQQToken());
        info.getUserInfo(new UiListenerImpl() {
            @Override
            public void onComplete(Object o) {
                JSONObject json = (JSONObject)o;
                try {
                    json.put(OPEN_ID, mTencent.getOpenId());
                    json.put(ACCESS_TOKEN, mTencent.getAccessToken());
                } catch (JSONException e) {
                }
                sUserInfo = json.toString();
                onLoginSuccess(json);
              /*  if(json.has("figureurl")){ //头像地址
                    Bitmap bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
                }*/
            }
        });
    }

    private void onLoginFailed(String msg){
       if(mCallback !=null){
           mCallback.onFailed(msg);
           mCallback = null;
       }
    }

    private void onLoginSuccess(JSONObject response){
       if(mCallback !=null){
           mCallback.onSuccess(response);
           mCallback = null;
       }
    }
    private void onLoginCancel(){
       if(mCallback !=null){
           mCallback.onUserCancel();
           mCallback = null;
       }
    }

    public interface IQQCallback {

        void onFailed(String msg);

        /**
         * @param response   用户信息的json对象
         */
        void onSuccess(JSONObject response);

        void onUserCancel();
    }

    private abstract class UiListenerImpl implements IUiListener{

        @Override
        public void onError(UiError uiError) {
            onLoginFailed(uiError.errorMessage + " and " + uiError.errorDetail);
        }

        @Override
        public void onCancel() {
            onLoginCancel();
        }
    }
   /* static  class BaseApiListener implements IRequestListener {
        @Override
        public void onComplete(JSONObject jsonObject) {
        }
        @Override
        public void onIOException(IOException e) {
        }
        @Override
        public void onMalformedURLException(MalformedURLException e) {

        }
        @Override
        public void onJSONException(JSONException e) {

        }
        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e) {

        }
        @Override
        public void onSocketTimeoutException(SocketTimeoutException e) {

        }
        @Override
        public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {
// 当前网络不可用时触发此异常
        }

        @Override
        public void onHttpStatusException(HttpUtils.HttpStatusException e) {
// http请求返回码非200时触发此异常
        }

        @Override
        public void onUnknowException(Exception e) {
// 出现未知错误时会触发此异常
        }
    }*/
}
