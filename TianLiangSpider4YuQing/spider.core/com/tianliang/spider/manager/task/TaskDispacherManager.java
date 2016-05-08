package com.tianliang.spider.manager.task;

import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.SystemParas;
import com.zel.spider.pojos.CrawlTaskPojo;

/**
 * 任务协调管理器
 * 
 * @author zel
 * 
 */
public class TaskDispacherManager {
	// 日志
	public static MyLogger logger = new MyLogger(TaskDispacherManager.class);

	// 为循环队列而添加
	private static long task_count = 0;

	// prefix string
	private static String prefix_mode_3 = "%3==0,";
	private static String prefix_mode_10 = "%10==0,";
	private static String prefix_second_queue = "second queue,";
	
	//判断是否可以去获取新的任务的资格，主要为了防止task_todo_level_2过大
	public static boolean isAbleToTakeNewTask(){
		return TaskDispatcherControler.isAbleToTakeNewTask();
	}
	
	public static CrawlTaskPojo getCrawlTask() {
		/**
		 * 先判定几个要填充的redis中key的名称，就不用在后边用if...else去每次去判定了
		 */
		CrawlTaskPojo taskPojo = null;
		try {
			taskPojo = TaskDispatcherControler.getTaskFirstQueue();
			// 如果一级队列为空，则从二级队取值，再为空，则不再抓取
			if (taskPojo == null) {
				// 每20个执行周期中，执行一次circle task
				if (task_count % SystemParas.task_count_circle_keyword == 0) {
					taskPojo = TaskDispatcherControler.getTaskCircleKeyword();
					if (taskPojo != null) {
						System.out.println(prefix_mode_3 + "从元搜索循环队列中取出一个任务，"
								+ taskPojo);
					} else {
						taskPojo = TaskDispatcherControler.getTaskSecondQueue();
						if (taskPojo != null) {
							System.out.println(prefix_mode_3 + "从第二队列取出一个任务,"
									+ taskPojo);
						} else {
							taskPojo = TaskDispatcherControler
									.getTaskCircleNormal();
							if (taskPojo != null) {
								System.out.println(prefix_mode_3
										+ "从普通循环队列取出一个任务," + taskPojo);
							} else {
								System.out.println(prefix_mode_3
										+ "从普通循环队列也没有取到可执行任务!");
							}
						}
					}
				} else if (task_count % SystemParas.task_count_circle_normal == 0) {
					taskPojo = TaskDispatcherControler.getTaskCircleNormal();
					if (taskPojo != null) {
						System.out.println(prefix_mode_10 + "从普通循环队列取出一个任务,"
								+ taskPojo);
					} else {
						taskPojo = TaskDispatcherControler
								.getTaskCircleKeyword();
						if (taskPojo != null) {
							System.out.println(prefix_mode_10
									+ "从元搜索循环队列取出一个任务," + taskPojo);
						} else {
							// System.out.println(prefix_mode_10
							// + "从元搜索循环队列也没有取到可执行的任务!");
							taskPojo = TaskDispatcherControler
									.getTaskSecondQueue();
							if (taskPojo != null) {
								System.out.println(prefix_mode_10
										+ "从第二队列取出一个任务," + taskPojo);
							} else {
								System.out.println(prefix_mode_10
										+ "从第二队列中也没有取出一个任务");
							}
						}
					}
				} else {
					// 此处是一般的二级任务获取，如果获取不到，则获取一般的循还周期任务
					// 先从第二队列去
					taskPojo = TaskDispatcherControler.getTaskSecondQueue();
					if (taskPojo != null) {
						System.out.println("从第二队列取出一个任务," + taskPojo);
					} else {
						taskPojo = TaskDispatcherControler
								.getTaskCircleKeyword();
						if (taskPojo != null) {
							System.out.println(prefix_second_queue
									+ "从元搜索循环队列中取出一个任务，" + taskPojo);
						} else {
							taskPojo = TaskDispatcherControler
									.getTaskCircleNormal();
							if (taskPojo != null) {
								System.out.println(prefix_second_queue
										+ "从普通循环队列中取出一个任务，" + taskPojo);
							} else {
								System.out.println(prefix_second_queue
										+ "从第二队列中也没有取出一个任务");
							}
						}
					}
				}
				if (task_count > 10000000) {
					task_count = 0;
					logger.info("counter reset,task_task_count > 10000000 will set task_task_count=0");
				}
				task_count++;
			} else {
				System.out.println("从第一队列取出一个任务," + taskPojo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskPojo;
	}
}
