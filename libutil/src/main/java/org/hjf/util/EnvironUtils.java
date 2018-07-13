package org.hjf.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 获取手机环境信息的工具类
 * 包括 App 软件信息、 OS 固件信息、 手机硬件信息
 */
public final class EnvironUtils {


	/*############################################
	##########  手机 - 【软件】 - 信息  ############
	#############################################*/

	/**
	 * 获取应用版本名字
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取应用版本号
	 */
	public static int getAppVersionCode(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return pInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取应用名
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}


	/**
	 * 获取应用名
	 */
	public static String getApplicationID(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.applicationInfo.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}


	/*############################################
	##########  手机 - 【固件】 - 信息  ############
	#############################################*/

	/**
	 * 获取 android 版本
	 */
	public static String getSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取手机品牌
	 */
	public static String getBrand() {
		return android.os.Build.BRAND != null ? android.os.Build.BRAND.replace(
				" ", "") : "unknown";
	}

	/**
	 * 获取手机型号
	 */
	public static String getModel() {
		return android.os.Build.MODEL != null ? android.os.Build.MODEL.replace(
				" ", "") : "unknown";
	}


	/*############################################
	##########  手机 - 【硬件】 - 信息  ############
	#############################################*/

	/**
	 * 屏幕DPI密度
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 屏幕宽度
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.widthPixels;
	}

	/**
	 * 屏幕高度
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.heightPixels;
	}

	/**
	 * Wi-Fi 模块是否打开
	 */
	public static boolean isWifiOpen(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 是否挂在 SD 卡
	 */
	public static boolean isSdAvailable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/**
	 * 获取手机内存总量
	 */
	public static long getMemTotalSize(Context context) {
		// 支持 Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN（17）（Android 4.1） 的设备
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(memInfo);
		return memInfo.totalMem;
	}

	/**
	 * 获取手机剩余内存量(MB)
	 */
	public static long getMemFreeSize(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		// 取得剩余的内存空间
		am.getMemoryInfo(memInfo);
		return memInfo.availMem / 1024;
	}

	/**
	 * Java虚拟机可用的处理器数量
	 */
	public static int getCpuAvailable(){
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Java虚拟机可用的处理器数量
	 */
	public static int getCpuReal(){
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * 获取 CPU 的频率
	 */
	public static float getProcessCpuRate() {
		float totalCpuTime1 = getCpuTimeTotal();
		float processCpuTime1 = getCpuTime4AppUsed();
		try {
			Thread.sleep(360);
		} catch (Exception e) {
			e.printStackTrace();
		}

		float totalCpuTime2 = getCpuTimeTotal();
		float processCpuTime2 = getCpuTime4AppUsed();

		float cpuRate = 100 * (processCpuTime2 - processCpuTime1) / (totalCpuTime2 - totalCpuTime1);

		return cpuRate;
	}

	/**
	 * 获取系统总CPU使用时间
	 */
	private static long getCpuTimeTotal() {
		String[] cpuInfos = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long totalCpu = Long.parseLong(cpuInfos[2])
				+ Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
				+ Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
				+ Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
		return totalCpu;
	}

	/**
	 * 获取应用占用的CPU时间
	 */
	private static long getCpuTime4AppUsed() {
		String[] cpuInfos = null;
		try {
			int pid = android.os.Process.myPid();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/" + pid + "/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long appCpuTime = Long.parseLong(cpuInfos[13])
				+ Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
				+ Long.parseLong(cpuInfos[16]);
		return appCpuTime;
	}

	/**
	 * 获取手机的Mac地址
	 */
	public static String getMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * 获取手机的国际移动设备标志【IMEI】
	 */
	public static String getIMEI(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}

	/**
	 * 获取手机的国际移动台识别【IMSI】
	 */
	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}

	/**
	 * 获取手机的成电卡识别码【ICCID】
	 */
	public static String getICCID(Context context) {
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getSimSerialNumber();
	}

	/**
	 * 获取移动台综合业务数字网号码【MSISDN】
	 */
	public static String getMSISDN(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number();
	}


	/**
	 * 获取操作员名字
	 */
	public static String getOperatorName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimOperatorName();
	}
}
