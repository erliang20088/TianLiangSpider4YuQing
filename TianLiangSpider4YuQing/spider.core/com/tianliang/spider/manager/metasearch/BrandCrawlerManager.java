package com.tianliang.spider.manager.metasearch;

import java.io.BufferedReader;
import java.util.Random;

import com.tianliang.spider.crawler.brand.BrandCrawler;
import com.tianliang.spider.pojos.BrandPojo;
import com.tianliang.spider.utils.DBUtils;
import com.tianliang.spider.utils.IOUtil;
import com.tianliang.spider.utils.MyLogger;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;

/**
 * 元搜索抓取引擎管理器
 * 
 * @author zel
 * 
 */
public class BrandCrawlerManager {
	// 日志
	public static MyLogger logger = new MyLogger(BrandCrawlerManager.class);
	// 搜索引擎使用计数，为了节省时间，暂时规则性只抓取任选一个搜索引擎的结果
	public static int random_max_int = 200;
	public static Random randomUtil = new Random();
	private static String url_format = "http://g.chofn.com/guanjia/Member/tmView?&id=${register_number}&tmclass=${classify_number}";
	private static DBUtils dbUtil = new DBUtils();

	public static void processTask(String inputFilePathCsv, String from_source) {
		BufferedReader br = IOUtil.getBufferedReader(inputFilePathCsv,
				StaticValue.default_encoding);
		String line = null;
		try {
			String[] column_array = null;
			String register_number = null;
			String classify_number = null;
			String temp_url = null;
			while ((line = br.readLine()) != null) {
				if (StringOperatorUtil.isNotBlank(line)) {
					column_array = line.split(",");
					if (column_array.length == 2) {
						register_number = column_array[0];
						if (register_number.trim().equals("注册号")) {
							continue;
						}
						classify_number = column_array[1];
						temp_url = url_format.replace("${register_number}",
								register_number).replace("${classify_number}",
								classify_number);
						// System.out.println("temp_url=" + temp_url);
						try {
							// 得到指定hurl对应的商标信息
							BrandPojo brandPojo = crawlOnePage(temp_url,
									from_source);
							// 将该信息插入到数据库中，暂定为mysql中
							saveBrandItemToDB(brandPojo);
							logger.info("success to process \nregister number="
									+ register_number + "\n"
									+ "classify_number=" + classify_number
									+ "\ntemp_url=" + temp_url);
						} catch (Exception e) {
							logger.info("fail to process \nregister number="
									+ register_number + "\n"
									+ "classify_number=" + classify_number
									+ "\ntemp_url=" + temp_url);
							e.printStackTrace();
						}
					} else {
						logger.info(line
								+ ",input line is not a csv line,please check!");
					}
				}
				// 每抓完一条数据，暂定休息1s
				Thread.sleep(1000);
				// break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BrandPojo crawlOnePage(String url, String from_source) {
		BrandPojo brandPojo = BrandCrawler.crawlOnePage(url, from_source);
		return brandPojo;
	}

	public static boolean saveBrandItemToDB(BrandPojo brandPojo) {
		String temp_sql = "insert into brand_baseinfo(" + "image_url" + ",name"
				+ ",register_or_apply_number" + ",classify_number"
				+ ",apply_date" + ",classify_level"
				+ ",product_or_service_content" + ",apply_color"
				+ ",is_common_use" + ",international_register_date"
				+ ",after_specify_date" + ",priority_date"
				+ ",apply_username_chinese" + ",apply_address_chinese"
				+ ",apply_username_english" + ",apply_address_english"
				+ ",agent_company" + ",progress_apply_date"
				+ ",progress_first_check_publish_number"
				+ ",progress_regsiter_publish_number"
				+ ",progress_first_check_publish_date"
				+ ",progress_regsiter_publish_date"
				+ ",progress_regsiter_have_three_years_publish_date"
				+ ",progress_regsiter_date,progress_deadline_date"
				+ ",newest_message" + ",from_source)" + "values ('"
				+ brandPojo.getImage_url()
				+ "','"
				+ brandPojo.getName()
				+ "','"
				+ brandPojo.getRegister_or_apply_number()
				+ "','"
				+ brandPojo.getClassify_number()
				+ "','"
				+ brandPojo.getApply_date()
				+ "','"
				+ brandPojo.getClassify_level()
				+ "','"
				+ brandPojo.getProduct_or_service_content()
				+ "','"
				+ brandPojo.getApply_color()
				+ "','"
				+ brandPojo.getIs_common_use()
				+ "','"
				+ brandPojo.getInternational_register_date()
				+ "','"
				+ brandPojo.getAfter_specify_date()
				+ "','"
				+ brandPojo.getPriority_date()
				+ "','"
				+ brandPojo.getApply_username_chinese()
				+ "','"
				+ brandPojo.getApply_address_chinese()
				+ "','"
				+ brandPojo.getApply_username_english()
				+ "','"
				+ brandPojo.getApply_address_english()
				+ "','"
				+ brandPojo.getAgent_company()
				+ "','"
				+ brandPojo.getProgress_apply_date()
				+ "','"
				+ brandPojo.getProgress_first_check_publish_number()
				+ "','"
				+ brandPojo.getProgress_regsiter_publish_number()
				+ "','"
				+ brandPojo.getProgress_first_check_publish_date()
				+ "','"
				+ brandPojo.getProgress_regsiter_publish_date()
				+ "','"
				+ brandPojo
						.getProgress_regsiter_have_three_years_publish_date()
				+ "','"
				+ brandPojo.getProgress_regsiter_date()
				+ "','"
				+ brandPojo.getProgress_deadline_date()
				+ "','"
				+ brandPojo.getNewest_message()
				+ "','"
				+ brandPojo.getFrom_source() + "')";
		boolean flag = false;
		try {
			// System.out.println("temp_sql="+temp_sql);
			flag = dbUtil.getStat().execute(temp_sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	public static void main(String[] args) {
		// String url =
		// "http://g.chofn.com/guanjia/Member/tmView?&id=1077616&tmclass=33";
		String inputFilePathCsv = "brand_input.csv";
		String from_source = "超凡商标管家";
		BrandCrawlerManager.processTask(inputFilePathCsv, from_source);
		System.out.println("done!");
	}
}
