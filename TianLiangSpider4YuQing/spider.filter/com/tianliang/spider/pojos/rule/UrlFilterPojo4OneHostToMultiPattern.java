package com.tianliang.spider.pojos.rule;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tianliang.spider.iface.rule.IExtractorContentRule;
import com.tianliang.spider.impl.rule.FormatExtractorImpl;
import com.tianliang.spider.pojos.enumeration.CrawlEngineEnum;
import com.tianliang.spider.pojos.parser.MatchResultKeyValue;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.vaolan.extkey.utils.UrlOperatorUtil;

/**
 * 当一个host对应多个url过滤器时用该pojo类
 * 
 * @author zel
 * 
 */
public class UrlFilterPojo4OneHostToMultiPattern implements Serializable {

	private String host;
	private String name;

	private List<String> regexList;
	private List<MatchPatternPojo> patternList;
	private boolean is_host_match_wildcard;
	private String root_host;
	
	// 以不同的host下的url为基本的匹配单位
	// private List<MatchContentRulsListPojo> extContentRuleList;

	public UrlFilterPojo4OneHostToMultiPattern(String name, String host) {
		this.host = host;
		this.name = name;
		this.regexList = new LinkedList<String>();
		this.patternList = new LinkedList<MatchPatternPojo>();
	}
	
	public void addPattern(String regex, String crawlEngine) {
		this.regexList.add(regex);
		// 将regex加上一个(),用来作为后边的group(1)操作
		Pattern pattern = Pattern.compile(StaticValue.separator_left_bracket
				+ regex + StaticValue.separator_right_bracket);
		MatchPatternPojo matchPatternPojo = new MatchPatternPojo(pattern,
				crawlEngine);
		this.patternList.add(matchPatternPojo);
	}

	public boolean isMatch(String url) {
		if (StringOperatorUtil.isBlank(url)) {
			return false;
		}
		for (MatchPatternPojo matchPatternPojo : patternList) {
			Matcher matcher = matchPatternPojo.getPattern().matcher(url);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 从url中匹配出正则中匹配的值来，主要是能去掉一些无用参数
	 * 
	 * @param url
	 * @return
	 */
	public String getMatchValue(String url) {
		if (StringOperatorUtil.isBlank(url)) {
			return null;
		}
		for (MatchPatternPojo matchPatternPojo : patternList) {
			Matcher matcher = matchPatternPojo.getPattern().matcher(url);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	// 得到匹配上的engine pojo
	public MatchCrawlPojo getMatchCrawlPojo(String url) {
		if (StringOperatorUtil.isBlank(url)) {
			return null;
		}
		MatchCrawlPojo matchCrawlPojo = null;
		for (MatchPatternPojo matchPatternPojo : patternList) {
			// System.out.println(pattern.toString());
			Matcher matcher = matchPatternPojo.getPattern().matcher(url);
			if (matcher.find()) {
				if (matchCrawlPojo != null) {
					if (matchPatternPojo.getCrawlEngineEnum() == CrawlEngineEnum.Phantomjs) {
						matchCrawlPojo
								.setCrawlEngineEnum(CrawlEngineEnum.Phantomjs);
					}
				} else {
					matchCrawlPojo = new MatchCrawlPojo(true, matcher.group(1),
							matchPatternPojo.getCrawlEngineEnum());
				}
			}
		}
		return matchCrawlPojo;
	}

	/**
	 * 得到某个url的设定的抓取引擎的结果pojo类
	 * 
	 * @author zel
	 * 
	 */
	public static class MatchCrawlPojo implements Serializable {
		public MatchCrawlPojo(boolean isMatch, String format_url,
				CrawlEngineEnum crawlEngineEnum) {
			this.isMatch = isMatch;
			this.format_url = format_url;
			this.crawlEngineEnum = crawlEngineEnum;
		}

		private boolean isMatch;
		// 通过正则格式化好后的url值，原始的url就没什么意义了
		private String format_url;

		public String getFormat_url() {
			return format_url;
		}

		public void setFormat_url(String format_url) {
			this.format_url = format_url;
		}

		public boolean isMatch() {
			return isMatch;
		}

		public void setMatch(boolean isMatch) {
			this.isMatch = isMatch;
		}

		private CrawlEngineEnum crawlEngineEnum;

		public CrawlEngineEnum getCrawlEngineEnum() {
			return crawlEngineEnum;
		}

		public void setCrawlEngineEnum(CrawlEngineEnum crawlEngineEnum) {
			this.crawlEngineEnum = crawlEngineEnum;
		}

	}

	/**
	 * 一次性传递多个规则串,针对一个host下的一个url的 isOver代表是否加截完毕
	 * 
	 * @param ruleLine
	 */
	public void addIExtContentRuleList(String ruleName, List<String> ruleList,
			boolean isOver) {
		if (StringOperatorUtil.isBlankCollection(ruleList)) {
			return;
		}
		List<IExtractorContentRule> temp_extContentRuleList = new LinkedList<IExtractorContentRule>();
		for (String ruleLine : ruleList) {
			IExtractorContentRule iRule = new FormatExtractorImpl(ruleLine);
			temp_extContentRuleList.add(iRule);
		}
		MatchContentRulsListPojo matchContentRulsListPojo = new MatchContentRulsListPojo();
		matchContentRulsListPojo.setRuleName(ruleName);
		matchContentRulsListPojo.setExtContentRuleList(temp_extContentRuleList);
		// 根据初始化的加载策略来对应关系
		for (MatchPatternPojo matchPatternPojo : this.patternList) {
			if (matchPatternPojo.getMatchContentRulsListPojo() == null) {
				matchPatternPojo
						.setMatchContentRulsListPojo(matchContentRulsListPojo);
				break;
			}
		}
	}

	// 解析出具体的字段内容
	public MatchContentPojo getMatchContentList(String url, String htmlsource) {
		if (StringOperatorUtil.isNotBlankCollection(this.patternList)) {
			// List<String> tempValueList = null;
			List<MatchResultKeyValue> resultKVlist = null;
			// 防止会有多个匹配的正则项，那个的匹配item最多，要哪个
			List<MatchResultKeyValue> finalResultList = null;
			List<MatchResultKeyValue> tempResultList = null;
			MatchContentPojo matchContentPojo = null;
			int match_rule_times = 0;
			
			for (MatchPatternPojo matchPatternPojo : this.patternList) {
				if (matchPatternPojo.isMatch(url)) {
					// 得到与之匹配的规则
					MatchContentRulsListPojo matchContentRulsListPojo = matchPatternPojo
							.getMatchContentRulsListPojo();
					// 这是以防为空的规则列表
					if (matchContentRulsListPojo == null) {
						continue;
					}
					List<IExtractorContentRule> iRuleList = matchContentRulsListPojo
							.getExtContentRuleList();
					tempResultList = new LinkedList<MatchResultKeyValue>();
					for (IExtractorContentRule iRule : iRuleList) {
						resultKVlist = iRule.getContent(htmlsource);
						if (StringOperatorUtil
								.isNotBlankCollection(resultKVlist)) {
							match_rule_times++;
							tempResultList.addAll(resultKVlist);
						} else {
							break;
						}
					}
					if (match_rule_times == iRuleList.size()
							&& StringOperatorUtil
									.isNotBlankCollection(tempResultList)) {
						if (finalResultList == null) {
							// 在为空时进行一次初始化，之后就不需要了!
							matchContentPojo = new MatchContentPojo();
							finalResultList = tempResultList;
							matchContentPojo
									.setRule_name(matchContentRulsListPojo
											.getRuleName());
						} else if (tempResultList.size() > finalResultList
								.size()) {
							finalResultList = tempResultList;
							matchContentPojo
									.setRule_name(matchContentRulsListPojo
											.getRuleName());
						}
						match_rule_times = 0;
						matchContentPojo.setNormal(true);
						matchContentPojo.setMatchContentList(finalResultList);
						// return finalResultList;
					} else {
						match_rule_times = 0;
						// finalResultList.clear();
					}
				}
			}
			return matchContentPojo;
		}
		return null;
	}

	/**
	 * 对象抽取结果的封装
	 * 
	 * @author zel
	 */
	public static class MatchContentPojo implements Serializable {
		@Override
		public String toString() {
			return "MatchContentPojo [isNormal=" + isNormal + ", rule_name="
					+ rule_name + ", matchContentList=" + matchContentList
					+ "]";
		}

		public MatchContentPojo() {

		}

		public MatchContentPojo(String rule_name,
				List<MatchResultKeyValue> matchContentList) {
			this.rule_name = rule_name;
			this.matchContentList = matchContentList;
		}

		// 解析是否正常
		private boolean isNormal;

		public boolean isNormal() {
			return isNormal;
		}

		public void setNormal(boolean isNormal) {
			this.isNormal = isNormal;
		}

		/**
		 * 匹配时候所属的标题
		 */
		private String rule_name;

		public String getRule_name() {
			return rule_name;
		}

		public void setRule_name(String rule_name) {
			this.rule_name = rule_name;
		}

		/**
		 * 匹配所属的内容
		 */
		private List<MatchResultKeyValue> matchContentList;

		public List<MatchResultKeyValue> getMatchContentList() {
			return matchContentList;
		}

		public void setMatchContentList(
				List<MatchResultKeyValue> matchContentList) {
			this.matchContentList = matchContentList;
		}
	}

	static class MatchContentRulsListPojo implements Serializable {
		private String ruleName;

		public String getRuleName() {
			return ruleName;
		}

		public void setRuleName(String ruleName) {
			this.ruleName = ruleName;
		}

		private List<IExtractorContentRule> extContentRuleList;

		public List<IExtractorContentRule> getExtContentRuleList() {
			return extContentRuleList;
		}

		public void setExtContentRuleList(
				List<IExtractorContentRule> extContentRuleList) {
			this.extContentRuleList = extContentRuleList;
		}

	}

	/**
	 * 匹配正则时会有该url相对应的抓取引擎，目前默认为httpclient或是phantomjs
	 * 现在将其设置为匹配和解析的中间类--2015-03-14
	 * 
	 * @author zel
	 * 
	 */
	public static class MatchPatternPojo {
		public MatchPatternPojo(Pattern pattern, String crawlEngine) {
			this.pattern = pattern;
			if (crawlEngine.toLowerCase().equals("phantomjs")) {
				this.crawlEngineEnum = CrawlEngineEnum.Phantomjs;
			} else if (crawlEngine.toLowerCase().equals("httpclient")) {
				this.crawlEngineEnum = CrawlEngineEnum.HttpClient;
			} else {
				try {
					throw new Exception("crawl engine is error,please check!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 该url是否适配该pattern
		public boolean isMatch(String url) {
			Matcher matcher = this.getPattern().matcher(url);
			return matcher.find();
		}

		private Pattern pattern;
		private CrawlEngineEnum crawlEngineEnum;
		// 与该规则对应的匹配的字段匹配对应关系
		private MatchContentRulsListPojo matchContentRulsListPojo;

		public MatchContentRulsListPojo getMatchContentRulsListPojo() {
			return matchContentRulsListPojo;
		}

		public void setMatchContentRulsListPojo(
				MatchContentRulsListPojo matchContentRulsListPojo) {
			this.matchContentRulsListPojo = matchContentRulsListPojo;
		}

		public CrawlEngineEnum getCrawlEngineEnum() {
			return crawlEngineEnum;
		}

		public void setCrawlEngineEnum(CrawlEngineEnum crawlEngineEnum) {
			this.crawlEngineEnum = crawlEngineEnum;
		}

		public Pattern getPattern() {
			return pattern;
		}

		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
