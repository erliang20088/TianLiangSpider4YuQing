package com.tianliang.spider.pojos;


/**
 * 对nutch的Content对象的封装类
 * 
 * @author zel
 */
public class ContentPojo {
	private String url;

	private byte[] byteArray;
	public byte[] getByteArray() {
		return byteArray;
	}

	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	private String charset;
	private String title;

	private String content_type_string;
	
	public String getContent_type_string() {
		return content_type_string;
	}

	public void setContent_type_string(String contentTypeString) {
		content_type_string = contentTypeString;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public ContentPojo(byte[] byteArray,String content_type_string) {
		this.byteArray=byteArray;
		this.content_type_string=content_type_string;
		
	}
}
