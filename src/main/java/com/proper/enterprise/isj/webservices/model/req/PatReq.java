package com.proper.enterprise.isj.webservices.model.req;

import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;

/**
 * Created by think on 2016/9/14 0014.
 */
public class PatReq {

	/**
	 * 医院Id
	 */
	private String hosId;

	/**
	 * 身份证号类别
	 */
	private IDCardType cardType;

	/**
	 * 身份证号
	 */
	private String markNo;

	/**
	 * 病历号
	 */
	private String cardNo = "";

	/**
	 * 姓名
	 */
	private String name = "";

	/**
	 * 性别
	 */
	private Sex sex;

	/**
	 * 生日,格式：YYYY-MM-DD
	 */
	private String birthday;

	/**
	 * 地址
	 */
	private String address = "";

	/**
	 * 电话
	 */
	private String mobile = "";

	public String getHosId() {
		return hosId;
	}

	public void setHosId(String hosId) {
		this.hosId = hosId;
	}

	public IDCardType getCardType() {
		return cardType;
	}

	public void setCardType(IDCardType cardType) {
		this.cardType = cardType;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getMarkNo() {
		return markNo;
	}

	public void setMarkNo(String markNo) {
		this.markNo = markNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sex getSex() {
		return sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
