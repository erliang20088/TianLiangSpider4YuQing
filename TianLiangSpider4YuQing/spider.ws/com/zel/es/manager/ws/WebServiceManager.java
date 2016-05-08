package com.zel.es.manager.ws;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.spi.WsProvider;

import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.zel.impl.ws.task.TaskServiceImpl;

/**
 * web service发布管理器
 */
public class WebServiceManager {

	private List<Endpoint> ws_list = new LinkedList<Endpoint>();

	// 开启web service
	public void startWebService() {
		List<ServiceNameAndPathComp> wsCompList = new ArrayList<ServiceNameAndPathComp>();
		getWSClass4All(wsCompList);

		String address = StaticValue.prefix_http + SystemParas.ws_server_ip
				+ ":" + SystemParas.ws_server_port + "/";// 绑定的地址
		int count = 0;
		Endpoint endPoint = null;
		for (ServiceNameAndPathComp serviceNameAndPathComp : wsCompList) {
			try {
				if (StringOperatorUtil.isBlank(serviceNameAndPathComp
						.getServiceName())) {
					throw new IllegalArgumentException(
							"传入的service类，必须以annotation的方式配置serviceName属性!");
				} else {
					// endPoint = Endpoint.publish(address
					// + serviceNameAndPathComp.getServiceName(),
					// serviceNameAndPathComp.getImplementor());
					Endpoint endpoint = WsProvider
							.provider()
							.createAndPublishEndpoint(
									address
											+ serviceNameAndPathComp
													.getServiceName(),
									serviceNameAndPathComp.getImplementor());
					ws_list.add(endPoint);
				}
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("共开启了--" + ws_list.size() + "--个服务!");
	}

	// 关闭web service
	public void stopWebService() {
		for (Endpoint endPoint : ws_list) {
			endPoint.stop();
		}
		System.out.println("ws service is stop!");
	}

	// 得到指定class的annotation的serviceName属性
	public static String getDeclaredServiceName(Class sourceClass)
			throws IllegalArgumentException {
		Annotation[] annoArray = sourceClass.getAnnotations();
		for (Annotation anno : annoArray) {
			if (anno instanceof WebService) {
				WebService ws = (WebService) anno;
				return ws.serviceName();
			}
		}
		return null;
	}

	public static String temp_str = null;

	// 将所有ws类都组装起来
	public static void getWSClass4All(List<ServiceNameAndPathComp> wsCompList) {
		addOneWs(TaskServiceImpl.class, wsCompList);
	}

	public static void addOneWs(Class wsClass,
			List<ServiceNameAndPathComp> wsCompList) {
		try {
			wsCompList.add(new ServiceNameAndPathComp(
					getDeclaredServiceName(wsClass), wsClass.newInstance()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ws的服务名称和服务类所在类路径的封装
	 * 
	 * @author zel
	 */
	static class ServiceNameAndPathComp {
		public ServiceNameAndPathComp(String serviceName, Object implementor) {
			this.serviceName = serviceName;
			this.implementor = implementor;
		}

		private String serviceName;

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		private Object implementor;

		public Object getImplementor() {
			return implementor;
		}

		public void setImplementor(Object implementor) {
			this.implementor = implementor;
		}
	}

	public static void main(String[] args) {
		WebServiceManager webServiceManager = new WebServiceManager();
		webServiceManager.startWebService();
	}
}
