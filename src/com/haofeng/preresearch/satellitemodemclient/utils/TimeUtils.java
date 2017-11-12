package com.haofeng.preresearch.satellitemodemclient.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * 
 * @author 
 * @date 2016-11-29 14:25
 */
public class TimeUtils {

	/**
	 * <string name="txt_res_time_format">yyyy年MM月dd日&#160;HH:mm</string>
	 */
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	/**
	 * 获取当前系统时间
	 * 
	 * @return
	 */
	public static String getSystemTime() {
		Date date = new Date();
		return sdf.format(date);
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 格式化时间格式
	 */
	public static String getFormatTime(long time) {
		Date date = new Date(time);
		return sdf.format(date);
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
		return format.format(new Date(time));
	}
}
