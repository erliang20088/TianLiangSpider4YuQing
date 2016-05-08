package com.tianliang.spider.crawler.metasearch;

/**
 * 用360新闻搜索的 api来获取数据
 */
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tianliang.spider.crawler.httpclient.Crawl4HttpClient;
import com.tianliang.spider.manager.crawler.ProxyManager;
import com.tianliang.spider.pojos.CrawlResultPojo;
import com.tianliang.spider.pojos.HttpRequestPojo;
import com.tianliang.spider.pojos.ProxyPojo;
import com.tianliang.spider.utils.DateUtil;
import com.tianliang.spider.utils.GsonOperatorUtil;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.QiHuNewsSearchAccessTokenGeneratorUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.vaolan.extkey.utils.UrlOperatorUtil;
import com.zel.es.manager.nlp.TianLiangEmotionCalcManager;
import com.zel.es.manager.nlp.TianLiangKeyWordExtractorManager;
import com.zel.es.manager.nlp.TianLiangThemeExtractorManager;
import com.zel.es.pojos.index.CrawlData4PortalSite;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.enums.CrawlEngineEnum;

public class Crawl4_360_4_API {
	// 日志
	public static MyLogger logger = new MyLogger(Crawl4_360_4_API.class);

	public static String client_id = "conac";
	public static String md5_key = "H3mnoa83Eb3T";
	public static String rank_type = "pdate";

	static {
		init();
	}

	public static void init() {
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
		String query = taskPojo.getValue();

		// 提取出来的搜索结果集合存储变量
		LinkedList<CrawlData4PortalSite> searchResultList = new LinkedList<CrawlData4PortalSite>();
		// 遇到http请求错误，则重复请求http_req_error_repeat次
		for (int i = 0; i < SystemParas.http_req_error_repeat_number; i++) {
			try {
				// 该采集暂不用代理采集
				// ProxyPojo proxyPojo = null;
				// if (SystemParas.proxy_open) {
				// proxyPojo = ProxyManager.getOneProxy();
				// System.out.println(proxyPojo);
				// }
				String enocde_query = new String(query.getBytes("utf-8"),
						"iso8859-1");
				String access_token = QiHuNewsSearchAccessTokenGeneratorUtil
						.getAccessToken(enocde_query, client_id, md5_key);

				// 默认按5页处理
				int page_size = 10;
				for (int pageNumber = 0; pageNumber < 5; pageNumber++) {
					List<CrawlData4PortalSite> onePageResultList = getOnePageNewsSearchResult(
							query, pageNumber * page_size, page_size,
							rank_type, access_token, client_id,
							taskPojo.getSource_title(),
							taskPojo.getMedia_type(), isTest);
					if (StringOperatorUtil
							.isNotBlankCollection(onePageResultList)) {
						searchResultList.addAll(onePageResultList);
						if (onePageResultList.size() < page_size) {
							// 当抓取的当前页的条数小于pageSize，认为已抓取到尾页
							logger.info("360新闻搜索api方式，将" + query
									+ "抓取的当前页记录条数小于" + page_size + ",认为抓取完成!");
							break;
						}
					} else {
						logger.info("360新闻搜索api方式，将" + query + "抓取完成!");
						break;
					}
				}
			} catch (Exception e) {
				// 如果发现异常，先清空所有已采集到的集合
				searchResultList.clear();
				e.printStackTrace();
			}
		}
		return searchResultList;
	}

	// 得到某一页的搜索结果
	/**
	 * 
	 * @param query
	 * @param start_offset
	 * @param length
	 *            ,其中的length暂不管用，均为10条数据
	 * @param rank_type
	 * @param access_token
	 * @param client_id
	 * @return
	 */
	public static List<CrawlData4PortalSite> getOnePageNewsSearchResult(
			String query, int start_offset, int length, String rank_type,
			String access_token, String client_id, String source_title,
			int media_type, boolean isTest) {

//		ProxyPojo proxyPojo = null;
//		if (SystemParas.proxy_open) {
//			proxyPojo = ProxyManager.getOneProxy();
//			System.out.println(proxyPojo);
//		}
		try {
			query = URLEncoder.encode(query, StaticValue.default_encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// http://open.www.haosou.com/newssearch?cid=conac&q=360&m=519e67e7a7e0a194&s=0&r=rank
		String format_url = StaticValue.qihu360_news_search_url_format_4_api
				.replace("${client_id}", client_id).replace("${query}", query)
				.replace("${access_token}", access_token)
				.replace("${start_offset}", "" + start_offset)
				.replace("${rank_type}", rank_type);
		System.out.println("format_url=" + format_url);
		CrawlResultPojo crawlResultPojo = crawlHtmlSourceByRandomUrl4HttpClient(
				format_url);

		// System.out.println("crawlResultPojo htmlSource="
		// + crawlResultPojo.getHtmlSource());
		String htmlSource = crawlResultPojo.getHtmlSource();
		LinkedList<CrawlData4PortalSite> searchResultList = null;
		if (StringOperatorUtil.isNotBlank(htmlSource)) {
			// 解析该返回的json串
			String title = null;
			String url = null;
			String sitename = null;
			String content_body = null;
			String publish_time_string = null;
			try {
				// System.out.println("htmlSource="+htmlSource);
				JsonObject jsonObj = GsonOperatorUtil.parse(htmlSource)
						.getAsJsonObject();
				String errorFlag = jsonObj.get("errno").getAsString();
				// System.out.println("errorFlag="+errorFlag);
				if ("0".equals(errorFlag)) {
					// 代表正常请求到了
					searchResultList = new LinkedList<CrawlData4PortalSite>();
					JsonArray resultJsonArray = jsonObj.get("result")
							.getAsJsonArray();
					Iterator<JsonElement> iter = resultJsonArray.iterator();
					CrawlData4PortalSite crawlData4PortalSite = null;
					while (iter.hasNext()) {
						crawlData4PortalSite = new CrawlData4PortalSite();
						JsonObject temp_object = iter.next().getAsJsonObject();
						title = temp_object.get("title").getAsString();
						url = temp_object.get("url").getAsString();
						sitename = temp_object.get("sitename").getAsString();
						content_body = temp_object.get("content").getAsString();
						publish_time_string = temp_object.get("pdate")
								.getAsString();

						crawlData4PortalSite.setTitle(title);
						crawlData4PortalSite.setUrl(url);
						crawlData4PortalSite.setAuthor(sitename);
						crawlData4PortalSite.setBody(content_body);
						crawlData4PortalSite.setSummary(content_body);
						if (StringOperatorUtil.isNotBlank(publish_time_string)) {
							crawlData4PortalSite
									.setPublish_time_string(publish_time_string);
							crawlData4PortalSite.setPublish_time_long(Long
									.parseLong(publish_time_string + "000"));
						} else {
							continue;
						}

						// 进行nlp提取
						if (!isTest) {
							// 进行nlp提取
							String body = crawlData4PortalSite.getBody();
							if (StringOperatorUtil.isNotBlank(body)) {
								// 提取正文,暂时不用智能提取了!
								// crawlData4PortalSite.setBody(body);
								// 提取关键词
								crawlData4PortalSite
										.setKeyword(TianLiangKeyWordExtractorManager
												.getKeywordString(
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
						crawlData4PortalSite.setInsert_time(DateUtil
								.getLongByDate());
						// 加入source_title字段
						crawlData4PortalSite.setSource_title(source_title);
						crawlData4PortalSite.setMedia_type(media_type);

//						System.out.println("title="
//								+ crawlData4PortalSite.getTitle()
//								+ "\n"
//								+ "url="
//								+ crawlData4PortalSite.getUrl()
//								+ "\n"
//								+ "author="
//								+ crawlData4PortalSite.getAuthor()
//								+ "\n"
//								+ "publish_time_string="
//								+ crawlData4PortalSite.getPublish_time_string()
//								+ "\n"
//								+ "publish_time_date="
//								+ new Date(crawlData4PortalSite
//										.getPublish_time_long()) + "\n"
//								+ "body=" + crawlData4PortalSite.getBody());
						searchResultList.add(crawlData4PortalSite);
					}
				} else {
					// 代表请正异常，直接break;
					logger.info("360搜索返回结果出现异常,errno!=0，请检查!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("360搜索返回结果出现异常，请检查!");
			}
		}

		return searchResultList;
	}

	// 抓取任意URL的内容或截图
	public static CrawlResultPojo crawlHtmlSourceByRandomUrl4HttpClient(
			String pageUrl) {
		CrawlResultPojo resultPojo = new CrawlResultPojo();
		if (StringOperatorUtil.isBlank(pageUrl)) {
			resultPojo.setNormal(false);
			return resultPojo;
		}
		HttpRequestPojo requestPojo = new HttpRequestPojo(pageUrl);

		if (requestPojo.getHeaderMap() == null) {
			String host = UrlOperatorUtil.getHost(pageUrl);
			StaticValue.headerMap.put("Host", host);
			StaticValue.headerMap.put("Connection", "Keep-Alive");
			StaticValue.headerMap.put("Accept",
					"text/html, application/xhtml+xml, */*");
			StaticValue.headerMap.put("Accept-Encoding", "gzip, deflate");
			StaticValue.headerMap.put("Accept-Language", "zh-CN");

			requestPojo.setHeaderMap(StaticValue.headerMap);
		}
		// 如果遇到http请求错误，则重复请求http_req_error_repeat次来确定是否能得到内容
		for (int i = 0; i < SystemParas.http_req_error_repeat_number; i++) {
			try {
				String htmlSource = Crawl4HttpClient.crawlWebPage(requestPojo);
				if (htmlSource == null || htmlSource.isEmpty()) {
					continue;
				}

				resultPojo.setNormal(true);
				resultPojo.setHtmlSource(htmlSource);
				return resultPojo;
			} catch (Exception e) {
				resultPojo.setNormal(false);
				resultPojo.setDesc(e.getLocalizedMessage());
				e.printStackTrace();
				logger.info("httpclient请求过程中出现问题，请检查!");
				try {
					Thread.sleep(SystemParas.http_req_once_wait_time);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return resultPojo;
	}

	public static void main(String[] args) throws Exception {
		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		// String root_url = "http://www.baidu.com/";
		String root_url = "http://sh.qihoo.com/";
		// String root_url =
		// "http://news.haosou.com/ns?q=%E7%94%B5%E8%84%91&pq=%E7%94%B5%E8%84%91%E9%98%BF&rank=rank&src=srp&tn=news";

		String keyword = "阅兵";
		// taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage);
		taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage_360Search);
		taskPojo.setValue(keyword);

		// List<CrawlData4PortalSite> searchResultList = getAllNewsSearchResult(
		// taskPojo, true);

		String query = "奥巴马";
		int start_offset = 0;
		int length = 10;

		String enocde_query = new String(query.getBytes("utf-8"), "iso8859-1");
		String access_token = QiHuNewsSearchAccessTokenGeneratorUtil
				.getAccessToken(enocde_query, client_id, md5_key);

		// getOnePageNewsSearchResult(query, start_offset, length, rank_type,
		// access_token, client_id, "", 8, true);
		List<CrawlData4PortalSite> resultList = getAllNewsSearchResult(
				taskPojo, true);

		System.out.println(resultList.size());

		System.out.println("执行完成!");
	}
}
