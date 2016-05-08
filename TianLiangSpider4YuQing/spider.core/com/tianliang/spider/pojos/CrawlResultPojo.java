package com.tianliang.spider.pojos;

import com.tianliang.spider.iface.rule.IResultPojo;

/**
 * 抓取结果的返回对象
 * 
 * @author zel
 * 
 */
public class CrawlResultPojo extends IResultPojo{
	@Override
	public String toString() {
		return "CrawlResultPojo [isNormal=" + isNormal + ", desc=" + desc
				+ ", content=" + htmlSource + "]";
	}
	
}
