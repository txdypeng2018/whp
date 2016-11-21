package com.proper.enterprise.isj.webservices.model.res;

import com.proper.enterprise.isj.webservices.model.enmus.HosLevel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class HosInfo implements Serializable {

	/**
	 * 医院ID 必填
	 */
	@XmlElement(name = "HOS_ID")
	private String hosId;

	/**
	 * 医院名称 必填
	 */
	@XmlElement(name = "NAME")
	private String name;

	/**
	 * 医院名称简称 必填
	 */
	@XmlElement(name = "SHORT_NAME")
	private String shortName;

	/**
	 * 医院地址 必填
	 */
	@XmlElement(name = "ADDRESS")
	private String address;

	/**
	 * 联系电话 必填
	 */
	@XmlElement(name = "TEL")
	private String tel;

	/**
	 * 医院网站
	 */
	@XmlElement(name = "WEBSITE")
	private String website;

	/**
	 * 医院微博地址
	 */
	@XmlElement(name = "WEIBO")
	private String weibo;

	/**
	 * 医院等级，详见 “医院等级” 必填
	 */
	@XmlElement(name = "LEVEL")
	private int level;

	/**
	 * 医院介绍
	 */
	@XmlElement(name = "DESC")
	private String desc;

	/**
	 * 医院专长
	 */
	@XmlElement(name = "SPECIAL")
	private String special;

	/**
	 * 医院所在位置经度，例：113.452472
	 */
	@XmlElement(name = "LONGITUDE")
	private String longitude;

	/**
	 * 医院所在位置纬度，例：23.111814
	 */
	@XmlElement(name = "LATITUDE")
	private String latitude;

	/**
	 * 最大预约天数 必填
	 */
	@XmlElement(name = "MAX_REG_DAYS")
	private int maxRegDays;

	/**
	 * 开始预约时间，格式：HH:MI
	 */
	@XmlElement(name = "START_REG_TIME")
	private String startRegTime;

	/**
	 * 停止预约时间，格式：HH:MI
	 */
	@XmlElement(name = "END_REG_TIME")
	private String endRegTime;

	/**
	 * 上午停止取号时间，格式：HH:MI
	 */
	@XmlElement(name = "STOP_BOOK_TIMEA")
	private String stopBookTimeA;

	/**
	 * 下午停止取号时间，格式：HH:MI
	 */
	@XmlElement(name = "STOP_BOOK_TIMEP")
	private String stopBookTimeP;

	public String getHosId() {
		return hosId;
	}

	public void setHosId(String hosId) {
		this.hosId = hosId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public HosLevel getLevel() {
		return HosLevel.codeOf(level);
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSpecial() {
		return special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public int getMaxRegDays() {
		return maxRegDays;
	}

	public void setMaxRegDays(int maxRegDays) {
		this.maxRegDays = maxRegDays;
	}

	public String getStartRegTime() {
		return startRegTime;
	}

	public void setStartRegTime(String startRegTime) {
		this.startRegTime = startRegTime;
	}

	public String getEndRegTime() {
		return endRegTime;
	}

	public void setEndRegTime(String endRegTime) {
		this.endRegTime = endRegTime;
	}

	public String getStopBookTimeA() {
		return stopBookTimeA;
	}

	public void setStopBookTimeA(String stopBookTimeA) {
		this.stopBookTimeA = stopBookTimeA;
	}

	public String getStopBookTimeP() {
		return stopBookTimeP;
	}

	public void setStopBookTimeP(String stopBookTimeP) {
		this.stopBookTimeP = stopBookTimeP;
	}
}
