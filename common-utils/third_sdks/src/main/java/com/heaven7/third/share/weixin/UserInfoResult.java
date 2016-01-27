package com.heaven7.third.share.weixin;

import com.heaven.appframework.core.lib.json.PropertyField;

/**
 * http请求方式: GET
 https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
 * Created by heaven7 on 2015/9/16.
 */
public class UserInfoResult {

    @PropertyField(name="unionid",negligible = true)
    public String unionid;  //用户唯一标识

    @PropertyField(name="openid",negligible = true)
    public String openid;

    @PropertyField(name="nickname",negligible = true)
    public String nickname;

    @PropertyField(name="sex",negligible = true)
    public String sex;

    @PropertyField(name="province",negligible = true)
    public String province;

    @PropertyField(name="city",negligible = true)
    public String city;

    @PropertyField(name="country",negligible = true)
    public String country;

    @PropertyField(name="headimgurl",negligible = true)
    public String headUrl;

    // error
    @PropertyField(name="errcode",negligible = true)
    public String errcode;

    @PropertyField(name="errmsg",negligible = true)
    public String errMsg;

    @PropertyField(name="access_token",negligible = true)
    public String accessToken;

    public boolean isOk(){
        return errcode == null && errMsg == null;
    }

    @Override
    public String toString() {
        return "UserInfoResult{" +
                "unionid='" + unionid + '\'' +
                ", openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", errcode='" + errcode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
