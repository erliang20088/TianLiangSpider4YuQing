package com.tianliang.spider.crawler.threads;

import com.tianliang.spider.manager.bloom.BloomFilterManager;
import com.tianliang.spider.manager.task.TaskQueueManager;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.SystemParas;

/**
 * 守护线程，负责周期性保存某些数据
 * 
 * @author zel
 * 
 */
public class DaemonThread4SaveCacheData implements Runnable {
	public static MyLogger logger = new MyLogger(
			DaemonThread4SaveCacheData.class);
	private boolean runnable_able = false;

	public DaemonThread4SaveCacheData(boolean runnable_able) {
		this.runnable_able = runnable_able;
	}

	@Override
	public void run() {
		while (runnable_able) {
			try {
				Thread.sleep(SystemParas.cache_data_circle_save_interval);
				BloomFilterManager.saveToRedis();
				
				//定期存储circle set
				TaskQueueManager.saveToRedis();
			} catch (Exception e) {
				logger.info("save cache data occur exception,please check!");
				e.printStackTrace();
			}
		}
	}
}
