package com.tianliang.spider.pojos;

import com.tianliang.spider.utils.SystemParas;

/**
 * 代理对象的pojo类
 * 
 * @author zel
 * 
 */
public class ProxyPojo {
	private String ip;
	/**
	 * 是不是需要输入代理的用户名和密码的标志位
	 */
	private boolean authEnable;

	public boolean isAuthEnable() {
		return authEnable;
	}

	// 用此代理时,失败的请求次数
	private int fail_count;

	public int getFail_count() {
		return fail_count;
	}

	public void setFail_count(int failCount) {
		fail_count = failCount;
	}

	@Override
	public String toString() {
		return "ProxyPojo [authEnable=" + authEnable + ", ip=" + ip
				+ ", password=" + password + ", port=" + port + ", username="
				+ username + "]";
	}

	public void setAuthEnable(boolean authEnable) {
		this.authEnable = authEnable;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private String username;
	private int port;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String password;

	// 无认证
	public ProxyPojo(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void reset(ProxyPojo proxyPojo) {
		if (proxyPojo == null) {
			return;
		}
		this.ip = proxyPojo.getIp();
		this.port = proxyPojo.getPort();
		this.username = proxyPojo.getUsername();
		this.password = proxyPojo.getPassword();
		// 有用户名和密码，说明需要验证
		this.authEnable = proxyPojo.isAuthEnable();
	}

	// 有认证
	public ProxyPojo(String ip, int port, String username, String password) {
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.password = password;
		// 有用户名和密码，说明需要验证
		this.authEnable = true;
	}
	
	public boolean isAbandon(){
		return this.getFail_count()>=SystemParas.proxy_fail_max_count;
	}

}
