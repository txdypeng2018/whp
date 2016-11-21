package com.proper.enterprise.isj.proxy.enums;

/**
 * Created by think on 2016/9/21 0021.
 */
public enum DistrictEnum {

	/**
	 * 南湖
	 */
	NANHU("1"),
	/**
	 * 滑翔
	 */
	HUAXIANG("2"),

	/**
	 * 沈北
	 */
	SHENBEI("9");

	private String value;

	private DistrictEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
