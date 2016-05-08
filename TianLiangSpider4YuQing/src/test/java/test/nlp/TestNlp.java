package test.nlp;

import com.zel.es.manager.nlp.TianLiangThemeExtractorManager;
   
public class TestNlp { 
	public static void main(String[] args) {
		String sentence = "成功快递!"; 
		// System.out.println(AnsjSegManager.split4Nlp(sentence));
		// System.out
		// .println(TianLiangAnalyzerManager.getSplitPOSResult(sentence));
		// System.out.println(TianLiangBodyExtractorManager.getBody(sentence));
		// System.out.println(TianLiangEmotionCalcManager
		// .getSentencePolar(sentence));

		// System.out.println(TianLiangKeyWordExtractorManager
		// .getKeyword(sentence));

		// System.out.println(TianLiangSummaryExtractorManager.getSummary(300,
		// null, sentence));
		System.out.println(TianLiangThemeExtractorManager
				.getThemeKeyword(sentence));
 
		System.out.println("done!");
	}
}
