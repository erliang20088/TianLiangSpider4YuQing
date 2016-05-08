package com.tianliang.spider.crawler.threads;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tianliang.spider.manager.controler.ControlerManager;
import com.tianliang.spider.manager.task.TaskQueueManager;
import com.tianliang.spider.manager.task.TaskQueueManager.TaskQueueType;
import com.tianliang.spider.pojos.rule.ExtContentRuleConfigManager;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.utils.JedisOperatorUtil;
import com.zel.es.utils.StringOperatorUtil;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.enums.TaskTypeEnum;

/**
 * 守护线程，负责周期性更新规则库
 * 
 * @author zel
 * 
 */
public class DaemonThread4UpdateRuleKey implements Runnable {
	public static MyLogger logger = new MyLogger(
			DaemonThread4UpdateRuleKey.class);

	private int thread_id;

	public int getThread_id() {
		return thread_id;
	}

	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	private boolean runnable_able = false;
	private JedisOperatorUtil jedisOperatorUtil = null;

	public DaemonThread4UpdateRuleKey(int thread_id, boolean runnable_able) {
		this.thread_id = thread_id;
		this.runnable_able = runnable_able;
		this.jedisOperatorUtil = new JedisOperatorUtil(SystemParas.redis_host,
				SystemParas.redis_port, SystemParas.redis_password);
	}

	@Override
	public void run() {
		// 将一些二次产生的url加入到该队列中
		String task_todo_level_2 = StaticValue.redis_task_todo_list_key_name_level_2;
		String task_todo_circle_key = StaticValue.redis_task_todo_key_name_circle;
		String task_todo_circle_keyword_key = StaticValue.redis_task_todo_key_name_circle_keyword;
		boolean is_first_run = true;
		while (this.runnable_able) {
			// 首先将规则集合初始化后，转化成二进制对象，然后放入redis中
			// 客户端在抓取时，首先获取该规则集合，从而进行抓取、解析与内容抽取
			try {
				ExtContentRuleConfigManager extContentRuleConfigManager = new ExtContentRuleConfigManager(
						SystemParas.ext_content_rule_config_fs,
						SystemParas.ext_content_rule_config_root_dir);
				String new_rule_value = extContentRuleConfigManager
						.getRuleString();
				// 判断下是否该将重写规则缓存数据
				String cache_rule_value = jedisOperatorUtil
						.getObj(StaticValue.ext_content_rule_key);
				// 如果相等，无需做任何改动
				if (new_rule_value.equals(cache_rule_value)) {
					jedisOperatorUtil.putObj(
							StaticValue.ext_content_rule_key_is_changed, "0");
					logger.info("服务器端的ext_rule规则库没有更新，将跳过!");
				} else {
					// 将相应的值放入redis相应的key对应的值中
					jedisOperatorUtil.putObj(StaticValue.ext_content_rule_key
							.getBytes(StaticValue.default_encoding),
							extContentRuleConfigManager.getRuleString()
									.getBytes(StaticValue.default_encoding));
					jedisOperatorUtil.putObj(
							StaticValue.ext_content_rule_key_is_changed, "1");

					// 更新一下ControlerManager类的extractorContentManager4SimpleCrawler实例
					//加个标志位，防止ControlerManager.extractorContentManager4SimpleCrawler的对象为空
					if (!is_first_run) {
						ControlerManager.extractorContentManager4SimpleCrawler
								.resetRuleByGlobalLock(
										extContentRuleConfigManager
												.getRuleString(), false);
						is_first_run=false;
					}

					logger.info("服务器端的ext_rule规则库有更新，已更新到redis中!");
				}

				// 兼职将TaskMamager中的有的taskPojo添加到redis to do task队列中去
				List<CrawlTaskPojo> to_do_task_all = TaskQueueManager
						.getAllToTaskList(TaskQueueType.To_Visit);
				if (StringOperatorUtil.isNotBlankCollection(to_do_task_all)) {
					for (CrawlTaskPojo taskPojo : to_do_task_all) {
						TaskQueueManager.addTaskToDoQueue(task_todo_level_2,
								taskPojo, TaskQueueType.To_Visit, true);
					}
					logger.info("添加所有非种子任务进入二级任务队列!");
				} else {
					logger.info("没有非种子任务的产生，不加入二任务队列!");
				}

				// 兼职做circle task set检查，如果有满足条件的则加入到to do circle task中
				List<CrawlTaskPojo> to_do_circle_task_list = TaskQueueManager
						.getAllToDoCicleTaskList(TaskQueueType.Circle_Visit);
				if (StringOperatorUtil
						.isNotBlankCollection(to_do_circle_task_list)) {
					for (CrawlTaskPojo taskPojo : to_do_circle_task_list) {
						if (taskPojo.getType() == TaskTypeEnum.Keyword) {
							// 元搜索采集放在特殊位置
							TaskQueueManager.addTaskToDoQueue(
									task_todo_circle_keyword_key, taskPojo,
									TaskQueueType.Circle_Visit_Keyword, true);
						} else {
							TaskQueueManager.addTaskToDoQueue(
									task_todo_circle_key, taskPojo,
									TaskQueueType.Circle_Visit, true);
						}
					}
					logger.info("添加所有到时的循环任务到待执行的circle to do队列中!");
				} else {
					logger.info("没有到时的循环任务!");
				}

				// 兼职统计任务各项参数，并同步到redis中
				// 包括待抓取任务、已抓取任务、周期任务等
				Map<String, String> statisticMap = TaskQueueManager
						.getStatistic();
				if (!statisticMap.isEmpty()) {
					Set<Map.Entry<String, String>> resultMap = statisticMap
							.entrySet();
					for (Map.Entry<String, String> entry : resultMap) {
						jedisOperatorUtil.HSet(StaticValue.statistic_key_task,
								entry.getKey(), entry.getValue());
					}
					logger.info("update task statistic successful!");
				} else {
					logger.info("任务统计statisticMap为空，请根据实际情况检查!");
				}
				// 休息一下,然后再去更新规则库,主要是为了让别的client去有时间去更新到各自的节点中
				Thread.sleep(SystemParas.ext_content_rule_key_syn_circle * 5);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("init occur error,system will exit,please check!");
				System.exit(-1);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		DaemonThread4UpdateRuleKey daemonThread4UpdateRuleKey = new DaemonThread4UpdateRuleKey(
				1, true);

		ExtContentRuleConfigManager extContentRuleConfigManager = new ExtContentRuleConfigManager(
				SystemParas.ext_content_rule_config_fs,
				SystemParas.ext_content_rule_config_root_dir);
		String new_rule_value = extContentRuleConfigManager.getRuleString();
		// 判断下是否该将重写规则缓存数据
		String cache_rule_value = daemonThread4UpdateRuleKey.jedisOperatorUtil
				.getObj(StaticValue.ext_content_rule_key);
		daemonThread4UpdateRuleKey.jedisOperatorUtil.putObj(
				StaticValue.ext_content_rule_key
						.getBytes(StaticValue.default_encoding),
				extContentRuleConfigManager.getRuleString().getBytes(
						StaticValue.default_encoding));
		daemonThread4UpdateRuleKey.jedisOperatorUtil.putObj(
				StaticValue.ext_content_rule_key_is_changed, "1");
		logger.info("服务器端的ext_rule规则库没有更新，将跳过!");
	}
}
