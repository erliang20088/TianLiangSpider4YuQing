package com.tianliang.spider.crawler.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.tianliang.spider.manager.bloom.BloomFilterManager;
import com.tianliang.spider.manager.task.TaskQueueManager;
import com.tianliang.spider.manager.task.TaskQueueManager.TaskQueueType;
import com.tianliang.spider.pojos.enumeration.CrawlEngineEnum;
import com.tianliang.spider.pojos.parser.ParserResultPojo;
import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.ObjectAndByteArrayConvertUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.utils.JedisOperatorUtil;
import com.zel.es.manager.ws.client.index.ESIndexServiceManager;
import com.zel.es.pojos.index.CrawlData4PortalSite;
import com.zel.es.pojos.statics.StaticValue4SearchCondition;
import com.zel.spider.pojos.CrawlTaskPojo;

/**
 * 守护线程，负责守护工作
 * 
 * @author zel
 * 
 */
public class DaemonThread4SaveCrawlData implements Runnable {
	public static MyLogger logger = new MyLogger(
			DaemonThread4SaveCrawlData.class);

	// public String retNormalValue;
	public ParserResultPojo resultPojo_normal = null;
	// public String retErrorValue;
	public ParserResultPojo resultPojo_error = null;
	private int thread_id;
	private boolean runnable_able = false;
	private JedisOperatorUtil jedisOperatorUtil = null;

	public DaemonThread4SaveCrawlData(int thread_id, boolean runnable_able) {
		this.thread_id = thread_id;
		this.runnable_able = runnable_able;
		this.jedisOperatorUtil = new JedisOperatorUtil(SystemParas.redis_host,
				SystemParas.redis_port, SystemParas.redis_password);
	}

	public static String msg_fail_meta_search_repeat = "meta search data find repeat,abandon send index to server!";
	public static String msg_success_meta_search = "index metadata search data success~";

	public static String msg_fail_web_page_repeat = "web page index find repeat,will abandon!";
	public static String msg_success_web_page = "web page index portal web data success~";

	@Override
	public void run() {
		// 取得保存数据时候的根目录
		String final_normal_file = SystemParas.spider_data_dir + "/normal.txt";
		String final_error_file = SystemParas.spider_data_dir + "/error.txt";

		boolean is_occur_save = false;

		while (this.runnable_able) {
			is_occur_save = false;
			byte[] byteArray = null;
			// 从redis中取得要保存的数据，没有取得，则sleep一段时间
			try {
				byteArray = jedisOperatorUtil
						.rpop(StaticValue.ext_content_to_save_list_key
								.getBytes(StaticValue.default_encoding));
				resultPojo_normal = null;
				if (byteArray != null) {
					resultPojo_normal = (ParserResultPojo) ObjectAndByteArrayConvertUtil
							.ByteArrayToObject(byteArray);
					/**
					 * 解析返回过来的结果集
					 */
					if (resultPojo_normal != null) {
						// 首先将抓回来二级url set加入待采集队列
						addNewUrlSetToCrawl(resultPojo_normal);
						
						// 首先将抓回来的数据索引
						CrawlData4PortalSite crawlData4PortalSite = resultPojo_normal
								.getCrawlData4PortalSite();
						if (crawlData4PortalSite != null) {
							// 如果是元搜索任务，则不进行如下的增量url的处理
							com.zel.spider.pojos.enums.CrawlEngineEnum crawlEngineEnum = resultPojo_normal
									.getOwnToCrawlTaskPojo().getCrawlEngine();
							if (com.zel.spider.pojos.enums.CrawlEngineEnum.MetaSearch == crawlEngineEnum
									|| com.zel.spider.pojos.enums.CrawlEngineEnum.MetaSearch_NEWSPage == crawlEngineEnum
									|| com.zel.spider.pojos.enums.CrawlEngineEnum.MetaSearch_NEWSPage_360Search == crawlEngineEnum) {
								// 做针对from_url+title的去重
								// String meta_search_uniq =
								// crawlData4PortalSite
								// .getUrl()
								// + crawlData4PortalSite.getTitle();

								// 现更改去重字段为url，不加其它任何字段，即能去重，又能防止出现重复数据后的情感正负不一致的情况
								// String meta_search_uniq =
								// crawlData4PortalSite
								// .getUrl();
								// 更新uniq key,将url类的key去掉最后的"/"，防止A与"A/"类的重复
								String meta_search_uniq = crawlData4PortalSite
										.getUrlFormat();

								// 索引该条数据
								if (StringOperatorUtil
										.isNotBlank(meta_search_uniq)) {
									indexOneRecord(crawlData4PortalSite,
											meta_search_uniq,
											msg_success_meta_search,
											msg_fail_meta_search_repeat);
								} else {
									logger.info("find a metasearch meta_search_uniq为blank,"
											+ crawlData4PortalSite.toString()
											+ ",请检查!");
								}
							} else {
								indexOneRecord(crawlData4PortalSite,
										crawlData4PortalSite.getUrl(),
										msg_success_web_page,
										msg_fail_web_page_repeat);
							}
						}

						// 这一步暂定为测试与调试，最后会被注掉
						if (resultPojo_normal.isNormal) {
							IOUtil.writeFile(final_normal_file,
									resultPojo_normal.toString() + "\n", true,
									StaticValue.default_encoding);
							is_occur_save = true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 对抓取错误的url进行处理
			try {
				byteArray = jedisOperatorUtil
						.rpop(StaticValue.ext_content_error_list_key
								.getBytes(StaticValue.default_encoding));
				resultPojo_error = null;
				if (byteArray != null) {
					resultPojo_error = (ParserResultPojo) ObjectAndByteArrayConvertUtil
							.ByteArrayToObject(byteArray);

					if (resultPojo_error != null) {
						IOUtil.writeFile(final_error_file,
								resultPojo_error.toErrorString() + "\n", true,
								StaticValue.default_encoding);
						is_occur_save = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!is_occur_save) {
				try {
					logger.info("没有要存储的数据，存储线程将休息一下");
					Thread.sleep(SystemParas.ext_content_save_thread_sleep_time);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 将待抓的url加入待抓取集合
	public static void addNewUrlSetToCrawl(ParserResultPojo resultPojo_normal) {
		// 将待抓的url加入待抓取集合
		Set<String> urlSet = resultPojo_normal.getNewUrlSet();
		if (urlSet != null && (!urlSet.isEmpty())) {
			// System.out.println("server title---"
			// + resultPojo_normal.getSource_title());
			CrawlTaskPojo taskPojo = null;
			for (String url : urlSet) {
				taskPojo = new CrawlTaskPojo();
				taskPojo.setValue(url);
				taskPojo.setMedia_type(resultPojo_normal
						.getOwnToCrawlTaskPojo().getMedia_type());
				taskPojo.setSource_title(resultPojo_normal
						.getOwnToCrawlTaskPojo().getSource_title());
				taskPojo.setDepth(resultPojo_normal.getOwnToCrawlTaskPojo()
						.getDepth());
				taskPojo.setCurrent_depth(resultPojo_normal.getCurrent_depth());
				taskPojo.setTopN(resultPojo_normal.getOwnToCrawlTaskPojo()
						.getTopN());
				taskPojo.setLevel(resultPojo_normal.getOwnToCrawlTaskPojo()
						.getLevel());
				taskPojo.setType(resultPojo_normal.getOwnToCrawlTaskPojo()
						.getType());

				TaskQueueManager.addTask(taskPojo, TaskQueueType.To_Visit);
			}
		}
	}

	public static void indexOneRecord(
			CrawlData4PortalSite crawlData4PortalSite, String uniq_string,
			String successMsg, String failMsg) {
		if (BloomFilterManager.containsTaskInVisitedBloom(uniq_string)) {
			logger.info(failMsg);
			return;
		}
		List<CrawlData4PortalSite> pojoList = new LinkedList<CrawlData4PortalSite>();
		pojoList.add(crawlData4PortalSite);
		ESIndexServiceManager.addBatchIndex4PortalWeb(
				"yuqing_studio",
				StaticValue4SearchCondition.type_name_portals_web_data,
				pojoList);

		// 将已抓取完成的任务，放置到done task bloom filter一份
		BloomFilterManager.addToBloom(TaskQueueType.Visited, uniq_string);
		logger.info(successMsg);
	}

}
