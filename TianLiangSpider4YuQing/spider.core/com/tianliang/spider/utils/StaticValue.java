package com.tianliang.spider.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 静态变量定义
 * 
 * @author zel
 * 
 */
public class StaticValue {
	public static String default_encoding = "utf-8";
	public static String gbk_encoding = "gbk";
	public static final String gb2312_encoding = "gb2312";

	public static String default_refer = "http://www.baidu.com/";

	public static String baidu_index = "http://www.baidu.com";

	/**
	 * 符号定义
	 */
	public static String separator_tab = "\t";
	public static String separator_dot = ",";
	public static String separator_next_line = "\n";
	public static String separator_space = " ";
	public static String separator_file_path = "/";

	public static String separator_left_bracket = "(";
	public static String separator_right_bracket = ")";

	// http parasmeter
	public static String prefix_http = "http://";

	/**
	 * 关于规则库中的分隔符的定义
	 * 解释性语句,除第一类外，其它行是不能出现tab的。#split_big#是最大的分隔符,#=>#是第二分隔符,其前是jsoup的格式
	 * ，其后是正则表达式,#split_small#是第三分隔符 其中的#split_union#是表示前后两个正则匹配是等同层次的
	 */
	public static String rule_file_split_big = "#split_big#";
	public static String rule_file_split_block_index = com.vaolan.utils.StaticValue.split_block_index;
	public static String rule_file_split_regex = "#=>#";
	public static String rule_file_split_union = "#split_union#";
	public static String rule_file_split_small = "#split_small#";
	public static String rule_file_split_paras = "#paras#";

	/**
	 * 为服务器与客户数据交互新增的key
	 */
	public static String ext_content_rule_key = "ext_content_rule_key";
	public static String ext_content_rule_key_is_changed = "ext_content_rule_key_is_changed";
	// 以这个key去redis中去获取数据，并做为下一步去存储
	public static String ext_content_to_save_list_key = "ext_content_to_save_list_key";
	public static String ext_content_error_list_key = "ext_content_error_list_key";

	/**
	 * bloom filter to save key 4 redis
	 */
	public static String bloom_to_do_task_key = "bloom_to_do_task_key";
	public static String bloom_done_task_key = "bloom_done_task_key";

	/**
	 * redis相关默认变量
	 */
	// 正常的todo和finish任务队列
	public static String redis_task_todo_list_key_name = "task_todo";
	public static String redis_task_finished_key_name = "task_finish";
	// 循环任务的hash set，兼管去重
	public static String redis_task_set_key_name_circle = "task_circle";
	public static String redis_task_circle_queue_key_cache = "task_circle_key_cache";
	public static String redis_task_circle_keyword_queue_key_cache = "task_circle_keyword_key_cache";

	public static String redis_task_todo_key_name_circle = "task_circle_todo";
	public static String redis_task_todo_key_name_circle_keyword = "task_circle_keyword_todo";

	public static String redis_task_todo_list_key_name_level_2 = "task_todo_level_2";

	/**
	 * 专为解决网页编码提取而添加
	 */
	// 单点定义
	public static final char point = '.';
	public static int url_data_min_byte_length = 500;

	/**
	 * 以下是对网页的charset下的charset相应定义
	 */
	// 默认编码方式
	public static final String SYSTEM_ENCODING = "utf-8";
	// 默认gbk中文的处理编码
	public static final String GBK_ENCODING = "gbk";
	// 默认gb2312中文的处理编码
	public static final String GB2312_ENCODING = "gb2312";
	// 台湾big5编码
	public static final String BIG5_ENCODING = "big5";
	// 日本Shift_JIS
	public static final String Japan_Shift_ENCODING = "shift_jis";
	public static final String Japan_Euc_ENCODING = "euc-jp";
	// 西里尔文window
	public static final String Xili_Window_ENCODING = "windows-1251";
	/**
	 * 以下是对网页的charset部分的lang来定义
	 */
	public static final String Japan_Lang = "ja";
	public static final String Japan_Lang_First = Japan_Shift_ENCODING;

	/**
	 * 关于crawl config 4 phantomjs
	 */
	// public static boolean is_inject_jquery_default=true;
	public static boolean is_inject_jquery_default = SystemParas.is_inject_jquery_default;
	// public static boolean is_capture_pic_default=true;
	// public static boolean is_capture_pic_default_seg=true;
	public static boolean is_capture_pic_default_seg = SystemParas.is_capture_pic_default_seg;
	// public static boolean is_capture_pic_default_body=true;
	public static boolean is_capture_pic_default_body = SystemParas.is_capture_pic_default_body;
	public static boolean is_capture_pic_default_random_page = SystemParas.is_capture_pic_default_random_page;

	public static String pic_file_prefix_name_default = "capture-";
	public static String pic_file_suffix_name_default = ".png";
	public static int max_page_number_default = SystemParas.crawl_max_page_number;
	public static boolean is_data_write_to_file_default = SystemParas.is_data_write_to_file_default;
	public static String data_write_to_file_root_path_default = ".png";
	public static String data_file_prefix_name_default = "page-";
	public static String data_file_suffix_name_default = ".txt";

	/**
	 * 为任意url定义子路径
	 */
	public static String random_url_capture_dir_name = "/capture/";
	public static String random_url_body_data_dir_name = "/body/";
	public static String phantomjs_config_file_name_default = "config_phantomjs.json";
	/**
	 * phantomjs默认参数
	 */
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko)Safari/534.35";
	public static String userAgent_360 = "Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13";

	/**
	 * phantomjs的执行的js脚本文件名称
	 */
	public static String phantomjs_js_crawl_4_baidu_news = "baidu_news_crawl_search_result.js";
	public static String phantomjs_js_crawl_4_baidu_webpage = "baidu_webpage_crawl_search_result.js";
	public static String phantomjs_js_crawl_4_baidu_random_page = "baidu_crawl_body_random_page.js";
	public static String phantomjs_js_crawl_4_baidu_body = "baidu_crawl_body.js";

	public static String phantomjs_js_crawl_4_360_news = "360_news_crawl_search_result.js";
	public static String phantomjs_js_crawl_4_sogou_news = "sogou_news_crawl_search_result.js";

	public static String phantomjs_js_crawl_4_weixin_sogou = "weixin_crawl_search_result.js";

	public static String qihu360_news_search_url_format = "http://news.haosou.com/ns?q=${query}&src=newhome";
	public static String qihu360_news_search_url_format_4_api = "http://open.www.haosou.com/newssearch?cid=${client_id}&q=${query}&m=${access_token}&s=${start_offset}&r=${rank_type}";
	public static String weixin_news_search_url_format = "http://weixin.sogou.com/weixin?query=${query}&fr=sgsearch&type=2";

	/**
	 * 关于phantomjs config
	 */
	public static String output_encoding_default = default_encoding;
	public static String script_encoding_default = default_encoding;

	// -----------------phantomjs end-------------------

	// 默认的参数设置
	// UA默认设置
	public static Map<String, Object> headerMap = null;
	static {
		headerMap = new HashMap<String, Object>();
		headerMap
				.put("User-Agent",
						"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
		// headerMap.put("Connection", "keep-alive");
		// headerMap.put("Content-Length",500000);
	}

	// 关于统计信息的静态变量定义
	public static String statistic_key_template = "template_useful";
	public static String statistic_key_task = "task_counter";

}
