package org.heaven7.core.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.heaven7.core.viewhelper.ViewHelper;


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

    public DialogHelper setOnClickListener(int viewId, View.OnClickListener l){
        mViewHelper.setOnClickListener(viewId, l);
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

    public boolean isShowing(){
        return mDialog!=null && mDialog.isShowing();
    }

    public void dismiss(){
        if(mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public <T extends View> T getView(int viewId) {
        return mViewHelper.getView(viewId);
    }

    public DialogHelper setCancelable(boolean cancelable) {
        if(mDialog!=null){
            mDialog.setCancelable(cancelable);
        }
        return this;
    }
    public DialogHelper setCanceledOnTouchOutside(boolean cancel) {
        if(mDialog!=null){
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }
    public DialogHelper setOnCancelListener(DialogInterface.OnCancelListener l) {
        if(mDialog!=null){
            mDialog.setOnCancelListener(l);
        }
        return this;
    }

    public static void dismissDialog(Dialog d){
        if(d!=null && d.isShowing()){
            d.dismiss();
        }
    }
}
