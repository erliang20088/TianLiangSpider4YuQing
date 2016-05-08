package com.tianliang.spider.pojos.parser;

import com.tianliang.spider.iface.rule.IResultPojo;
import com.tianliang.spider.utils.StaticValue;

/**
 * 解析对象的返回结果
 * 
 * @author zel
 * 
 */
public class ParserResultPojo extends IResultPojo {

	private String ruleName;

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getSource_title() + StaticValue.separator_tab);
		sb.append(this.getCrawlEngine() + StaticValue.separator_tab);
		sb.append(ruleName + StaticValue.separator_tab);
		sb.append(this.getFromUrl() + StaticValue.separator_next_line);
		// sb.append(content);
		// sb.append("real data---"+this.getCrawlData4PortalSite());
		if (newUrlSet != null) {
			sb.append("new url set length " + this.newUrlSet.size()
					+ StaticValue.separator_next_line);
		} else {
			sb.append("new url set is empty " + StaticValue.separator_next_line);
		}
		sb.append("real parser data \n" + this.getCrawlData4PortalSite());

		return sb.toString();
	}

	public String toErrorString() {
		// return "ParserResultPojo [fromUrl=" + fromUrl + ", isNormal="
		// + isNormal + ", content=" + content + "]";
		StringBuilder sb = new StringBuilder();
		sb.append(this.getCrawlEngine() + StaticValue.separator_tab);
		// sb.append(this.getFileName() + StaticValue.separator_tab);
		sb.append(this.getFromUrl() + StaticValue.separator_tab);

		return sb.toString();
	}

}
