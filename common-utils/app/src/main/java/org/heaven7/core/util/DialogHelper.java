package org.heaven7.core.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yyh.singcat.viewhelper.ViewHelper;

/**
 * Created by heaven7 on 2015/8/31.
 */
public class DialogHelper {

    private AlertDialog mDialog;
    private ViewHelper mViewHelper;

    public DialogHelper createFrom(Context ctx,int layoutId){
        final View view = LayoutInflater.from(ctx).inflate(layoutId, null);
        mViewHelper = new ViewHelper(view);
        mDialog = new AlertDialog.Builder(ctx,AlertDialog.THEME_HOLO_LIGHT)
                .setView(view).setCancelable(true).create();
        mDialog.setCanceledOnTouchOutside(true);
        return this;
    }

    public DialogHelper addOnClickListener(int viewId, View.OnClickListener l){
        mDialog.findViewById(viewId).setOnClickListener(l);
        return this;
    }

    public String getTextById(int textId){
        return ((TextView)mViewHelper.getView(textId)).getText().toString();
    }

    public ViewHelper getViewHelper(){
        return mViewHelper;
    }

    public AlertDialog show(){
         mDialog.show();
        return mDialog;
    }

    public void dismiss(){
        if(mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
