package com.heaven7.core.internal;

/**
 * Created by heaven7 on 2015/9/15.
 */
public interface SdkConfig {

  /*  String WEIXIN_APPID       = "wx04b6a1a9282dc734";
    String WEIXIN_APP_SECRET  = "f90ab26a4a63c1638ef387fa2b717883";

    //申请微信支付的时候会分配
    String WEIXIN_MERCHANT_ID        = "1270007301";          //商户id
    String WEIXIN_PAY_APP_ID         = "wx04b6a1a9282dc734";
    String WEIXIN_PAY_APP_KEY        = "ee71dca5d9d0b73c385bc957bzhulili";

    String ZHIFUBAO_PID                   = "2088911995646891";
    String ZHIFUBAO_ACCOUNT               = "zhulili@yinyuehui.mobi";
    //私钥
    String ZHIFUBAO_RSA_KEY               = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAKRC39BHzbdRkGBe\n" +
            "OqSqvn+mPOM37wr8gEXAR3x7VqAukaIgyd8A/ScPgZCTksHBvAQnMPeB25RB3vTG\n" +
            "zSs4IpiXk+hehTTmX8OfTPJm+H1I/P50rHxTqIlF4e5RNXsblJhZ90HBLyi8aM2t\n" +
            "FujjwHXDpMLWF4T2zhq/Gq39K3EpAgMBAAECgYBTRlgDVrOzMVWZlYvzeWCUhGmv\n" +
            "4+Z7UmGRexaCxGC5WoKYiDjvpqewysqryjUHp7ky/c90W1A/zFlFDRhsxcu7coJh\n" +
            "3xEy2/kVSPx0aqDbxitEpkmE3UFw/9Nr7nto9W+bsARzLoMeuZsoWC5wYhsSqEyD\n" +
            "Ko09zWNoSVS2w/VidQJBANBF9fdKT9BwAgfI7kkfIDO9aBB01d3Y19OyDVApta9p\n" +
            "aYKNTjpBncLye4ObOkbZL6V3dK7PbiePGl9lzL/lh+8CQQDJ5wMmibnIM2mFz77u\n" +
            "sS3ALzU7/PHBbxXbKEKweMNnrhNEm9EX8TF3lEdNk0FSBbXsQTQt466WGKAKLELR\n" +
            "ikBnAkEAwTDUCnG0/VyYlY4Ncmhb34KG4BTwONumv6h+buhxrmdyRnY6pREufKGe\n" +
            "bqJzeTqG1s6qvmiM/kbgqLLuLOaFowJBAMBaI0lC60lNmdnPC0NSb5jqINhu6k/0\n" +
            "KilqGOcRlnCfimHR5QnfUtZu4OOKSDABmsljcXfKs5jyCv7GHj6NbT8CQQCN+nbO\n" +
            "sA1qcuNq2dIcO4JffJLlCkt7uw5sr1czVEz+ituTxHGk1UU3cnjTpr5b+kLumdA6\n" +
            "N9bxJxs8ousg5X85";

    String URL_NOTIFY = "http://yk.yinyuehui.mobi/yyh-yk/api/appservice/apporder";

    String QQ_APP_ID = "1104668213"; */

    String URL_WEIXIN_ACCESS_TOKEN        = "https://api.weixin.qq.com/sns/oauth2/access_token";
    String URL_WEIXIN_REFRESH_TOKEN       = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    String URL_WEIXIN_CHECK_ACCESS_TOKEN  = "https://api.weixin.qq.com/sns/auth";
    String URL_WEIXIN_USER_INFO           = "https://api.weixin.qq.com/sns/userinfo";

    String URL_WEIXIN_PAY_GET_PREPARED_ID    = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    String URL_WEIXIN_PAY_CLOSE_ORDER        = "https://api.mch.weixin.qq.com/pay/closeorder";
}
