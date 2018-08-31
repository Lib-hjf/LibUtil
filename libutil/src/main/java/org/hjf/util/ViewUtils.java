package org.hjf.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class ViewUtils {

    /**
     * DIP -> PX
     * px = density * dp
     * {@link android.util.TypedValue#applyDimension(int, float, DisplayMetrics)}
     */
    public static int dp2px(Context context, int dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density);
    }

    /**
     * PX -> DIP
     * px = density * dp
     * {@link android.util.TypedValue#applyDimension(int, float, DisplayMetrics)}
     */
    public static int px2dp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    /**
     * SP -> PX
     * px = density * dp
     * {@link android.util.TypedValue#applyDimension(int, float, DisplayMetrics)}
     */
    public static int sp2px(Context context, int sp) {
        return (int) (sp * context.getResources().getDisplayMetrics().scaledDensity);
    }
}
