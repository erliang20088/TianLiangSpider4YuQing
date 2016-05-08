package test.spider;

import com.tianliang.spider.manager.controler.ControlerManager;
import com.zel.spider.pojos.CrawlTaskPojo;
 
/**
 * 规则测试类
 * 
 * @author zel  
 * 
 */
public class TestParserRule4Everyone {
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
		// args = new String[2];
		// args[0] = "http://news.sina.com.cn/c/2015-03-06/114831575946.shtml";
		if (args == null || args.length == 0) {
			System.out.println("提供的参数不正确，请检查!");
			System.out.println("ussage:");
			System.out.println("java -jar myName.jar url title");
			System.out.println("至少得填写url字段值!");

			System.exit(-1);
		}
		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		// taskPojo.setValue("http://news.sina.com.cn/c/2015-03-06/134531576211.shtml");
		// taskPojo.setSource_title("新闻中心——国际");
		taskPojo.setValue(args[0]);
		if (args.length > 1) {
			taskPojo.setSource_title(args[1]);
		}

		ControlerManager.processTask(taskPojo, true);

		System.out.println("done!");
	}
}
