package com.tianliang.spider.pojos;

import com.tianliang.spider.manager.crawler.PhantomManager;
import com.tianliang.spider.utils.DateUtil;
import com.tianliang.spider.utils.MD5;
import com.tianliang.spider.utils.PhantomJsOperatorUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.SystemParas;

/**
 * 对应于config_crawl_para.js中的json字符串的每个key/value
 * 
 * @author zel
 * 
 */
public class CrawlConfigParaPojo {
	private MD5 md5 = new MD5();

	// 在构造方法中添加一些必要的默认值
	public CrawlConfigParaPojo(String root_url, String searchKeyword, int flag) {
		// 这两个是必填的，其它的均可以默认，不默认则自行调用set即可
		this.setRoot_url(root_url);
		this.setSearch_keyword(searchKeyword);

		String keyword_md5_add_seq = null;
		if (searchKeyword != null) {
			this.keyword_md5 = md5.MD5(searchKeyword);
			keyword_md5_add_seq = PhantomManager
					.getConfigFileNamePrefix(root_url)
					+ this.keyword_md5
					+ StaticValue.separator_file_path;
		}

		this.setFlag(flag);

		if (flag == 0) {
			this.setPic_capture_save_root_path(SystemParas.phantomjs_crawl_config_capture_pic_root_path_seg);
			this.setData_write_to_file_root_path(SystemParas.phantomjs_crawl_config_txt_root_path_seg);
			this.setIs_capture_pic(StaticValue.is_capture_pic_default_seg);
			// this.setBody_pic_or_txt_count(1);
		} else if (flag == 1) {
			this.setPic_capture_save_root_path(SystemParas.phantomjs_crawl_config_capture_pic_root_path_body);
			this.setData_write_to_file_root_path(SystemParas.phantomjs_crawl_config_txt_root_path_body);
			this.setIs_capture_pic(StaticValue.is_capture_pic_default_body);
			// this.setBody_pic_or_txt_count(batchNumber);
		} else if (flag == 2) {
			/**
			 * 在此生成random_page_output_path
			 */
			String yyyy_mm_dd = DateUtil.getTodateString();
			this.root_url_md5 = md5.MD5(this.root_url);
			this.random_url_output_path_body = SystemParas.phantomjs_crawl_config_all_data_root_path_random_url
					+ yyyy_mm_dd
					+ StaticValue.random_url_body_data_dir_name
					+ this.root_url_md5;
			this.random_url_output_path_capture = SystemParas.phantomjs_crawl_config_all_data_root_path_random_url
					+ yyyy_mm_dd
					+ StaticValue.random_url_capture_dir_name
					+ this.root_url_md5;
			this.setIs_capture_pic(StaticValue.is_capture_pic_default_random_page);
			/**
			 * 设置下任意url抓取时的输出路径，包括图片和网页源码的输出目录
			 */
			this.setData_write_to_file_root_path(SystemParas.phantomjs_crawl_config_all_data_root_path_random_url
					+ yyyy_mm_dd + StaticValue.random_url_body_data_dir_name);
			this.setPic_capture_save_root_path(SystemParas.phantomjs_crawl_config_all_data_root_path_random_url
					+ yyyy_mm_dd + StaticValue.random_url_capture_dir_name);
			
			// 设置任意url抓取时候的文件的全路径
			this.setAbsolute_body_file_path(this.random_url_output_path_body
					+ StaticValue.data_file_suffix_name_default);
			this.setAbsolute_capture_file_path(this.random_url_output_path_capture
					+ StaticValue.pic_file_suffix_name_default);

		}

		// 设置默认
		this.setIs_inject_jquery(StaticValue.is_inject_jquery_default);
		this.setJquery_path(SystemParas.phantomjs_crawl_config_jquery_path);
		this.setUserAgent(StaticValue.userAgent);
		
		this.setPic_capture_save_sub_path(keyword_md5_add_seq);
		this.setPic_file_prefix_name(StaticValue.pic_file_prefix_name_default);
		this.setPic_file_suffix_name(StaticValue.pic_file_suffix_name_default);
		this.setMax_page_number(StaticValue.max_page_number_default);
		this.setIs_data_write_to_file(StaticValue.is_data_write_to_file_default);

		this.setData_write_to_file_sub_path(keyword_md5_add_seq);
		this.setData_file_prefix_name(StaticValue.data_file_prefix_name_default);
		this.setData_file_suffix_name(StaticValue.data_file_suffix_name_default);

		this.setNo_response_waitting_time_max(SystemParas.phantomjs_crawl_config_no_response_waitting_time_max);
		this.setNo_response_waitting_fail_time_max(SystemParas.phantomjs_crawl_config_no_response_waitting_fail_time_max);
	}

	// 指定图片输出路径，给conac或其它地方使用
	public CrawlConfigParaPojo(String root_url, String searchKeyword, int flag,
			String outputPicFilePathString) {
		this(root_url, searchKeyword, flag);
		if (outputPicFilePathString != null) {
//			this.setAbsolute_capture_file_path(outputPicFilePathString);
			this.setRandom_url_output_path_capture(outputPicFilePathString);
		}
	}

	private String keyword_md5;
	private String root_url_md5;

	private String absolute_body_file_path;

	public String getAbsolute_body_file_path() {
		return absolute_body_file_path;
	}

	public void setAbsolute_body_file_path(String absolute_body_file_path) {
		this.absolute_body_file_path = absolute_body_file_path;
	}

	public String getAbsolute_capture_file_path() {
		return absolute_capture_file_path;
	}

	public void setAbsolute_capture_file_path(String absolute_capture_file_path) {
		this.absolute_capture_file_path = absolute_capture_file_path;
	}

	private String absolute_capture_file_path;

	public String getRoot_url_md5() {
		return root_url_md5;
	}

	public void setRoot_url_md5(String rootUrlMd5) {
		root_url_md5 = rootUrlMd5;
	}

	public String getKeyword_md5() {
		return keyword_md5;
	}

	public void setKeyword_md5(String keywordMd5) {
		keyword_md5 = keywordMd5;
	}

	// 用于区分不同用途的该对象，暂定为分词和抓取关键词对应的正文两种，0为seg,1为body.
	private int flag;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * 当对任意URL进行抓取时，提前定义好其url的抓取完成后的输出路径,暂定为由日期如2014-04-14为文件夹，
	 * 其内部存放由每个url加密码的md5为文件名称,包括文本内容和图片内容,其开关与其它的位置相同
	 */
	// 任意给定的url抓取时的存放路径，在这里传的值已包含了文件父路径及文件名称，只需要在后边加后缀即可，而其后缀直接取值于前边对body定义的后缀取值，如txt、jpg等
	private String random_url_output_path_body;

	public String getRandom_url_output_path_body() {
		return random_url_output_path_body;
	}

	public void setRandom_url_output_path_body(String randomUrlOutputPathBody) {
		random_url_output_path_body = randomUrlOutputPathBody;
	}

	public String getRandom_url_output_path_capture() {
		return random_url_output_path_capture;
	}

	public void setRandom_url_output_path_capture(
			String randomUrlOutputPathCapture) {
		random_url_output_path_capture = randomUrlOutputPathCapture;
	}

	private String random_url_output_path_capture;

	@Override
	public String toString() {
		// 封装成json格式
		StringBuilder sb = new StringBuilder();

		sb.append("{\n");

		sb.append("\"root_url\":\"" + root_url + "\",\n");
		sb.append("\"is_inject_jquery\":" + is_inject_jquery + ",\n");
		sb.append("\"jquery_path\":\"" + jquery_path + "\",\n");
		sb.append("\"userAgent\":\"" + userAgent + "\",\n");
		if (search_keyword != null) {
			sb.append("\"search_keyword\":\""
					+ search_keyword.replace("\"", "'").replace("\\", "\\\\")
					+ "\",\n");
		} else {
			sb.append("\"search_keyword\":\"" + search_keyword + "\",\n");
		}
		sb.append("\"is_capture_pic\":" + is_capture_pic + ",\n");
		sb.append("\"pic_capture_save_root_path\":\""
				+ pic_capture_save_root_path + "\",\n");
		sb.append("\"pic_capture_save_sub_path\":\""
				+ pic_capture_save_sub_path + "\",\n");
		sb.append("\"pic_file_prefix_name\":\"" + pic_file_prefix_name
				+ "\",\n");
		sb.append("\"pic_file_suffix_name\":\"" + pic_file_suffix_name
				+ "\",\n");
		sb.append("\"max_page_number\":" + max_page_number + ",\n");
		sb.append("\"is_data_write_to_file\":" + is_data_write_to_file + ",\n");
		sb.append("\"data_write_to_file_root_path\":\""
				+ data_write_to_file_root_path + "\",\n");
		sb.append("\"data_file_prefix_name\":\"" + data_file_prefix_name
				+ "\",\n");
		sb.append("\"data_file_suffix_name\":\"" + data_file_suffix_name
				+ "\",\n");
		sb.append("\"data_write_to_file_sub_path\":\""
				+ data_write_to_file_sub_path + "\",\n");

		sb.append("\"no_response_waitting_time_max\":"
				+ no_response_waitting_time_max + ",\n");
		sb.append("\"no_response_waitting_fail_time_max\":"
				+ no_response_waitting_fail_time_max + ",\n");

		// 说明是对任意web url的抓取
		if (this.flag == 2) {
			sb.append("\"random_url_output_path_body\":\""
					+ random_url_output_path_body + "\",\n");
			sb.append("\"random_url_output_path_capture\":\""
					+ random_url_output_path_capture + "\",\n");
		}

		sb.append("\"body_pic_or_txt_count\":" + body_pic_or_txt_count + "\n");
		sb.append("}");

		return sb.toString();
	}

	// 入口的root_url
	private String root_url;
	// 是否注入jquery.js文件
	private boolean is_inject_jquery;
	// jquery.js的文件
	private String jquery_path;
	// userAgent代码
	private String userAgent;
	// 搜索的关键词
	private String search_keyword;
	// 是否捕捉图片并存储
	private boolean is_capture_pic;
	// 捕捉图片后保存的根据路径，是所有不同任务的根目录
	private String pic_capture_save_root_path;
	// 某个任务执行中，捕捉图片后保存的指定任务
	private String pic_capture_save_sub_path;
	// 图片名称的前缀
	private String pic_file_prefix_name;
	// 图片名称的后缀
	private String pic_file_suffix_name;
	// 指定要抓取的最大网页数量
	private int max_page_number;
	// 是否将抓取到的文本保存到文件
	private boolean is_data_write_to_file;
	// 将文本写到的文件路径的根据路径，所有的文本抓取数据均已在根路径中
	private String data_write_to_file_root_path;
	// 保存到的文件的前缀名称
	private String data_file_prefix_name;
	// 每个任务的文本数据保存到的根目录
	private String data_write_to_file_sub_path;
	// 保存到的文件的后缀名称
	private String data_file_suffix_name;
	// 请求过程中，没有任何数据时的最长等待时间
	private int no_response_waitting_time_max;

	// 最多可以重复‘等待超过最长时间’的次数，有可能是网络的原因，故要重复请求一下
	private int no_response_waitting_fail_time_max = 2;

	public int getNo_response_waitting_fail_time_max() {
		return no_response_waitting_fail_time_max;
	}

	public void setNo_response_waitting_fail_time_max(
			int noResponseWaittingFailTimeMax) {
		no_response_waitting_fail_time_max = noResponseWaittingFailTimeMax;
	}

	public int getNo_response_waitting_time_max() {
		return no_response_waitting_time_max;
	}

	public void setNo_response_waitting_time_max(int noResponseWaittingTimeMax) {
		no_response_waitting_time_max = noResponseWaittingTimeMax;
	}

	// 在抓取单个url的body正文时，由于一个关键字产生的N多正文是分进程抓取的，所以要在每个URL的图片或文本文件中计数,下面的专为此事
	private int body_pic_or_txt_count;

	public int getBody_pic_or_txt_count() {
		return body_pic_or_txt_count;
	}

	public void setBody_pic_or_txt_count(int bodyPicOrTxtCount) {
		body_pic_or_txt_count = bodyPicOrTxtCount;
	}

	public String getRoot_url() {
		return root_url;
	}

	public void setRoot_url(String rootUrl) {
		root_url = rootUrl;
	}

	public boolean isIs_inject_jquery() {
		return is_inject_jquery;
	}

	public void setIs_inject_jquery(boolean isInjectJquery) {
		is_inject_jquery = isInjectJquery;
	}

	public String getJquery_path() {
		return jquery_path;
	}

	public void setJquery_path(String jqueryPath) {
		jquery_path = jqueryPath;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public boolean isIs_capture_pic() {
		return is_capture_pic;
	}

	public void setIs_capture_pic(boolean isCapturePic) {
		is_capture_pic = isCapturePic;
	}

	public String getPic_capture_save_root_path() {
		return pic_capture_save_root_path;
	}

	public void setPic_capture_save_root_path(String picCaptureSaveRootPath) {
		pic_capture_save_root_path = picCaptureSaveRootPath;
	}

	public String getPic_capture_save_sub_path() {
		return pic_capture_save_sub_path;
	}

	public void setPic_capture_save_sub_path(String picCaptureSaveSubPath) {
		pic_capture_save_sub_path = picCaptureSaveSubPath;
	}

	public String getPic_file_prefix_name() {
		return pic_file_prefix_name;
	}

	public void setPic_file_prefix_name(String picFilePrefixName) {
		pic_file_prefix_name = picFilePrefixName;
	}

	public String getPic_file_suffix_name() {
		return pic_file_suffix_name;
	}

	public void setPic_file_suffix_name(String picFileSuffixName) {
		pic_file_suffix_name = picFileSuffixName;
	}

	public int getMax_page_number() {
		return max_page_number;
	}

	public void setMax_page_number(int maxPageNumber) {
		max_page_number = maxPageNumber;
	}

	public boolean isIs_data_write_to_file() {
		return is_data_write_to_file;
	}

	public void setIs_data_write_to_file(boolean isDataWriteToFile) {
		is_data_write_to_file = isDataWriteToFile;
	}

	public String getData_write_to_file_root_path() {
		return data_write_to_file_root_path;
	}

	public void setData_write_to_file_root_path(String dataWriteToFileRootPath) {
		data_write_to_file_root_path = dataWriteToFileRootPath;
	}

	public String getData_file_prefix_name() {
		return data_file_prefix_name;
	}

	public void setData_file_prefix_name(String dataFilePrefixName) {
		data_file_prefix_name = dataFilePrefixName;
	}

	public String getData_write_to_file_sub_path() {
		return data_write_to_file_sub_path;
	}

	public void setData_write_to_file_sub_path(String dataWriteToFileSubPath) {
		data_write_to_file_sub_path = dataWriteToFileSubPath;
	}

	public String getData_file_suffix_name() {
		return data_file_suffix_name;
	}

	public void setData_file_suffix_name(String dataFileSuffixName) {
		data_file_suffix_name = dataFileSuffixName;
	}

	public String getSearch_keyword() {
		return search_keyword;
	}

	public void setSearch_keyword(String searchKeyword) {
		search_keyword = searchKeyword;
	}

	public static void main(String[] args) {
		String url = "http://www.baidu.com/";
		String keyword = "成功";

		CrawlConfigParaPojo crawlConfigParaPojo = new CrawlConfigParaPojo(url,
				keyword, 0);

		crawlConfigParaPojo.getData_write_to_file_sub_path();

		// crawlConfigParaPojo.setRoot_url("http://www.baidu.com/");
		// crawlConfigParaPojo.setIs_inject_jquery(true);
		// crawlConfigParaPojo.setJquery_path("jquery-2.1.0.min.js");
		// crawlConfigParaPojo
		// .setUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko)Safari/534.34");
		// crawlConfigParaPojo.setSearch_keyword("减肥瘦身");
		// crawlConfigParaPojo.setIs_capture_pic(true);
		// crawlConfigParaPojo.setPic_capture_save_root_path("picture/");
		// crawlConfigParaPojo.setPic_capture_save_sub_path("keyword/");
		// crawlConfigParaPojo.setPic_file_prefix_name("capture-");
		// crawlConfigParaPojo.setPic_file_suffix_name(".png");
		// crawlConfigParaPojo.setMax_page_number(5);
		// crawlConfigParaPojo.setIs_data_write_to_file(true);
		// crawlConfigParaPojo.setData_write_to_file_root_path("crawlData/");
		// crawlConfigParaPojo.setData_write_to_file_sub_path("keyword_txt/");
		// crawlConfigParaPojo.setData_file_prefix_name("page-");
		// crawlConfigParaPojo.setData_file_suffix_name(".txt");

		System.out.println(crawlConfigParaPojo);

	}
}
