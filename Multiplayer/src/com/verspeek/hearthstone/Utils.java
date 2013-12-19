package com.verspeek.hearthstone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;


public class Utils {

	public static String userName = "";
	public static Toast toastobject;
	
	public static void initToast(final Activity ctx){
		ctx.runOnUiThread(new Runnable() {
			@Override
			public void run() {
		toastobject = Toast.makeText(ctx, null, Toast.LENGTH_SHORT);
			}
		});
	}
	
	
	public static float getPercentFromValue(float number, float amount){
		float percent = (number/amount)*100;
		return percent;
	}
	
	public static float getValueFromPercent(float percent, float amount){
		float value = (percent/100)*amount;
		return value;
	}
	
	public static void showToastAlert(Activity ctx, String alertMessage){
		if (toastobject == null) initToast(ctx);
		else				toastobject.cancel();
		initToast(ctx);
		toastobject.setText(alertMessage);
		toastobject.show();
	}
	
	public static void showToastOnUIThread(final Activity ctx, final String alertMessage){
		ctx.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toastobject == null) initToast(ctx);
				else				toastobject.cancel();
				initToast(ctx);
				toastobject.setText(alertMessage);
				toastobject.show();
			}
		});
	}
	
}
