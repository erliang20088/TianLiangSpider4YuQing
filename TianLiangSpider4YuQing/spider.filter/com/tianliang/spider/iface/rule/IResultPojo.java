package com.tianliang.spider.iface.rule;

import java.io.Serializable;
import java.util.Set;

import com.tianliang.spider.pojos.enumeration.CrawlEngineEnum;
import com.zel.es.pojos.index.CrawlData4PortalSite;
import com.zel.spider.pojos.CrawlTaskPojo;

/**
 * result pojo
 * 
 * @author zel
 * 
 */
public abstract class IResultPojo implements Serializable {
	// 即是否真的返回数据了
	public boolean isNormal;
	public String desc;
	public String htmlSource;

	private String fromUrl;
	private CrawlEngineEnum crawlEngine;

	// 抓取结果解析后要获取的数据封装对象
	private CrawlData4PortalSite crawlData4PortalSite;
	private String source_title;
	// 产生的新的url列表
	public Set<String> newUrlSet;
	// 该两个参数与CrawlTaskPojo中的两个参数一致，主要是为了解决层次抓取种子url的问题
	private int current_depth;
	// 该解析出来的数据，属于那个过来的task pojo
	private CrawlTaskPojo ownToCrawlTaskPojo;

	// 标志是否跟正则匹配上,如果没匹配上说明匹配失败
	private boolean isMatchRegex;

	public boolean isMatchRegex() {
		return isMatchRegex;
	}

	public void setMatchRegex(boolean isMatchRegex) {
		this.isMatchRegex = isMatchRegex;
	}

	public String getSource_title() {
		return source_title;
	}

	public void setSource_title(String source_title) {
		this.source_title = source_title;
	}

	public CrawlTaskPojo getOwnToCrawlTaskPojo() {
		return ownToCrawlTaskPojo;
	}

	public void setOwnToCrawlTaskPojo(CrawlTaskPojo ownToCrawlTaskPojo) {
		this.ownToCrawlTaskPojo = ownToCrawlTaskPojo;
	}

	public int getCurrent_depth() {
		return current_depth;
	}

	public void setCurrent_depth(int current_depth) {
		this.current_depth = current_depth;
	}

	public Set<String> getNewUrlSet() {
		return newUrlSet;
	}

	public void setNewUrlSet(Set<String> newUrlSet) {
		this.newUrlSet = newUrlSet;
	}

	public String getHtmlSource() {
		return htmlSource;
	}

	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}

	public CrawlData4PortalSite getCrawlData4PortalSite() {
		return crawlData4PortalSite;
	}

	public void setCrawlData4PortalSite(
			CrawlData4PortalSite crawlData4PortalSite) {
		this.crawlData4PortalSite = crawlData4PortalSite;
	}

	public CrawlEngineEnum getCrawlEngine() {
		return crawlEngine;
	}

	public void setCrawlEngine(CrawlEngineEnum crawlEngine) {
		this.crawlEngine = crawlEngine;
	}

	public String getFromUrl() {
		return fromUrl;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}

	public boolean isNormal() {
		return isNormal;
	}

	public void setNormal(boolean isNormal) {
		this.isNormal = isNormal;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
