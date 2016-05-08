package com.tianliang.spider.impl.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.tianliang.spider.iface.rule.IExtractorContentRule;
import com.tianliang.spider.manager.rule.ExtractorContentManager4SimpleCrawler;
import com.tianliang.spider.pojos.parser.MatchResultKeyValue;
import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.vaolan.extkey.utils.RegexParserUtil;
import com.vaolan.parser.JsoupHtmlParser;
import com.vaolan.parser.pojo.JsoupSelectItemPojo;
import com.vaolan.status.DataFormatStatus;

/**
 * jsoup格式的时候的规则提取
 * 
 * @author zel
 * 
 */
public class FormatExtractorImpl implements IExtractorContentRule, Serializable {
	// 选择器集合
	private List<String> selectorList;
	// 默认是要纯文本
	private DataFormatStatus dataFormatStatus = DataFormatStatus.CleanTxt;

	// 是不是要取某些属性值,如果要则在此配置属性名称，如title,href等,暂定为单一元素
	private String attr;

	@Override
	public String toString() {
		return "FormatExtractorImpl [selectorList=" + selectorList
				+ ", regexMatchItemPojoList=" + regexMatchItemPojoList + "]";
	}

	// RegexParserUtil工具类集合
	private List<RegexMatchItemPojo> regexMatchItemPojoList;
	private String fieldKey = null;

	public FormatExtractorImpl(String patternLine) {
		this.selectorList = new ArrayList<String>();
		regexMatchItemPojoList = new ArrayList<RegexMatchItemPojo>();
		
		if (StringOperatorUtil.isNotBlank(patternLine)) {
			String[] col_array = null;
			if ((col_array = patternLine.split(StaticValue.separator_tab)).length != 2) {
				System.out.println("规则库存在不合理条目，请检查! 将跳过该规则匹配");
				return;
			}

			this.fieldKey = col_array[0];
			patternLine = col_array[1];

			// 取得外围最大块分隔数组
			String[] col_array_split_big = patternLine
					.split(StaticValue.rule_file_split_big);
			for (String element_block : col_array_split_big) {
				element_block = parseJsoupLine(element_block);
				
				// 这个是jsoup直接搞不定时候的正则辅助搞定
				String[] col_array_split_regex = element_block
						.split(StaticValue.rule_file_split_regex);
				// 如果包括#=>#则是前边jsoup格式，后边正则格式,此种类型元素如果层次出现，则必须是最后一个元素
				if (col_array_split_regex.length > 1) {
					// 那必须是由两部分构成，第一部分是jsoup部分,则将其封装进selectorList
					this.selectorList.add(col_array_split_regex[0]);
					
					String[] col_array_split_union = col_array_split_regex[1]
							.split(StaticValue.rule_file_split_union);
					// 得到有几个并行的正则表达式，都是以()为一个单元,每个单元中包括3个元素，正则起始部分-正则中间所要获取的内容部分-正则的结束部分
					for (String union_ele : col_array_split_union) {
						union_ele = union_ele.substring(1,
								union_ele.length() - 1);
						String[] split_small_array = union_ele
								.split(StaticValue.rule_file_split_small);
						split_small_array[0] = split_small_array[0].trim();
						split_small_array[1] = split_small_array[1].trim();
						split_small_array[2] = split_small_array[2].trim();

						String defaultValue = null;
						if (split_small_array.length == 4) {
							defaultValue = split_small_array[3].trim();
						}
						// System.out.println(split_small_array[0]);
						// System.out.println(split_small_array[1]);
						// System.out.println(split_small_array[2]);

						RegexParserUtil regexUtil = new RegexParserUtil(
								split_small_array[0], split_small_array[2],
								split_small_array[1],true);
						RegexMatchItemPojo regexMatchItemPojo = new RegexMatchItemPojo(
								regexUtil, defaultValue, this.fieldKey);
						this.regexMatchItemPojoList.add(regexMatchItemPojo);
					}
				} else {
					// 此种为纯jsoup格式,直接顺序加入
					this.selectorList.add(element_block);
				}
			}
		}
	}

	private String parseJsoupLine(String element_block) {
		// 做是否有#paras#做参数设定
		String[] paras_array = element_block
				.split(StaticValue.rule_file_split_paras);
		if (paras_array.length == 1) {
			element_block = paras_array[0];
		} else if (paras_array.length == 2) {
			element_block = paras_array[0].trim();
			String para_string = paras_array[1].trim();
			paras_array = para_string.split(StaticValue.separator_dot);
			if (paras_array.length == 1) {
				// 说明只给了DataFormatStatus值
				if (paras_array[0].equals("html")) {
					this.dataFormatStatus = DataFormatStatus.TagAllContent;
				}
			} else if (paras_array.length == 2) {
				if (paras_array[0].equals("html")) {
					this.dataFormatStatus = DataFormatStatus.TagAllContent;
				}
				String attr = paras_array[1];
				if (StringOperatorUtil.isNotBlank(attr)) {
					this.attr = attr;
				}
			} else {
				try {
					throw new Exception("paras参数在一个规则块中，不能超过两个定义!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				throw new Exception("paras参数在一个规则块中，不能超过两个定义!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return element_block;
	}

	@Override
	public List<MatchResultKeyValue> getContent(String source) {
		// 先过滤selector
		List<String> cleanTxtList = null;
		List<MatchResultKeyValue> finalResultList = new LinkedList<MatchResultKeyValue>();
		if (StringOperatorUtil.isNotBlankCollection(this.selectorList)) {
			cleanTxtList = JsoupHtmlParser
					.getNodeContentBySelector4MultiSameBlock(source,
							this.selectorList, this.dataFormatStatus, false);
			if (StringOperatorUtil.isNotBlank(this.attr)
					&& StringOperatorUtil.isNotBlankCollection(cleanTxtList)) {
				int size = cleanTxtList.size();
				String attrValue = null;
				for (int i = 0; i < size; i++) {
					attrValue = JsoupHtmlParser.getAttributeValue(
							cleanTxtList.get(i), this.attr);
					cleanTxtList.set(i, attrValue);
				}
			}

			// 如果有命中的字符串，亦有正则，则继续
			if (StringOperatorUtil
					.isNotBlankCollection(this.regexMatchItemPojoList)
					&& StringOperatorUtil.isNotBlankCollection(cleanTxtList)) {
				for (String block : cleanTxtList) {
					for (RegexMatchItemPojo regexMatchItemPojo : this.regexMatchItemPojoList) {
//						regexMatchItemPojo.getRegexParserUtil().reset(block);
						String value = regexMatchItemPojo.getRegexParserUtil()
								.getText4YuQing(block);
						// 如果是null说明什么都没有,直接略过该次
						if (value == null) {
							continue;
						} else if (value.trim().length() == 0) {
							if (StringOperatorUtil
									.isNotBlank(regexMatchItemPojo
											.getDefaultValue())) {
								MatchResultKeyValue matchResultKeyValue = new MatchResultKeyValue();
								matchResultKeyValue
										.setFieldKey(regexMatchItemPojo
												.getFieldKey());
								matchResultKeyValue.setValue(regexMatchItemPojo
										.getDefaultValue());
								// 检查是否有默认值
								finalResultList.add(matchResultKeyValue);
							}
						} else {
							MatchResultKeyValue matchResultKeyValue = new MatchResultKeyValue();
							matchResultKeyValue.setFieldKey(regexMatchItemPojo
									.getFieldKey());
							matchResultKeyValue.setValue(value.trim());
							finalResultList.add(matchResultKeyValue);
						}
					}
				}
				// 有正则过滤，则返回最后的正则匹配值，没有的话，则直接返回jsoup的过滤值。
				return finalResultList;
			}
		}
		if (cleanTxtList != null) {
			for (String line : cleanTxtList) {
				// 如果到这里返回，必须是jsoup返回,直接封装即可
				MatchResultKeyValue matchResultKeyValue = new MatchResultKeyValue();
				matchResultKeyValue.setFieldKey(this.fieldKey);
				// 也就只能有一个值
				matchResultKeyValue.setValue(line);
				finalResultList.add(matchResultKeyValue);
			}
		}
		return finalResultList;
	}

	static class RegexMatchItemPojo {
		// 说明匹配上时，这个匹配值对应哪个字段
		private String fieldKey;

		public String getFieldKey() {
			return fieldKey;
		}

		public void setFieldKey(String fieldKey) {
			this.fieldKey = fieldKey;
		}

		// 当匹配上时，是否用默认值来替代
		private String defaultValue;
		// 正则表达式集合
		private RegexParserUtil regexParserUtil;

		public RegexParserUtil getRegexParserUtil() {
			return regexParserUtil;
		}

		public void setRegexParserUtil(RegexParserUtil regexParserUtil) {
			this.regexParserUtil = regexParserUtil;
		}

		public RegexMatchItemPojo() {

		}

		public RegexMatchItemPojo(RegexParserUtil regexParserUtil,
				String defaultValue, String fieldKey) {
			this.regexParserUtil = regexParserUtil;
			this.defaultValue = defaultValue;
			this.fieldKey = fieldKey;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	public static void main(String[] args) {
		String filePath = "D:\\eclipse_jee_workspace_2014\\CrawlerSimpleWebPage\\phantomjs\\random_url\\20141031\\body\\49B09132285ED2ADB082EE4767F174D9.txt";
		String htmlSource = IOUtil.readDirOrFile(filePath,
				StaticValue.default_encoding);

		// String
		// formatString="div.crumbs#split_big#div.a_blue_d_all#split_big#a";
		// String
		// formatString="div.crumbs#split_big#div.a_blue_d_all#split_big#span";
		String formatString = "h1#stockName#=>#( #split_small#[\\s\\S]*#split_small#\\()#split_union#(\\(#split_small#[\\s\\S]*#split_small#\\))";

		FormatExtractorImpl formatExtractorImpl = new FormatExtractorImpl(
				formatString);
		System.out.println(formatExtractorImpl.getContent(htmlSource));

		// System.out.println(htmlSource);

	}
}
