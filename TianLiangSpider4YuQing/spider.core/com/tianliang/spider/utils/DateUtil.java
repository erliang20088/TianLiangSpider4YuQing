package com.tianliang.spider.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 日期操作类
 */
public class DateUtil {
	public static int dateStringLength = "yyyy-MM-dd HH:mm:ss".length();

	public SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public SimpleDateFormat yyyyMMddHHmm = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

	public static SimpleDateFormat yyyyMMddHH_NOT_ = new SimpleDateFormat(
			"yyyyMMdd");

	public static long DATEMM = 24 * 60 * 60;

	public static long getTimeNumberToday() {
		Date date = new Date();
		String str = yyyyMMdd.format(date);
		try {
			date = yyyyMMdd.parse(str);
			return date.getTime() / 1000L;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static String getTodateString() {
		Date date = new Date();
		String str = yyyyMMddHH_NOT_.format(date);
		return str;
	}

	// 得到今天的如yyyy-MM-dd的形式
	public static String getTodayFomatString() {
		Date date = new Date();
		String str = yyyyMMdd.format(date);
		return str;
	}

	public static String getYesterdayString() {
		Date date = new Date(System.currentTimeMillis() - DATEMM * 1000L);
		String str = yyyyMMddHH_NOT_.format(date);
		return str;
	}

	public static String getYesterdayStringByLine() {
		Date date = new Date(System.currentTimeMillis() - DATEMM * 1000L);
		String str = yyyyMMdd.format(date);
		return str;
	}
	
	public static int getIntDate(String dateString) {
		System.out.println("date string:" + dateString);
		String[] str = dateString.split("-");
		int all = 0;
		try {
			str[1] = ((Integer.parseInt(str[1])) < 10 ? ("0" + Integer
					.parseInt(str[1])) : str[1]);
			str[2] = ((Integer.parseInt(str[2])) < 10 ? ("0" + Integer
					.parseInt(str[2])) : str[2]);
			all = Integer.parseInt(str[0] + str[1] + str[2]);
		} catch (Exception e) {
			System.out.println("日期转化时出现错误!");
			all = 99999999;
		}
		return all;
	}

	public static Date dd = null;

	public static long getTimeLong(int years) {
		dd = new Date();
		dd.setYear(dd.getYear() + years);
		// Timestamp now2 = new Timestamp(dd.getTime());

		return dd.getTime();
	}

	public static long getLongByDate() {
		return new Date().getTime();
	}

	public Date getDateByString(String date_str) throws Exception {
		date_str = date_str.trim();
		if (date_str.length() == dateStringLength && date_str.startsWith("20")) {
			return yyyyMMddHHmmss.parse(date_str);
		} else {
			throw new Exception();
		}
	}

	public String getStringByDate(Date date) throws Exception {
		return yyyyMMddHHmmss.format(date);
	}

	String temp_time_string = null;

	public String getPhpLongTime() {
		temp_time_string = "" + new Date().getTime() + 30000;
		// System.out.println(temp_time_string);
		return temp_time_string.substring(0, temp_time_string.length() - 3);
	}

	public static long getTimeByLevel(int taskLevel) {
		switch (taskLevel) {
		case 1:
			taskLevel = 10;
			break;
		case 2:
			taskLevel = 30;
			break;
		case 3:
			taskLevel = 120;
			break;
		case 4:
			taskLevel = 240;
			break;
		case 5:
			taskLevel = 1440;
			break;
		default:
			taskLevel = 1440;
			break;
		}
		return taskLevel * 60 * 1000;
	}

	String temp_time = null;

	public Date getDateByNoneStruture4Sina(String publishTimeString)
			throws ParseException {
		if (publishTimeString.contains("\"")) {
			publishTimeString = publishTimeString.trim().substring(1,
					publishTimeString.trim().length() - 1);
		}
		if (publishTimeString.contains("月")) {
			String temp_date = "" + (1900 + new Date().getYear());
			temp_time = publishTimeString.substring(0,
					publishTimeString.indexOf("月"));
			if (temp_time.length() == 1) {
				temp_time = "0" + temp_time;
			}
			temp_date = temp_date + "-" + temp_time;
			temp_time = publishTimeString.substring(
					publishTimeString.indexOf("月") + 1,
					publishTimeString.indexOf("日"));
			if (temp_time.length() == 1) {
				temp_time = "0" + temp_time;
			}
			temp_date = temp_date + "-" + temp_time;
			temp_time = publishTimeString.substring(
					publishTimeString.indexOf(" ") + 1,
					publishTimeString.length());
			temp_date = temp_date + " " + temp_time + ":00";
			// System.out.println("时间---"+temp_time);
			// System.out.println("temp_date----"+temp_date);
			return yyyyMMddHHmmss.parse(temp_date);
		} else if (publishTimeString.contains("今天")) {
			publishTimeString = publishTimeString.substring(
					publishTimeString.indexOf(" "), publishTimeString.length())
					.trim();
			publishTimeString += ":00";
			temp_time = yyyyMMddHHmmss.format(new Date());
			temp_time = temp_time.substring(0, temp_time.indexOf(" "));
			publishTimeString = temp_time + " " + publishTimeString;

			return yyyyMMddHHmmss.parse(publishTimeString);
		} else if (publishTimeString.contains("分钟")) {
			temp_time = publishTimeString.substring(0,
					publishTimeString.indexOf('分'));
			// 通过化成ms后的相减操作来完成减多少minutes的操作
			return new Date(getLongByDate() - Integer.parseInt(temp_time) * 60
					* 1000);
		} else if (publishTimeString.contains("-")) {
			String temp_all = null;
			if (publishTimeString.length() == "yyyy-mm-dd hh:mm".length()) {
				publishTimeString = publishTimeString + ":00";
				return yyyyMMddHHmmss.parse(publishTimeString);
			} else {
				temp_time = publishTimeString.split(" ")[0];
				String[] temp_array = temp_time.split("-");
				for (String temp : temp_array) {
					if (temp.length() == 4) {
						temp_all = temp;
					} else if (temp.length() == 1) {
						temp_all += ("-0" + temp);
					} else {
						temp_all += ("-" + temp);
					}
				}
				temp_all += " " + publishTimeString.split(" ")[1] + ":00";
			}
			return yyyyMMddHHmmss.parse(temp_all);
		} else if (publishTimeString.contains("秒")) {
			return new Date();
		} else {
			return new Date();
		}
	}

	// 对新闻网页中的时间进行转换
	public Date getDateByNoneStructure4News(String publish_time_string)
			throws Exception {
		if (StringOperatorUtil.isBlank(publish_time_string)) {
			return null;
		}
		// 去除所有空白字符为空格 ，最后再做trim()
		publish_time_string = publish_time_string.replaceAll("[\\s]+", " ");
		publish_time_string = publish_time_string.trim();

		String format_publish_time_string = formatWebDateString(publish_time_string);
		// System.out.println(format_publish_time_string);
		/**
		 * 首先确定该串符合哪种模板
		 */
		if (format_publish_time_string != null) {// 说明匹配上模板了,都转化成yyyy-MM-dd
			if (format_publish_time_string
					.matches("\\d{4,4}-\\d{2,2}-\\d{2,2}\\s\\d{2,2}:\\d{2,2}:\\d{2,2}")) {
				return yyyyMMddHHmmss.parse(format_publish_time_string);
			}
		} else if (publish_time_string.matches("\\d{1,2}[\\s]*小时前")) {
			// 匹配百度中的几小时前
			publish_time_string = publish_time_string.replace("小时前", "").trim();
			try {
				return new Date(getLongByDate()
						- Integer.parseInt(publish_time_string) * 60 * 60
						* 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (publish_time_string.matches("\\d{1,2}[\\s]*分钟前")) {
			// 匹配百度中的几分钟前
			publish_time_string = publish_time_string.replace("分钟前", "").trim();
			try {
				return new Date(getLongByDate()
						- Integer.parseInt(publish_time_string) * 60 * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// 为网页版日期时间解析而添加
	public String checkFormat4DatePart(String part) {
		return part.length() == 1 ? "0" + part : part;
	}

	public String formatWebDateString(String dateString) {
		dateString = dateString.replaceAll("[年月/]", "-");
		dateString = dateString.replaceAll("日[\\s]*", " ");
		dateString = dateString
				.replaceAll("今天", DateUtil.getTodayFomatString());
		dateString = dateString.replaceAll("昨天", DateUtil.getYesterdayStringByLine());
		
		// dateString=dateString.trim();
		String[] arr = dateString.split("[\\-:\\s]");
		int len = arr.length;
		if (len >= 3) {
			if (len == 3) {
				// 说明只有日期部分或时间部分
				if (dateString.contains("-")) {// 说明是日期格式
					// 对年份的处理
					if (arr[0].length() == 2) {
						arr[0] = "20" + arr[0];
					}
					arr[1] = checkFormat4DatePart(arr[1]);
					arr[2] = checkFormat4DatePart(arr[2]);

					return arr[0] + "-" + arr[1] + "-" + arr[2] + " "
							+ "00:00:00";
				} else {// 说明是时间格式
					arr[0] = checkFormat4DatePart(arr[0]);
					arr[1] = checkFormat4DatePart(arr[1]);
					arr[2] = checkFormat4DatePart(arr[2]);

					return DateUtil.getTodayFomatString() + " " + "00:00:00";
				}
			} else if (len == 5) {
				// 对年份的处理
				if (arr[0].length() == 2) {
					arr[0] = "20" + arr[0];
				}
				// 说明包括日期和时间两部分
				arr[1] = checkFormat4DatePart(arr[1]);
				arr[2] = checkFormat4DatePart(arr[2]);

				arr[3] = checkFormat4DatePart(arr[3]);
				arr[4] = checkFormat4DatePart(arr[4]);

				return arr[0] + "-" + arr[1] + "-" + arr[2] + " " + arr[3]
						+ ":" + arr[4] + ":00";
			} else if (len == 6) {
				// 对年份的处理
				if (arr[0].length() == 2) {
					arr[0] = "20" + arr[0];
				}
				// 说明包括日期和时间两部分
				arr[1] = checkFormat4DatePart(arr[1]);
				arr[2] = checkFormat4DatePart(arr[2]);

				arr[3] = checkFormat4DatePart(arr[3]);
				arr[4] = checkFormat4DatePart(arr[4]);
				arr[5] = checkFormat4DatePart(arr[5]);

				return arr[0] + "-" + arr[1] + "-" + arr[2] + " " + arr[3]
						+ ":" + arr[4] + ":" + arr[5];
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// String date_str = "2013-03-12 11:33:5";
		DateUtil dateUtil = new DateUtil();
		/**
		 * 取得分钟前的数字
		 */
		// String minutes = date_str.substring(0, date_str.indexOf('分'));
		// System.out.println("minutes---" + minutes);
		// String publishTimeString = "(2012-12-31 16:19) ";
		// System.out.println("publishTimeString---"
		// + dateUtil.getDateByNoneStruture4Sina(publishTimeString));
		//
		// System.out.println("time long----" + new Date().getTime());
		// System.out.println(getTodateString());

		// String publis_time_String = "2015年03月01日15:00";
		// String publis_time_String = "2015-03-01 08:58";
		// String publis_time_String = "2015-03-01 10:58:42";
		// String publish_time_String = "今天 08:28";
		String publish_time_String = "昨天 08:28";
		
		Date date = dateUtil.getDateByNoneStructure4News(publish_time_String);

		System.out.println(date);
	}
}
