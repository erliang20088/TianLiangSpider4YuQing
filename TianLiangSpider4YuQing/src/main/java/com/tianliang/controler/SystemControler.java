package com.tianliang.controler;

import com.tianliang.spider.manager.controler.ControlerManager;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.SystemParas;
import com.zel.es.manager.ws.WebServiceManager;

/**
 * 系统运行的控制器类
 * 
 * @author zel
 */
public class SystemControler {
	// 日志
	public static MyLogger logger = new MyLogger(SystemControler.class);
	
	public static void main(String[] args) {
		if (SystemParas.node_is_master) {
			/**
			 * 启动系统web service服务
			 */
			WebServiceManager webServiceManager = new WebServiceManager();
			webServiceManager.startWebService();
			
			ControlerManager controlerManager = new ControlerManager();
			controlerManager.startServer();
		} else {
			ControlerManager controlerManager = new ControlerManager();
			controlerManager.startClient();
		}
		System.out.println("done!");
	}
}
