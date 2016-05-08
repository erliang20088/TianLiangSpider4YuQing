package com.tianliang.spider.utils;

import java.util.List;

/**
 * 字符串操作工具类
 * 
 * @author zel
 * 
 */
public class StringOperatorUtil {
	public static String getValue(String source, String default_value) {
		if (source == null || source.trim().length() == 0) {
			return default_value;
		}
		return source;
	}

	public static boolean isBlank(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isBlankCollection(List list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(String str) {
		if (str == null || str.trim().length() == 0) {
			return false;
		}
		return true;
	}

	public static boolean isNotBlankCollection(List list) {
		if (list == null || list.isEmpty()) {
			return false;
		}
		return true;
	}

	public static String removeLRWhiteChar(String source) {
		String result = null;
		if (source.matches("[\\S]*\\s[\\S]*[\\s]*")) {
			// source.
		}
		return result;
	}

	public static int getNumber(String numberString) {
		if (StringOperatorUtil.isBlank(numberString)) {
			return 0;
		}
		numberString = numberString.replaceAll("\\D", "");
		if (StringOperatorUtil.isBlank(numberString)) {
			return 0;
		}
		return Integer.parseInt(numberString);
	}

	public static void main(String[] args) {
		String str = "11abc2";
		System.out.println(getNumber(str));
	}
}
