package com.tianliang.spider.manager.task;

import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.ObjectAndByteArrayConvertUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.utils.JedisOperatorUtil;
import com.zel.spider.pojos.CrawlTaskPojo;

/**
 * 任务调度控制器
 * 
 * @author zel
 * 
 */
public class TaskDispatcherControler {
	// 日志
	public static MyLogger logger = new MyLogger(TaskDispatcherControler.class);

	private static JedisOperatorUtil jedisOperatorUtil = new JedisOperatorUtil(
			SystemParas.redis_host, SystemParas.redis_port,
			SystemParas.redis_password);

	private static String to_do_task_key_circle_keyword = StaticValue.redis_task_todo_key_name_circle_keyword;
	private static String to_do_task_key_circle = StaticValue.redis_task_todo_key_name_circle;
	private static String task_todo_level_2 = StaticValue.redis_task_todo_list_key_name_level_2;
	private static String todo_key_name = StaticValue.redis_task_todo_list_key_name;

	/**
	 * 获取cirlce task 4 keyword
	 * 
	 * @return
	 */
	public static CrawlTaskPojo getTaskCircleKeyword() {
		CrawlTaskPojo taskPojo = null;
		try {
			byte[] byteArray = jedisOperatorUtil
					.rpop(StaticValue.redis_task_circle_keyword_queue_key_cache
							.getBytes(StaticValue.default_encoding));
			if (byteArray != null && byteArray.length > 0) {
				taskPojo = (CrawlTaskPojo) ObjectAndByteArrayConvertUtil
						.ByteArrayToObject(jedisOperatorUtil.HGet(
								to_do_task_key_circle_keyword
										.getBytes(StaticValue.default_encoding),
								byteArray));
				// 删除掉已经取出来的循环任务中的key
				long delTag = jedisOperatorUtil.HDel(
						to_do_task_key_circle_keyword
								.getBytes(StaticValue.default_encoding),
						byteArray);
				if (delTag > 0) {
					logger.info("success to delete circle keyword to do key!");
				} else {
					logger.info("fail to delete circle keyword to do key!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return taskPojo;
	}

	/**
	 * 获取 cirlce task 4 normal circle task
	 * 
	 * @return
	 */
	public static CrawlTaskPojo getTaskCircleNormal() {
		CrawlTaskPojo taskPojo = null;
		try {
			byte[] byteArray = jedisOperatorUtil
					.rpop(StaticValue.redis_task_circle_queue_key_cache
							.getBytes(StaticValue.default_encoding));
			if (byteArray != null && byteArray.length > 0) {
				taskPojo = (CrawlTaskPojo) ObjectAndByteArrayConvertUtil
						.ByteArrayToObject(jedisOperatorUtil.HGet(
								to_do_task_key_circle
										.getBytes(StaticValue.default_encoding),
								byteArray));
				// 删除掉已经取出来的循环任务中的key
				long delTag = jedisOperatorUtil.HDel(to_do_task_key_circle
						.getBytes(StaticValue.default_encoding), byteArray);
				if (delTag > 0) {
					logger.info("success to delete circle url to do key!");
				} else {
					logger.info("fail to delete circle url to do key!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskPojo;
	}

	/**
	 * 从二级队列中得到一个任务
	 * 
	 * @return
	 */
	public static CrawlTaskPojo getTaskSecondQueue() {
		CrawlTaskPojo taskPojo = null;
		try {
			taskPojo = (CrawlTaskPojo) ObjectAndByteArrayConvertUtil
					.ByteArrayToObject(jedisOperatorUtil.rpop(task_todo_level_2
							.getBytes(StaticValue.default_encoding)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskPojo;
	}

	/**
	 * 得到第一队列的任务
	 * 
	 * @return
	 */
	public static CrawlTaskPojo getTaskFirstQueue() {
		CrawlTaskPojo taskPojo = null;
		try {
			taskPojo = (CrawlTaskPojo) ObjectAndByteArrayConvertUtil
					.ByteArrayToObject(jedisOperatorUtil.rpop(todo_key_name
							.getBytes(StaticValue.default_encoding)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskPojo;
	}

	// 判断是否可以去获取新的任务的资格，主要为了防止task_todo_level_2过大
	public static boolean isAbleToTakeNewTask() {
		long redis_task_todo_list_key_name_level_2_length = jedisOperatorUtil
				.llen(StaticValue.redis_task_todo_list_key_name_level_2);
		if (redis_task_todo_list_key_name_level_2_length <= SystemParas.task_todo_level_2_max_items_in_redis) {
			return true;
		}
		return false;
	}

}
