package com.tianliang.spider.manager.threads;

import com.tianliang.spider.crawler.threads.DaemonThread4SaveCrawlData;
import com.tianliang.spider.crawler.threads.DaemonThread4UpdateRuleKey4Client;

/**
 * 为数据保存添加的守护线程
 * 
 * @author zel
 * 
 */
public class ThreadManager4DataSave {
	public ThreadManager4DataSave(int threadNumber) {
		for (int threadId = 0; threadId < threadNumber; threadId++) {
			DaemonThread4SaveCrawlData daemonThread4SaveData = new DaemonThread4SaveCrawlData(
					threadId,true);
			Thread tt = new Thread(daemonThread4SaveData);
			tt.start();
		}
	}

	public static void startDaemon(int threadNumber){
		ThreadManager4DataSave threadManager4DataSave =new ThreadManager4DataSave(threadNumber);
	}
	
	public static void startDaemon4UpdateRuleKey4Client(int threadNumber){
		DaemonThread4UpdateRuleKey4Client threadManager4DataSave4Client =new DaemonThread4UpdateRuleKey4Client(threadNumber,true);
		Thread tt=new Thread(threadManager4DataSave4Client);
		tt.start();
	}
	
	public static void main(String[] args) {

	}
}
