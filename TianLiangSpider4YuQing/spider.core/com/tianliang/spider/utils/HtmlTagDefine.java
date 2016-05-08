package com.tianliang.spider.utils;

import java.net.URL;

/**
 * html标签的标签定义
 * 
 * @author zel
 * 
 */
public class HtmlTagDefine {
	public static String A_Href = "href";
	public static String Link_Sign_And = "&amp;";
	public static String Http_Prefix = "http:";
	public static String Title = "title";

	public static String data_filter_url_prefix = "http://detail.tmall.com/item.htm?";

	public static String getUrlAndNeedParas(String url) {
		String joinString = "";
		try {
			URL myUrl = new URL(url);

			String[] splitKeyArray = myUrl.getQuery().split("&");

			for (String str : splitKeyArray) {
				String[] tempArray = str.split("=");
				if (tempArray[0].equals("id")) {
					// 不加&,其它的情况均加&
					joinString = joinString + str;
				} else if (tempArray[0].equals("cat_id")) {
					joinString = joinString + "&" + str;
				} else if (tempArray[0].equals("is_b")) {
					joinString = joinString + "&" + str;
				}
				// else {
				// System.out.println("=========================" + url
				// + "，这个url不规范");
				// }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return data_filter_url_prefix + joinString;
	}

	public static void main(String[] args) {
		String url = "http://detail.tmall.com/item.htm?id=35024402964&areaId=&user_id=&is_b=1&cat_id=52336010&q=&rn=";
		System.out.println(getUrlAndNeedParas(url));
	}
}
