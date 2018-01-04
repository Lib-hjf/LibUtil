package org.hjf.util;


public class BASE64Utils {
	public static String getBASE64(String s) {
		if (s == null){
			return null;
		}
		return android.util.Base64.encodeToString(s.getBytes(), android.util.Base64.DEFAULT);
	}

	public static String getBASE64(byte[] byte_array) {
		if (byte_array == null){
			return null;
		}
		return android.util.Base64.encodeToString(byte_array, android.util.Base64.DEFAULT);
	}

	// 将 BASE64Utils 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null){
			return null;
		}
		return new String(android.util.Base64.decode(s, android.util.Base64.DEFAULT));
	}
}
