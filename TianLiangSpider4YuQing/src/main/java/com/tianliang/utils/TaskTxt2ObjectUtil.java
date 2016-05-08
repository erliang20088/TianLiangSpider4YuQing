package com.tianliang.utils;

import java.util.LinkedList;
import java.util.List;

import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.tianliang.spider.utils.SystemParas;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.enums.CrawlEngineEnum;
import com.zel.spider.pojos.enums.TaskLevelEnum;
import com.zel.spider.pojos.enums.TaskTypeEnum;
import com.zel.spider.pojos.statics.StaticValue4RelationMap;

/**
 * 文件行串到对象之间的转换
 * 
 * @author zel
 * 
 */
public class TaskTxt2ObjectUtil {
	public static List<CrawlTaskPojo> convertTxt2Object(List<String> txtContent) {
		if (StringOperatorUtil.isBlankCollection(txtContent)) {
			return null;
		}
		String[] strArray = null;
		List<CrawlTaskPojo> taskList = new LinkedList<CrawlTaskPojo>();
		CrawlTaskPojo taskPojo = null;
		// 遍历每一行
		for (String line : txtContent) {
			strArray = line.split(StaticValue.separator_tab);
			// 种子中每个url的tab分隔的长度为4、5、8
			if (strArray.length == 4) {
				taskPojo = new CrawlTaskPojo();

				taskPojo.setTitle(strArray[0]);
				taskPojo.setValue(strArray[1]);
				try {
					taskPojo.setMedia_type(Integer.parseInt(strArray[2]));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("种子行---" + line
							+ ",出现不合理值，请检查，该种子记录将略过!");
					continue;
				}
				taskPojo.setSource_title(taskPojo.getTitle());

				// 进行任务类别判断
				if (strArray[3].toLowerCase().equals(
						CrawlEngineEnum.MetaSearch_NEWSPage.toString()
								.toLowerCase())) {
					taskPojo.setType(TaskTypeEnum.Keyword);
					taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage);
				} else if (strArray[3].toLowerCase().equals(
						CrawlEngineEnum.MetaSearch_NEWSPage_360Search
								.toString().toLowerCase())) {
					taskPojo.setType(TaskTypeEnum.Keyword);
					taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage_360Search);
				} else {
					taskPojo.setType(TaskTypeEnum.Url);
					taskPojo.setCrawlEngine(CrawlEngineEnum.WebPage_Url);
				}

				// System.out.println("StaticValue4RelationMap.taskLevelDefault---"+StaticValue4RelationMap.taskLevelDefault);
				taskPojo.setLevel(StaticValue4RelationMap.taskLevelDefault);

				taskPojo.setDepth(SystemParas.depth);
				taskPojo.setCurrent_depth(0);
				taskPojo.setTopN(SystemParas.topN);

				taskList.add(taskPojo);
			} else if (strArray.length == 5) {
				taskPojo = new CrawlTaskPojo();

				taskPojo.setTitle(strArray[0]);
				taskPojo.setValue(strArray[1]);
				try {
					taskPojo.setMedia_type(Integer.parseInt(strArray[2]));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("种子行---" + line
							+ ",出现不合理值，请检查，该种子记录将略过!");
					continue;
				}
				taskPojo.setSource_title(taskPojo.getTitle());

				// 进行任务类别判断
				if (strArray[3].toLowerCase().equals(
						CrawlEngineEnum.MetaSearch_NEWSPage.toString()
								.toLowerCase())) {
					taskPojo.setType(TaskTypeEnum.Keyword);
					taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage);
				} else if (strArray[3].toLowerCase().equals(
						CrawlEngineEnum.MetaSearch_NEWSPage_360Search
								.toString().toLowerCase())) {
					taskPojo.setType(TaskTypeEnum.Keyword);
					taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage_360Search);
				} else {
					taskPojo.setType(TaskTypeEnum.Url);
					taskPojo.setCrawlEngine(CrawlEngineEnum.WebPage_Url);
				}
				// task level枚举转换
				taskPojo.setLevel(StaticValue4RelationMap
						.getTaskLevelEnumByString(strArray[4].toUpperCase()));

				taskPojo.setDepth(SystemParas.depth);
				taskPojo.setCurrent_depth(0);
				taskPojo.setTopN(SystemParas.topN);

				taskList.add(taskPojo);
			} else if (strArray.length == 7) {
				taskPojo = new CrawlTaskPojo();

				taskPojo.setTitle(strArray[0]);
				taskPojo.setValue(strArray[1]);
				try {
					taskPojo.setMedia_type(Integer.parseInt(strArray[2]));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("种子行---" + line
							+ ",出现不合理值，请检查，该种子记录将略过!");
					continue;
				}
				taskPojo.setSource_title(taskPojo.getTitle());

				if (strArray[3].toLowerCase().equals(
						CrawlEngineEnum.MetaSearch_NEWSPage.toString()
								.toLowerCase())) {
					taskPojo.setType(TaskTypeEnum.Keyword);
					taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage);
				} else if (strArray[3].toLowerCase().equals(
						CrawlEngineEnum.MetaSearch_NEWSPage_360Search
								.toString().toLowerCase())) {
					taskPojo.setType(TaskTypeEnum.Keyword);
					taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage_360Search);
				} else {
					taskPojo.setType(TaskTypeEnum.Url);
					taskPojo.setCrawlEngine(CrawlEngineEnum.WebPage_Url);
				}

				// task level枚举转换
				taskPojo.setLevel(StaticValue4RelationMap
						.getTaskLevelEnumByString(strArray[4].toUpperCase()));

				try {
					taskPojo.setDepth(Integer.parseInt(strArray[5]));
				} catch (Exception e) {
					e.printStackTrace();
					taskPojo.setDepth(SystemParas.depth);
				}

				try {
					taskPojo.setTopN(Integer.parseInt(strArray[6]));
				} catch (Exception e) {
					e.printStackTrace();
					taskPojo.setTopN(SystemParas.topN);
				}
				
				taskPojo.setCurrent_depth(0);
				taskList.add(taskPojo);
			}
		}
		return taskList;
	}
 
	public static void main(String[] args) {
		System.out.println(CrawlEngineEnum.MetaSearch_NEWSPage.toString());
		System.out.println(StaticValue4RelationMap.taskLevelDefault);
		// "".equalsIgnoreCase("");
	}

}
