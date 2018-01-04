package org.hjf.util;

import android.content.Context;
import android.view.View;

/**
 * 手机屏幕参数工具
 */
public class ViewUtils {

	/**
	 * 屏幕DPI密度
	 */
	public static float getDensity(Context context) {
		return EnvironUtils.getScreenDensity(context);
	}

	/**
	 * 屏幕宽度
	 */
	public static int getScreenWidth(Context context) {
		return EnvironUtils.getScreenWidth(context);
	}

	/**
	 * 屏幕高度
	 */
	public static int getScreenHeight(Context context) {
		return EnvironUtils.getScreenHeight(context);
	}


	/**
	 * 获取状态栏高度
	 */
	private static int getStatusHeight(Context context) {
		int height = 0;
		int dimenResId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (dimenResId > 0) {
			height = context.getResources().getDimensionPixelSize(dimenResId);
		}
		return height;
	}

	/**
	 * DIP -> px
	 */
	public static int dp2px(Context context, int dip) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dip*scale + 0.5f * (dip >= 0 ? 1 : -1));
	}

	/**
	 * PX -> DIP
	 */
	public static int px2dp(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(px/scale + 0.5f * ( px >= 0 ? 1 : -1));
	}

	/**
	 * 增量的形式改变 View 的 padding 属性
	 */
	public static void setPadding4Increment(View view, int lInc, int tInc, int rInc, int bInc){
		view.setPadding(view.getPaddingLeft() + lInc, view.getPaddingTop() + tInc, view.getPaddingRight() + rInc, view.getPaddingBottom() + bInc);
	}
}
