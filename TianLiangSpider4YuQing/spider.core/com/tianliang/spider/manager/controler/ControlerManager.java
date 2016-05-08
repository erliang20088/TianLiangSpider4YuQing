package com.tianliang.spider.manager.controler;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.tianliang.spider.crawler.httpclient.Crawl4HttpClient;
import com.tianliang.spider.crawler.phantomjs.Crawl4Phantomjs;
import com.tianliang.spider.crawler.threads.DaemonThread4SaveCacheData;
import com.tianliang.spider.crawler.threads.DaemonThread4UpdateRuleKey;
import com.tianliang.spider.iface.rule.IResultPojo;
import com.tianliang.spider.impl.rule.UrlExtractorImpl;
import com.tianliang.spider.manager.metasearch.MetaSearchManager;
import com.tianliang.spider.manager.metasearch.MetaSearchManagerFor360Search;
import com.tianliang.spider.manager.rule.ExtractorContentManager4SimpleCrawler;
import com.tianliang.spider.manager.task.TaskDispacherManager;
import com.tianliang.spider.manager.task.TaskQueueManager;
import com.tianliang.spider.manager.task.TaskQueueManager.TaskQueueType;
import com.tianliang.spider.manager.threads.ThreadManager4DataSave;
import com.tianliang.spider.pojos.CrawlResultPojo;
import com.tianliang.spider.pojos.HttpRequestPojo;
import com.tianliang.spider.pojos.ProxyPojo;
import com.tianliang.spider.pojos.enumeration.CrawlEngineEnum;
import com.tianliang.spider.pojos.parser.MatchResultKeyValue;
import com.tianliang.spider.pojos.parser.ParserResultPojo;
import com.tianliang.spider.pojos.rule.ExtContentRuleConfigManager;
import com.tianliang.spider.pojos.rule.UrlFilterPojo4OneHostToMultiPattern.MatchContentPojo;
import com.tianliang.spider.pojos.rule.UrlFilterPojo4OneHostToMultiPattern.MatchCrawlPojo;
import com.tianliang.spider.utils.DateUtil;
import com.tianliang.spider.utils.FileOperatorUtil;
import com.tianliang.spider.utils.HtmlParserUtil;
import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.ObjectAndByteArrayConvertUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.utils.JedisOperatorUtil;
import com.tianliang.utils.TaskTxt2ObjectUtil;
import com.vaolan.extkey.utils.UrlOperatorUtil;
import com.zel.es.manager.nlp.TianLiangEmotionCalcManager;
import com.zel.es.manager.nlp.TianLiangKeyWordExtractorManager;
import com.zel.es.manager.nlp.TianLiangSummaryExtractorManager;
import com.zel.es.manager.nlp.TianLiangThemeExtractorManager;
import com.zel.es.pojos.index.CrawlData4PortalSite;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.RetStatus;
import com.zel.spider.pojos.enums.SearchEngineEnum;

/**
 * 系统启动官理器
 * 
 * @author zel
 * 
 */
public class ControlerManager {
	// 日志
	public static MyLogger logger = new MyLogger(ControlerManager.class);

	// 日期处理工具类
	public static DateUtil dateUtil = new DateUtil();
	public static HtmlParserUtil htmlParserUtil = new HtmlParserUtil();

	// private static JedisOperatorUtil jedisOperatorUtil = new
	// JedisOperatorUtil(
	// SystemParas.redis_host, SystemParas.redis_port,
	// SystemParas.redis_password);
	private static JedisOperatorUtil jedisOperatorUtil = null;
	public static ExtractorContentManager4SimpleCrawler extractorContentManager4SimpleCrawler = null;
	static {
		if (!SystemParas.application_is_test) {
			jedisOperatorUtil = new JedisOperatorUtil(SystemParas.redis_host,
					SystemParas.redis_port, SystemParas.redis_password);
		}
		if (SystemParas.application_is_test) {
			ExtContentRuleConfigManager extContentRuleConfigManager = new ExtContentRuleConfigManager(
					SystemParas.ext_content_rule_config_fs,
					SystemParas.ext_content_rule_config_root_dir);
			String new_rule_value = extContentRuleConfigManager.getRuleString();
			extractorContentManager4SimpleCrawler = new ExtractorContentManager4SimpleCrawler(
					new_rule_value, false);
		} else if (SystemParas.node_is_master) {
			// 说明是主节点
			// 将拿到的最新的规则串放到redis一份
			// 新的获取规则已改，由init中的独立线程去维护规则库，master和slave均是直接从redis中取值更新自己的manager
			extractorContentManager4SimpleCrawler = new ExtractorContentManager4SimpleCrawler(
					jedisOperatorUtil.getObj(StaticValue.ext_content_rule_key),
					false);
		} else {// 说明是子节点
			extractorContentManager4SimpleCrawler = new ExtractorContentManager4SimpleCrawler(
					jedisOperatorUtil.getObj(StaticValue.ext_content_rule_key),
					false);
		}
	}

	// 规则库守护线程
	public void init() {
		// 规则库守护线程开启
		DaemonThread4UpdateRuleKey daemonThread4UpdateRuleKey = new DaemonThread4UpdateRuleKey(
				1, true);
		Thread update_rule_key_thread = new Thread(daemonThread4UpdateRuleKey);
		update_rule_key_thread.start();

		// 开启缓存数据保存线程
		DaemonThread4SaveCacheData daemonThread4SaveCacheData = new DaemonThread4SaveCacheData(
				true);
		Thread cache_data_thread = new Thread(daemonThread4SaveCacheData);
		cache_data_thread.start();
		
		// 为保证规则库至少一次写入到redis cache中，等待几秒
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("system init occur error,will exit!");
			System.exit(-1);
		}
	}

	private boolean runningFlag = true;

	private void loadSeedDir(String seedDir) {
		List<String> taskFilePathList = FileOperatorUtil.getAllFilePathList(
				seedDir, null);
		// System.out.println("taskFilePathList----"+taskFilePathList);
		// for(String str:taskFilePathList){
		// System.out.println(str);
		// }
		if (StringOperatorUtil.isNotBlankCollection(taskFilePathList)) {
			for (String filePath : taskFilePathList) {
				// 每处理一个文件，均设置一下该值
				runningFlag = true;
				addFileTaskToQueue(filePath);
				// 认为该文件处理完成，将直接从硬盘删除掉
				FileOperatorUtil.removeFile(filePath);
				logger.info(filePath + ",数据文件已加载到内存，将删除之!");
			}
			logger.info("once load seed dir,the seeds task have pushed all to the queue");
		}
	}

	public void addFileTaskToQueue(String filePath) {
		/**
		 * 首先对输入的关键词库串或是url串进行去重与计数,即程序的输入就是一个不去重的keyword或是url的文本文件，一行一一个即可
		 */
		// FileLineStatisticUtil.outputFileLineStatisticResult(filePath,
		// filePath);

		/**
		 * 先判定几个要填充的redis中key的名称，就不用在后边用if...else去每次去判定了
		 */
		String todo_key_name = null;
		String finish_key_name = null;

		// 拿到队列在redis所对应的key name
		todo_key_name = StaticValue.redis_task_todo_list_key_name;
		finish_key_name = StaticValue.redis_task_finished_key_name;

		long begin_line_number = 0;
		List<CrawlTaskPojo> taskPojoList = null;
		List<String> txtContent = null;
		long task_total = 0;
		long task_finish_count = 0;

		int url_list_size = 0;
		boolean is_inject_new_url = false;

		// 取得已完成的任务的数量
		String finish_value = jedisOperatorUtil.getObj(finish_key_name);
		if (StringOperatorUtil.isNotBlank(finish_value)) {
			task_finish_count = Integer.parseInt(finish_value);
		}
		// 取得现在池子中还有多少todo url list size
		long redis_list_size_current = jedisOperatorUtil.llen(todo_key_name);

		// 已完成行业+todoListSize
		task_total = task_finish_count + redis_list_size_current;

		// 初始化时，总数量即为现在redis中已抓取的数量+todo list size
		begin_line_number = 0;// 只要是开始读取，都从0开始

		// 循环遍历
		while (runningFlag) {
			while (task_total < SystemParas.node_seeds_max_size) {
				if (redis_list_size_current < SystemParas.node_redis_size_threshold) {
					logger.info("redis list size is little to threshold，will inject a batch of "
							+ "tasks!");
					// 读取出指定的文件中的内容
					txtContent = IOUtil.getLineArrayFromFile(filePath,
							StaticValue.default_encoding, begin_line_number,
							Math.min(SystemParas.node_seeds_inject_batch_size,
									SystemParas.node_seeds_max_size));

					taskPojoList = TaskTxt2ObjectUtil
							.convertTxt2Object(txtContent);

					// 说明已经读取完成，该文件的任务已添加完成
					if (StringOperatorUtil.isBlankCollection(taskPojoList)) {
						runningFlag = false;
						logger.info("the file task list have added to the redis quene all!");
						break;
					}

					// System.out.println(txtContent);

					try {
						if (StringOperatorUtil
								.isNotBlankCollection(taskPojoList)) {
							for (CrawlTaskPojo taskPojo : taskPojoList) {
								// 添加到正常的待访问队列
								TaskQueueManager.addTaskToDoQueue(
										todo_key_name, taskPojo, null, true);
								// 暂定只有从这个途径过添加的任务都是原始任务,根据条件加入循环队列
								if (SystemParas.task_circle_enable
										&& taskPojo.isEnableToCircle()) {
									TaskQueueManager.addTask(taskPojo,
											TaskQueueType.Circle_Visit);
								}
							}
							logger.info("从种子文件中批量添加任务到种子队列中!");
						} else {
							logger.info("种子文件中没有发现新增的任务，不做添加任务操作!");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					url_list_size = taskPojoList.size();
					begin_line_number += url_list_size;
					redis_list_size_current += url_list_size;
					// url总数计算
					task_total = task_total + url_list_size;
					is_inject_new_url = true;

					// 说明文件已经提取结束
					if (url_list_size == 0) {
						logger.info("one file task list have added to the redis quene all,will remove it!");
						runningFlag = false;
					}
				} else {
					break;
				}
			}

			// 写一些守护信息
			redis_list_size_current = jedisOperatorUtil.llen(todo_key_name);

			task_finish_count = task_total - redis_list_size_current;

			// 将已完成任务写入redis中
			jedisOperatorUtil.putObj(finish_key_name, "" + task_finish_count);

			// 输出些守护信息，在此简单处理
			logger.info("current to do task size in redis---"
					+ redis_list_size_current);

			try {
				// 说明文件已经提取结束
				if (is_inject_new_url && url_list_size == 0) {
					runningFlag = false;
				}
				Thread.sleep(SystemParas.node_inject_urls_sleep_time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			is_inject_new_url = false;
		}
	}

	public RetStatus startServer() {
		// 首先进行初始相关
		init();

		// 开始守护线程保存数据
		ThreadManager4DataSave
				.startDaemon(SystemParas.ext_content_save_threads_numbers);

		if (SystemParas.ext_content_load_seeds_is_circle) {
			while (true) {
				loadSeedDir(SystemParas.spider_seeds_root_path);
				// load完一次种子目录，休息指定时间后再次扫描加载
				try {
					Thread.sleep(SystemParas.ext_content_load_seeds_sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			loadSeedDir(SystemParas.spider_seeds_root_path);
		}

		return null;
	}

	// 抓取任意URL的内容或截图
	public static CrawlResultPojo crawlHtmlSourceByRandomUrl4HttpClient(
			String pageUrl, ProxyPojo proxyPojo) {
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
				// System.out.println("抓取到数据了!");

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

	public static ParserResultPojo parserHtmlSource(CrawlTaskPojo taskPojo,
			String htmlSource, boolean isTest) {
		String url = taskPojo.getValue();
		String host = UrlOperatorUtil.getHost(url);
		MatchContentPojo matchContentPojo = extractorContentManager4SimpleCrawler
				.getMatchContentList(url, host, htmlSource);
		ParserResultPojo parserResultPojo = new ParserResultPojo();
		if (matchContentPojo != null && matchContentPojo.isNormal()) {
			// parserResultPojo.setNormal(true);
			parserResultPojo.setRuleName(matchContentPojo.getRule_name());
			parserResultPojo.setFromUrl(url);
			// 代表完全匹配上规则库了
			parserResultPojo.setMatchRegex(true);
			// 要解析出来的数据封装对象
			CrawlData4PortalSite crawlData4PortalSite = new CrawlData4PortalSite();
			crawlData4PortalSite.setUrl(url);
			String fieldKey = null;
			String fieldValue = null;
			for (MatchResultKeyValue matchKeyValue : matchContentPojo
					.getMatchContentList()) {
				fieldKey = matchKeyValue.getFieldKey();
				fieldValue = matchKeyValue.getValue();
				// System.out.println(matchKeyValue.getFieldKey() + "\t"
				// + matchKeyValue.getValue());
				if (fieldKey.equals("标题")) {
					crawlData4PortalSite.setTitle(fieldValue);
				} else if (fieldKey.equals("作者")) {
					crawlData4PortalSite.setAuthor(fieldValue);
				} else if (fieldKey.equals("正文")) {
					if (crawlData4PortalSite.getBody() != null) {
						crawlData4PortalSite.setBody(crawlData4PortalSite
								.getBody()
								+ StaticValue.separator_next_line
								+ fieldValue);
					} else {
						crawlData4PortalSite.setBody(fieldValue);
					}
				} else if (fieldKey.equals("评论数")) {
					try {
						crawlData4PortalSite
								.setDiscuss_number(StringOperatorUtil
										.getNumber(fieldValue));
					} catch (Exception e) {
						crawlData4PortalSite.setDiscuss_number(0);
					}
				} else if (fieldKey.equals("转发数")) {
					try {
						crawlData4PortalSite
								.setTransmit_number(StringOperatorUtil
										.getNumber(fieldValue));
					} catch (Exception e) {
						crawlData4PortalSite.setTransmit_number(0);
					}
				} else if (fieldKey.equals("发布时间")) {
					crawlData4PortalSite.setPublish_time_string(fieldValue);
					// 在这里做时间的格式化
					try {
						Date date = dateUtil
								.getDateByNoneStructure4News(crawlData4PortalSite
										.getPublish_time_string());
						if (date != null) {
							crawlData4PortalSite.setPublish_time_long(date
									.getTime());
						} else {
							// 遇到不转化，也暂定为0
							// crawlData4PortalSite.setPublish_time_long(DateUtil
							// .getLongByDate());
							crawlData4PortalSite.setPublish_time_long(0);
						}
					} catch (Exception e) {
						// 如果发现date无法解析，则将日期暂定上为0
						crawlData4PortalSite.setPublish_time_long(0);
						// crawlData4PortalSite.setPublish_time_long(DateUtil
						// .getLongByDate());
					}
				}
			}

			if (!isTest) {
				// 对需要nlp提取的做如下封装
				// String body =
				// TianLiangBodyExtractorManager.getBody(htmlSource);
				String body = crawlData4PortalSite.getBody();
				if (body != null && (!body.isEmpty())) {
					// 提取正文,暂时不用智能提取了!
					// crawlData4PortalSite.setBody(body);
					// 提取关键词
					crawlData4PortalSite
							.setKeyword(TianLiangKeyWordExtractorManager
									.getKeywordString(
											crawlData4PortalSite.getTitle(),
											body));
					// 提取主题词
					crawlData4PortalSite
							.setTheme_word(TianLiangThemeExtractorManager
									.getThemeKeywordString(body));
					// 提取摘要
					crawlData4PortalSite
							.setSummary(TianLiangSummaryExtractorManager
									.getSummaryString(300,
											crawlData4PortalSite.getTitle(),
											body));
					// 提取情感极性
					crawlData4PortalSite
							.setEmotion_polar(TianLiangEmotionCalcManager
									.getSentencePolarString(body));
				}

				// 加入一些非处理字段
				crawlData4PortalSite.setInsert_time(DateUtil.getLongByDate());
				// 加入source_title字段
				crawlData4PortalSite
						.setSource_title(taskPojo.getSource_title());
				crawlData4PortalSite.setMedia_type(taskPojo.getMedia_type());
			}
			// 设置parserResultPojo的信息
			parserResultPojo.setCrawlData4PortalSite(crawlData4PortalSite);
		}
		// 提取新产生的url
		if (taskPojo.isContinue()) {
			int temp_topN = taskPojo.getTopN();
			// 首先判断是否能否去抓取新任务，从而限制slave节点产生新的二级任务，防止内存溢出
			if (!TaskDispacherManager.isAbleToTakeNewTask()) {
				temp_topN = taskPojo.getTopN() * 1 / 10;
				logger.info("find task_todo_level_2 > "
						+ SystemParas.task_todo_level_2_max_items_in_redis
						+ " items,will take page topN=" + temp_topN);
			}
			Set<String> urlSet = UrlExtractorImpl.getNewUrls(taskPojo
					.getValue(), host, htmlSource, temp_topN,
					ControlerManager.extractorContentManager4SimpleCrawler
							.getAll_wildcard_host_set());
			parserResultPojo.setNewUrlSet(urlSet);
		}

		// System.out.println(htmlSource);
		// Set<String> urlSet = UrlExtractorImpl.getNewUrls(taskPojo.getValue(),
		// host, htmlSource, taskPojo.getTopN());
		// parserResultPojo.setNewUrlSet(urlSet);
		// System.out.println(urlSet);

		parserResultPojo.setNormal(true);
		parserResultPojo.setSource_title(taskPojo.getSource_title());
		parserResultPojo.setCurrent_depth(taskPojo.getCurrent_depth() + 1);
		parserResultPojo.setOwnToCrawlTaskPojo(taskPojo);
		return parserResultPojo;
	}

	// 处理一个网页的开始
	public static IResultPojo processTask(CrawlTaskPojo taskPojo, boolean isTest) {
		String url = taskPojo.getValue();

		IResultPojo resultPojo = null;
		ParserResultPojo parser_resultPojo = null;
		MatchCrawlPojo matchCrawlPojo = extractorContentManager4SimpleCrawler
				.getMatchCrawlPojo(url);
		if (matchCrawlPojo != null && matchCrawlPojo.isMatch()) {
			// 在这里将格式化后的format_url值更新url
			url = matchCrawlPojo.getFormat_url();
			if (matchCrawlPojo.getCrawlEngineEnum() == CrawlEngineEnum.Phantomjs) {
				resultPojo = Crawl4Phantomjs.crawlHtmlSourceByRandomUrl(url);
			} else {
				resultPojo = crawlHtmlSourceByRandomUrl4HttpClient(url, null);
				if (resultPojo != null
						&& StringOperatorUtil.isNotBlank(resultPojo
								.getFromUrl())) {
					url = resultPojo.getFromUrl();
					taskPojo.setValue(url);
				}

				// System.out.println("test html source="+resultPojo.getHtmlSource());
			}

			// test
			// IOUtil.writeFile("d:/test.txt", resultPojo.getHtmlSource(),
			// StaticValue.default_encoding);
			// System.out.println(resultPojo.getHtmlSource());

			if (resultPojo != null && resultPojo.isNormal()) {
				parser_resultPojo = parserHtmlSource(taskPojo,
						resultPojo.getHtmlSource(), isTest);
				if (parser_resultPojo == null) {
					logger.info(url + ",解析出现问题，请检查!");
					System.out.println(parser_resultPojo);
					return parser_resultPojo;
				} else if (parser_resultPojo.isNormal()) {
					// 说明是处理过程没出现异常!
					if (parser_resultPojo.isMatchRegex()) {
						// 说明规则库匹配上了
						parser_resultPojo.setCrawlEngine(matchCrawlPojo
								.getCrawlEngineEnum());
						System.out.println(parser_resultPojo.toString());
						System.out.println("Match Rule Sucess!");

						// 在这里将有效模板的统计次数上报到redis缓存中
						String field = parser_resultPojo.getRuleName() + "#"
								+ parser_resultPojo.getSource_title();

						if (!SystemParas.application_is_test) {
							boolean is_exists = jedisOperatorUtil
									.HContainsFields(
											StaticValue.statistic_key_template,
											field);
							if (is_exists) {
								String value = jedisOperatorUtil.HGet(
										StaticValue.statistic_key_template,
										field);
								if (StringOperatorUtil.isNotBlank(value)) {
									jedisOperatorUtil.HSet(
											StaticValue.statistic_key_template,
											field,
											(Integer.parseInt(value) + 1) + "");
									// System.out.println("value---"+value);
									logger.info("update template statistic successful");
								} else {
									logger.info("模板有效性统计出现value非数值异常，请检查!");
								}
							} else {
								jedisOperatorUtil.HSet(
										StaticValue.statistic_key_template,
										field, "1");
								logger.info("update template statistic successful");
							}
						}
					} else {
						// 说明规则库没有匹配上
						System.out.println("Match Rule Fail!");
					}
				}
			} else {
				logger.info(url + ",下载失败或处理出现异常,请检查!");
			}
		} else {
			System.out.println("任务url中提取的host不在规则库中，请检查!");
		}
		return parser_resultPojo;
	}

	public static SearchEngineEnum[] searchEngineEnumArray = {
			SearchEngineEnum.Baidu, SearchEngineEnum.Sogou,
			SearchEngineEnum.QiHu360, SearchEngineEnum.WeiXin };
	// public static SearchEngineEnum[] searchEngineEnumArray = {
	// SearchEngineEnum.Baidu, SearchEngineEnum.Sogou,
	// SearchEngineEnum.WeiXin };
	// public static SearchEngineEnum[] searchEngineEnumArray = {
	// SearchEngineEnum.Baidu };

	public static SearchEngineEnum[] searchEngineEnumArray_360NewsSearch = { SearchEngineEnum.QiHu360 };

	// 从redis端接受任务，暂定为以一个url为单位进行获取
	public void startClient() {
		// 在启动之处启动守护线程,来更新跟踪规则状态是否发生更新,如有更新，则同步更新
		ThreadManager4DataSave.startDaemon4UpdateRuleKey4Client(1);
		int count_flag = 0;
		while (runningFlag) {
			// 取得一个任务，在方法内调度
			CrawlTaskPojo taskPojo = TaskDispacherManager.getCrawlTask();
			if (taskPojo != null) {
				// 在这个位置保证下唯一
				IResultPojo resultPojo = null;
				count_flag++;
				if (count_flag > 10 * 10000) {
					count_flag = 0;
				}
				/**
				 * 首先判断任务的类型,看看是元搜索，还是网页搜索，或其它
				 */
				if (taskPojo.getCrawlEngine() == com.zel.spider.pojos.enums.CrawlEngineEnum.MetaSearch_NEWSPage) {
					// if (count_flag % 5 != 0) {
					// logger.info("目前每5个关键词执行一次百度采集，该次将不执行，即跳过该次百度采集!");
					// continue;
					// }
					List<IResultPojo> resultPojoList = MetaSearchManager
							.processTask(taskPojo, false, searchEngineEnumArray);
					if (StringOperatorUtil.isNotBlankCollection(resultPojoList)) {
						for (IResultPojo resultItem : resultPojoList)
							saveResult(taskPojo, resultItem);
					}
				} else if (taskPojo.getCrawlEngine() == com.zel.spider.pojos.enums.CrawlEngineEnum.MetaSearch_NEWSPage_360Search) {
					// 此处来加入专门的360新闻搜索的代码
					List<IResultPojo> resultPojoList = MetaSearchManagerFor360Search
							.processTask(taskPojo, false,
									searchEngineEnumArray_360NewsSearch);
					if (StringOperatorUtil.isNotBlankCollection(resultPojoList)) {
						for (IResultPojo resultItem : resultPojoList)
							saveResult(taskPojo, resultItem);
					}
				} else {
					// 保证更新规则库跟使用不冲突!
					synchronized (ControlerManager.class) {
						resultPojo = processTask(taskPojo, false);
					}
					// 保存抓取完成的一个结果
					saveResult(taskPojo, resultPojo);
				}
				try {
					// Thread.sleep(SystemParas.node_client_grab_success_sleep_interval_time);
				} catch (Exception e) {
					logger.info("client sleep is interrupted");
				}
			} else {
				try {
					logger.info("crawl client---get task "
							+ " from redis is null,will sleep a period time!");
					Thread.sleep(SystemParas.node_inject_urls_sleep_time);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("crawl client run finished,this is not right,please check!");
	}

	// 处理一个保存任务
	private static void saveResult(CrawlTaskPojo taskPojo,
			IResultPojo resultPojo) {
		if (resultPojo != null && resultPojo.isNormal) {
			// 对返回的
			try {
				jedisOperatorUtil.lpush(
						StaticValue.ext_content_to_save_list_key
								.getBytes(StaticValue.default_encoding),
						ObjectAndByteArrayConvertUtil
								.ObjectToByteArray(resultPojo));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 说明该url遇到错误,直接加入到redis对列中
			resultPojo = new ParserResultPojo();
			System.out.println("error url pojo---" + taskPojo);
			resultPojo.setNormal(false);

			MatchCrawlPojo matchCrawlPojo = extractorContentManager4SimpleCrawler
					.getMatchCrawlPojo(resultPojo.getFromUrl());
			if (matchCrawlPojo != null) {
				resultPojo.setCrawlEngine(matchCrawlPojo.getCrawlEngineEnum());
			} else {
				resultPojo.setCrawlEngine(null);
			}

			// 当fromUrl为null说明是元搜索，将其值赋于fromUrl属性
			if (resultPojo.getFromUrl() == null) {
				resultPojo.setFromUrl(taskPojo.getValue());
			}
			resultPojo.setDesc("procee_error");
			try {
				jedisOperatorUtil.lpush(StaticValue.ext_content_error_list_key
						.getBytes(StaticValue.default_encoding),
						ObjectAndByteArrayConvertUtil
								.ObjectToByteArray(resultPojo));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 暂用该类作为测试类
	public static void main(String[] args) {
		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		// taskPojo.setValue("http://news.sina.com.cn/c/2015-03-01/150031554777.shtml");
		// taskPojo.setValue("http://mil.news.sina.com.cn/china/2015-12-27/doc-ifxmxxsr3810578.shtml");
		taskPojo.setValue("http://data.auto.sina.com.cn/2203");

		// taskPojo.setSource_title("新浪新闻");
		// taskPojo.setValue("http://news.qq.com/a/20150303/049418.htm");
		// taskPojo.setSource_title("腾讯新闻");
		// taskPojo.setValue("http://news.sina.com.cn/w/2012-04-13/110924267172.shtml");
		// taskPojo.setSource_title("新闻中心——国际");
		// taskPojo.setValue(args[0]);
		taskPojo.setSource_title("test");
		taskPojo.setDepth(3);
		taskPojo.setTopN(20);

		IResultPojo resultPojo = processTask(taskPojo, true);
		System.out.println(resultPojo.getNewUrlSet().toString());

		System.out.println("done!");

	}

}
