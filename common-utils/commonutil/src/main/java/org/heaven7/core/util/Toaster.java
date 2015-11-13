/*
 * Copyright (C) 2015 
 *            heaven7(donshine723@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.heaven7.core.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * this is a toast wrapper. help to to fast use {@link Toaster}.
 * @author heaven7
 *
 */
public class Toaster{
	
	private static final String TAG = "Toaster";
	private final WeakReference<Context> mWrfContext;
	private static WeakReference<Toast> sWeakToast;
	
	public Toaster(Context ctx) {
		mWrfContext = new WeakReference<Context>(ctx.getApplicationContext());
	}
	
	public boolean show(String msg){
		Context ctx = mWrfContext.get();
		if(ctx == null){
			Logger.w(TAG, "Toaster_show", "Context == null! , msg = " + msg);
			return false;
		}
		show(ctx, msg);
		return true;
	}
	public boolean show(int resid){
		Context ctx = mWrfContext.get();
		if(ctx == null){
			Logger.w(TAG, "show", "Context == null");
			return false;
		}
		show(ctx, resid);
		return true;
	}
	
	/**Toast.LENGTH_SHORT */
	public static void show(Context ctx,String msg){
		show(ctx, msg, false);
	}
	/**Toast.LENGTH_SHORT */
	public static void show(Context ctx,int resId){
		show(ctx, resId, false);
	}
	public static void show(final Context ctx,final CharSequence msg,final boolean warn){
		show(ctx,msg,warn, Gravity.CENTER);
	}
	/** warning meas Toast.LENGTH_LONG */
	public static void show(final Context ctx,final CharSequence msg,final boolean warn, final int gravity){
		MainWorker.post(new Runnable() {
			public void run() {
				if(sWeakToast!=null){
					Toast toast = sWeakToast.get();
					if(toast!=null)
						toast.cancel();
				}
				Toast toast = Toast.makeText(ctx, msg, warn?Toast.LENGTH_LONG:
					Toast.LENGTH_SHORT);
				toast.setGravity(gravity,0,0);
				sWeakToast = new WeakReference<Toast>(toast);
				toast.show();
			}
		});
	}
	/** warning meas Toast.LENGTH_LONG */
	public static void show(final Context ctx,final int resId,final boolean warn){
		CharSequence msg = ctx.getResources().getText(resId);
		show(ctx, msg, warn);
	}
	
}
