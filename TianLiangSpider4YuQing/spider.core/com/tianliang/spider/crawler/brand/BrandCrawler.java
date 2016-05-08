package com.tianliang.spider.crawler.brand;

import java.util.LinkedList;
import java.util.List;

import com.tianliang.spider.crawler.httpclient.Crawl4HttpClient;
import com.tianliang.spider.pojos.BrandPojo;
import com.tianliang.spider.pojos.CrawlResultPojo;
import com.tianliang.spider.pojos.HttpRequestPojo;
import com.tianliang.spider.utils.HtmlParserUtil;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.vaolan.extkey.utils.UrlOperatorUtil;
import com.vaolan.parser.JsoupHtmlParser;
import com.vaolan.status.DataFormatStatus;

public class BrandCrawler {
	// 日志
	public static MyLogger logger = new MyLogger(BrandCrawler.class);
	public static HtmlParserUtil htmlParserUtil = new HtmlParserUtil();

	public static List<String> all_block_selector = new LinkedList<String>();
	// public static List<String> image_url_selector = new LinkedList<String>();
	public static List<String> item_selector = new LinkedList<String>();

	static {
		init();
	}

	public static void init() {
		all_block_selector.add("div#print_star");
		item_selector.add("table>tbody>tr>td");

	}

	public static BrandPojo crawlOnePage(String url, String from_source) {
		CrawlResultPojo crawlResultPojo = crawlHtmlSourceByRandomUrl4HttpClient(url);
		BrandPojo brandPojo = null;
		// System.out
		// .println("crawlResultPojo=" + crawlResultPojo.getHtmlSource());
		if (crawlResultPojo.isNormal) {
			String htmlSource = crawlResultPojo.getHtmlSource();
			List<String> blockList = JsoupHtmlParser.getNodeContentBySelector(
					htmlSource, all_block_selector,
					DataFormatStatus.TagAllContent, false);
			if (StringOperatorUtil.isNotBlankCollection(blockList)) {
				String blockDivContent = blockList.get(0);

				List<String> itemList = JsoupHtmlParser
						.getNodeContentBySelector(blockDivContent,
								item_selector, DataFormatStatus.TagAllContent,
								false);
				// 至少得有两个td及以上
				if (itemList.size() >= 2) {
					String title = null;
					String value = null;
					brandPojo = new BrandPojo();
					brandPojo.setFrom_source(from_source);

					for (int i = 0; i < itemList.size() && i+2<itemList.size(); i = i + 2) {
						title = itemList.get(i);
						value = itemList.get(i + 1);

						title = JsoupHtmlParser.getCleanTxt(title);
						if (title.equals("商标图样")) {
							// 取得链接
							String image_src = JsoupHtmlParser
									.getAttributeValue(value, "src");
							brandPojo.setImage_url(image_src);
							// System.out.println("title=" + title);
							// System.out.println("image_src=" + image_src);
						} else if (title.equals("商标名称")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setName(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("注册号/申请号")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setRegister_or_apply_number(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("商标类别")) {
							// 取得链接
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setClassify_number(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("申请日")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setApply_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("商标类型")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setClassify_level(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("商品/服务")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setProduct_or_service_content(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("指定颜色")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setApply_color(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("是否共有商标")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setIs_common_use(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("国际注册日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setInternational_register_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("后指定日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setAfter_specify_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("优先权日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setPriority_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("申请人名称（中文）")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setApply_username_chinese(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("申请人地址（中文）")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setApply_address_chinese(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("申请人名称（英文）")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setApply_username_english(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("申请人地址（英文）")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setApply_address_english(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("代理公司")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setAgent_company(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("申请日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setProgress_apply_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("初审公告期号")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo
									.setProgress_first_check_publish_number(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("注册公告期号")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo
									.setProgress_regsiter_publish_number(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("初审公告日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo
									.setProgress_first_check_publish_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("注册公告日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setProgress_regsiter_publish_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("注册满三年")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo
									.setProgress_regsiter_have_three_years_publish_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("注册日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setProgress_regsiter_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("截止日期")) {
							value = JsoupHtmlParser.getCleanTxt(value);
							brandPojo.setProgress_deadline_date(value);
							// System.out.println("title=" + title);
							// System.out.println("value=" + value);
						} else if (title.equals("最新动态")) {
							// 因为动态可能有多长，直接进行向后遍历后进行break直接退出for循环
							value = JsoupHtmlParser.getCleanTxt(value);
							StringBuilder sb = new StringBuilder();
							sb.append(value + "\n");
							for (int j = i + 2; j < itemList.size(); j++) {
								if (j > i + 2) {
									sb.append("\n");
								}
								value = JsoupHtmlParser.getCleanTxt(itemList
										.get(j));
								sb.append(value);
							}
							brandPojo.setNewest_message(sb.toString());
							// System.out.println("title=" + title);
							// System.out.println("value=" + sb.toString());
						}
					}
				} else {
					logger.info("itemList length<2 is on error,please check!");
				}
			}
		}
		return brandPojo;
	}

	// 抓取任意URL的内容
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
		int refresh_time = 0;
		// 如果遇到http请求错误，则重复请求http_req_error_repeat次来确定是否能得到内容
		for (int i = 0; i < SystemParas.http_req_error_repeat_number; i++) {
			try {
				String htmlSource = Crawl4HttpClient.crawlWebPage(requestPojo);
				if (htmlSource == null || htmlSource.isEmpty()) {
					continue;
				}
				// 在这里做页面内是否有内容跳转
				String refresh_location = htmlParserUtil.getRefreshLocationUrl(
						requestPojo.getUrl(), htmlSource);
				if (StringOperatorUtil.isNotBlank(refresh_location)) {
					if (refresh_time <= 3) {
						requestPojo.setUrl(refresh_location);
						logger.info("find webpage refresh location,will trace continue the jump,"
								+ refresh_location);
						i = 0;
						// 此时的url产生了变化
						resultPojo.setFromUrl(refresh_location);

						refresh_time++;
						continue;
					} else {
						logger.info("refresh location超过最大次数，将跳过该url--"
								+ pageUrl);
					}
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

	public static void main(String[] args) {
		String url = "http://g.chofn.com/guanjia/Member/tmView?&id=12669296&tmclass=33";
		String from_source = "超凡商标管家";
		BrandPojo brandPojo = BrandCrawler.crawlOnePage(url, from_source);
		System.out.println(brandPojo);
		System.out.println("done!");
	}
}
