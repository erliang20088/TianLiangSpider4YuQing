package com.tianliang.spider.manager.metasearch;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.tianliang.spider.crawler.metasearch.Crawl4Baidu;
import com.tianliang.spider.crawler.metasearch.Crawl4Sogou;
import com.tianliang.spider.crawler.metasearch.Crawl4WeiXin;
import com.tianliang.spider.crawler.metasearch.Crawl4_360;
import com.tianliang.spider.iface.rule.IResultPojo;
import com.tianliang.spider.pojos.parser.ParserResultPojo;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.zel.es.pojos.index.CrawlData4PortalSite;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.enums.SearchEngineEnum;

/**
 * 元搜索抓取引擎管理器
 * 
 * @author zel
 * 
 */
public class MetaSearchManager {
	// 日志
	public static MyLogger logger = new MyLogger(MetaSearchManager.class);
	// 搜索引擎使用计数，为了节省时间，暂时规则性只抓取任选一个搜索引擎的结果
	public static int random_max_int = 200;
	public static Random randomUtil = new Random();

	public static List<IResultPojo> processTask(CrawlTaskPojo taskPojo,
			boolean isTest, SearchEngineEnum[] searchEngineEnumList) {
		if (taskPojo == null) {
			return null;
		}
		List<IResultPojo> resultList_all = new LinkedList<IResultPojo>();

		// for (SearchEngineEnum searchEngineEnum : searchEngineEnumList) {
		// List<IResultPojo> resultList = getOneSeResult(taskPojo,
		// searchEngineEnum, isTest);
		// if (StringOperatorUtil.isNotBlankCollection(resultList)) {
		// resultList_all.addAll(resultList);
		// }
		// }

		// for (SearchEngineEnum searchEngineEnum : searchEngineEnumList) {
		// 抓取某个轮回到的搜索引擎上
		SearchEngineEnum searchEngineEnum = searchEngineEnumList[(randomUtil
				.nextInt(random_max_int) % searchEngineEnumList.length)];
		List<IResultPojo> resultList = getOneSeResult(taskPojo,
				searchEngineEnum, isTest);
		if (StringOperatorUtil.isNotBlankCollection(resultList)) {
			resultList_all.addAll(resultList);
		}
		// 抓取微信的
		/*
		 * searchEngineEnum = searchEngineEnumList[3]; resultList =
		 * getOneSeResult(taskPojo, searchEngineEnum, isTest); if
		 * (StringOperatorUtil.isNotBlankCollection(resultList)) {
		 * resultList_all.addAll(resultList); }
		 */

		// }
		return resultList_all;
	}

	public static List<IResultPojo> getOneSeResult(CrawlTaskPojo taskPojo,
			SearchEngineEnum searchEngineEnum, boolean isTest) {
		List<IResultPojo> resultList = null;
		String root_url = taskPojo.getSearchRootUrl(searchEngineEnum);
		if (SearchEngineEnum.Baidu == searchEngineEnum) {
			if (StringOperatorUtil.isBlank(root_url)) {
				logger.info("meta search task occur a NULL root_url,please check!");
			} else {
				// 暂定以下均为元搜索情况，暂不包括微博元搜索的情况
				List<CrawlData4PortalSite> crawlDataList = Crawl4Baidu
						.getAllNewsSearchResult(taskPojo, root_url, isTest);

				resultList = new LinkedList<IResultPojo>();
				if (StringOperatorUtil.isNotBlankCollection(crawlDataList)) {
					// 将抓取的数据封装，并加入result list中
					ParserResultPojo parserResultPojo = null;
					for (CrawlData4PortalSite crawlData4PortalSite : crawlDataList) {
						parserResultPojo = new ParserResultPojo();
						parserResultPojo
								.setCrawlData4PortalSite(crawlData4PortalSite);
						parserResultPojo.setRuleName("元搜索");
						parserResultPojo.setMatchRegex(true);

						parserResultPojo.setNormal(true);
						parserResultPojo.setSource_title(taskPojo
								.getSource_title());
						// 因为是元搜索，暂认为此字段无意义
						parserResultPojo.setCurrent_depth(0);
						// 此字段也无意义,但要设置，为在数据存储中判断处是哪种类型的任务得来的数据
						parserResultPojo.setOwnToCrawlTaskPojo(taskPojo);

						resultList.add(parserResultPojo);
					}
				}
			}
		} else if (SearchEngineEnum.Sogou == searchEngineEnum) {
			if (StringOperatorUtil.isBlank(root_url)) {
				logger.info("meta search task occur a NULL root_url,please check!");
			} else {
				// 暂定以下均为元搜索情况，暂不包括微博元搜索的情况
				List<CrawlData4PortalSite> crawlDataList = Crawl4Sogou
						.getAllNewsSearchResult(taskPojo, root_url, isTest);

				resultList = new LinkedList<IResultPojo>();
				if (StringOperatorUtil.isNotBlankCollection(crawlDataList)) {
					// 将抓取的数据封装，并加入result list中
					ParserResultPojo parserResultPojo = null;
					for (CrawlData4PortalSite crawlData4PortalSite : crawlDataList) {
						parserResultPojo = new ParserResultPojo();
						parserResultPojo
								.setCrawlData4PortalSite(crawlData4PortalSite);
						parserResultPojo.setRuleName("元搜索");
						parserResultPojo.setMatchRegex(true);

						parserResultPojo.setNormal(true);
						parserResultPojo.setSource_title(taskPojo
								.getSource_title());
						// 因为是元搜索，暂认为此字段无意义
						parserResultPojo.setCurrent_depth(0);
						// 此字段也无意义,但要设置，为在数据存储中判断处是哪种类型的任务得来的数据
						parserResultPojo.setOwnToCrawlTaskPojo(taskPojo);

						resultList.add(parserResultPojo);
					}
				}
			}
		} else if (SearchEngineEnum.QiHu360 == searchEngineEnum) {
			if (StringOperatorUtil.isBlank(root_url)) {
				logger.info("meta search task occur a NULL root_url,please check!");
			} else {
				// 暂定以下均为元搜索情况，暂不包括微博元搜索的情况
				List<CrawlData4PortalSite> crawlDataList = Crawl4_360
						.getAllNewsSearchResult(taskPojo, isTest);

				resultList = new LinkedList<IResultPojo>();
				if (StringOperatorUtil.isNotBlankCollection(crawlDataList)) {
					// 将抓取的数据封装，并加入result list中
					ParserResultPojo parserResultPojo = null;
					for (CrawlData4PortalSite crawlData4PortalSite : crawlDataList) {
						parserResultPojo = new ParserResultPojo();
						parserResultPojo
								.setCrawlData4PortalSite(crawlData4PortalSite);
						parserResultPojo.setRuleName("元搜索");
						parserResultPojo.setMatchRegex(true);

						parserResultPojo.setNormal(true);
						parserResultPojo.setSource_title(taskPojo
								.getSource_title());
						// 因为是元搜索，暂认为此字段无意义
						parserResultPojo.setCurrent_depth(0);
						// 此字段也无意义,但要设置，为在数据存储中判断处是哪种类型的任务得来的数据
						parserResultPojo.setOwnToCrawlTaskPojo(taskPojo);
						resultList.add(parserResultPojo);
					}
				}
			}
		} else if (SearchEngineEnum.WeiXin == searchEngineEnum) {// 微信抓取
			if (StringOperatorUtil.isBlank(root_url)) {
				logger.info("meta search task occur a NULL root_url,please check!");
			} else {
				// 暂定以下均为元搜索情况，暂不包括微博元搜索的情况
				List<CrawlData4PortalSite> crawlDataList = Crawl4WeiXin
						.getAllNewsSearchResult(taskPojo, root_url, isTest);

				resultList = new LinkedList<IResultPojo>();
				if (StringOperatorUtil.isNotBlankCollection(crawlDataList)) {
					// 将抓取的数据封装，并加入result list中
					ParserResultPojo parserResultPojo = null;
					for (CrawlData4PortalSite crawlData4PortalSite : crawlDataList) {
						parserResultPojo = new ParserResultPojo();
						parserResultPojo
								.setCrawlData4PortalSite(crawlData4PortalSite);

						parserResultPojo.setRuleName("元搜索-微信");
						parserResultPojo.setMatchRegex(true);

						// 专为微信设置media_type
						crawlData4PortalSite.setMedia_type(5);

						parserResultPojo.setNormal(true);
						parserResultPojo.setSource_title(taskPojo
								.getSource_title());
						// 因为是元搜索，暂认为此字段无意义
						parserResultPojo.setCurrent_depth(0);
						// 此字段也无意义,但要设置，为在数据存储中判断处是哪种类型的任务得来的数据
						parserResultPojo.setOwnToCrawlTaskPojo(taskPojo);

						resultList.add(parserResultPojo);
					}
				}
			}
		}
		return resultList;
	}

}
