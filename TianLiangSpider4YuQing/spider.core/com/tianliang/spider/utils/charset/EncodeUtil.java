package com.tianliang.spider.utils.charset;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.tianliang.spider.manager.crawler.HttpClientPojoManager;
import com.tianliang.spider.pojos.ContentPojo;
import com.tianliang.spider.utils.HtmlParserUtil;
import com.tianliang.spider.utils.ObjectAndByteArrayConvertUtil;
import com.tianliang.spider.utils.RegexPaserUtil;
import com.tianliang.spider.utils.StaticValue;
import com.tianliang.spider.utils.StringOperatorUtil;
import com.vaolan.parser.JsoupHtmlParser;
import com.vaolan.status.DataFormatStatus;

public class EncodeUtil {
	private HtmlParserUtil htmlParserUtil;
	private RegexPaserUtil regexUtil = null;
	private CpDetectorUtil encodingDetectorUtil = null;

	public EncodeUtil() {
		htmlParserUtil = new HtmlParserUtil();
		regexUtil = new RegexPaserUtil();
		encodingDetectorUtil = new CpDetectorUtil();
	}

	public ContentPojo getWebPageCharset(byte[] byteArray,
			String content_type_string) {
		ContentPojo contentPojo = new ContentPojo(byteArray,
				content_type_string);
		if (byteArray == null || byteArray.length == 0
				|| byteArray.length <= StaticValue.url_data_min_byte_length) {// 最后的判断是去掉URL对应的数据内容太短的情况
			// 当内容很短时，我们也要拿到charset返回
			String temp_charset = getCharsetByMetadata(content_type_string);
			// 当内容极少时，则认为是无意义的内容部分，如果得不到charset，则按默认处理
			if (StringOperatorUtil.isBlank(temp_charset)) {
				temp_charset = StaticValue.SYSTEM_ENCODING;
			}
			contentPojo.setCharset(temp_charset);

			return contentPojo;
		}
		String title = null;// 暂存title
		String lastEncoding = StaticValue.SYSTEM_ENCODING;// 最后的charset返回的编码coding
		String charsetByMetadata = null;
		boolean isFoundCharset = false;
		try {
			/**
			 * 先查看content-type中是否有显式声明的charset
			 */
			charsetByMetadata = getCharsetByMetadata(contentPojo
					.getContent_type_string());
			if (charsetByMetadata != null) {// 说明从metadata是得到charset，直接用，不进行后边的计算了
				contentPojo.setCharset(charsetByMetadata);
				title = getUrlTitle(charsetByMetadata, byteArray, null);
				contentPojo.setTitle(title);
			} else {
				// 为contentPojo设置charset,此时的设置不一定是最终的charset,最终要看看使title无乱码的情况
				// 其返回值代表是否显式得到了charset
				isFoundCharset = setCharset(contentPojo, lastEncoding,
						byteArray);

				// 暂存首获得的charset和title
				String oldCharset = contentPojo.getCharset();
				String oldTitle = contentPojo.getTitle();

				if (!isFoundCharset) {
					// 此处的是否全中文判断，用默认的编码得到的是正常的，用探测的反而不行了，很奇怪，在此处做此判断
					if (regexUtil.isAllChineseChar(oldTitle)) {
						return contentPojo;
					}
					String detector_encoding = encodingDetectorUtil
							.getURLEncoding(
									(InputStream) (new ByteArrayInputStream(
											contentPojo.getByteArray())),
									contentPojo.getByteArray().length);
					if (detector_encoding != null) {
						lastEncoding = detector_encoding.toLowerCase();
					}
					contentPojo.setCharset(lastEncoding);
					// 为contentPojo设置title
					title = getUrlTitle(contentPojo.getCharset(), byteArray,
							null);
					contentPojo.setTitle(title);
				} else {
					// 处理当编码有，但old title==null的情况
					if (oldTitle == null) {
						oldTitle = getUrlTitle(oldCharset, byteArray, null);
					}
					// 解决GBK为charset,但实际为UTF-8的情况
					if (regexUtil.isAllChineseChar(oldTitle)) {
						contentPojo.setCharset(StaticValue.SYSTEM_ENCODING);
						return contentPojo;
					}
					// 解决UTF为charset,但实际为utf-8的情况，同时去除一些非中文字的情况
					if (oldCharset == StaticValue.SYSTEM_ENCODING) {// 如果得到oldCharset是UTF-8，这种情况即实际和charset不符，则默认再按GBK处理一次，默认只有国内有这种情况
						StringTemp4BodyFront bodyFront = new StringTemp4BodyFront();
						// 为contentPojo设置title
						title = getUrlTitle(StaticValue.GBK_ENCODING,
								byteArray, bodyFront);
						if (regexUtil.isContainFreqChineseChar(bodyFront
								.getBodyFront())) {
							contentPojo.setCharset(StaticValue.GBK_ENCODING);
							contentPojo.setTitle(title);
						}
					} else {// 当是除utf-8之外的时候，就是其它编码了,此时得到的编码一律认为是准确的,包括GBK等中文编码
						title = getUrlTitle(contentPojo.getCharset(),
								byteArray, null);
						contentPojo.setTitle(title);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			contentPojo.setCharset(lastEncoding);
			contentPojo.setTitle(title);
		}
		return contentPojo;
	}

	// 根据默认的编码和bytes，重到bytes的真正编码
	// 返回的boolean值，是代表该次charset是否是在网页源文直接找到的，如果是默认的还要用cpdetector进行编码的再次检测
	public boolean setCharset(ContentPojo contentPojo, String lastEncoding,
			byte[] bytes) throws Exception {
		String str = new String(bytes, lastEncoding);
		StringReader sr = new StringReader(str);
		BufferedReader br = new BufferedReader(sr);
		String line = null;
		String temp = null;
		String temp_title = null;
		boolean isFoundBody = false;
		String title = null;
		boolean isFoundCharset = false;
		// 暂存body之前的string，用来一次性来匹配title
		StringBuilder sb = new StringBuilder();

		while ((line = br.readLine()) != null && (!isFoundBody)) {
			temp = line.toLowerCase();
			if (temp.contains("<body")) {
				isFoundBody = true;
			}
			if (temp.contains("<meta") && temp.contains("charset")) {
				if (isFoundBody) {
					temp = temp.substring(0, temp.indexOf("<body"));
				}
				if (temp.contains(StaticValue.SYSTEM_ENCODING)) {
					lastEncoding = StaticValue.SYSTEM_ENCODING;
				} else if (temp.contains(StaticValue.GB2312_ENCODING)) {
					lastEncoding = StaticValue.GB2312_ENCODING;
				} else if (temp.contains(StaticValue.GBK_ENCODING)) {
					lastEncoding = StaticValue.GBK_ENCODING;
				} else if (temp.contains(StaticValue.BIG5_ENCODING)) {
					lastEncoding = StaticValue.BIG5_ENCODING;
				} else if (temp.contains(StaticValue.Xili_Window_ENCODING)) {
					lastEncoding = StaticValue.Xili_Window_ENCODING;
				} else if (temp.contains(StaticValue.Japan_Shift_ENCODING)) {
					lastEncoding = StaticValue.Japan_Shift_ENCODING;
				} else if (temp.contains(StaticValue.Japan_Euc_ENCODING)) {
					lastEncoding = StaticValue.Japan_Euc_ENCODING;
				}
				isFoundCharset = true;
			} else if (temp.contains("<html") && temp.contains("lang=")) {
				if (temp.contains(StaticValue.Japan_Lang)) {
					lastEncoding = StaticValue.Japan_Lang_First;
					isFoundCharset = true;
				}
			}
			sb.append(temp);
		}
		br.close();

		contentPojo.setCharset(lastEncoding);// 设置charset，不一定的对的，后边要再次验证

		// 取得title
		temp = sb.toString();
		if ((temp_title = htmlParserUtil.getTitleByLine(temp)) != null) {// 说明得到标题了,但不一定是正确的编码
			title = temp_title.trim();
		}
		contentPojo.setTitle(title);// 设置title,不一定是对的，后边要再次验证

		return isFoundCharset;
	}

	public static String getPageSourceCharset(String pageSource) {
		if (pageSource == null || pageSource.isEmpty()) {
			return null;
		}
		String meta_string = JsoupHtmlParser.getTagContent(pageSource, "meta",
				DataFormatStatus.TagAllContent);
		return getCharsetByMetadata(meta_string);
	}

	public static String getCharsetByMetadata(String content_type) {
		if (content_type == null) {
			return null;
		}
		// String content_type = metadata.get(Metadata.CONTENT_TYPE);
		if (content_type != null) {
			content_type = content_type.toLowerCase();
			if (content_type.contains("charset")) {
				if (content_type.contains(StaticValue.SYSTEM_ENCODING)) {
					return StaticValue.SYSTEM_ENCODING;
				} else if (content_type.contains(StaticValue.GB2312_ENCODING)) {
					return StaticValue.GB2312_ENCODING;
				} else if (content_type.contains(StaticValue.GBK_ENCODING)) {
					return StaticValue.GBK_ENCODING;
				} else if (content_type.contains(StaticValue.BIG5_ENCODING)) {
					return StaticValue.BIG5_ENCODING;
				} else if (content_type
						.contains(StaticValue.Xili_Window_ENCODING)) {
					return StaticValue.Xili_Window_ENCODING;
				} else if (content_type
						.contains(StaticValue.Japan_Shift_ENCODING)) {
					return StaticValue.Japan_Shift_ENCODING;
				} else if (content_type
						.contains(StaticValue.Japan_Euc_ENCODING)) {
					return StaticValue.Japan_Euc_ENCODING;
				}
			}
		}
		return null;
	}

	// 设置contengPojo的title
	public String getUrlTitle(String lastEncoding, byte[] bytes,
			StringTemp4BodyFront bodyFront) throws Exception {
		// 上边已得到相应编码,下边得到content的文档标题即title
		boolean isFoundBody = false;
		String str = new String(bytes, lastEncoding);
		String title = null;

		if ((title = htmlParserUtil.getTitleByLine(str)) != null) {// 说明得到标题了,但不一定是正确的编码
			title = title.trim();
		}

		if (title != null) {
			isFoundBody = true;
		}

		return title;
	}

	public String getUrlTitle_bak(String lastEncoding, byte[] bytes,
			StringTemp4BodyFront bodyFront) throws Exception {
		// 上边已得到相应编码,下边得到content的文档标题即title
		boolean isFoundBody = false;
		String line = null;
		String temp = null;
		isFoundBody = false;
		String str = new String(bytes, lastEncoding);
		StringReader sr = new StringReader(str);
		BufferedReader br = new BufferedReader(sr);
		String temp_title = null;
		String title = null;
		StringBuilder sb = new StringBuilder();

		// 先取出可能函有title的部分，一次性取出，而不是一行判断一次，主要是应对把title标答分行来写的情况
		while ((line = br.readLine()) != null && (!isFoundBody)) {
			temp = line.toLowerCase();
			if (temp.contains("<body")) {
				isFoundBody = true;
			}
			if (isFoundBody) {
				temp = temp.substring(0, temp.indexOf("<body"));
			}
			sb.append(temp);
		}
		temp = sb.toString();
		if (bodyFront != null) {
			bodyFront.setBodyFront(temp);
		}
		if ((temp_title = htmlParserUtil.getTitleByLine(temp)) != null) {// 说明得到标题了,但不一定是正确的编码
			title = temp_title.trim();
		}
		br.close();
		return title;
	}

	public static byte[] translateByteArray(byte[] bytes, String charset) {
		try {
			return new String(bytes, charset)
					.getBytes(StaticValue.SYSTEM_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 转换失败则返回旧数组
		return bytes;
	}

	public static byte[] translateStringToByteArray(String source,
			String charset) {
		if (source == null) {
			return null;
		} else {
			try {
				return source.getBytes(charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	// 暂存body标签前边的网页数据，主要是为解决UTF-8编码为表面，其实际为gbk而设制
	class StringTemp4BodyFront {
		private String bodyFront;

		public String getBodyFront() {
			return bodyFront;
		}

		public void setBodyFront(String bodyFront) {
			this.bodyFront = bodyFront;
		}
	}

	public String getCharset(String url) throws Exception {
		RequestBuilder rb = RequestBuilder.get().setUri(URI.create(url));
		HttpUriRequest urlRequest = rb.build();
		CloseableHttpResponse response = HttpClientPojoManager
				.getHttpClientPojo().getHttpClient().execute(urlRequest);

		HttpEntity entity = response.getEntity();

		byte[] byteArray = ObjectAndByteArrayConvertUtil
				.getByteArrayOutputStream(entity.getContent());

		ContentPojo contentPojo = getWebPageCharset(byteArray, entity
				.getContentType().toString());

		System.out.println(contentPojo.getCharset());
		System.out.println(contentPojo.getTitle());

		return null;
	}

	public static void main(String[] args) throws Exception {
		// String url = "http://www.altavista.ru/";
		String url = "http://www.bodyig.net/view/166.html";

		EncodeUtil encodeUtil = new EncodeUtil();
		encodeUtil.getCharset(url);
	}

}
