package com.tianliang.spider.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * gson操作工具类
 * 
 * @author zel
 * 
 */
public class GsonOperatorUtil {
	/**
	 * 将一个json串转换为Gson的JsonObject
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static JsonParser jsonParser = new JsonParser();
	
	public static JsonElement parse(String jsonStr) {
		return jsonParser.parse(jsonStr);
	}

	public static JsonObject getJsonObject() {
		return new JsonObject();
	}

	public static JsonArray getObjectArray() {
		return new JsonArray();
	}

	public static void addElement(JsonObject srcObj, String key, String value) {
		srcObj.addProperty(key, value);
	}

	public static void addElement(JsonObject srcObj, String key,
			JsonElement jsonElement) {
		srcObj.add(key, jsonElement);
	}

	public static void main(String[] args) {
		JsonObject json = new JsonObject();
		json.addProperty("k1", "v1");
		json.addProperty("k2", "v2");
		json.addProperty("k3", "v3");

		JsonObject json2 = new JsonObject();
		json2.addProperty("json2_1", "value_1");
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(json2);
		json.add("inner_1", jsonArray);
		System.out.println(json.toString());
	}

}
