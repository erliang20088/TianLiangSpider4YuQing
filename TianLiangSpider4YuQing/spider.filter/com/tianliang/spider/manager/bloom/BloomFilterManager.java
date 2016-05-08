package com.tianliang.spider.manager.bloom;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.tianliang.spider.manager.task.TaskQueueManager.TaskQueueType;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.utils.JedisOperatorUtil;
import com.vaolan.extkey.utils.ObjectAndByteArrayConvertUtil;
import com.zel.bloomFilter.BloomFilter;

/**
 * 用于去重的布隆过滤器的管理器
 * 
 * @author zel
 * 
 */
public class BloomFilterManager {
	private static Logger logger = Logger.getLogger(BloomFilterManager.class);
	// 待抓取任务布隆
	public static BloomFilter toDoTaskBloom = null;
	// 已完成抓取任务布隆
	public static BloomFilter doneTaskBloom = null;

	// redis操作工具,所有与redis任务队列相关的操作均在此完成
	private static JedisOperatorUtil jedisOperatorUtil = new JedisOperatorUtil(
			SystemParas.redis_host, SystemParas.redis_port,
			SystemParas.redis_password);
	static {
		init();
	}

	// 对各任务队列的锁
	static final Lock lock_toDoTaskBloom = new ReentrantLock();
	static final Lock lock_doneTaskBloom = new ReentrantLock();

	// 标志两个过滤器是否发生变化
	private static boolean toDoTaskBloom_is_changed = false;
	private static boolean doneTaskBloom_is_changed = false;

	public static void init() {
		// 首先来看redis存储中是否存在bloom key，如果存在则进行加载已有的bloom value,如果不存在，则直接重新初始化
		byte[] byteArray = null;
		try {
			byteArray = jedisOperatorUtil
					.getObj(StaticValue.bloom_to_do_task_key
							.getBytes(StaticValue.default_encoding));
			if (byteArray == null) {
				toDoTaskBloom = new BloomFilter(32);
				logger.info(StaticValue.bloom_to_do_task_key + "初始化完成!");
			} else {
				toDoTaskBloom = (BloomFilter) ObjectAndByteArrayConvertUtil
						.ByteArrayToObject(byteArray);
				logger.info(StaticValue.bloom_to_do_task_key + "从redis加载完成!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("从redis加载" + StaticValue.bloom_to_do_task_key
					+ "出现错误，将重新初始化该对象");
			toDoTaskBloom = new BloomFilter(32);
		}

		try {
			byteArray = jedisOperatorUtil
					.getObj(StaticValue.bloom_done_task_key
							.getBytes(StaticValue.default_encoding));
			if (byteArray == null) {
				doneTaskBloom = new BloomFilter(32);
				logger.info(StaticValue.bloom_done_task_key + "初始化完成!");
			} else {
				doneTaskBloom = (BloomFilter) ObjectAndByteArrayConvertUtil
						.ByteArrayToObject(byteArray);
				logger.info(StaticValue.bloom_done_task_key + "从redis加载完成!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("从redis加载" + StaticValue.bloom_done_task_key
					+ "出现错误，将重新初始化该对象");
			doneTaskBloom = new BloomFilter(32);
		}
	}

	// 我们把url作为key去做布隆，因为url是唯一的.返回值代表是否真的添加到过滤器里边
	public static boolean addToBloomAllInOne(TaskQueueType taskType, String url) {
		if (TaskQueueType.To_Visit == taskType) {
			lock_toDoTaskBloom.lock();
			try {
				if (!toDoTaskBloom.contains(url)) {
					toDoTaskBloom.add(url);
					toDoTaskBloom_is_changed = true;
					return true;
				}
			} finally {
				lock_toDoTaskBloom.unlock();
			}
		} else if (TaskQueueType.Visited == taskType) {
			lock_doneTaskBloom.lock();
			try {
				if (!doneTaskBloom.contains(url)) {
					doneTaskBloom.add(url);
					doneTaskBloom_is_changed = true;

					return true;
				}
			} finally {
				lock_doneTaskBloom.unlock();
			}
		}
		return false;
	}

	// 判断某url值是否在todo或是done布隆中，如果在其中一个，则不需要重复抓取
	public static boolean containsTask(String url) {
		// 在此也加锁，主要是为了保证绝对的不重复.如不在此加锁，由存在多个添加任务的入口,则极有可能导致个别极限情况下的重复任务的添加
		lock_toDoTaskBloom.lock();
		lock_doneTaskBloom.lock();
		try {
			if (toDoTaskBloom.contains(url) || doneTaskBloom.contains(url)) {
				return true;
			}
		} finally {
			lock_doneTaskBloom.unlock();
			lock_toDoTaskBloom.unlock();
		}
		return false;
	}

	// 判断某url值是否在todo或是done布隆中，如果在其中一个，则不需要重复抓取
	public static boolean containsTaskInVisitedBloom(String url) {
		// 在此也加锁，主要是为了保证绝对的不重复.如不在此加锁，由存在多个添加任务的入口,则极有可能导致个别极限情况下的重复任务的添加
		lock_doneTaskBloom.lock();
		try {
			if (doneTaskBloom.contains(url)) {
				return true;
			}
		} finally {
			lock_doneTaskBloom.unlock();
		}
		return false;
	}
	//判断一个url是否在to visit bloom里面
	public static boolean containsTaskInToVisiteBloom(String url) {
		// 在此也加锁，主要是为了保证绝对的不重复.如不在此加锁，由存在多个添加任务的入口,则极有可能导致个别极限情况下的重复任务的添加
		lock_toDoTaskBloom.lock();
		try {
			if (toDoTaskBloom.contains(url)) {
				return true;
			}
		} finally {
			lock_toDoTaskBloom.unlock();
		}
		return false;
	}

	// 将指定类型的任务添加到相应的任务队列中
	public static boolean addToBloom(TaskQueueType taskType, String url) {
		if (TaskQueueType.To_Visit == taskType) {
			lock_toDoTaskBloom.lock();
			try {
				toDoTaskBloom.add(url);
				toDoTaskBloom_is_changed = true;
				return true;
			} finally {
				lock_toDoTaskBloom.unlock();
			}
		} else if (TaskQueueType.Visited == taskType) {
			lock_doneTaskBloom.lock();
			try {
				doneTaskBloom.add(url);
				doneTaskBloom_is_changed = true;
				return true;
			} finally {
				lock_doneTaskBloom.unlock();
			}
		}
		return false;
	}

	/**
	 * 得到布隆对应的二进制字节数据后，存放到redis中
	 * 
	 * @param taskType
	 * @return
	 */
	public static byte[] getByteArray(TaskQueueType taskType) {
		if (TaskQueueType.To_Visit == taskType) {
			lock_toDoTaskBloom.lock();
			try {
				return ObjectAndByteArrayConvertUtil
						.ObjectToByteArray(toDoTaskBloom);
			} finally {
				lock_toDoTaskBloom.unlock();
			}
		} else if (TaskQueueType.Visited == taskType) {
			lock_doneTaskBloom.lock();
			try {
				return ObjectAndByteArrayConvertUtil
						.ObjectToByteArray(doneTaskBloom);
			} finally {
				lock_doneTaskBloom.unlock();
			}
		}
		return null;
	}

	// 通过守护线程的调用将对应的布署过滤器对象缓存至redis中
	public static void saveToRedis() throws Exception {
		if (toDoTaskBloom_is_changed) {
			lock_toDoTaskBloom.lock();
			try {
				byte[] byteArray = getByteArray(TaskQueueType.To_Visit);
				if (byteArray != null) {
					jedisOperatorUtil.putObj(StaticValue.bloom_to_do_task_key
							.getBytes(StaticValue.default_encoding), byteArray);
				}
			} finally {
				lock_toDoTaskBloom.unlock();
			}
			toDoTaskBloom_is_changed = false;
		}
		if (doneTaskBloom_is_changed) {
			lock_doneTaskBloom.lock();
			try {
				byte[] byteArray = getByteArray(TaskQueueType.Visited);
				if (byteArray != null) {
					jedisOperatorUtil.putObj(StaticValue.bloom_done_task_key
							.getBytes(StaticValue.default_encoding), byteArray);
				}
			} finally {
				lock_doneTaskBloom.unlock();
			}
			doneTaskBloom_is_changed = false;
		}
	}
}
