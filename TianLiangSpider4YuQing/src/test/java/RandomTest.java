import java.util.Date;
import java.util.Random;

import com.tianliang.spider.utils.DateUtil;
import com.vaolan.extkey.utils.UrlOperatorUtil;

public class RandomTest { 

	public static String checkFormat4DatePart(String part) {
		return part.length() == 1 ? "0" + part : part;
	}

	public static String formatWebDateString(String dateString) {
		dateString = dateString.replaceAll("[年月/]", "-");
		dateString = dateString.replaceAll("日[\\s]*", " ");

		String[] arr = dateString.split("[\\-:\\s]");
		int len = arr.length;
		if (len >= 3) {
			if (len == 3) {
				// 说明只有日期部分或时间部分
				if (dateString.contains("-]")) {// 说明是日期格式
					// 年份部分不用管，一般不会出现露掉数值的情况
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
				// 说明包括日期和时间两部分
				arr[1] = checkFormat4DatePart(arr[1]);
				arr[2] = checkFormat4DatePart(arr[2]);

				arr[3] = checkFormat4DatePart(arr[3]);
				arr[4] = checkFormat4DatePart(arr[4]);

				return arr[0] + "-" + arr[1] + "-" + arr[2] + " " + arr[3]
						+ ":" + arr[4] + ":00";
			} else if (len == 6) {
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
		String host="http://www.news.sina.com.cn";
		System.out.println(UrlOperatorUtil.getDomain(host));
	}
}
