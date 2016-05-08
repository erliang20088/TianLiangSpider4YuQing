package test.nlp;

import com.tianliang.spider.utils.HtmlParserUtil;
import com.tianliang.spider.utils.IOUtil;

public class RandomTest {

	public static void main(String[] args) throws Exception {
		String fromUrl = "http://www.baidu.com/1.html";
		String htmlSource = IOUtil.readDirOrFile("d:/test.txt", "utf-8");

		HtmlParserUtil htmlParserUtil = new HtmlParserUtil();
		String jumpUrl = htmlParserUtil.getRefreshLocationUrl(fromUrl,
				htmlSource);
		System.out.println(jumpUrl);
	}
}
