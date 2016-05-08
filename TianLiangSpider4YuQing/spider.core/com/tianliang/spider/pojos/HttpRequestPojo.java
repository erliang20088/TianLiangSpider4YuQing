package com.tianliang.spider.pojos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.tianliang.spider.pojos.enumeration.CharsetEnum;
import com.tianliang.spider.pojos.enumeration.HttpRequestMethod;
import com.tianliang.spider.utils.StaticValue;

/**
 * 一个url请求所有的参数封装，主要是url,get参数、post参数、header参数，请求方式等
 * 
 * @author zel
 * 
 */
public class HttpRequestPojo {
	// 请求的方法
	private HttpRequestMethod requestMethod = HttpRequestMethod.GET;
	private String url;
	private Map<String, Object> headerMap;
	private ProxyPojo proxyPojo;
	// 传递form表单参数
	private UrlEncodedFormEntity formEntity;

	// 仅添加一次,暂不支持添加多次map值对.
	// 解决post中传递为utf-8编码的中文value
	public void setFormNameValePairMap(Map<String, String> formNameValePairMap,
			CharsetEnum charsetEnum) {
		if (!formNameValePairMap.isEmpty()) {
			// formNameValePairMap
			List<NameValuePair> formNameValueParams = new ArrayList<NameValuePair>();
			Set<String> keySet = formNameValePairMap.keySet();
			for (String key : keySet) {
				formNameValueParams.add(new BasicNameValuePair(key,
						formNameValePairMap.get(key)));
			}
			try {
				this.formEntity = new UrlEncodedFormEntity(formNameValueParams,
						charsetEnum.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public UrlEncodedFormEntity getFormEntity() {
		return formEntity;
	}

	public void setFormEntity(UrlEncodedFormEntity formEntity) {
		this.formEntity = formEntity;
	}

	public ProxyPojo getProxyPojo() {
		return proxyPojo;
	}

	public void setProxyPojo(ProxyPojo proxyPojo) {
		this.proxyPojo = proxyPojo;
	}

	public Map<String, Object> getHeaderMap() {
		return headerMap;
	}

	public HttpRequestPojo(String url, HttpRequestMethod requestMethod) {
		this.url = url;
		this.requestMethod = requestMethod;
	}

	public HttpRequestMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(HttpRequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setHeaderMap(Map<String, Object> headerMap) {
		this.headerMap = headerMap;
	}

	public Map<String, Object> getParasMap() {
		return parasMap;
	}

	public void setParasMap(Map<String, Object> parasMap) {
		this.parasMap = parasMap;
	}

	private Map<String, Object> parasMap;

	public HttpRequestPojo() {
		this.requestMethod = HttpRequestMethod.GET;
	}

	public HttpRequestPojo(String url) {
		this.url = url;
	}

	public void addHeaderMap(String key, Object value) {
		this.headerMap.put(key, value);
	}

	public void addParasMap(String key, Object value) {
		this.parasMap.put(key, value);
	}

	public boolean isPostMethod() {
		return this.requestMethod == HttpRequestMethod.POST ? true : false;
	}

	public boolean isGetMethod() {
		return this.requestMethod == HttpRequestMethod.GET ? true : false;
	}

}
