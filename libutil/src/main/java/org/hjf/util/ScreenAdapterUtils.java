package org.hjf.util;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Screen Adapter CacheDoubleUtils.
 *
 * @blog https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
 * @blog https://blankj.com/2018/07/30/easy-adapt-screen/#more
 * <p>
 * <p>
 * Fundamental formulae
 * 1. px = density * dp;
 * 2. density = dpi / 160;
 * 3. px = dp * (dpi / 160);
 * 4. dpi = √￣(height^2 + width^2) / ScreenSize(inch)
 * 5. px = dp * ((√￣(height^2 + width^2) / ScreenSize(inch)) / 160);
 * <p>
 * <p>
 * For example:
 * 1920px * 1080px, size = 5(inch). dpi = 440
 * <p>
 * if The design width is 360dp. The device dp is 1080/(440/160) = 392.7dp, cannot display perfectly.
 * <p>
 * <p>
 * Thinking
 * 1. PX is the last data used by the device to display View
 * 2. px = dp * (dpi / 160);
 * 3. px = getResources().getDisplayMetrics().density
 * 4. dpi = getResources().getDisplayMetrics().densityDpi
 * 5. dpi = getResources().getDisplayMetrics().scaledDensity   // font scale num
 * 6. layout xml view use {@link android.util.TypedValue#applyDimension(int, float, DisplayMetrics)} get PX from DP.
 * <p>
 * Solution:
 * Calculate DPI in your own way and replace the value of system getResources().getDisplayMetrics().densityDpi
 * Use previous examples, the design width is 360dp.
 * px = dp * (dpi / 160)   ==>   1080px = 360dp * (dpi / 160)   ==>   dpi=480
 * <p>
 * <p>
 * The above discussion is horizontal direction adaptation, perpendicular to the same direction.
 */
public final class ScreenAdapterUtils {

//    private static int ADAPT_SCREEN_ARGS_sizeInPx = 0;
//    private static boolean ADAPT_SCREEN_ARGS_isVerticalSlide = false;

    /**
     * Adapt the screen for vertical slide.
     *
     * @param activity        The activity.
     * @param designWidthInDP The size of design diagram's width, in pixel.
     */
    public static void adaptScreen4VerticalSlide(final Activity activity, final Application application, final int designWidthInDP) {
        adaptScreen(activity, application, designWidthInDP, true);
    }

    /**
     * Adapt the screen for horizontal slide.
     *
     * @param activity         The activity.
     * @param designHeightInDP The size of design diagram's height, in pixel.
     */
    public static void adaptScreen4HorizontalSlide(final Activity activity, final Application application, final int designHeightInDP) {
        adaptScreen(activity, application, designHeightInDP, false);
    }

    /**
     * 宽度为例子：
     * px 设备真实宽度
     * dp 设计图标准宽度
     * 1. px = density * dp;
     * 2. density = dpi / 160;
     * 3. px = dp * (dpi / 160);
     */
    private static void adaptScreen(final Activity activity, final Application application, final int sizeInDP, final boolean isVerticalSlide) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = application.getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        if (isVerticalSlide) {
            activityDm.density = activityDm.widthPixels / (float) sizeInDP;
        } else {
            activityDm.density = activityDm.heightPixels / (float) sizeInDP;
        }
        activityDm.densityDpi = (int) (160 * activityDm.density);
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);

        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;

//        ScreenAdapterUtils.ADAPT_SCREEN_ARGS_sizeInPx = sizeInPx;
//        ScreenAdapterUtils.ADAPT_SCREEN_ARGS_isVerticalSlide = isVerticalSlide;
    }

    /**
     * Cancel adapt the screen.
     *
     * @param activity The activity.
     */
    public static void cancelAdaptScreen(final Activity activity, final Application application) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = application.getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        activityDm.density = systemDm.density;
        activityDm.scaledDensity = systemDm.scaledDensity;
        activityDm.densityDpi = systemDm.densityDpi;

        appDm.density = systemDm.density;
        appDm.scaledDensity = systemDm.scaledDensity;
        appDm.densityDpi = systemDm.densityDpi;
    }

    /**
     * Restore adapt the screen.
     * <p>U should call the method of {@link ScreenAdapterUtils#adaptScreen4VerticalSlide(Activity, Application, int)}
     * or {@link ScreenAdapterUtils#adaptScreen4HorizontalSlide(Activity, Application, int)} firstly.</p>
     */
   /* public static void restoreAdaptScreen(final Activity activity, final Application application) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = application.getResources().getDisplayMetrics();
        if (activity != null) {
            final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
            if (ScreenAdapterUtils.ADAPT_SCREEN_ARGS_isVerticalSlide) {
                activityDm.density = activityDm.widthPixels / (float) ScreenAdapterUtils.ADAPT_SCREEN_ARGS_sizeInPx;
            } else {
                activityDm.density = activityDm.heightPixels / (float) ScreenAdapterUtils.ADAPT_SCREEN_ARGS_sizeInPx;
            }
            activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
            activityDm.densityDpi = (int) (160 * activityDm.density);

            appDm.density = activityDm.density;
            appDm.scaledDensity = activityDm.scaledDensity;
            appDm.densityDpi = activityDm.densityDpi;
        } else {
            if (ScreenAdapterUtils.ADAPT_SCREEN_ARGS_isVerticalSlide) {
                appDm.density = appDm.widthPixels / (float) ScreenAdapterUtils.ADAPT_SCREEN_ARGS_sizeInPx;
            } else {
                appDm.density = appDm.heightPixels / (float) ScreenAdapterUtils.ADAPT_SCREEN_ARGS_sizeInPx;
            }
            appDm.scaledDensity = appDm.density * (systemDm.scaledDensity / systemDm.density);
            appDm.densityDpi = (int) (160 * appDm.density);
        }
    }*/

    /**
     * Return whether adapt screen.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAdaptScreen(final Context context) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = context.getResources().getDisplayMetrics();
        return systemDm.density != appDm.density;
    }

}
