package com.tianliang.spider.iface.rule;

import java.util.List;

import com.tianliang.spider.pojos.parser.MatchResultKeyValue;

public interface IExtractorContentRule {
	public List<MatchResultKeyValue> getContent(String source);
}
