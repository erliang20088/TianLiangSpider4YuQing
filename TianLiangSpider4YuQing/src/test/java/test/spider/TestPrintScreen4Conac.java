package test.spider;

import com.tianliang.spider.manager.controler.ControlerManager;
import com.tianliang.spider.utils.WebPagePrintScreenUtil;
import com.zel.spider.pojos.CrawlTaskPojo;

/**
 * 测试截图功能类
 * 
 * @author zel
 * 
 */
public class TestPrintScreen4Conac {
	public static void test() {
		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		// taskPojo.setValue("http://news.sina.com.cn/c/2015-03-01/150031554777.shtml");
		// taskPojo.setSource_title("新浪新闻");
		// taskPojo.setValue("http://news.qq.com/a/20150303/049418.htm");
		// taskPojo.setSource_title("腾讯新闻");
		// taskPojo.setValue("http://news.sina.com.cn/c/2015-03-05/211831573365.shtml");
		taskPojo.setValue("http://news.sina.com.cn/c/2015-03-06/134531576211.shtml");
		taskPojo.setSource_title("新闻中心——国际");

		ControlerManager.processTask(taskPojo, true);

		System.out.println("done!");
	}

	public static void main(String[] args) {
		// if (args == null || args.length != 2) {
		// System.out.println("提供的参数不正确，请检查!");
		// System.out.println("ussage:");
		// System.out.println("java -jar myName.jar url outputPicFilePath");
		// System.out
		// .println("如：java -jar myName.jar http://www.baidu.com/ d:/test/tt"
		// + "\n" + "只提供路径和文件名称，后缀统一为png图片");
		// System.out.println("至少得填写url字段值!");
		//
		// System.exit(-1);
		// }
		// String url = args[0];
		// String aidPicFilePathString = args[1];
		String url = "http://weibo.com/2412348541/Cjcqol7VT";
//		String url = "http://www.baidu.com/";
		String aidPicFilePathString = "abcd";
		WebPagePrintScreenUtil.printScreen(url, aidPicFilePathString);
		
		System.out.println("done!");
	}
}
