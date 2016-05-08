package ws.client.es.index;

import java.util.LinkedList;
import java.util.List;

import com.zel.es.manager.ws.client.index.ESIndexServiceManager;
import com.zel.es.pojos.index.CrawlData4SnsWeiboDoc;
import com.zel.es.pojos.statics.StaticValue4SearchCondition;

public class TestEsIndex {
	public static void main(String[] args) {
		List<CrawlData4SnsWeiboDoc> pojoList = new LinkedList<CrawlData4SnsWeiboDoc>();
		ESIndexServiceManager.addBatchIndex4WeiboDoc(
				StaticValue4SearchCondition.index_name_yuqing,
				StaticValue4SearchCondition.type_name_weibo_doc, pojoList);
		System.out.println("done!");
	}
}
