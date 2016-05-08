package com.tianliang.spider.utils;

import java.util.LinkedList;
import java.util.List;

import com.tianliang.spider.pojos.ProxyPojo;

/**
 * 系统参数配置
 * 
 * @author zel
 * 
 */
public class SystemParas {
	// 日志
	public static MyLogger logger = new MyLogger(SystemParas.class);

	public static ReadConfigUtil readConfigUtil = new ReadConfigUtil(
			"tianliang_spider.properties", true);

	/**
	 * 较全局变量配置
	 */
	// 是否是主节点，不是主节点即是子节点
	public static boolean node_is_master = Boolean.parseBoolean(readConfigUtil
			.getValue("node_is_master"));

	/**
	 * 爬虫基础配置参数
	 */
	public static String spider_seeds_root_path = readConfigUtil
			.getValue("spider_seeds_root_path");
	public static String spider_data_dir = readConfigUtil.getValue("data_dir");
	public static int spider_threads = Integer.parseInt(readConfigUtil
			.getValue("threads"));
	public static int depth = Integer
			.parseInt(readConfigUtil.getValue("depth"));
	public static int topN = Integer.parseInt(readConfigUtil.getValue("topN"));

	public static int depth_max = Integer.parseInt(readConfigUtil
			.getValue("max_depth"));
	public static int topN_max = Integer.parseInt(readConfigUtil
			.getValue("max_topN"));

	// http请求遇到error时，重复请求的次数
	public static int http_req_error_repeat_number = Integer
			.parseInt(readConfigUtil.getValue("http_req_error_repeat_number"));
	// 一次http请求要等待的时间
	public static int http_req_once_wait_time = Integer.parseInt(readConfigUtil
			.getValue("http_req_once_wait_time"));

	/**
	 * 为simple crawler而添加的参数,为规则相关而添加
	 */
	public static String ext_content_rule_config_fs = readConfigUtil
			.getValue("ext_content_rule_config_fs");
	public static String ext_content_rule_config_root_dir = readConfigUtil
			.getValue("ext_content_rule_config_root_dir");
	public static int ext_content_save_threads_numbers = Integer
			.parseInt(readConfigUtil
					.getValue("ext_content_save_threads_numbers"));
	public static int ext_content_save_thread_sleep_time = Integer
			.parseInt(readConfigUtil
					.getValue("ext_content_save_thread_sleep_time"));
	// 标志规则文件是否发生变化
	public static int ext_content_rule_key_is_changed = Integer
			.parseInt(readConfigUtil
					.getValue("ext_content_rule_key_is_changed"));
	// 标志客户端每隔多长时间更新下自己的规则提取文件
	public static int ext_content_rule_key_syn_circle = Integer
			.parseInt(readConfigUtil
					.getValue("ext_content_rule_key_syn_circle")) * 1000;
	// 每次load种子目录时，多长时间循环一次
	public static int ext_content_load_seeds_sleep = Integer
			.parseInt(readConfigUtil.getValue("ext_content_load_seeds_sleep")) * 1000;
	// 是否开始循环加载种子目录
	public static boolean ext_content_load_seeds_is_circle = Boolean
			.parseBoolean(readConfigUtil
					.getValue("ext_content_load_seeds_is_circle"));

	public static int node_seeds_max_size = Integer.parseInt(readConfigUtil
			.getValue("node_seeds_max_size"));
	/*
	 * 低于这个值就会自动注入新的集合到redis队列中
	 */
	public static int node_redis_size_threshold = Integer
			.parseInt(readConfigUtil.getValue("node_redis_size_threshold"));
	// server向redis端注入一次url集合后的眨眼时间
	public static int node_inject_urls_sleep_time = Integer
			.parseInt(readConfigUtil.getValue("node_inject_urls_sleep_time"));
	// 一次性导入到redis时的url条数
	public static int node_seeds_inject_batch_size = Integer
			.parseInt(readConfigUtil.getValue("node_seeds_inject_batch_size"));
	public static int node_client_grab_success_sleep_interval_time = Integer
			.parseInt(readConfigUtil
					.getValue("node_client_grab_success_sleep_interval_time"));
	// 请求失败后，可以重复请求的次数
	public static int node_req_fail_max_count = Integer.parseInt(readConfigUtil
			.getValue("node_req_fail_max_count"));

	/**
	 * 可以抓取url的最大重复次数
	 */
	public static int crawl_page_repeat_number = Integer
			.parseInt(readConfigUtil.getValue("crawl_page_repeat_number"));

	/**
	 * 代理参数设置
	 */

	// proxy代理是否启用
	public static boolean proxy_open = Boolean.parseBoolean(readConfigUtil
			.getValue("proxy_open"));
	public static boolean proxy_self = Boolean.parseBoolean(readConfigUtil
			.getValue("proxy_self"));
	// 从文件中取得每对proxy ip的ip与port并加入proxyList集合
	public static String ip_proxy_file_path = readConfigUtil
			.getValue("ip_proxy_file_path");

	// 读取代理列表，并加入到proxy中
	public static List<ProxyPojo> proxyList = new LinkedList<ProxyPojo>();
	public static int proxy_fail_max_count = Integer.parseInt(readConfigUtil
			.getValue("proxy_fail_max_count"));

	// 设置链接超时时间,这个是链接时间
	public static int http_connection_timeout = Integer.parseInt(readConfigUtil
			.getValue("connection_timeout"));
	// 这个时读取超时时间
	public static int http_read_timeout = Integer.parseInt(readConfigUtil
			.getValue("read_timeout"));

	// web service config parameters
	public static String ws_server_ip = readConfigUtil.getValue("ws_server_ip");
	public static int ws_server_port = Integer.parseInt(readConfigUtil
			.getValue("ws_server_port"));

	// 关于redis参数
	public static String redis_host = readConfigUtil.getValue("redis_host");
	public static int redis_port = Integer.parseInt(readConfigUtil
			.getValue("redis_port"));
	public static String redis_password = readConfigUtil
			.getValue("redis_password");
	
	// 关于缓存数据的设置，如布隆過濾器
	public static int cache_data_circle_save_interval = Integer
			.parseInt(readConfigUtil
					.getValue("cache_data_circle_save_interval")) * 1000;

	// 取得是否是测试节点的配置
	public static boolean application_is_test = Boolean
			.parseBoolean(readConfigUtil.getValue("application_is_test"));

	/**
	 * phantomjs start
	 */
	public static String phantomjs_path = readConfigUtil
			.getValue("phantomjs_path");
	/**
	 * 得到是linux还是window调用
	 */
	public static String pre_phantomjs_exe_name = OperatorSystemUtil
			.isWindows() ? "win_" : "linux_";
	// phantomjs执行文件的名字，为方便win和linux文件名不同而准备
	public static String phantomjs_exe_name = pre_phantomjs_exe_name
			+ readConfigUtil.getValue("phantomjs_exe_name");
	// jquery path
	public static String phantomjs_crawl_config_jquery_path = readConfigUtil
			.getValue("phantomjs_crawl_config_jquery_path");
	// 文件配置phantomjs相关参数时的json文件路径
	public static String phantomjs_crawl_config_json_root_path = readConfigUtil
			.getValue("phantomjs_crawl_config_json_root_path");
	// phantomjs爬虫时的js文件路径的根路径，该路径下可能存在多个不同的爬虫js文件
	public static String phantomjs_crawl_config_js_root_path = readConfigUtil
			.getValue("phantomjs_crawl_config_js_root_path");
	// phantomjs执行js时的参数以文件传递时的文件根路径，这个路径可能存在多个参数文件,每个参数配置文件对应一个js爬虫文件
	public static String phantomjs_crawl_config_js_para_root_path = readConfigUtil
			.getValue("phantomjs_crawl_config_js_para_root_path");
	// 捕捉的图片存放的默认根路径
	public static String phantomjs_crawl_config_capture_pic_root_path_seg = readConfigUtil
			.getValue("phantomjs_crawl_config_capture_pic_root_path_seg");

	// 爬取下来的文本数据默认根路径
	public static String phantomjs_crawl_config_txt_root_path_seg = readConfigUtil
			.getValue("phantomjs_crawl_config_txt_root_path_seg");

	// 捕捉的图片存放的默认根路径---为抓取正文时
	public static String phantomjs_crawl_config_capture_pic_root_path_body = readConfigUtil
			.getValue("phantomjs_crawl_config_capture_pic_root_path_body");

	// 爬取下来的文本数据默认根路径---为抓取正文时
	public static String phantomjs_crawl_config_txt_root_path_body = readConfigUtil
			.getValue("phantomjs_crawl_config_txt_root_path_body");

	// 为任意url而设置的数据存放而设置，没有区分文本和图片的路径，其区分在StaticValue变量中定义在其下方的子目录
	public static String phantomjs_crawl_config_all_data_root_path_random_url = readConfigUtil
			.getValue("phantomjs_crawl_config_all_data_root_path_random_url");

	// 请求过程中，没有任何数据时的最长等待时间
	public static int phantomjs_crawl_config_no_response_waitting_time_max = Integer
			.parseInt(readConfigUtil
					.getValue("phantomjs_crawl_config_no_response_waitting_time_max"));

	// 最多可以重复‘等待超过最长时间’的次数，有可能是网络的原因，故要重复请求一下
	public static int phantomjs_crawl_config_no_response_waitting_fail_time_max = Integer
			.parseInt(readConfigUtil
					.getValue("phantomjs_crawl_config_no_response_waitting_fail_time_max"));
	public static String phantojs_seg_output_root_path = readConfigUtil
			.getValue("phantojs_seg_output_root_path");

	/**
	 * 原static value配置参数的迁移
	 */
	public static boolean is_inject_jquery_default = Boolean
			.parseBoolean(readConfigUtil.getValue("is_inject_jquery_default"));
	public static boolean is_capture_pic_default_seg = Boolean
			.parseBoolean(readConfigUtil.getValue("is_capture_pic_default_seg"));
	public static boolean is_capture_pic_default_body = Boolean
			.parseBoolean(readConfigUtil
					.getValue("is_capture_pic_default_body"));
	public static boolean is_capture_pic_default_random_page = Boolean
			.parseBoolean(readConfigUtil
					.getValue("is_capture_pic_default_random_page"));

	public static boolean is_data_write_to_file_default = Boolean
			.parseBoolean(readConfigUtil
					.getValue("is_data_write_to_file_default"));
	public static int crawl_max_page_number = Integer.parseInt(readConfigUtil
			.getValue("crawl_max_page_number"));

	// -------------------phantomjs end---------------------------

	/**
	 * 需要初始化的数据目录
	 */
	static {
		FileOperatorUtil.createRootDir(spider_data_dir);
	}

	/**
	 * task相关参数
	 */
	public static boolean task_circle_enable = Boolean
			.parseBoolean(readConfigUtil.getValue("task_circle_enable"));
	public static int task_count_circle_keyword = Integer.parseInt(readConfigUtil
			.getValue("task_count_circle_keyword"));
	public static int task_count_circle_normal = Integer.parseInt(readConfigUtil
			.getValue("task_count_circle_normal"));
	//限制采集列表在redis中的最大key的最大的条目数
	public static int task_todo_level_2_max_items_in_redis = Integer.parseInt(readConfigUtil
			.getValue("task_todo_level_2_max_items_in_redis"));
	
	//为jdbc声明的几个预值参数
	public static String jdbc_driver = readConfigUtil.getValue("jdbc.driver");
	public static String jdbc_url = readConfigUtil.getValue("jdbc.url");
	public static String jdbc_userName = readConfigUtil
			.getValue("jdbc.username");
	public static String jdbc_userPwd = readConfigUtil
			.getValue("jdbc.password");
	
	static {
		try {
			if (proxy_open) {
				logger.info("proxy server has been used!");
				ReadConfigUtil readProxyConfig = new ReadConfigUtil(
						ip_proxy_file_path, false);
				String temp_proxy_list = readProxyConfig.getLineConfigTxt();
				String temp_proxy_paras[] = null;
				ProxyPojo proxyPojo = null;
				if (temp_proxy_list.trim().length() > 0) {
					for (String proxy_line : temp_proxy_list.split("\n")) {
						temp_proxy_paras = proxy_line.split("	");
						if (temp_proxy_paras.length == 2) {
							proxyPojo = new ProxyPojo(temp_proxy_paras[0],
									Integer.parseInt(temp_proxy_paras[1]));
							proxyPojo.setAuthEnable(false);// 无需用户名和密码
							proxyList.add(proxyPojo);
						} else {
							proxyPojo = new ProxyPojo(temp_proxy_paras[0],
									Integer.parseInt(temp_proxy_paras[1]),
									temp_proxy_paras[2], temp_proxy_paras[3]);
							proxyPojo.setAuthEnable(true);// 需要用户名和密码
							proxyList.add(proxyPojo);
						}

					}
				}
			} else {
				logger.info("proxy server is forbidden!");
			}
		} catch (Exception e) {
			logger.info("读取代理服务器列表参数时抛出异常，请检查!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		System.out.println(readConfigUtil.getValue("test"));
//		System.out.println(readConfigUtil.getValue("spider_seeds_root_path"));
		
		System.out.println("done");
	}
}
