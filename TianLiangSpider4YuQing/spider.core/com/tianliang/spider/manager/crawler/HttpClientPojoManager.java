package com.tianliang.spider.manager.crawler;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.tianliang.spider.pojos.ProxyPojo;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.SystemParas;

/**
 * httpclient管理器，主要来管理httpclient实例
 * 
 * @author zel
 * 
 */
public class HttpClientPojoManager {
	public static MyLogger logger = new MyLogger(HttpClientPojoManager.class);
	// 链接池管理器，用于多线程调用
	public static PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
	public static int pool_current = 0;
	public static int pool_size = 0;
	public static List<HttpClientPojo> httpClientPojoList = null;
	public static HttpClientPojo httpClientPojo;
	public static RequestConfig default_requestConfig = null;

	public static void init() {
		httpClientPojoList = new LinkedList<HttpClientPojo>();
		if (SystemParas.proxy_open) {
			pool_size = SystemParas.proxyList.size();
			for (ProxyPojo proxyPojo : SystemParas.proxyList) {
				httpClientPojoList.add(new HttpClientPojo(proxyPojo));
			}
		}
		// 为了简单，无论如何都会创建一个httpclient
		connectionManager.setMaxTotal(10);
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(connectionManager).build();
		httpClientPojo = new HttpClientPojo(httpClient);

		// 取得默认的request config,用于非代理情况下的使用
		RequestConfig.Builder config_builder = RequestConfig.custom();
		config_builder.setSocketTimeout(SystemParas.http_connection_timeout);
		config_builder.setConnectTimeout(SystemParas.http_read_timeout);
		default_requestConfig = config_builder.build();
	}

	static {
		init();
	}

	public static void removeHttpClientPojo(HttpClientPojo clientPojo) {
		synchronized (HttpClientPojoManager.class) {
			httpClientPojoList.remove(clientPojo);
			pool_size--;
		}
	}

	public static HttpClientPojo getHttpClientPojo() {
		synchronized (HttpClientPojoManager.class) {
			if (SystemParas.proxy_open) {
				if (pool_current >= pool_size) {
					pool_current = 0;
					if (SystemParas.proxy_self) {
						// 这里是指也用一下当前的IP地址
						return httpClientPojo;
					}
				}
				HttpClientPojo temp_httpClientPojo = httpClientPojoList
						.get(pool_current);
				pool_current++;
				return temp_httpClientPojo;
			}
			// 如果没有合适的httpclientPojo,就用该管理器一直用的
			return httpClientPojo;
		}
	}

	/**
	 * http client封装,主要是为了加上proxy封装中的proxy host
	 * 
	 * @author zel
	 */
	public static class HttpClientPojo {
		@Override
		public String toString() {
			return "proxy=" + proxy + ",httpClient=" + httpClient + "";
		}

		private HttpHost proxy;
		private RequestConfig requestConfig;

		public RequestConfig getRequestConfig() {
			return requestConfig;
		}

		public void setRequestConfig(RequestConfig requestConfig) {
			this.requestConfig = requestConfig;
		}

		public HttpHost getProxy() {
			return proxy;
		}

		public void setProxy(HttpHost proxy) {
			this.proxy = proxy;
		}

		public CloseableHttpClient getHttpClient() {
			return httpClient;
		}

		public void setHttpClient(CloseableHttpClient httpClient) {
			this.httpClient = httpClient;
		}

		private CloseableHttpClient httpClient;
		private ProxyPojo proxyPojo;

		public ProxyPojo getProxyPojo() {
			return proxyPojo;
		}

		public void setProxyPojo(ProxyPojo proxyPojo) {
			this.proxyPojo = proxyPojo;
		}

		private HttpClientPojo(ProxyPojo proxyPojo) {
			if (proxyPojo == null || (!proxyPojo.isAuthEnable())) {
				this.proxy = null;
				this.httpClient = HttpClients.custom().build();
			} else {
				this.proxyPojo = proxyPojo;
				this.proxy = new HttpHost(proxyPojo.getIp(),
						proxyPojo.getPort());
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(new AuthScope(proxyPojo.getIp(),
						proxyPojo.getPort()), new UsernamePasswordCredentials(
						proxyPojo.getUsername(), proxyPojo.getPassword()));
				this.httpClient = HttpClients.custom()
						.setDefaultCredentialsProvider(credsProvider).build();
				
				RequestConfig.Builder config_builder = RequestConfig.custom();
				config_builder.setProxy(proxy);
				config_builder
						.setSocketTimeout(SystemParas.http_connection_timeout);
				config_builder.setConnectTimeout(SystemParas.http_read_timeout);
				this.requestConfig = config_builder.build();
			}
		}

		/**
		 * 空构造
		 */
		private HttpClientPojo() {
			this.httpClient = HttpClients.custom().build();
//			this.httpClient.get
		}

		public HttpClientPojo(CloseableHttpClient httpclient) {
			this.httpClient = httpclient;
		}

		/**
		 * 动态添加是否认证与代理，暂认为是只有代理时才会有认证
		 */
		
	}

	public static void main(String[] args) {
		// PoolingHttpClientConnectionManager cm = new
		// PoolingHttpClientConnectionManager();
		// cm.setMaxTotal(100);
	}

}
