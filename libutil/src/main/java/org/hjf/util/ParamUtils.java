package org.hjf.util;

public class ParamUtils {

	/**
	 * 对指定 int 值进行值域检查，并返回在值域中最符合的数值
	 *
	 * @return 如果value < min，则返回min；如果value > max，则返回max；否则返回value本身。
	 */
	public static int getValueInRange(int value, int min, int max){
		return value < min ? min : (value > max ? max : value);
	}

	/**
	 * 检查指定 int 值是否在值域内
	 * @return true：在值域内
	 */
	public static boolean isInValueRange(int value, int min, int max){
		return value == getValueInRange(value, min, max);
	}
}
