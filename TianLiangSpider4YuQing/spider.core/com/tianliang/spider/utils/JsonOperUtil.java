package com.tianliang.spider.utils;

import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * json操作工具类
 * 
 * @author zel
 * 
 */
public class JsonOperUtil {
	public static JSONArray getJsonArray(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		JSONParser parser = new JSONParser();
		try {
			return (JSONArray) (parser.parse(str));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getJsonObject(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) (parser.parse(str));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void printJsonObject(JSONObject jsonObj) {
		if (jsonObj == null) {
			return;
		}
		Set<String> keySet = jsonObj.keySet();
		for (String key : keySet) {
			System.out.println(key + "====" + jsonObj.get(key));
		}
	}

}
