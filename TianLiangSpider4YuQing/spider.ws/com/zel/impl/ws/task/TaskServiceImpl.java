package com.zel.impl.ws.task;

import javax.jws.WebService;

import com.tianliang.spider.manager.task.TaskQueueManager;
import com.tianliang.spider.manager.task.TaskQueueManager.TaskQueueType;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.SystemParas;
import com.zel.iface.ws.spider.task.ITaskService;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.RetStatus;
import com.zel.spider.pojos.enums.RetCodeEnum;
import com.zel.spider.pojos.enums.RetDescEnum;

/**
 * 任务服务实现类,通过该web service接口，外部可以通过程序接口对任务进行管理
 * 
 * @author zel
 * 
 */
@WebService(endpointInterface = "com.zel.iface.ws.spider.task.ITaskService", serviceName = "TaskService")
public class TaskServiceImpl implements ITaskService {

	@Override
	public String test() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RetStatus addTask(CrawlTaskPojo taskTaskPojo) {
		// TaskQueueManager.addTask(taskTaskPojo, TaskQueueType.To_Visit);
		TaskQueueManager.addTaskToDoQueue(
				StaticValue.redis_task_todo_list_key_name, taskTaskPojo, null,
				false);
		// 暂定只有从这个途径过添加的任务都是原始任务,根据条件加入循环队列
		if (SystemParas.task_circle_enable && taskTaskPojo.isEnableToCircle()) {
			TaskQueueManager.addTask(taskTaskPojo, TaskQueueType.Circle_Visit);
		}

		System.out.println("ws client add task operator!");
		return new RetStatus(RetCodeEnum.Ok, RetDescEnum.Success);
	}

	@Override
	public RetStatus removeTask(CrawlTaskPojo taskTaskPojo) {
		boolean remove_status = TaskQueueManager.removeCircleTask(taskTaskPojo);
		if (remove_status) {
			System.out.println("ws client remove task success!");
			return new RetStatus(RetCodeEnum.Ok, RetDescEnum.Success);
		} else {
			System.out.println("ws client remove task fail!");
			return new RetStatus(RetCodeEnum.Error, RetDescEnum.Fail);
		}

	}

}
