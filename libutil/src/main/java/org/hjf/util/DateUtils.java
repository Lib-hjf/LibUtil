package org.hjf.util;

import android.content.Context;

import java.util.Calendar;

/**
 * 时间日期工具类
 * @author huangjf
 *
 */
public class DateUtils {

	/**
	 * 获取指定的时间描述。
	 * format：年:月:日  小时:分钟:秒
	 */
	public static String getDateInfo4_YMD_HMS_InTheTime(Context context, long timeInMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		int year =  calendar.get(Calendar.YEAR);
		int month =  calendar.get(Calendar.MONTH) + 1; // 月份是从0开始的
		int day =  calendar.get(Calendar.DAY_OF_MONTH);
		int hour =  calendar.get(Calendar.HOUR_OF_DAY);
		int minute =  calendar.get(Calendar.MINUTE);
		int second =  calendar.get(Calendar.SECOND);
		return context.getString(R.string.date_y_m_d_h_m_s, year, month, day, hour, minute, second);
	}

	/**
	 * 获取指定的时间描述。
	 * format：年:月:日
	 */
	public static String getDateInfo4_YMD_InTheTime(Context context, long timeInMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		int year =  calendar.get(Calendar.YEAR);
		int month =  calendar.get(Calendar.MONTH) + 1; // 月份是从0开始的
		int day =  calendar.get(Calendar.DAY_OF_MONTH);
		return context.getString(R.string.date_y_m_d, year, month, day);
	}

	/**
	 * 获取指定的时间描述。
	 * format：年:月:日
	 */
	public static String getDateInfo4_HMS_InTheTime(Context context, long timeInMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		int hour =  calendar.get(Calendar.HOUR_OF_DAY);
		int minute =  calendar.get(Calendar.MINUTE);
		int second =  calendar.get(Calendar.SECOND);
		return context.getString(R.string.date_h_m_s, hour, minute, second);
	}
}
