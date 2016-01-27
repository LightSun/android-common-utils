package com.heaven7.third.share.weixin;

import com.heaven.appframework.core.lib.json.PropertyField;

/**
 * 检验授权凭证（access_token）是否有效
 * http请求方式: GET
 https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID
 * Created by heaven7 on 2015/9/16.
 */
public class ValidateAccessTokenResult {

    @PropertyField(name="errcode",negligible = true)
    public String errcode;

    @PropertyField(name="errmsg",negligible = true)
    public String errMsg;

    public boolean isOk(){
        return errcode!=null && Integer.parseInt(errcode) == 0;
    }

}
