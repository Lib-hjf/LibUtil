package org.hjf.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/**
 * 网络相关工具类
 */
public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    public static final int TYPE_NONE = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_NET = 2;
    public static final int TYPE_WAP = 3;
    public static final int TYPE_ALL = 4;

    public static int getNetWorkType(Context context) {
        // 获取系统的连接服务
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // 获取网络的连接情况
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // 无网络连接
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return TYPE_NONE;
        }

        // WIFI链接
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return TYPE_WIFI;

            // 移动数据链接
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return TYPE_WAP;

            // 以太网数据连接
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            return TYPE_NET;

        } else {
            return TYPE_WAP;
        }
    }

    public static boolean isWIFI(Context context) {
        int type = getNetWorkType(context);
        return (type == TYPE_WIFI);
    }

    public static String getWifiSSID(Context context) {
        if (context == null) {
            return "";
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public static String getWifiMacAddress(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int wifi_state = wm.getWifiState();
            if (wifi_state == WifiManager.WIFI_STATE_ENABLED) {
                WifiInfo info = wm.getConnectionInfo();
                String mac_addr = info.getMacAddress();
                String lower_addr = mac_addr.toLowerCase();
                return lower_addr;
            }
        } catch (Exception e) {
            // 模拟器或某些平板可能为空
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isNetworkAvailable(Context paramContext) {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext.getSystemService("connectivity");
        if (localConnectivityManager == null) {
            return false;
        }

        NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager
                .getAllNetworkInfo();
        if (arrayOfNetworkInfo == null) {
            return false;
        }

        for (int i = 0; i < arrayOfNetworkInfo.length; i++) {

            if ((!arrayOfNetworkInfo[i].isAvailable()) || (!arrayOfNetworkInfo[i].isConnected()))
                continue;

            return true;
        }
        return false;

    }

    /**
     * 判断wifi是否有密码
     */
    public static boolean isWifiHasPassword(ScanResult scanResult) {
        String capabilities = scanResult.capabilities.trim();
        if (TextUtils.isEmpty(capabilities)
                || !capabilities.contains("WPA")) {
            return false;
        }
        return true;
    }
}
