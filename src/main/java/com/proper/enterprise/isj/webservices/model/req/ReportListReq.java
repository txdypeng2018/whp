package com.proper.enterprise.isj.webservices.model.req;

import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;

import java.io.Serializable;

/**
 * 检验/检测报告请求对象.
 */
public class ReportListReq implements Serializable {

    /**
     * 医院ID
     */
    private String hosId = "";

    /**
     * 医院病人ID
     */
    private String hospPatientId = "";

    /**
     * 用户证件类型，详情见 “证件类型”
     */
    private IDCardType patientIdcardType;

    /**
     * 用户证件号码
     */
    private String patientIdcardNo = "";

    /**
     * 患者卡类型，详见 “卡类型”
     */
    private CardType patientCardType;

    /**
     * 患者卡号
     */
    private String patientCardNo = "";

    /**
     * 患者姓名 必填
     */
    private String patientName = "";

    /**
     * 患者性别，详见 “性别” 必填
     */
    private Sex patientSex;

    /**
     * 用户年龄 必填
     */
    private int patientAge;

    /**
     * 起始日期，格式：YYYY-MM-DD 必填
     */
    private String beginDate;

    /**
     * 结束日期，格式：YYYY-MM-DD 必填
     */
    private String endDate;

    /**
     * 用户手机号
     */
    private String patientPhone = "";

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public String getHospPatientId() {
        return hospPatientId;
    }

    public void setHospPatientId(String hospPatientId) {
        this.hospPatientId = hospPatientId;
    }

    public IDCardType getPatientIdcardType() {
        return patientIdcardType;
    }

    public void setPatientIdcardType(IDCardType patientIdcardType) {
        this.patientIdcardType = patientIdcardType;
    }

    public String getPatientIdcardNo() {
        return patientIdcardNo;
    }

    public void setPatientIdcardNo(String patientIdcardNo) {
        this.patientIdcardNo = patientIdcardNo;
    }

    public CardType getPatientCardType() {
        return patientCardType;
    }

    public void setPatientCardType(CardType patientCardType) {
        this.patientCardType = patientCardType;
    }

    public String getPatientCardNo() {
        return patientCardNo;
    }

    public void setPatientCardNo(String patientCardNo) {
        this.patientCardNo = patientCardNo;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Sex getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(Sex patientSex) {
        this.patientSex = patientSex;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
}
