package com.tianliang.spider.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 操作系统参数获取
 * 
 * @author zel
 * 
 */
public class OperatorSystemUtil {

	// 得到net操作类之InetAddress
	public static InetAddress getInetAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			System.out.println("unknown host!");
		}
		return null;
	}

	public static String getHostIp() {
		InetAddress netAddress = getInetAddress();
		String ip = netAddress.getHostAddress(); // get the ip address
		return ip;
	}

	public static String getHostName() {
		InetAddress netAddress = getInetAddress();
		String name = netAddress.getHostName(); // get the host address
		return name;
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Window")) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String ip = getHostIp();
		String name = getHostName();

		System.out.println("ip---" + ip);
		System.out.println("name---" + name);
	}
}
