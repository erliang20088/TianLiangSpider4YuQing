package com.tianliang.spider.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tianliang.spider.crawler.httpclient.Crawl4HttpClient;
import com.tianliang.spider.pojos.HttpRequestPojo;
import com.vaolan.extkey.utils.UrlOperatorUtil;
import com.vaolan.parser.JsoupHtmlParser;
import com.vaolan.status.DataFormatStatus;
import com.zel.es.utils.StringOperatorUtil;

public class HtmlParserUtil {
	public static final String EMPTY_STRING = "";
	public final static String all_text_regex = "[\\s\\S]*?";
	private String title_regex = "<title>" + all_text_regex + "</title>";

	private Pattern title_pattern = Pattern.compile(title_regex);
	private Matcher matcher = null;
	private String temp = null;

	public static Pattern refresh_location_pattern = Pattern
			.compile("url=(.*)[\\s]*",Pattern.CASE_INSENSITIVE);

	public String getRefreshLocationUrl(String fromUrl, String htmlSource) {
//		String fromUrl = "http://www.baidu.com/";
//		String htmlSource = IOUtil.readDirOrFile("d:/test.txt", "utf-8");
		List<String> selList = new LinkedList<String>();
		selList.add("meta[http-equiv=refresh]");
		
		List<String> resultList = JsoupHtmlParser.getNodeContentBySelector(
				htmlSource, selList, DataFormatStatus.TagAllContent, false);
		String jumpUrl = null;
		if (StringOperatorUtil.isNotBlankCollection(resultList)) {
			String resultItem = resultList.get(0);
			String content = JsoupHtmlParser.getAttributeValue(resultItem,
					"content");
			if (StringOperatorUtil.isNotBlank(content)) {
				content = content.trim();
				Matcher matcher = refresh_location_pattern.matcher(content);
				if (matcher.find()) {
					// System.out.println(matcher.group(1));
					jumpUrl = matcher.group(1).trim();
				}
			}
			if (StringOperatorUtil.isNotBlank(jumpUrl)) {
				if (jumpUrl.startsWith("http://")) {
					// 说明是绝对地址，故不做处理
				} else if (jumpUrl.startsWith("/")) {
					// 说明是绝对路径重定向
					jumpUrl = "http://" + UrlOperatorUtil.getHost(fromUrl)
							+ jumpUrl;
				} else {
					// 说明是相对路径
					int last_pos = fromUrl.lastIndexOf("/");
					String relative_path = fromUrl.substring(0, last_pos + 1);
					jumpUrl = relative_path + jumpUrl;
				}
			}
		}
		return jumpUrl;
	}

	public static List<String> titleSelector = null;
	static {
		titleSelector = new LinkedList<String>();
		titleSelector.add("title");
	}

	public String getTitleByLine(String htmlSource) {
		List<String> list = JsoupHtmlParser.getNodeContentBySelector(
				htmlSource, titleSelector, false);
		if (StringOperatorUtil.isNotBlankCollection(list)) {
			return list.get(0);
		}
		return null;
	}

	public String getTitleByLine_bak(String line) {
		matcher = this.title_pattern.matcher(line);
		if (matcher.find()) {
			temp = matcher.group();
			return temp.replace("<title>", EMPTY_STRING).replace("</title>",
					EMPTY_STRING);
		}
		return null;
	}

	public String getTitleByUrl(String url) {
		HttpRequestPojo requestPojo = new HttpRequestPojo(url);
		String source = Crawl4HttpClient.crawlWebPage(requestPojo);
		return this.getTitleByLine(source);
	}

	public static void main(String[] args) {
		// HtmlParserUtil htmlParserUtil = new HtmlParserUtil();
		// String title = htmlParserUtil.getTitleByLine("<title>123</title>");
		// System.out.println("title---" + title);
	}
}
