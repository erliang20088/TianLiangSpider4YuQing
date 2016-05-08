package com.tianliang.spider.manager.crawler;

import java.io.File;
import java.net.URLEncoder;

import com.tianliang.spider.pojos.CrawlConfigParaPojo;
import com.tianliang.spider.pojos.PhantomjsConfigParaPojo;
import com.tianliang.spider.pojos.ProxyPojo;
import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.PhantomJsOperatorUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;

/**
 * phantomjs调用管理类
 * 
 * @author zel
 * 
 */
public class PhantomManager {
	/**
	 * 抓取给定的url中搜索keyword值时的标红结果
	 * 
	 * @param root_url
	 * @param keyword
	 * @param proxyPojo
	 * @return
	 */
	public static CrawlConfigParaPojo crawlKeywordSeg4MetaSearch(
			String root_url, String keyword, ProxyPojo proxyPojo) {
		if (StringOperatorUtil.isBlank(root_url)
				|| StringOperatorUtil.isBlank(keyword)) {
			return null;
		}
		// 组合成相关参数集合
		CrawlConfigParaPojo crawlConfigParaPojo = new CrawlConfigParaPojo(
				root_url, keyword, 0);
		String config_para_string = crawlConfigParaPojo.toString();
		String config_para_json_path = SystemParas.phantomjs_crawl_config_js_para_root_path
				+ "baidu_config_para_file_"
				+ crawlConfigParaPojo.getKeyword_md5() + ".json";
		IOUtil.writeFile(config_para_json_path, config_para_string,
				StaticValue.default_encoding);

		String phantomjs_config_path = getPhantomjsConfigFilePath(
				crawlConfigParaPojo, proxyPojo);

		PhantomJsOperatorUtil.crawl(phantomjs_config_path,
				SystemParas.phantomjs_crawl_config_js_root_path
						+ StaticValue.phantomjs_js_crawl_4_baidu_webpage,
				config_para_json_path);

		return crawlConfigParaPojo;
	}

	public static CrawlConfigParaPojo crawlKeywordSeg4NewsSearchResult(
			String root_url, String keyword, ProxyPojo proxyPojo) {
		if (StringOperatorUtil.isBlank(root_url)
				|| StringOperatorUtil.isBlank(keyword)) {
			return null;
		}

		// 组合成相关参数集合
		CrawlConfigParaPojo crawlConfigParaPojo = new CrawlConfigParaPojo(
				root_url, keyword, 0);
		String config_para_string = crawlConfigParaPojo.toString();
		String config_para_json_path = SystemParas.phantomjs_crawl_config_js_para_root_path
				+ getConfigFileNamePrefix(root_url)
				+ crawlConfigParaPojo.getKeyword_md5() + ".json";
		IOUtil.writeFile(config_para_json_path, config_para_string,
				StaticValue.default_encoding);

		String phantomjs_config_path = getPhantomjsConfigFilePath(
				crawlConfigParaPojo, proxyPojo);

		PhantomJsOperatorUtil.crawl(phantomjs_config_path,
				SystemParas.phantomjs_crawl_config_js_root_path
						+ getCrawlJsFilePath(root_url), config_para_json_path);

		// 4 donate
		// PhantomJsOperatorUtil.crawl(phantomjs_config_path,
		// SystemParas.phantomjs_crawl_config_js_root_path
		// + "donate4snow.js", config_para_json_path);

		return crawlConfigParaPojo;
	}

	public static String getCrawlJsFilePath(String root_url) {
		if (root_url.contains("baidu.com")) {
			return StaticValue.phantomjs_js_crawl_4_baidu_news;
		} else if (root_url.contains("qihoo.com")
				|| root_url.contains("haosou.com")) {
			return StaticValue.phantomjs_js_crawl_4_360_news;
		} else if (root_url.contains("weixin.sogou.com")) {
			return StaticValue.phantomjs_js_crawl_4_weixin_sogou;
		} else if (root_url.contains("sogou.com")) {
			return StaticValue.phantomjs_js_crawl_4_sogou_news;
		}
		return null;
	}

	public static String getConfigFileNamePrefix(String root_url) {
		if (root_url.contains("baidu.com")) {
			return "baidu_";
		} else if (root_url.contains("qihoo.com")
				|| root_url.contains("haosou.com")) {
			return "360_";
		} else if (root_url.contains("weixin.sogou.com")) {
			return "weixin_";
		} else if (root_url.contains("sogou.com")) {
			return "sogou_";
		}
		return null;
	}

	// 创建phantomjs config file
	private static String getPhantomjsConfigFilePath(
			CrawlConfigParaPojo crawlConfigParaPojo, ProxyPojo proxyPojo) {
		String config_file_para = crawlConfigParaPojo.getKeyword_md5() == null ? crawlConfigParaPojo
				.getRoot_url_md5() : crawlConfigParaPojo.getKeyword_md5();

		// phantomjs config path值
		String phantomjs_config_path = SystemParas.phantomjs_crawl_config_json_root_path
				+ "config_phantomjs_" + config_file_para + ".json";
		// 将phantomjs的配置参数写入文件
		if (proxyPojo != null) {
			PhantomjsConfigParaPojo phantomjsConfigParaPojo = new PhantomjsConfigParaPojo(
					proxyPojo);
			IOUtil.writeFile(phantomjs_config_path,
					phantomjsConfigParaPojo.toString(),
					StaticValue.default_encoding);
		} else {// 没有proxy直接按默认的配置即可
			phantomjs_config_path = SystemParas.phantomjs_crawl_config_json_root_path
					+ StaticValue.phantomjs_config_file_name_default;
			if (new File(phantomjs_config_path).exists()) {
				// 如果存在，则不再重新写入了!
			} else {
				// 重新写入一次
				PhantomjsConfigParaPojo phantomjsConfigParaPojo = new PhantomjsConfigParaPojo(
						null);
				IOUtil.writeFile(phantomjs_config_path,
						phantomjsConfigParaPojo.toString(),
						StaticValue.default_encoding);
			}

		}
		return phantomjs_config_path;
	}

	// 其中batchNumber是指一个关键字命中的URL的第几个，以此作为同一个关键字对应的不同的URL的正文
	public static CrawlConfigParaPojo crawlBody4MetaSearch(String root_url,
			String keyword, int batchNumber, ProxyPojo proxyPojo) {
		if (StringOperatorUtil.isBlank(root_url)
				|| StringOperatorUtil.isBlank(keyword)) {
			return null;
		}
		// 组合成相关参数集合
		CrawlConfigParaPojo crawlConfigParaPojo = new CrawlConfigParaPojo(
				root_url, keyword, 1);
		crawlConfigParaPojo.setBody_pic_or_txt_count(batchNumber);

		String config_para_string = crawlConfigParaPojo.toString();

		String config_para_json_path = SystemParas.phantomjs_crawl_config_js_para_root_path
				+ "baidu_config_para_file_"
				+ crawlConfigParaPojo.getKeyword_md5() + ".json";
		IOUtil.writeFile(config_para_json_path, config_para_string,
				StaticValue.default_encoding);

		String phantomjs_config_path = getPhantomjsConfigFilePath(
				crawlConfigParaPojo, proxyPojo);

		PhantomJsOperatorUtil.crawl(phantomjs_config_path,
				SystemParas.phantomjs_crawl_config_js_root_path
						+ StaticValue.phantomjs_js_crawl_4_baidu_body,
				config_para_json_path);

		return crawlConfigParaPojo;
	}

	// 抓取任意url的内容和截图等
	public static CrawlConfigParaPojo crawlBody4MetaSearch(String pageUrl,
			ProxyPojo proxyPojo, String aidPicFilePathString) {
		if (StringOperatorUtil.isBlank(pageUrl)) {
			return null;
		}
		// 组合成相关参数集合
		CrawlConfigParaPojo crawlConfigParaPojo = new CrawlConfigParaPojo(
				pageUrl, null, 2,aidPicFilePathString);

		String config_para_string = crawlConfigParaPojo.toString();
		
		String config_para_json_path = SystemParas.phantomjs_crawl_config_js_para_root_path
				+ "baidu_config_para_file_"
				+ crawlConfigParaPojo.getRoot_url_md5() + ".json";
		IOUtil.writeFile(config_para_json_path, config_para_string,
				StaticValue.default_encoding);

		String phantomjs_config_path = getPhantomjsConfigFilePath(
				crawlConfigParaPojo, proxyPojo);

		PhantomJsOperatorUtil.crawl(phantomjs_config_path,
				SystemParas.phantomjs_crawl_config_js_root_path
						+ StaticValue.phantomjs_js_crawl_4_baidu_random_page,
				config_para_json_path);

		return crawlConfigParaPojo;
	}

	public static void main(String[] args) {
		// String root_url = "http://www.baidu.com/";
		String root_url = "http://news.sina.com.cn/c/2015-03-06/131531576160.shtml";
		String keyword = "来自星星的你";

		CrawlConfigParaPojo crawlConfigParaPojo = crawlBody4MetaSearch(
				root_url, null, null);

		System.out.println("执行完成");
	}
}
