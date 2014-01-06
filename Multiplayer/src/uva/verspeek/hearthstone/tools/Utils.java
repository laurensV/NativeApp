package uva.verspeek.hearthstone.tools;

import android.app.Activity;
import android.widget.Toast;


public class Utils {

	public static String userName = "";
	
	public static float getPercentFromValue(float number, float amount){
		float percent = (number/amount)*100;
		return percent;
	}
	
	public static float getValueFromPercent(float percent, float amount){
		float value = (percent/100)*amount;
		return value;
	}
	
    public static void showToastAlert(Activity ctx, String alertMessage){
        Toast.makeText(ctx, alertMessage, Toast.LENGTH_SHORT).show();
    }

	public static void showToastOnUIThread(final Activity ctx, final String alertMessage){
	        ctx.runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                        Toast.makeText(ctx, alertMessage, Toast.LENGTH_SHORT).show();
	                }
	        });
	}
	
	public static boolean allElementsTheSame(boolean[] array) {
	    if (array.length == 0) {
	        return true;
	    } else {
	        boolean first = array[0];
	        for (boolean element : array) {
	            if (element != first) {
	                return false;
	            }
	        }
	        return true;
	    }
	}
	
}
