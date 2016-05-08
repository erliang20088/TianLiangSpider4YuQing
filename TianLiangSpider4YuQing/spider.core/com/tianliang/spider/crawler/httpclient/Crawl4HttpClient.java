package com.tianliang.spider.crawler.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.util.EntityUtils;

import com.tianliang.spider.manager.crawler.HttpClientPojoManager;
import com.tianliang.spider.manager.crawler.HttpClientPojoManager.HttpClientPojo;
import com.tianliang.spider.pojos.ContentPojo;
import com.tianliang.spider.pojos.HttpRequestPojo;
import com.tianliang.spider.pojos.enumeration.HttpRequestMethod;
import com.tianliang.spider.utils.ObjectAndByteArrayConvertUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.tianliang.spider.utils.charset.EncodeUtil;

/**
 * 用httpclient实现的下载器
 * 
 * @author zel
 * 
 */
public class Crawl4HttpClient {
	public static String crawlWebPage(HttpRequestPojo requestPojo) {
		CloseableHttpResponse response = null;
		try {
			RequestBuilder rb = null;
			if (requestPojo.isGetMethod()) {
				rb = RequestBuilder.get().setUri(
						URI.create(requestPojo.getUrl()));
			} else {
				rb = RequestBuilder.post()
						.setUri(new URI(requestPojo.getUrl()));
			}
			Map<String, Object> map = null;
			if ((map = requestPojo.getHeaderMap()) != null) {
				for (Entry<String, Object> entry : map.entrySet()) {
					rb.addHeader(entry.getKey(), entry.getValue().toString());
				}
			}
			if ((map = requestPojo.getParasMap()) != null) {
				for (Entry<String, Object> entry : map.entrySet()) {
					rb.addParameter(entry.getKey(), entry.getValue().toString());
				}
			}
			// 将form data编码完成后放入entity中
			if (requestPojo.getFormEntity() != null) {
				rb.setEntity(requestPojo.getFormEntity());
			}

			// 查看是否设置代理
			HttpUriRequest requestAll = null;
			HttpClientPojo httpClientPojo = HttpClientPojoManager
					.getHttpClientPojo();
			// 执行请求
			if (SystemParas.proxy_open) {
				rb.setConfig(httpClientPojo.getRequestConfig());
				requestAll = rb.build();
				System.out.println(httpClientPojo.getProxyPojo());
				response = httpClientPojo.getHttpClient().execute(requestAll);
			} else {
				rb.setConfig(HttpClientPojoManager.default_requestConfig);
				requestAll = rb.build();
				response = httpClientPojo.getHttpClient().execute(requestAll);
			}
			// return parserResponse(response);
			return parserResponse_v2(response);
		} catch (SocketTimeoutException timeOutException) {
			// 此种情况将会认为可能是代理异常失效，但暂不处理这种异常对代理替换策略的影响的!
			timeOutException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String crawlWebPage4ES(HttpRequestPojo requestPojo,
			String jsonEntity) {
		CloseableHttpResponse response = null;
		try {
			RequestBuilder rb = null;
			if (requestPojo.isGetMethod()) {
				rb = RequestBuilder.get().setUri(
						URI.create(requestPojo.getUrl()));
			} else {
				rb = RequestBuilder.post()
						.setUri(new URI(requestPojo.getUrl()));
			}
			StringEntity stringEntity = new StringEntity(jsonEntity,
					StaticValue.default_encoding);
			// StringEntity stringEntity = new StringEntity(filename);
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			rb.setEntity(stringEntity);

			Map<String, Object> map = null;
			if ((map = requestPojo.getHeaderMap()) != null) {
				for (Entry<String, Object> entry : map.entrySet()) {
					rb.addHeader(entry.getKey(), entry.getValue().toString());
				}
			}
			if ((map = requestPojo.getParasMap()) != null) {
				for (Entry<String, Object> entry : map.entrySet()) {
					rb.addParameter(entry.getKey(), entry.getValue().toString());
				}
			}

			// 查看是否设置代理
			// 暂不用
			HttpUriRequest requestAll = null;
			HttpClientPojo httpClientPojo = HttpClientPojoManager
					.getHttpClientPojo();
			// 执行请求
			rb.setConfig(HttpClientPojoManager.default_requestConfig);
			requestAll = rb.build();
			response = httpClientPojo.getHttpClient().execute(requestAll);
			return parserResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String parserResponse(CloseableHttpResponse response) {
		try {
			HttpEntity entity = response.getEntity();
			String content_type_encoding = EncodeUtil
					.getCharsetByMetadata(entity.getContentType().toString());
			byte[] byteArray = ObjectAndByteArrayConvertUtil
					.getByteArrayOutputStream(entity.getContent());
			if (StringOperatorUtil.isNotBlank(content_type_encoding)) {
				String htmlSource = new String(byteArray, content_type_encoding);
				return htmlSource;
			} else {
				// 将页面中的meta中的charset拿到作为charset
				String htmlSource = new String(byteArray,
						StaticValue.default_encoding);
				String page_source_charset = EncodeUtil
						.getPageSourceCharset(htmlSource);
				if (StringOperatorUtil.isBlank(page_source_charset)) {
					return htmlSource;
				} else if (!StaticValue.default_encoding
						.equals(page_source_charset)) {
					return new String(byteArray, page_source_charset);
				}
				return htmlSource;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

	public static EncodeUtil encodeUtil = new EncodeUtil();

	public static String parserResponse_v2(CloseableHttpResponse response) {
		try {
			HttpEntity entity = response.getEntity();
			InputStream is = null;
			// System.out.println(entity.toString());
			is = entity.getContent();
			byte[] byteArray = ObjectAndByteArrayConvertUtil
					.getByteArrayOutputStream(is);
			// System.out.println("byte_array length---" + byteArray.length);
			ContentPojo contentPojo = encodeUtil.getWebPageCharset(byteArray,
					entity.getContentType().toString());
			// System.out.println("---------" + contentPojo.getCharset());
//			System.out.println("===" + entity.getContentType().toString());
			if (contentPojo != null) {
				return new String(byteArray, contentPojo.getCharset());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		HttpRequestPojo requestPojo = new HttpRequestPojo();
		requestPojo.setRequestMethod(HttpRequestMethod.GET);

		// String url = "http://www.oscca.gov.cn/";
		// String url = "http://www.baidu.com/";
		String url = "http://news.sina.com/";
		Map<String, Object> headerMap = new HashMap<String, Object>();
		Map<String, Object> parasMap = new HashMap<String, Object>();
		// Map<String, String> formNameValueMap = new HashMap<String, String>();

		requestPojo.setUrl(url);
		requestPojo.setHeaderMap(headerMap);
		requestPojo.setParasMap(parasMap);
		// form name value是为非iso-8859-1编码的value pair而添加,当然是指存在中文的情况
		// requestPojo.setFormNameValePairMap(formNameValueMap,
		// CharsetEnum.UTF8);

		String source = crawlWebPage(requestPojo);

		// System.out.println(source);

		System.out.println("done!");
	}
}
