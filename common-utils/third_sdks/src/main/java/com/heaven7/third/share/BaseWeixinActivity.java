package com.heaven7.third.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.heaven7.third.core.SdkFactory;
import com.heaven7.third_sdks.R;

/**
 * Created by heaven7 on 2016/2/4.
 */
public abstract class BaseWeixinActivity extends AppCompatActivity {

    private WeixinHelper mWeiXinHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);//透明处理

        mWeiXinHelper = SdkFactory.getWeixinHelper(this);
        if( mWeiXinHelper.onHandleIntent(getIntent())){
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if( mWeiXinHelper.onHandleIntent(intent)){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        mWeiXinHelper.reset();
        super.onDestroy();
    }
}
