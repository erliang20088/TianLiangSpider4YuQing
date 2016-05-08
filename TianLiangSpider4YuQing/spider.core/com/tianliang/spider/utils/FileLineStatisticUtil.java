package com.tianliang.spider.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对搜索query语句的统计，主要是统计出词频来
 * 
 * @author zel
 * 
 */
public class FileLineStatisticUtil {

	public static Map<String, Integer> getFileLineStatisticsMap(String inputPath) {
		Map<String, Integer> search_query_map = new HashMap<String, Integer>();

		List<String> search_query_list = IOUtil.getLineArrayFromFile(inputPath,
				StaticValue.default_encoding);
		
		for (String query : search_query_list) {
			query = query.trim().toLowerCase();
			if (StringOperatorUtil.isBlank(query)) {
				continue;
			}
			if (search_query_map.containsKey(query)) {
				search_query_map.put(query, search_query_map.get(query) + 1);
			} else {
				search_query_map.put(query, 1);
			}
		}

		return search_query_map;
	}

	public static void outputFileLineStatisticResult(String inputFilePath,
			String outputFilePath) {
		Map<String, Integer> statisticMap = getFileLineStatisticsMap(inputFilePath);
		StringBuilder sb = new StringBuilder();
		Set<String> lineSet = statisticMap.keySet();

		for (String line : lineSet) {
			sb.append(line + StaticValue.separator_next_line);
		}

		IOUtil.writeFile(outputFilePath, sb.toString(),
				StaticValue.default_encoding);
	}

	public static void main(String[] args) {
		// String inputFile = SystemParas.phantomjs_seeds_path;
		String inputFile = "d://fuxie_keyword_all.txt";
		FileLineStatisticUtil.outputFileLineStatisticResult(inputFile,
				inputFile);
		System.out.println("task is finished!");
	}
}
