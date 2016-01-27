package com.heaven7.third.share.weixin;

import com.heaven.appframework.core.lib.json.PropertyField;
import com.heaven.appframework.core.lib.json.Token;

/**
 * weixin access token 返回信息
 * http请求方式: GET
 https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET
 &code=CODE&grant_type=authorization_code

 刷新或续期access_token使用 .接口返回数据是一样的
 http请求方式: GET
 https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token
 &refresh_token=REFRESH_TOKEN
 *
 * Created by heaven7 on 2015/9/16.
 *
 */
public class AccessTokenResult {
    //error
    @PropertyField(name="errcode",negligible = true)
    public String errcode;

    @PropertyField(name="errmsg",negligible = true)
    public String errMsg;

    // ok
    @PropertyField(name="access_token",negligible = true)
    public String accessToken; //接口调用凭证

    @PropertyField(name="expires_in",negligible = true,token = Token.LONG)
    public long expiresIn;  //过期时间

    @PropertyField(name="refresh_token",negligible = true)
    public String refreshToken;

    @PropertyField(name="openid",negligible = true)
    public String openid;    //用户授权唯一标识

    @PropertyField(name="scope",negligible = true)
    public String scope;    //用户授权的作用域，使用逗号（,）分隔

    public boolean isOk(){
        return errcode == null;
    }

}
