package com.tianliang.spider.manager.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.simple.JSONObject;

import com.tianliang.spider.pojos.rule.ExtContentRuleConfigManager;
import com.tianliang.spider.pojos.rule.UrlFilterPojo4OneHostToMultiPattern;
import com.tianliang.spider.pojos.rule.UrlFilterPojo4OneHostToMultiPattern.MatchContentPojo;
import com.tianliang.spider.pojos.rule.UrlFilterPojo4OneHostToMultiPattern.MatchCrawlPojo;
import com.tianliang.spider.utils.JsonOperUtil;
import com.tianliang.spider.utils.ReadConfigUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.vaolan.extkey.utils.UrlOperatorUtil;

/**
 * 专为采集各种简单网页而设置的url规则管理器
 * 
 * @author zel
 * 
 */
public class ExtractorContentManager4SimpleCrawler implements Serializable {
	public Map<String, UrlFilterPojo4OneHostToMultiPattern> urlFilterMap4Finance = null;
	private static final Lock lock_single_instance = new ReentrantLock();
	// 用来存储其所有的要进行模糊匹配的host集合，也是要加入lock保其线程安全的
	private Set<String> all_wildcard_host_set = new HashSet<String>();

	/**
	 * isSelf代表该rule配置文件是自带，还是外部传入的
	 * 
	 * @param rule_string
	 *            UrlFilterPojo4OneHostToMultiPattern.java * @param isSelf
	 */
	public ExtractorContentManager4SimpleCrawler(String rule_string,
			boolean isSelf) {
		resetRuleByGlobalLock(rule_string, isSelf);
	}

	public void resetRule(String rule_string, boolean isSelf) {
		// 每次用一个新的来存放最新的
		urlFilterMap4Finance = new HashMap<String, UrlFilterPojo4OneHostToMultiPattern>();

		String[] lineArray = null;
		String[] temp_array = null;
		String host = null;
		String name = null;
		String regex = null;
		String crawlEngine = null;
		String option_json_string = null;
		boolean is_host_match_wildcard = false;

		if (isSelf) {
			ReadConfigUtil readConfigUtil = new ReadConfigUtil(rule_string,
					false);
			rule_string = readConfigUtil.getLineConfigTxt();
		}
		if (StringOperatorUtil.isNotBlank(rule_string)) {
			lineArray = rule_string.split(StaticValue.separator_next_line);
			List<String> ruleList = new LinkedList<String>();
			String last_host = null;
			String last_rule_name = null;
			for (String line : lineArray) {
				// length==3时，是构造url的过滤规则部分
				if ((temp_array = line.split(StaticValue.separator_tab)).length >= 4) {
					name = temp_array[0].trim();
					host = temp_array[1].trim();
					regex = temp_array[2].trim();
					crawlEngine = temp_array[3].trim();
					is_host_match_wildcard = false;

					// 解析每个匹配项的可选项，用json串来表示
					if (temp_array.length > 4) {
						option_json_string = temp_array[4];
						// System.out.println("option_json_string="+option_json_string);
						JSONObject json_obj = JsonOperUtil
								.getJsonObject(option_json_string);
						if (json_obj != null) {
							Object obj = json_obj.get("config_option");
							if (obj != null) {
								json_obj = JsonOperUtil.getJsonObject(obj
										.toString());
								if (json_obj != null) {
									obj = json_obj.get("host_match_wildcard");
									if (obj != null) {
										String config_host_match_is_wildcard = obj
												.toString();
										is_host_match_wildcard = Boolean
												.parseBoolean(config_host_match_is_wildcard);
									}
								}
							}
						}
						if (is_host_match_wildcard) {
							host = UrlOperatorUtil.getDomain("http://" + host);
							all_wildcard_host_set.add(host);
						}
						// System.out.println("is_host_match_wildcard="+is_host_match_wildcard);
					}

					if (urlFilterMap4Finance.containsKey(host)) {
						// 说明一个host对应了多个pattern
						urlFilterMap4Finance.get(host).addPattern(regex,
								crawlEngine);
					} else {
						UrlFilterPojo4OneHostToMultiPattern urlFilterPojo4MultiPattern = new UrlFilterPojo4OneHostToMultiPattern(
								name, host);
						urlFilterPojo4MultiPattern.addPattern(regex,
								crawlEngine);
						urlFilterMap4Finance.put(host,
								urlFilterPojo4MultiPattern);
					}

					// 将内容抽取的规则部分加入对象中
					if (StringOperatorUtil.isNotBlankCollection(ruleList)) {
						urlFilterMap4Finance.get(last_host)
								.addIExtContentRuleList(last_rule_name,
										ruleList, false);
						ruleList.clear();
					}
				} else {
					last_host = host;
					last_rule_name = name;
					// 当不等于指定列时，即认为是上一个规则配置的内容抽取部分
					if (StringOperatorUtil.isNotBlank(line)) {
						ruleList.add(line.trim());
					}
				}
			}
			// 对rule list扫尾,即最后一波的规则不在上边的for循环中
			if (StringOperatorUtil.isNotBlankCollection(ruleList)) {
				urlFilterMap4Finance.get(last_host).addIExtContentRuleList(
						last_rule_name, ruleList, true);
				ruleList.clear();
			}
		}
	}

	public boolean isInHostFilter(String host) {
		return urlFilterMap4Finance.containsKey(host);
	}

	public boolean isMatch4AllPattern(String host, String url) {
		UrlFilterPojo4OneHostToMultiPattern urlFilterPojo4OneHostToMultiPattern = urlFilterMap4Finance
				.get(host);
		if (urlFilterPojo4OneHostToMultiPattern == null) {
			return false;
		}
		return urlFilterPojo4OneHostToMultiPattern.isMatch(url);
	}

	public String getMatchValue(String host, String url) {
		UrlFilterPojo4OneHostToMultiPattern urlFilterPojo4OneHostToMultiPattern = urlFilterMap4Finance
				.get(host);
		if (urlFilterPojo4OneHostToMultiPattern == null) {
			return null;
		}
		return urlFilterPojo4OneHostToMultiPattern.getMatchValue(url);
	}

	public MatchCrawlPojo getMatchCrawlPojo(String host, String url) {
		UrlFilterPojo4OneHostToMultiPattern urlFilterPojo4OneHostToMultiPattern = urlFilterMap4Finance
				.get(host);
		if (urlFilterPojo4OneHostToMultiPattern == null) {
			return null;
		}
		return urlFilterPojo4OneHostToMultiPattern.getMatchCrawlPojo(url);
	}

	public MatchCrawlPojo getMatchCrawlPojo(String url) {
		String host = UrlOperatorUtil.getHost(url);
		if (StringOperatorUtil.isNotBlank(host)) {
			UrlFilterPojo4OneHostToMultiPattern urlFilterPojo4OneHostToMultiPattern = urlFilterMap4Finance
					.get(host);
			if (urlFilterPojo4OneHostToMultiPattern == null) {
				//如果为null,再判断其是否要取其根host来作为二次判断
				host=UrlOperatorUtil.getDomain("http://"+host);
				if(this.all_wildcard_host_set.contains(host)){
					urlFilterPojo4OneHostToMultiPattern = urlFilterMap4Finance
							.get(host);
					if(urlFilterPojo4OneHostToMultiPattern==null){
						return null;
					}
				}else{
					return null;					
				}
			}
			return urlFilterPojo4OneHostToMultiPattern.getMatchCrawlPojo(url);
		}
		return null;
	}

	public MatchCrawlPojo getMatchCrawlPojoByGlobalLock(String url) {
		try {
			lock_single_instance.lock();
			return getMatchCrawlPojo(url);
		} finally {
			lock_single_instance.unlock();
		}
	}

	public void resetRuleByGlobalLock(String rule_string, boolean isSelf) {
		try {
			lock_single_instance.lock();
			this.resetRule(rule_string, isSelf);
		} finally {
			lock_single_instance.unlock();
		}
	}

	public MatchContentPojo getMatchContentList(String url, String host,
			String htmlSource) {
		UrlFilterPojo4OneHostToMultiPattern urlFilterPojo4OneHostToMultiPattern = urlFilterMap4Finance
				.get(host);
		if (urlFilterPojo4OneHostToMultiPattern == null) {
			host=UrlOperatorUtil.getDomain("http://"+host);
			if(this.all_wildcard_host_set.contains(host)){
				urlFilterPojo4OneHostToMultiPattern = urlFilterMap4Finance
						.get(host);
				if(urlFilterPojo4OneHostToMultiPattern==null){
					return null;
				}
			}else{
				return null;				
			}
		}
		return urlFilterPojo4OneHostToMultiPattern.getMatchContentList(url,
				htmlSource);
	}

	public Set<String> getAll_wildcard_host_set() {
		return all_wildcard_host_set;
	}

	public void setAll_wildcard_host_set(Set<String> all_wildcard_host_set) {
		this.all_wildcard_host_set = all_wildcard_host_set;
	}

	public static void main(String[] args) {
		// String rule_String = "crawl_content_extractor_regex.adapter";
		ExtContentRuleConfigManager extContentRuleConfigManager = new ExtContentRuleConfigManager(
				SystemParas.ext_content_rule_config_fs,
				SystemParas.ext_content_rule_config_root_dir);
		String rule_String = extContentRuleConfigManager.getRuleString();
		// String rule_String = JedisOperatorUtil
		// .getObj(StaticValue.ext_content_rule_key);
		ExtractorContentManager4SimpleCrawler extractorContentManager4SimpleCrawler = new ExtractorContentManager4SimpleCrawler(
				rule_String, false);

		// String host = "quote.eastmoney.com";
		// String host = "vip.stock.finance.sina.com.cn";
		// String host = "quote.eastmoney.com";
		// String host = "stock.finance.sina.com.cn";
		// String host = "stock.finance.sina.com.cn";
		// String host = "hotels.ctrip.com";

		// String url =
		// "http://quote.eastmoney.com/agquote2014test.html?rt=0.963860091753304";
		// String url = "http://quote.eastmoney.com/us/CPHI.html";
		// String url =
		// "http://quote.eastmoney.com/search.html?t=&stockcode=519606";
		// String url = "http://quote.eastmoney.com/sz150118.html";
		// String url =
		// "http://stock.finance.sina.com.cn/usstock/quotes/BIDU.html";

		/**
		 * 餐饮匹配
		 */
		// String host = "www.meishij.net";
		// String host = "www.dianping.com";
		// String url = "http://www.meishij.net/%E9%AB%98%E8%89%AF%E5%A7%9C";
		// String url = "http://www.meishij.net/zuofa/naixiangfanqiezhong.html";
		// String url =
		// "http://www.meishij.net/shiliao/xiajirongyifankunmeijingshen8zhongshiwutishenkangpilao.html";
		// String url = "http://www.dianping.com/shop/6427802";
		// String url =
		// "http://www.dianping.com/shop/6427802/dish-%E6%89%8B%E5%88%87%E8%9D%B4%E8%9D%B6%E6%B3%89%E4%B9%B3%E7%BE%8A%E8%82%.";

		/**
		 * 教育匹配
		 */
		// String host = "www.shmeea.edu.cn";
		// String host = "www.51test.net";
		// String host = "edu.sina.com.cn";
		// String host = "www.exam8.com";
		// String host = "www.homeinns.com";
		// String host = "vacations.ctrip.com";
		// String host = "lvyou.baidu.com";
		// String host = "www.xiami.com";
		// String host = "tuan.ctrip.com";
		// String host = "www.1ting.com";
		String host = "news.sina.com.cn";
		// String host = "quote.eastmoney.com";
		// String host = "stock.finance.sina.com.cn";

		// String url =
		// "http://www.shmeea.edu.cn/node2/node118/node123/node388/node474/userobject1ai17951.html";
		// String url = "http://www.51test.net/show/4202921.html";
		// String url =
		// "http://edu.sina.com.cn/yyks/2014-11-07/1234442356.shtml";
		// String url = "http://cruise.ctrip.com/c/124.html";
		// String url = "http://lvyou.baidu.com/guangzhou/";
		// String url =
		// "http://vacations.ctrip.com/grouptravel/p2125686s1.html";
		// String url =
		// "http://www.xiami.com/collect/37309978a?spm=a1z1s.2943601.6856189.7.ecKksB";
		// String url = "http://www.1ting.com/player/18/player_333630.html";
		String url = "http://news.sina.com.cn/c/nd/2015-12-26/doc-ifxmxxst0542666.shtml";

		// System.out.println(extractorContentManager4SimpleCrawler
		// .isMatch4AllPattern(host, url));

		System.out.println(extractorContentManager4SimpleCrawler.getMatchValue(
				host, url));

		// System.out.println(extractorContentManager4SimpleCrawler
		// .getMatchContentList(host, htmlSource));
		// MatchCrawlPojo matchCrawlPojo = extractorContentManager4SimpleCrawler
		// .getMatchCrawlPojo(host, url);
		// System.out.println(matchCrawlPojo.getCrawlEngineEnum());
		// System.out.println(htmlSource);

//		Set<Entry<String, UrlFilterPojo4OneHostToMultiPattern>> entrySet = extractorContentManager4SimpleCrawler.urlFilterMap4Finance.entrySet();
//		for(Entry<String, UrlFilterPojo4OneHostToMultiPattern> temp:entrySet){
//			System.out.println(temp.getKey()+"  "+temp.getValue().getHost());
//		}
		
		System.out.println("done!");
	}
}
