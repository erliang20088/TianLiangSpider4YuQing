package com.tianliang.spider.utils;

import com.tianliang.spider.crawler.phantomjs.Crawl4Phantomjs;
import com.tianliang.spider.pojos.CrawlResultPojo;

/**
 * 网页截图工具类
 * 
 * @author zel
 * 
 */
public class WebPagePrintScreenUtil {
	public static void printScreen(String url, String aidPicFilePathString) {
		CrawlResultPojo resultPojo = Crawl4Phantomjs
				.crawlHtmlSourceByRandomUrl(url, aidPicFilePathString);
	}
	
	public static void main(String[] args) {
		String url = "http://fanyi.baidu.com/?keyfrom=alading#en/zh/printscreen";
		String picPath = "D:/tmp/tttt";
		
		printScreen(url, picPath);
	}
}
