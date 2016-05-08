package com.tianliang.spider.crawler.threads;

import com.tianliang.spider.manager.controler.ControlerManager;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.utils.JedisOperatorUtil;

/**
 * 守护线程，负责守护工作 ;1、周期性更新规则库
 * 
 * @author zel
 * 
 */
public class DaemonThread4UpdateRuleKey4Client implements Runnable {
	public static MyLogger logger = new MyLogger(
			DaemonThread4UpdateRuleKey4Client.class);

	private int thread_id;
	private boolean runnable_able = true;
	private JedisOperatorUtil jedisOperatorUtil = null;

	public DaemonThread4UpdateRuleKey4Client(int thread_id, boolean runnable_able) {
		this.thread_id = thread_id;
		this.runnable_able = runnable_able;
		this.jedisOperatorUtil = new JedisOperatorUtil(SystemParas.redis_host,
				SystemParas.redis_port, SystemParas.redis_password);
	}

	@Override
	public void run() {
		String status = null;
		while (this.runnable_able) {
			// 首先进行sleep
			try {
				// 休息一个SystemParas.ext_content_rule_key_syn_circle就检查是否有更新
				Thread.sleep(SystemParas.ext_content_rule_key_syn_circle);
				status = jedisOperatorUtil
						.getObj(StaticValue.ext_content_rule_key_is_changed);
				if ("1".equals(status)) {
					String rule_string = jedisOperatorUtil
							.getObj(StaticValue.ext_content_rule_key);
					synchronized (ControlerManager.class) {
						ControlerManager.extractorContentManager4SimpleCrawler
								.resetRule(rule_string, false);
					}
					logger.info("规则库有更新，更新完成!");
				} else {
					logger.info("规则库没有更新!");
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		logger.info("规则库更新列表的守护线程被结束了，请检查程序的正确性!");
	}
}
