package com.tianliang.spider.manager.convert;

import java.util.Set;

import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.StaticValue;

/**
 * 文本文件转化成种子文件格式
 * 
 * @author zel
 * 
 */
public class ConvertTxtToSeedFormatManager {
	public static void formatManualRule() {
		// String input_file =
		// "d:\\conac_test\\yihui_crawl_content_extractor_regex.adapter";
		// String output_file =
		// "d:\\conac_test\\yihui_crawl_content_extractor_regex_final.txt";
		// String input_file =
		// "d:\\conac_test\\tangjiang_crawl_content_extractor_regex.properties";
		// String output_file =
		// "d:\\conac_test\\tangjiang_crawl_content_extractor_regex.txt";
		// String input_file =
		// "d:\\conac_test\\tangjiang_blog_crawl_content_extractor_regex.adapter";
		// String output_file =
		// "d:\\conac_test\\tangjiang_blog_crawl_content_extractor_regex.txt";
		// String input_file =
		// "d:\\conac_test\\tangjian_bbs_crawl_content_extractor_regex.adapter";
		// String output_file =
		// "d:\\conac_test\\tangjian_bbs_crawl_content_extractor_regex.txt";

		String input_file = "d:\\conac_test\\tangwang_crawl_content_extractor_regex.adapter";
		String output_file = "d:\\conac_test\\tangwang_crawl_content_extractor_regex.txt";

		Set<String> valueSet = IOUtil.readFileToSet(input_file,
				StaticValue.default_encoding);

		// System.out.println(valueSet.size());
		StringBuilder sb = new StringBuilder();
		for (String line : valueSet) {
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			}
			String[] arr = line.split(StaticValue.separator_tab);
			if (arr.length == 4) {
				sb.append(arr[0]);
				sb.append(StaticValue.separator_tab);
				sb.append(arr[2].replace("[\\s\\S]*", ""));
				sb.append(StaticValue.separator_tab);
				sb.append(1);
				// sb.append(3);
				// sb.append(4);
				sb.append(StaticValue.separator_tab);
				sb.append("WebPage_Url");
				sb.append(StaticValue.separator_next_line);
			}
		}
		IOUtil.writeFile(output_file, sb.toString(),
				StaticValue.default_encoding);
	}

	public static void produceSeed4Url() {
		// String input_file = "d:\\conac_test\\blog_seed.txt";
		// String output_file = "d:\\conac_test\\blog_seed_final.txt";
		// String input_file = "d:\\conac_test\\luntan_seed.txt";
		// String output_file = "d:\\conac_test\\luntan_seed_final.txt";

		String input_file = "d:\\conac_test\\pingmei_tangjian_url.txt";
		String output_file = "d:\\conac_test\\pingmei_tangjian_url_final.txt";

		Set<String> valueSet = IOUtil.readFileToSet(input_file,
				StaticValue.default_encoding);

		// System.out.println(valueSet.size());
		StringBuilder sb = new StringBuilder();
		for (String line : valueSet) {
			sb.append(line);
			sb.append(StaticValue.separator_tab);
			// 新闻为1，博客为4,论坛为3
			// sb.append(1);
			// sb.append(4);
			// sb.append(3);
			sb.append(7);
			sb.append(StaticValue.separator_tab);
			sb.append("WebPage_Url");
			sb.append(StaticValue.separator_tab);
			sb.append("F");
			sb.append(StaticValue.separator_next_line);

			// System.out.println(line.split("\t").length);
		}
		IOUtil.writeFile(output_file, sb.toString(),
				StaticValue.default_encoding);
	}

	public static void producSeed4Keyword() {
		// String input_file = "d:\\conac_test\\seed_keyword_all_original.txt";
		// String output_file = "d:\\conac_test\\seed_keyword_all_final.txt";
		String input_file = "d:\\conac_test\\seeds_keyword_20150414.txt";
		String output_file = "d:\\conac_test\\seeds_keyword_20150414.txt_final";

		Set<String> valueSet = IOUtil.readFileToSet(input_file,
				StaticValue.default_encoding);

		// System.out.println(valueSet.size());
		StringBuilder sb = new StringBuilder();
		for (String line : valueSet) {
			sb.append("元搜索");
			sb.append(StaticValue.separator_tab);
			sb.append(line);
			sb.append(StaticValue.separator_tab);
			sb.append("8");
			sb.append(StaticValue.separator_tab);
			sb.append("MetaSearch_NEWSPage");
			sb.append(StaticValue.separator_tab);
			sb.append("C");
			sb.append(StaticValue.separator_next_line);

			// System.out.println(line.split("\t").length);
		}

		IOUtil.writeFile(output_file, sb.toString(),
				StaticValue.default_encoding);
	}

	public static void main(String[] args) {
		// produceSeed4Url();
		producSeed4Keyword();
		// formatManualRule();
	}
}
