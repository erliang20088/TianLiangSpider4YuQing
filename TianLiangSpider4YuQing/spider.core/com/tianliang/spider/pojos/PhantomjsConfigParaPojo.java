package com.tianliang.spider.pojos;

import com.tianliang.spider.utils.StaticValue;

/**
 * 对应于config_phantomjs.json中的json字符串的每个key/value
 * 
 * @author zel
 * 
 */
public class PhantomjsConfigParaPojo {

	public PhantomjsConfigParaPojo() {
		this.outputEncoding = StaticValue.output_encoding_default;
		this.scriptEncoding = StaticValue.script_encoding_default;
	}

	// 在构造方法中添加一些必要的默认值
	public PhantomjsConfigParaPojo(ProxyPojo proxyPojo) {
		this();
		this.proxyPojo = proxyPojo;
	}

	private ProxyPojo proxyPojo;

	public ProxyPojo getProxyPojo() {
		return proxyPojo;
	}

	public void setProxyPojo(ProxyPojo proxyPojo) {
		this.proxyPojo = proxyPojo;
	}

	// phantomjs 输出编码
	private String outputEncoding;
	// js和所涉及到的数据的编码
	private String scriptEncoding;

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	public String getScriptEncoding() {
		return scriptEncoding;
	}

	public void setScriptEncoding(String scriptEncoding) {
		this.scriptEncoding = scriptEncoding;
	}

	@Override
	public String toString() {
		// 封装成json格式
		StringBuilder sb = new StringBuilder();

		sb.append("{\n");

		sb.append("\"outputEncoding\":\"" + this.outputEncoding + "\",\n");

		if (proxyPojo != null) {
			sb.append("\"proxy\":\"" + this.proxyPojo.getIp() + ":"
					+ this.proxyPojo.getPort() + "\",\n");
			if (proxyPojo.isAuthEnable()) {
				sb.append("\"proxyAuth\":\"" + this.proxyPojo.getUsername()
						+ ":" + this.proxyPojo.getPassword() + "\",\n");
			}
		}
		sb.append("\"scriptEncoding\":\"" + this.scriptEncoding + "\"");

		sb.append("\n}");

		return sb.toString();
	}

	public static void main(String[] args) {
		ProxyPojo proxy=new ProxyPojo("127.0.0.1",8000,"zel","zhouking");
		
		PhantomjsConfigParaPojo phantomjsConfigParaPojo = new PhantomjsConfigParaPojo(proxy);

		System.out.println(phantomjsConfigParaPojo.toString());
	}
}
