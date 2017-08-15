package cf.mirror.com.myapplication;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 系统屏幕的一些操作
 */
public final class DisplayUtils {

	/**
	 * 得到设备屏幕的宽度
	 * 
	 * @param context
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}
	
	/**
	 * 得到设备屏幕的高度
	 * 
	 * @param context
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * 
	 * @param context
	 * @param dpValue
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	/**
	 * 
	 * 获得屏幕的密度，用于屏幕适配
	 * 
	 * @param ctx
	 * @return
	 */
	public static float getDensity(Context ctx) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics.density;
	}
	
	
	
	
}