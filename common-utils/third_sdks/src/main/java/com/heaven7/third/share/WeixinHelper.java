package com.heaven7.third.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.SparseArray;

import com.android.volley.VolleyError;
import com.android.volley.data.ApiParams;
import com.android.volley.extra.util.VolleyUtil;
import com.heaven.appframework.core.lib.json.JsonParser;
import com.heaven7.core.AsyncTask2;
import com.heaven7.core.IResetable;
import com.heaven7.core.ITaskManager;
import com.heaven7.core.ImageParser;
import com.heaven7.core.SdkFactory;
import com.heaven7.core.internal.SdkConfig;
import com.heaven7.core.internal.TaskManagerImpl;
import com.heaven7.third.pay.IoUtil;
import com.heaven7.third.share.weixin.AccessTokenResult;
import com.heaven7.third.share.weixin.UserInfoResult;
import com.heaven7.third.share.weixin.ValidateAccessTokenResult;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * 用于微信登陆和分享
 * Created by heaven7 on 2015/9/15.
 */
public class WeixinHelper implements IResetable{

    private static final int THUMB_SIZE = 150;

    private IWXAPI mWxApi;
    private final SparseArray<Object> mCallbackMap;
    private AccessTokenResult mAccessTokenResult;
    private final ITaskManager mTaskManager = new TaskManagerImpl();

    private final IWXAPIEventHandler mWeixinHandler = new IWXAPIEventHandler() {

        // 微信发送请求到第三方应用时，会回调到该方法,最后回到微信
        @Override
        public void onReq(BaseReq baseReq) {
        }

        // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法.最后回到第三方应用
        @Override
        public void onResp(BaseResp baseResp) {
            if(baseResp instanceof SendAuth.Resp) {
                SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                if(((SendAuth.Resp) baseResp).errCode ==0){
                    //用户同意
                    getAccessToken(resp.state ,resp.code);
                }else{
                    //用户取消或者拒绝
                    callbackWithRequestFailed(resp.state, null, "when auth, user canceled or refused !");
                }
            }else if(baseResp instanceof  SendMessageToWX.Resp){
                SendMessageToWX.Resp resp = (SendMessageToWX.Resp) baseResp;
                final int key = resp.transaction.hashCode();
                Object callback = mCallbackMap.get(key);
                if(baseResp.errCode ==0){
                    //share success
                    if(callback != null){
                        ((IWXShareCallback) callback).onShareSuccess();
                        mCallbackMap.delete(key);
                    }
                }else{
                    //cancel or refused
                    if(callback != null){
                        ((IWXShareCallback) callback).onShareFailed(resp.errStr);
                        mCallbackMap.delete(key);
                    }
                }
            }else{
                System.out.println("BaseResp = " +baseResp);
            }
        }
    };

    private void getAccessToken(String unionKey,String code) {
       VolleyUtil.get(SdkConfig.URL_WEIXIN_ACCESS_TOKEN, new ApiParams()
                       .with("appid", SdkFactory.ShareConfig.sWeixin_app_Id)
                       .with("secret", SdkFactory.ShareConfig.sWeixin_app_secret)
                       .with("code", code)
                       .with("grant_type", "authorization_code"),
               this, new HttpCallbackImpl(unionKey) {
                   @Override
                   public void onResponse(String url, String response) {
                       final AccessTokenResult result = new JsonParser(response).parse(AccessTokenResult.class);
                       if(!result.isOk()){
                           WeixinHelper.this.callbackWithRequestFailed(getUnionKey(), url, response);
                       }else{
                            WeixinHelper.this.mAccessTokenResult = result;
                           // getUserInfo
                           getUserInfo(getUnionKey(), result);
                       }
                   }
               }
       );
    }

    private void getUserInfo(String unionKey,final AccessTokenResult result2) {
        VolleyUtil.get(SdkConfig.URL_WEIXIN_USER_INFO, new ApiParams()
                        .with("access_token", result2.accessToken)
                        .with("openid", result2.openid)
                , this, new HttpCallbackImpl(unionKey) {
                    @Override
                    public void onResponse(String url, String response) {
                        final UserInfoResult result = new JsonParser(response).parse(UserInfoResult.class);
                        if (!result.isOk()) {
                            WeixinHelper.this.callbackWithRequestFailed(getUnionKey(), url, response);
                        } else {
                            if (!TextUtils.isEmpty(result.nickname)) {
                                try {
                                    result.nickname = new String(result.nickname.getBytes("iso8859-1"), "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            result.accessToken = result2.accessToken;
                            callbackWithUserInfo(getUnionKey(), result);
                        }
                    }

                }
        );
    }
    private void callbackWithUserInfo(String unionKey,UserInfoResult result) {
        final int key = unionKey.hashCode();
        Object callback = mCallbackMap.get(key);
        if(callback != null){
            ((IWXAuthCallback) callback).onHandleResponse(result);
            mCallbackMap.delete(key);
        }
    }

    public WeixinHelper() {
        mCallbackMap = new SparseArray<>(4);
    }

    public WeixinHelper initWXApi(Context context){
       // if(mWxApi !=null)  return this;
        mWxApi = WXAPIFactory.createWXAPI(context.getApplicationContext(),
                SdkFactory.ShareConfig.sWeixin_app_Id, false);
        mWxApi.registerApp(SdkFactory.ShareConfig.sWeixin_app_Id);
        return this;
    }

    public boolean onHandleIntent(Intent intent){
        return mWxApi.handleIntent(intent, mWeixinHandler);
    }

    public void sendAuthRequest(IWXAuthCallback callback){

        final String id = buildTransaction("auth");
        mCallbackMap.put(id.hashCode(), callback);

        //if accessToken exist check it
        if(mAccessTokenResult != null){
            final AccessTokenResult mAccessTokenResult = this.mAccessTokenResult;
            VolleyUtil.get(SdkConfig.URL_WEIXIN_CHECK_ACCESS_TOKEN, new ApiParams()
                            .with("access_token", mAccessTokenResult.accessToken )
                            .with("openid", mAccessTokenResult.openid )
                    , this, new HttpCallbackImpl(id) {
                        @Override
                        public void onResponse(String url, String response) {
                            ValidateAccessTokenResult result = new JsonParser(response)
                                    .parse(ValidateAccessTokenResult.class);
                            if(!result.isOk()){
                                WeixinHelper.this.callbackWithRequestFailed(getUnionKey(), url, response);
                            }else{
                                getUserInfo(getUnionKey(),mAccessTokenResult);
                            }
                        }

                    }
            );
        }else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = id;
            mWxApi.sendReq(req);
        }
    }

    /**
     *  分享文本
     * @param text
     * @param shareToFriendCircle  是否分享到朋友圈
     */
    public void shareText(String text,boolean shareToFriendCircle,IWXShareCallback callback){
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.description = text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = shareToFriendCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        mCallbackMap.put(req.transaction.hashCode(),callback);
        // 调用api接口发送数据到微信
        mWxApi.sendReq(req);
    }

    /***
     * @param path  the local image path
     */
    public void shareImageByPath(String path,String title ,String desc,boolean shareToFriendCircle,IWXShareCallback callback){
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(path);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        msg.title = title;
        msg.description = desc;

       // Bitmap bmp = BitmapFactory.decodeFile(path);
        Bitmap bmp = new ImageParser(0,0).parseToBitmap(path);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bitampToByteArray(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = shareToFriendCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        mCallbackMap.put(req.transaction.hashCode(),callback);
        mWxApi.sendReq(req);
    }

    public void shareWebUrl(final String imgUrl,final String shareUrl,
                            final String title,final String desc,
                            final boolean shareToFriendCircle,final IWXShareCallback callback){

        AsyncTask2<Void,Void,Bitmap> task = new AsyncTask2<Void, Void, Bitmap>(mTaskManager) {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                   return new ImageParser(THUMB_SIZE,THUMB_SIZE).parseToBitmap(
                            IoUtil.getBytesFromStreamAndClose(new URL(imgUrl).openStream()));
                } catch (IOException e) {
                    System.out.println("share_img: url = " + imgUrl);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap == null){
                    if(callback != null){
                        callback.onShareFailed("get image bitmap from url failed !");
                    }
                }else{
                    shareWebUrlWithIcon(bitmap, shareUrl, title, desc, shareToFriendCircle, callback);
                }
                super.onPostExecute(bitmap);
            }
        };
        AsyncTaskCompat.executeParallel(task);
    }

    public void shareWebUrlWithIcon(Bitmap icon, String shareUrl, String title, String desc,
                                  boolean shareToFriendCircle, IWXShareCallback callback) {
        final String id = buildTransaction("web_url");
        WXWebpageObject webObj = new WXWebpageObject();
        webObj.webpageUrl = shareUrl;
        webObj.extInfo = "this is extInfo";

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = webObj;
        msg.title = title;
        msg.description = desc;
        msg.setThumbImage(icon);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = id;
        req.message = msg;
        req.scene = shareToFriendCircle ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;

        mCallbackMap.put(req.transaction.hashCode(),callback);
        mWxApi.sendReq(req);
    }

    /**
     * title 和 desc 在分享图片时无效
     */
    public void shareImageByUrl(String url,String title,String desc ,boolean shareToFriendCircle,
                                IWXShareCallback callback){
        AsyncTaskCompat.executeParallel(new ShareImageTask(mTaskManager), url, title,
                desc, shareToFriendCircle, callback);
    }

    @Override
    public void reset() {
        mTaskManager.reset();
        VolleyUtil.cancelAll(this);
        mCallbackMap.clear();
    }

    private class ShareImageTask extends AsyncTask2<Object,Void,SendMessageToWX.Req> {

        public ShareImageTask(ITaskManager manager) {
            super(manager);
        }

        @Override
        protected SendMessageToWX.Req doInBackground(Object... params) {
            final String url = (String) params[0];
            final String title = (String)params[1];
            final String desc = (String)params[2];
            final boolean shareToFriendCircle = (Boolean)params[3];
            final IWXShareCallback callback = (IWXShareCallback)params[4];

            WXImageObject imgObj = new WXImageObject();
            imgObj.imageUrl = url;

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;
            msg.title = title;
            msg.description = desc;

            final String transaction = buildTransaction("img");
            mCallbackMap.put(transaction.hashCode(), callback);
            try {
                Bitmap bmp = new ImageParser(0,0).parseToBitmap(IoUtil.getBytesFromStreamAndClose(
                        new URL(url).openStream()));
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData =  Util.bitampToByteArray(thumbBmp);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = transaction;
                req.message = msg;
                req.scene = shareToFriendCircle ? SendMessageToWX.Req.WXSceneTimeline
                        : SendMessageToWX.Req.WXSceneSession;
                return req ;
            } catch (IOException e) {
                e.printStackTrace();
            }
            SendMessageToWX.Req req2 = new SendMessageToWX.Req();
            req2.transaction = transaction;
            req2.message = null;
            return req2;
        }

        @Override
        protected void onPostExecute(SendMessageToWX.Req req) {
            if(req.message == null){
                Object cl = mCallbackMap.get(req.transaction.hashCode());
                if(cl!=null){
                    ((IWXShareCallback)cl).onShareFailed("read image to WXMediaMessage.thumbData failed");
                    mCallbackMap.clear();
                }
            }else{
                mWxApi.sendReq(req);
            }
            super.onPostExecute(req);
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public interface IWXAuthCallback{
        void onHandleResponse(UserInfoResult userInfo);
        void onRequestFailed(String url, String msg);
    }

    public interface IWXShareCallback{
       // void onHandleResponse(SendMessageToWX.Resp response);
        void onShareFailed(String msg);
        void onShareSuccess();
    }

    protected final void callbackWithRequestFailed(String unionKey, String url, String msg){
        Object callback ;
        if(unionKey == null){
            callback =mCallbackMap.valueAt(0);
        }else {
            final int key = unionKey.hashCode();
            callback = mCallbackMap.get(key);
        }
        if(callback!=null){
            ((IWXAuthCallback) callback).onRequestFailed(url,"when getting user info: msg = "+ msg);
            mCallbackMap.clear();
        }
    }

    private abstract class HttpCallbackImpl implements VolleyUtil.HttpCallback{

        private final String unionKey;

        public HttpCallbackImpl(String unionKey) {
            this.unionKey = unionKey;
        }
        public String getUnionKey(){
            return unionKey;
        }

        @Override
        public void onErrorResponse(String url, VolleyError error) {
           WeixinHelper.this.callbackWithRequestFailed(getUnionKey(), url,
                   error != null ? error.getMessage() : "");
        }

    }

}
