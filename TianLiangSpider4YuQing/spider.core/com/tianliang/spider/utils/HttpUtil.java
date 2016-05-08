package com.tianliang.spider.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 暂不用，由GrabPageSource代替
 * 
 * @author zel
 * 
 */
public class HttpUtil {
	// 日志处理
	public static MyLogger logger = new MyLogger(HttpUtil.class);

	private static final String LINE = "\n";

	private static final String HTTP = "http://";

	/**
	 * 获得网址的源码
	 * 
	 * @param str
	 *            传入的需要获得的网页地址
	 * @return 网页的源码
	 */
	public static String getPageCodeNoCatch(String url, String encoding)
			throws Exception {
		URL u = new URL(url);
		InputStream is = u.openStream();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(is, encoding));
		StringBuffer sb = new StringBuffer();
		while ((url = bufferedReader.readLine()) != null) {
			sb.append(url);
			sb.append(LINE);
		}
		return sb.toString();
	}

	public static String getPageCodeWithCatch(String url, String encoding) {
		try {
			URL u = new URL(url);
			InputStream is = u.openStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(is, encoding));
			StringBuffer sb = new StringBuffer();
			while ((url = bufferedReader.readLine()) != null) {
				sb.append(url);
				sb.append(LINE);
			}
			return sb.toString();
		} catch (Exception e) {
			logger.info(e.getStackTrace());
		}
		return "null";
	}

	/**
	 * 从网址里面抽取链接
	 * 
	 * @return 链接的集合
	 */
	public static List<String> getUrlsByPage(String str) {
		List<String> urls = new ArrayList<String>();
		try {
			URL url = new URL(str);
			int end = 0;
			String host = url.getHost();
			Document doc = Jsoup.parse(url, 30000);
			Elements links = doc.select("a");
			String href = null;
			for (Element link : links) {
				href = link.attr("href");
				if (href.startsWith(HTTP)) {
					urls.add(href);
				} else if (href.startsWith("/")) {
					urls.add(HTTP + host + href);
				} else {
					if (end > 0) {
						urls.add(str + href);
					} else {
						urls.add(str + href);
					}

				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urls;
	}

	public static String getUrlQuery(String url) {
		if (url == null || url.isEmpty()) {
			return null;
		}
		try {
			URL urlPojo = new URL(url);
			return urlPojo.getQuery();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String url = "http://list.tmall.com/search_product.htm?spm=a220m.1000858.1000720.4.J6inmS&cat=50020909&brand=20582&start_price=1500&style=g&search_condition=119&from=sn_1_brand-qp&active=1#J_crumbs";
		System.out.println(getUrlQuery(url));
	}
}
