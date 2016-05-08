package com.tianliang.spider.utils;

/**
 * 系统断言类,方便引用,不使用junit之类的assert
 * 
 * @author zel
 *  
 */
public class SystemAssert {
	public static void assertNotNull(Object obj) {
		if (obj == null) {
			try {
				throw new Exception("object should not be null,please check");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}
