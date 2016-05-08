package test.spider;

import java.util.List;

import com.tianliang.spider.iface.rule.IResultPojo;
import com.tianliang.spider.manager.controler.ControlerManager;
import com.tianliang.spider.manager.metasearch.MetaSearchManager;
import com.zel.spider.pojos.CrawlTaskPojo;
import com.zel.spider.pojos.enums.CrawlEngineEnum;
import com.zel.spider.pojos.enums.SearchEngineEnum;

/**  
 * 规则测试类 
 *  
 * @author zel
 * 
 */
public class TestParserRule4MySelf {
	public static void testWebpage() {
		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		// taskPojo.setValue("http://news.sina.com.cn/c/2015-03-01/150031554777.shtml");
		taskPojo.setValue("http://blog.sina.com.cn/s/blog_4cedc5290102vst0.html");
		taskPojo.setSource_title("新浪新闻");

		// taskPojo.setTopN(100);
		// taskPojo.setValue("http://news.qq.com/a/20150303/049418.htm");
		// taskPojo.setSource_title("腾讯新闻");
		// taskPojo.setValue("http://news.sina.com.cn/c/2015-03-05/211831573365.shtml");
		// taskPojo.setValue("http://www.10yan.com/2015/0327/178943.shtml");
		// taskPojo.setSource_title("10-Yan");
		// taskPojo.setValue("http://www.ditiezu.com/thread-408594-1-1.html");
		// taskPojo.setSource_title("地铁一族");
		// taskPojo.setValue("http://bbs.365jilin.com/thread-1140404-1-1.html");
		// taskPojo.setSource_title("吉和网");

		// taskPojo.setValue("http://entertainment.anhuinews.com/system/2015/03/31/006738260.shtml");
		// taskPojo.setSource_title("中安在线");

		// taskPojo.setValue("http://club.kdnet.net/dispbbs.asp?f=w&ctid=164380&boardid=1&id=10799278");
		// taskPojo.setValue("http://club.kdnet.net/index.asp");
		// taskPojo.setSource_title("凯迪网络");

		// 以下为测试唐旺规则库的case
		// taskPojo.setValue("http://www.ce.cn/xwzx/gnsz/gdxw/201504/02/t20150402_5012990.shtml");
		// taskPojo.setSource_title("中国经济网新闻");

		// taskPojo.setValue("http://gov.hebnews.cn/2015-04/02/content_4673486.htm");
		// taskPojo.setSource_title("河北新闻网");

		// taskPojo.setValue("http://news.takungpao.com/world/exclusive/2015-04/2963269.html");
		// taskPojo.setSource_title("大公资讯");

		// taskPojo.setValue("http://www.chinanews.com/gn/2015/04-02/7180746.shtml");
		// taskPojo.setSource_title("中国新闻网");

		// taskPojo.setValue("http://world.huanqiu.com/article/2015-04/6080883.html");
		// taskPojo.setSource_title("环球网");

		// taskPojo.setValue("http://news.xinhuanet.com/politics/2015-04/02/c_1114855074.htm");
		// taskPojo.setSource_title("新华网");

		// taskPojo.setValue("http://www.cyzone.cn/a/20150331/271575.html");
		// taskPojo.setSource_title("创业邦");

		// taskPojo.setValue("http://zhidao.baidu.com/question/1447771327908157300.html?push=asking&entry=qb_home_new");
		// taskPojo.setSource_title("百度知道");

		// taskPojo.setValue("http://paper.people.com.cn/rmrb/html/2015-04/09/nw.D110000renmrb_20150409_2-07.htm");
		// taskPojo.setSource_title("人民日报电子版");

		// taskPojo.setValue("http://bbs.shangdu.com/t/20150405/0100800158077/58077-1.htm");
		// taskPojo.setSource_title("商都社区");

		// taskPojo.setValue("http://zhidao.baidu.com/");
		// taskPojo.setSource_title("百度知道");
		// taskPojo.setTopN(100);

		// taskPojo.setValue("http://epaper.gmw.cn/gmrb/");
		// taskPojo.setValue("http://epaper.jinghua.cn/html/2015-04/09/content_187998.htm");
		// taskPojo.setSource_title("日报电子版");
		// taskPojo.setTopN(100);

		/**
		 * 20150525新增
		 */
		// taskPojo.setValue("http://blog.sina.com.cn/s/blog_dd887ce40102vmfp.html");
		// taskPojo.setSource_title("中华网社区");

		ControlerManager.processTask(taskPojo, true);

		System.out.println("done!");
	}

	public static void testSearchEngineCrawl() {
		CrawlTaskPojo taskPojo = new CrawlTaskPojo();
		// taskPojo.setValue("123网址之家");
		taskPojo.setValue("0123");
		// taskPojo.setSource_title("搜狗元搜索");
		taskPojo.setSource_title("微信搜索");
		taskPojo.setCrawlEngine(CrawlEngineEnum.MetaSearch_NEWSPage);

		// SearchEngineEnum[] searchEngineEnumArray = { SearchEngineEnum.Sogou,
		// SearchEngineEnum.Baidu, SearchEngineEnum.QiHu360,
		// SearchEngineEnum.WeiXin };
		SearchEngineEnum[] searchEngineEnumArray = { SearchEngineEnum.WeiXin };

		// ControlerManager.processTask(taskPojo, true);
		List<IResultPojo> resultPojoList = MetaSearchManager.processTask(
				taskPojo, true, searchEngineEnumArray);

		System.out.println("resultPojoList size---" + resultPojoList.size());

		for (IResultPojo resultPojo : resultPojoList) {
			System.out.println(resultPojo);
		}
	}

	public static void main(String[] args) {
		testWebpage();
		// testSearchEngineCrawl();

		System.out.println("done!");
	}
}
