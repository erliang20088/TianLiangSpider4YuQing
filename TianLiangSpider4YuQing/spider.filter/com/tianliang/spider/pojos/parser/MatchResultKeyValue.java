package com.tianliang.spider.pojos.parser;

public class MatchResultKeyValue {
	@Override
	public String toString() {
		return "MatchResultKeyValue [fieldKey=" + fieldKey + ", value=" + value
				+ "]";
	}

	private String fieldKey;
	private String value;

	public String getFieldKey() {
		return fieldKey;
	}

	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
