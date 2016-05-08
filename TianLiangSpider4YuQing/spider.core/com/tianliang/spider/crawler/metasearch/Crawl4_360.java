package com.tianliang.spider.crawler.metasearch;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.tianliang.spider.manager.crawler.PhantomManager;
import com.tianliang.spider.manager.crawler.ProxyManager;
import com.tianliang.spider.pojos.CrawlConfigParaPojo;
import com.tianliang.spider.pojos.ProxyPojo;
import com.tianliang.spider.utils.DateUtil;
import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.RegexPaserUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.vaolan.extkey.utils.UrlOperatorUtil;
import com.vaolan.parser.JsoupHtmlParser;
import com.vaolan.status.DataFormatStatus;
import com.zel.es.manager.nlp.TianLiangEmotionCalcManager;
import com.zel.es.manager.nlp.TianLiangKeyWordExtractorManager;
import com.zel.es.manager.nlp.TianLiangSummaryExtractorManager;
import com.zel.es.manager.nlp.TianLiangThemeExtractorManager;
import com.zel.es.pojos.index.CrawlData4PortalSite;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.enums.CrawlEngineEnum;

public class Crawl4_360 {
	// 日志
	public static MyLogger logger = new MyLogger(Crawl4_360.class);
	/*
	 * 得到搜索结果第一条的content block
	 */
	public static String block_beginTag = "class=\"result";
	public static String block_endTag = "class=\"f13\"";

	/**
	 * 在block截取到相应的分词结果
	 */
	public static String content_beginTag = "<em>";
	public static String content_endTag = "</em>";

	/**
	 * 为得到每为搜索结果的链接块
	 */
	public static String block_beginTag_link = "<h3 class=\"t\"";
	public static String block_endTag_link = "</h3>";

	/**
	 * 提取链接块的链接地址
	 */
	public static String content_beginTag_link = "href=\"";
	public static String content_endTag_link = "\"";

	public static String blockSelector = "div";
	public static List<String> removeSelector = new LinkedList<String>();
	public static List<String> itemSelector = new LinkedList<String>();
	public static List<String> titleSelector = new LinkedList<String>();
	public static List<String> authorSelector = new LinkedList<String>();
	public static List<String> publishTimeSelector = new LinkedList<String>();
	public static List<String> bodySelector = new LinkedList<String>();
	public static List<String> bodySelector_2 = new LinkedList<String>();

	public static List<String> remove_selecot_4_body = new LinkedList<String>();

	// 日期处理类
	private static DateUtil dateUtil = new DateUtil();

	static {
		init();
	}

	public static void init() {
		blockSelector = "div";

		removeSelector.add("p");
		removeSelector.add("span");

//		itemSelector.add("div#main>ul.result>li.res-list");
		itemSelector.add("div#main>div>ul.result>li.res-list");

		titleSelector.add("li>h3");

		authorSelector.add("p.newsinfo>span.sitename");

		publishTimeSelector.add("p.newsinfo>span.posttime");

		bodySelector.add("li>p.content");

		bodySelector_2.add("div.c-summary");

		remove_selecot_4_body.add("p");
		remove_selecot_4_body.add("span");
	}

	/**
	 * 组合所有的百度新闻的搜索结果
	 * 
	 * @param root_url
	 * @param query
	 * @return
	 */
	public static List<CrawlData4PortalSite> getAllNewsSearchResult(
			CrawlTaskPojo taskPojo, boolean isTest) {
		if (taskPojo == null) {
			return null;
		}
		// taskPojo.get
		String query = taskPojo.getValue();

		String txt_data_sub_dir = null;
		String txtFileString = null;

		String query_encode = null;
		try {
			query_encode = URLEncoder.encode(query,
					StaticValue.default_encoding);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("传入的值进行encode时出错，请检查!");
			return null;
		}

		// 由于360搜索在首页跳转时有暂不可解决问题，故采用直接搜索的方式
		String root_url = StaticValue.qihu360_news_search_url_format.replace(
				"${query}", query_encode);

		// 提取出来的搜索结果集合存储变量
		LinkedList<CrawlData4PortalSite> searchResultList = new LinkedList<CrawlData4PortalSite>();
		// 遇到http请求错误，则重复请求http_req_error_repeat次
		for (int i = 0; i < SystemParas.http_req_error_repeat_number; i++) {
			try {
				ProxyPojo proxyPojo = null;
				if (SystemParas.proxy_open) {
					proxyPojo = ProxyManager.getOneProxy();
					System.out.println(proxyPojo);
				}
				CrawlConfigParaPojo crawlConfigParaPojo = PhantomManager
						.crawlKeywordSeg4NewsSearchResult(root_url, query,
								proxyPojo);

				txt_data_sub_dir = crawlConfigParaPojo
						.getData_write_to_file_root_path()
						+ crawlConfigParaPojo.getData_write_to_file_sub_path();
				File[] fileArray = new File(txt_data_sub_dir).listFiles();

				// 读取出其下的每个txtFile
				if (fileArray != null) {
					for (File txtFile : fileArray) {
						// 读出单个文件的文本内容
						txtFileString = IOUtil.readDirOrFile(
								txtFile.getAbsolutePath(),
								StaticValue.default_encoding);

						// 均采用jsoup+正则合力解析
						// 取得每个条目的完整html块
						List<String> itemList = JsoupHtmlParser
								.getNodeContentBySelector(txtFileString,
										itemSelector,
										DataFormatStatus.TagAllContent, false);

						if (StringOperatorUtil.isNotBlankCollection(itemList)) {
							for (String itemBlock : itemList) {
								// 逐条进行解析
								// 首先取得title部分
								List<String> titleList = JsoupHtmlParser
										.getNodeContentBySelector(itemBlock,
												titleSelector,
												DataFormatStatus.TagAllContent,
												false);
								CrawlData4PortalSite crawlData4PortalSite = null;
								if (StringOperatorUtil
										.isNotBlankCollection(titleList)) {
									crawlData4PortalSite = new CrawlData4PortalSite();

									String title_block = titleList.get(0);
									crawlData4PortalSite
											.setTitle(JsoupHtmlParser
													.getCleanTxt(title_block));

									// 取得链接
									String href = JsoupHtmlParser
											.getAttributeValue(title_block,
													"href");
									if (UrlOperatorUtil.isValidUrl(href)) {
										crawlData4PortalSite.setUrl(href);
									} else {
										logger.info("360元搜索时，提取的链接有问题，请检查!");
									}

									// 取得作者
									List<String> authorList = JsoupHtmlParser
											.getNodeContentBySelector(
													itemBlock, authorSelector,
													DataFormatStatus.CleanTxt,
													false);
									if (StringOperatorUtil
											.isNotBlankCollection(authorList)) {
										String author_name = authorList.get(0);
										if (StringOperatorUtil
												.isNotBlank(author_name)) {
											crawlData4PortalSite
													.setAuthor(author_name);
										}
									} else {
										// 没有发现发布者，不是合适的新闻，一般认为是官网
										System.out
												.println("find invalid publisher,will jump!");
										continue;
									}

									// 取得发布时间
									List<String> publishTimeList = JsoupHtmlParser
											.getNodeContentBySelector(
													itemBlock,
													publishTimeSelector,
													DataFormatStatus.TagAllContent,
													false);
									if (StringOperatorUtil
											.isNotBlankCollection(publishTimeList)) {
										String publish_time_block = publishTimeList
												.get(0);
										if (StringOperatorUtil
												.isNotBlank(publish_time_block)) {
											String publish_time_string = JsoupHtmlParser
													.getAttributeValue(
															publish_time_block,
															"title");
											crawlData4PortalSite
													.setPublish_time_string(publish_time_string);
											Date publish_date = dateUtil
													.getDateByNoneStructure4News(crawlData4PortalSite
															.getPublish_time_string());
											if (publish_date != null) {
												crawlData4PortalSite
														.setPublish_time_long(publish_date
																.getTime());
											}
										}
									}

									// 取得摘要，并将摘要作为正文
									List<String> bodyList = JsoupHtmlParser
											.getNodeContentBySelector(
													itemBlock, bodySelector,
													DataFormatStatus.CleanTxt,
													false);
									if (StringOperatorUtil
											.isNotBlankCollection(bodyList)) {
										String body_content = bodyList.get(0);
										crawlData4PortalSite
												.setBody(body_content);
										crawlData4PortalSite
												.setSummary(body_content);
									}
									if (!isTest) {
										// 进行nlp提取
										String body = crawlData4PortalSite
												.getBody();
										if (StringOperatorUtil.isNotBlank(body)) {
											// 提取正文,暂时不用智能提取了!
											// crawlData4PortalSite.setBody(body);
											// 提取关键词
											crawlData4PortalSite
													.setKeyword(TianLiangKeyWordExtractorManager.getKeywordString(
															crawlData4PortalSite
																	.getTitle(),
															body));
											// 提取主题词
											crawlData4PortalSite
													.setTheme_word(TianLiangThemeExtractorManager
															.getThemeKeywordString(body));
											// 提取情感极性
											crawlData4PortalSite
													.setEmotion_polar(TianLiangEmotionCalcManager
															.getSentencePolarString(body));
										}

									}

									// 加入一些非处理字段
									crawlData4PortalSite
											.setInsert_time(DateUtil
													.getLongByDate());
									// 加入source_title字段
									crawlData4PortalSite
											.setSource_title(taskPojo
													.getSource_title());
									crawlData4PortalSite.setMedia_type(taskPojo
											.getMedia_type());

									// System.out.println(crawlData4PortalSite
									// .getTitle()
									// + "\n"
									// + crawlData4PortalSite.getUrl()
									// + "\n"
									// + crawlData4PortalSite.getAuthor()
									// + "\n"
									// + crawlData4PortalSite
									// .getPublish_time_string()
									// + "\n"
									// + new Date(crawlData4PortalSite
									// .getPublish_time_long())
									// + "\n"
									// + crawlData4PortalSite.getBody());

									searchResultList.add(crawlData4PortalSite);
								}
							}
						}
					}
				}
				// 请求一次后，休息一下
				// Thread.sleep(SystemParas.http_req_once_wait_time);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("phantomjs请求过程中出现问题，请检查!");
				// try {
				// Thread.sleep(SystemParas.http_req_once_wait_time);
				// } catch (InterruptedException e1) {
				// e1.printStackTrace();
				// }
			}
		}
		return searchResultList;
	}

	public static void main(String[] args) throws Exception {
		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		// String root_url = "http://www.baidu.com/";
		String root_url = "http://sh.qihoo.com/";
		// String root_url =
		// "http://news.haosou.com/ns?q=%E7%94%B5%E8%84%91&pq=%E7%94%B5%E8%84%91%E9%98%BF&rank=rank&src=srp&tn=news";

		String keyword = "阅兵";
		taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage);
		taskPojo.setValue(keyword);

		List<CrawlData4PortalSite> searchResultList = getAllNewsSearchResult(
				taskPojo, true);
		System.out.println(searchResultList);

		System.out.println("执行完成!");
	}
}
