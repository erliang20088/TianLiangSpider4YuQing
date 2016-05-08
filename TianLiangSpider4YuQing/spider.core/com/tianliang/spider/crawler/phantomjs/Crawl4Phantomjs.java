package com.tianliang.spider.crawler.phantomjs;

import com.tianliang.spider.manager.crawler.PhantomManager;
import com.tianliang.spider.manager.crawler.ProxyManager;
import com.tianliang.spider.pojos.CrawlConfigParaPojo;
import com.tianliang.spider.pojos.CrawlResultPojo;
import com.tianliang.spider.pojos.ProxyPojo;
import com.tianliang.spider.utils.FileOperatorUtil;
import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;

public class Crawl4Phantomjs {
	// 日志
	public static MyLogger logger = new MyLogger(Crawl4Phantomjs.class);

	// 抓取任意URL的内容或截图
	public static CrawlResultPojo crawlHtmlSourceByRandomUrl(String pageUrl) {
		CrawlResultPojo resultPojo = new CrawlResultPojo();
		if (StringOperatorUtil.isBlank(pageUrl)) {
			resultPojo.setNormal(false);
			return resultPojo;
		}
		String txt_data_file_path = null;
		String txtFileString = null;
		ProxyPojo proxyPojo = null;
		// 遇到http请求错误，则重复请求http_req_error_repeat次
		for (int i = 0; i < SystemParas.http_req_error_repeat_number; i++) {
			try {
				if (SystemParas.proxy_open) {
					proxyPojo = ProxyManager.getOneProxy();
					System.out.println(proxyPojo);
				}

				CrawlConfigParaPojo crawlConfigParaPojo = PhantomManager
						.crawlBody4MetaSearch(pageUrl, proxyPojo,null);

				txt_data_file_path = crawlConfigParaPojo
						.getAbsolute_body_file_path();

				if (StringOperatorUtil.isNotBlank(txt_data_file_path)
						&& FileOperatorUtil.existFile(txt_data_file_path)) {
					// 读取出其下的每个txtFile
					txtFileString = IOUtil.readDirOrFile(txt_data_file_path,
							StaticValue.default_encoding);
					resultPojo.setNormal(true);
					resultPojo.setHtmlSource(txtFileString);
					return resultPojo;
				}
			} catch (Exception e) {
				resultPojo.setNormal(false);
				resultPojo.setDesc(e.getLocalizedMessage());
				e.printStackTrace();
				logger.info("phantomjs在请求过程中出现异常，将重新请求!");
			}
		}
		return resultPojo;
	}

	// 对单pageUrl参数的重载，解决指定文件路径的存储问题
	public static CrawlResultPojo crawlHtmlSourceByRandomUrl(String pageUrl,
			String aidPicFilePathString) {
		CrawlResultPojo resultPojo = new CrawlResultPojo();
		if (StringOperatorUtil.isBlank(pageUrl)) {
			resultPojo.setNormal(false);
			return resultPojo;
		}
		String txt_data_file_path = null;
		String txtFileString = null;
		ProxyPojo proxyPojo = null;
		// 遇到http请求错误，则重复请求http_req_error_repeat次
		for (int i = 0; i < SystemParas.http_req_error_repeat_number; i++) {
			try {
				if (SystemParas.proxy_open) {
					proxyPojo = ProxyManager.getOneProxy();
					System.out.println(proxyPojo);
				}

				CrawlConfigParaPojo crawlConfigParaPojo = PhantomManager
						.crawlBody4MetaSearch(pageUrl, proxyPojo,aidPicFilePathString);
				
				return null;
			} catch (Exception e) {
				resultPojo.setNormal(false);
				resultPojo.setDesc(e.getLocalizedMessage());
				e.printStackTrace();
				logger.info("phantomjs在请求过程中出现异常，将重新请求!");
			}
		}
		return resultPojo;
	}

	public static void main(String[] args) throws Exception {
		String url = "http://news.sina.com.cn/c/2015-03-06/131531576160.shtml";
		CrawlResultPojo crawlResultPojo = crawlHtmlSourceByRandomUrl(url);

		System.out.println(crawlResultPojo.getHtmlSource());

		System.out.println("执行完成!");
	}
}
