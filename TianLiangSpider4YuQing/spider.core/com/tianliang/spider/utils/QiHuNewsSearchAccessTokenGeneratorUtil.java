package com.tianliang.spider.utils;

/**
 * 360新闻搜索接口中的access_token值的生成
 * 
 * @author zhouerliang
 *
 */
public class QiHuNewsSearchAccessTokenGeneratorUtil {
	public static MD5 md5Util = new MD5();

	public synchronized static String getAccessToken(String query,
			String client_id, String key) {
		return md5Util.MD5_Normal(client_id + query + key).substring(0, 16);
	}

	public static void main(String[] args) throws Exception {
		String query = "编办";
		String clientId = "conac";
		String key = "H3mnoa83Eb3T";
		query = new String(query.getBytes("utf-8"), "iso8859-1");
		System.out.println(getAccessToken(query, clientId, key));
	}
}
