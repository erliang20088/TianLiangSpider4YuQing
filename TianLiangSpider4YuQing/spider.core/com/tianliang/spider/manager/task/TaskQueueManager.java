package com.tianliang.spider.manager.task;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.tianliang.spider.manager.bloom.BloomFilterManager;
import com.tianliang.spider.manager.controler.ControlerManager;
import com.tianliang.spider.pojos.rule.UrlFilterPojo4OneHostToMultiPattern.MatchCrawlPojo;
import com.tianliang.spider.utils.DateUtil;
import com.tianliang.spider.utils.MD5;
import com.tianliang.spider.utils.ObjectAndByteArrayConvertUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.utils.JedisOperatorUtil;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.RetStatus;
import com.zel.spider.pojos.enums.RetCodeEnum;
import com.zel.spider.pojos.enums.RetDescEnum;
import com.zel.spider.pojos.enums.TaskTypeEnum;

/**
 * 任务管理器
 * 
 * @author zel
 * 
 */
public class TaskQueueManager {
	public static Logger logger = Logger.getLogger(TaskQueueManager.class);
	public static MD5 md5Util = new MD5();
	// redis操作工具,所有与redis任务队列相关的操作均在此完成
	private static JedisOperatorUtil jedisOperatorUtil = new JedisOperatorUtil(
			SystemParas.redis_host, SystemParas.redis_port,
			SystemParas.redis_password);

	// 主要的任务队列
	public static LinkedList<CrawlTaskPojo> toVisitUrls = new LinkedList<CrawlTaskPojo>();
	public static LinkedList<CrawlTaskPojo> visitingUrls = new LinkedList<CrawlTaskPojo>();
	public static LinkedList<CrawlTaskPojo> visitedUrls = new LinkedList<CrawlTaskPojo>();
	public static LinkedList<CrawlTaskPojo> errorVisitUrls = new LinkedList<CrawlTaskPojo>();

	public static Set<CrawlTaskPojo> circleVisitUrls = null;

	static {
		init();
	}

	public static void init() {
		// 首先加载redis中的缓存的circle队列
		String key_name_circle = StaticValue.redis_task_set_key_name_circle;
		byte[] byteArray = null;
		try {
			byteArray = jedisOperatorUtil.getObj(key_name_circle
					.getBytes(StaticValue.default_encoding));
			if (byteArray == null) {
				circleVisitUrls = new HashSet<CrawlTaskPojo>();
				logger.info(key_name_circle + "初始化完成!");
			} else {
				circleVisitUrls = (HashSet<CrawlTaskPojo>) ObjectAndByteArrayConvertUtil
						.ByteArrayToObject(byteArray);
				logger.info(key_name_circle + "从redis加载完成!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("从redis加载" + key_name_circle + "出现错误，将重新初始化该对象");
			circleVisitUrls = new HashSet<CrawlTaskPojo>();
		}
	}

	// 对各任务队列的锁,以及最大的一个全局锁
	static final Lock lock_global = new ReentrantLock();
	static final Lock lock_toVisitUrls = new ReentrantLock();
	static final Lock lock_visitingUrls = new ReentrantLock();
	static final Lock lock_visitedUrls = new ReentrantLock();
	static final Lock lock_errorVisitUrls = new ReentrantLock();

	// 循环任务
	static final Lock lock_circleVisitUrls = new ReentrantLock();

	// circle url set是否发生变化
	private static boolean circle_visit_urls_set_is_changed = false;

	public static RetStatus addTask(CrawlTaskPojo taskPojo,
			TaskQueueType taskQueueType) {
		if (taskQueueType == TaskQueueType.To_Visit) {
			lock_toVisitUrls.lock();
			try {
				toVisitUrls.add(taskPojo);
			} finally {
				lock_toVisitUrls.unlock();
			}
		} else if (taskQueueType == TaskQueueType.Visiting) {
			lock_visitingUrls.lock();
			try {
				visitingUrls.add(taskPojo);
			} finally {
				lock_visitingUrls.unlock();
			}
		} else if (taskQueueType == TaskQueueType.Visited) {
			lock_visitedUrls.lock();
			try {
				visitedUrls.add(taskPojo);
			} finally {
				lock_visitedUrls.unlock();
			}
		} else if (taskQueueType == TaskQueueType.Error_Visit) {
			lock_errorVisitUrls.lock();
			try {
				errorVisitUrls.add(taskPojo);
			} finally {
				lock_errorVisitUrls.unlock();
			}
		} else if (taskQueueType == TaskQueueType.Circle_Visit) {
			lock_circleVisitUrls.lock();
			try {
				if (!circleVisitUrls.contains(taskPojo)) {
					circle_visit_urls_set_is_changed = true;
					taskPojo.reset4Circle();
					taskPojo.setLast_insert_time_long(DateUtil.getLongByDate());
					circleVisitUrls.add(taskPojo);
				} else {
					System.out.println("find circle task repeat,will jump!");
				}
			} finally {
				lock_circleVisitUrls.unlock();
			}
		} else {
			logger.info("添加任务的类型不正确，请检查!");
			return new RetStatus(RetCodeEnum.Error, RetDescEnum.Fail);
		}
		return new RetStatus(RetCodeEnum.Ok, RetDescEnum.Success);
	}

	public static CrawlTaskPojo getTask(TaskQueueType taskQueueType) {
		if (taskQueueType == TaskQueueType.To_Visit) {
			lock_toVisitUrls.lock();
			try {
				return toVisitUrls.poll();
			} finally {
				lock_toVisitUrls.unlock();
			}
		} else if (taskQueueType == TaskQueueType.Visiting) {
			lock_visitingUrls.lock();
			try {
				return visitingUrls.poll();
			} finally {
				lock_visitingUrls.unlock();
			}
		} else if (taskQueueType == TaskQueueType.Visited) {
			lock_visitedUrls.lock();
			try {
				return visitedUrls.poll();
			} finally {
				lock_visitedUrls.unlock();
			}
		} else if (taskQueueType == TaskQueueType.Error_Visit) {
			lock_errorVisitUrls.lock();
			try {
				return errorVisitUrls.poll();
			} finally {
				lock_errorVisitUrls.unlock();
			}
		}
		// 暂定循环任务不从此处获取
		// else if (taskQueueType == TaskQueueType.Circle_Visit) {
		// lock_circleVisitUrls.lock();
		// try {
		// return circleVisitUrls.poll();
		// } finally {
		// lock_circleVisitUrls.unlock();
		// }
		// }
		return null;
	}

	// 为ws client准备
	public static boolean removeCircleTask(CrawlTaskPojo taskPojo) {
		boolean remove_status = false;
		lock_circleVisitUrls.lock();
		try {
			if (circleVisitUrls.contains(taskPojo)) {
				circle_visit_urls_set_is_changed = true;
				circleVisitUrls.remove(taskPojo);
				remove_status = true;
			} else {
				System.out
						.println("circle task is not the circle task set,remove disable!");
				remove_status = false;
			}
		} finally {
			lock_circleVisitUrls.unlock();
		}
		return remove_status;
	}

	public static List<CrawlTaskPojo> getAllToTaskList(
			TaskQueueType taskQueueType) {
		if (taskQueueType == TaskQueueType.To_Visit) {
			lock_toVisitUrls.lock();
			List<CrawlTaskPojo> list = null;
			try {
				if (!toVisitUrls.isEmpty()) {
					list = new LinkedList<CrawlTaskPojo>(toVisitUrls);
					toVisitUrls.clear();
				}
			} finally {
				lock_toVisitUrls.unlock();
			}
			return list;
		}
		return null;
	}

	// 得到所有的可以加入到待执行的to do circle中
	public static List<CrawlTaskPojo> getAllToDoCicleTaskList(
			TaskQueueType taskQueueType) {
		if (taskQueueType == TaskQueueType.Circle_Visit) {
			lock_circleVisitUrls.lock();
			List<CrawlTaskPojo> list = null;
			try {
				if (!circleVisitUrls.isEmpty()) {
					list = new LinkedList<CrawlTaskPojo>();
					for (CrawlTaskPojo taskPojo : circleVisitUrls) {
						if (taskPojo.isShouldToDoThisCircleTask()) {
							// 在这里不用new新对象，直接使用这个即可
							taskPojo.reset4Circle();
							taskPojo.setLast_insert_time_long(DateUtil
									.getLongByDate());
							list.add(taskPojo);
						}
					}
				}
			} finally {
				lock_circleVisitUrls.unlock();
			}
			return list;
		}
		return null;
	}

	/**
	 * 将任务添加至相应的redis队列中,is_judge_in_bloom是代表是否将该任务的判断是否加入任务过滤中
	 */
	public static void addTaskToDoQueue(String todo_key_name,
			CrawlTaskPojo to_do_task_pojo, TaskQueueType taskType,
			boolean is_judge_in_bloom) {
		try {
			if (taskType == TaskQueueType.Circle_Visit) {
				// 得到task pojo对应的toUniqString的值进行md5后做为hset中的field字段
				String md5String = md5Util.MD5(to_do_task_pojo.toUniqString());

				boolean is_put_success = jedisOperatorUtil.HSetnx(todo_key_name
						.getBytes(StaticValue.default_encoding), md5String
						.getBytes(StaticValue.default_encoding),
						ObjectAndByteArrayConvertUtil
								.ObjectToByteArray(to_do_task_pojo));
				if (is_put_success) {
					// 把该key加入到circle_key_cache队列中
					jedisOperatorUtil.lpush(
							StaticValue.redis_task_circle_queue_key_cache,
							md5String);
					logger.info("添加待抓取周期任务到redis set成功!");
				} else {
					logger.info("周期任务中有重复，将不再放入," + to_do_task_pojo);
				}
			} else if (taskType == TaskQueueType.Circle_Visit_Keyword) {
				// 得到task pojo对应的toUniqString的值进行md5后做为hset中的field字段
				String md5String = md5Util.MD5(to_do_task_pojo.toUniqString());

				boolean is_put_success = jedisOperatorUtil.HSetnx(todo_key_name
						.getBytes(StaticValue.default_encoding), md5String
						.getBytes(StaticValue.default_encoding),
						ObjectAndByteArrayConvertUtil
								.ObjectToByteArray(to_do_task_pojo));
				if (is_put_success) {
					// 把该key加入到circle_key_cache队列中
					jedisOperatorUtil
							.lpush(StaticValue.redis_task_circle_keyword_queue_key_cache,
									md5String);
					logger.info("添加元搜索待抓取周期任务到redis set成功!");
				} else {
					logger.info("周期任务中有重复元搜索任务，将不再放入," + to_do_task_pojo);
				}
			} else {
				// 在这里做去重过滤判断,
				if (is_judge_in_bloom) {
					// 20151012改动
					MatchCrawlPojo matchCrawlPojo = null;
					if ((!BloomFilterManager
							.containsTaskInVisitedBloom(to_do_task_pojo
									.getValueFormat()))) {
						// 如果在to do visit bloom中
						if (TaskTypeEnum.Url == to_do_task_pojo.getType()
								&& BloomFilterManager
										.containsTaskInToVisiteBloom(to_do_task_pojo
												.getValueFormat())) {
							if (((matchCrawlPojo = ControlerManager.extractorContentManager4SimpleCrawler
									.getMatchCrawlPojoByGlobalLock(to_do_task_pojo
											.getValueFormat())) != null && matchCrawlPojo
									.isMatch())) {
								jedisOperatorUtil
										.lpush(todo_key_name
												.getBytes(StaticValue.default_encoding),
												ObjectAndByteArrayConvertUtil
														.ObjectToByteArray(to_do_task_pojo));
							} else {
								logger.info("find repeat task,will jump the one!");
							}
						} else {
							jedisOperatorUtil
									.lpush(todo_key_name
											.getBytes(StaticValue.default_encoding),
											ObjectAndByteArrayConvertUtil
													.ObjectToByteArray(to_do_task_pojo));
							BloomFilterManager.addToBloom(
									TaskQueueType.To_Visit,
									to_do_task_pojo.getValueFormat());
						}
					} else {
						logger.info("find repeat task,will jump the one!");
					}
				} else {
					jedisOperatorUtil.lpush(todo_key_name
							.getBytes(StaticValue.default_encoding),
							ObjectAndByteArrayConvertUtil
									.ObjectToByteArray(to_do_task_pojo));
					// 将已添加到待抓取队列的任务值加入到布隆中
					BloomFilterManager.addToBloom(TaskQueueType.To_Visit,
							to_do_task_pojo.getValueFormat());
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// 得到各项任务的统计参数
	public static Map<String, String> getStatistic() {
		Map<String, String> statisticMap = new HashMap<String, String>();
		// 得到待执行周期关键词任务的list length,其key为redis_task_todo_key_name_circle_keyword
		long redis_task_todo_key_name_circle_keyword_length = jedisOperatorUtil
				.llen(StaticValue.redis_task_circle_keyword_queue_key_cache);
		statisticMap.put(StaticValue.redis_task_circle_keyword_queue_key_cache,
				redis_task_todo_key_name_circle_keyword_length + "");

		// 得到待执行二级待执行任务的length,其key为StaticValue.redis_task_todo_list_key_name_level_2
		long redis_task_todo_list_key_name_level_2_length = jedisOperatorUtil
				.llen(StaticValue.redis_task_todo_list_key_name_level_2);
		statisticMap.put(StaticValue.redis_task_todo_list_key_name_level_2,
				redis_task_todo_list_key_name_level_2_length + "");

		// 得到待执行正常的循环队列，区别于关键词这种高优先级队列，StaticValue.redis_task_circle_queue_key_cache
		long redis_task_circle_queue_key_cache_length = jedisOperatorUtil
				.llen(StaticValue.redis_task_circle_queue_key_cache);
		statisticMap.put(StaticValue.redis_task_circle_queue_key_cache,
				redis_task_circle_queue_key_cache_length + "");

		return statisticMap;
	}

	public enum TaskQueueType {
		To_Visit, Visiting, Visited, Error_Visit, Circle_Visit, Circle_Visit_Keyword;
	}

	/**
	 * 得到布隆对应的二进制字节数据后，存放到redis中
	 * 
	 * @param taskType
	 * @return
	 */
	public static byte[] getByteArray(TaskQueueType taskType) {
		if (TaskQueueType.Circle_Visit == taskType) {
			lock_circleVisitUrls.lock();
			try {
				return ObjectAndByteArrayConvertUtil
						.ObjectToByteArray(circleVisitUrls);
			} finally {
				lock_circleVisitUrls.unlock();
			}
		}
		return null;
	}

	// 通过守护线程的调用将对应的布署过滤器对象缓存至redis中
	public static void saveToRedis() throws Exception {
		if (circle_visit_urls_set_is_changed) {
			lock_circleVisitUrls.lock();
			try {
				byte[] byteArray = getByteArray(TaskQueueType.Circle_Visit);
				if (byteArray != null) {
					jedisOperatorUtil.putObj(
							StaticValue.redis_task_set_key_name_circle
									.getBytes(StaticValue.default_encoding),
							byteArray);
					System.out
							.println("circle set is changed,have put data to redis cache!");
				}
			} finally {
				lock_circleVisitUrls.unlock();
			}
			circle_visit_urls_set_is_changed = false;
		}
	}

	public static Set<CrawlTaskPojo> getAllSubmitTaskPojoObject()
			throws Exception {
		byte[] byteArray = jedisOperatorUtil
				.getObj(StaticValue.redis_task_set_key_name_circle
						.getBytes(StaticValue.default_encoding));
		Set<CrawlTaskPojo> taskPojoSet = (HashSet<CrawlTaskPojo>) ObjectAndByteArrayConvertUtil
				.ByteArrayToObject(byteArray);

		return taskPojoSet;
	}

	public static void main(String[] args) throws Exception{
		Set<CrawlTaskPojo> set = getAllSubmitTaskPojoObject();
		for(CrawlTaskPojo pojo:set){
			System.out.println(pojo);
		}
		if (true) {
			System.out.println("done");
			System.exit(0);
		}

		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		taskPojo.setCurrent_depth(0);
		taskPojo.setValue("http://www.weibo.com/yaochen");
		taskPojo.setSource_title("title");
		taskPojo.setLast_insert_time_long(DateUtil.getLongByDate());

		byte[] byteArray = ObjectAndByteArrayConvertUtil
				.ObjectToByteArray(taskPojo);

		// set.add(byteArray);
		// set.add(taskPojo);

		CrawlTaskPojo taskPojo2 = new CrawlTaskPojo();
		taskPojo2.setCurrent_depth(0);
		taskPojo2.setValue("http://www.weibo.com/yaochen");
		taskPojo2.setSource_title("title");
		taskPojo2.setLast_insert_time_long(123);

		byte[] byteArray2 = ObjectAndByteArrayConvertUtil
				.ObjectToByteArray(taskPojo2);

		// if (set.contains(taskPojo2)) {
		// System.out.println("contains");
		// } else {
		// System.out.println("not contains");
		// }

		System.out.println(byteArray.length);
		System.out.println(byteArray2.length);

		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] != byteArray2[i]) {
				System.out.println("find not contains");
				break;
			}
		}

	}

}
