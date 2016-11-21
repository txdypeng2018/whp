package com.proper.enterprise.isj.webservices.model.req;

import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.QueryType;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;

import java.util.Date;

import static com.proper.enterprise.platform.core.utils.DateUtil.safeClone;

public class PayListReq {

    /**
     * 医院 ID 必填
     */
    private String hosId;

    /**
     * 用户院内ID
     */
    private String hospPatientId;

    /**
     * 用户证件类型，详情见 “证件类型”
     */
    private IDCardType idcardType;

    /**
     * 用户证件号码
     */
    private String idcardNo;

    /**
     * 用户卡类型，详见 “卡类型”
     */
    private CardType cardType;

    /**
     * 用户卡号
     */
    private String cardNo;

    /**
     * 用户姓名 必填
     */
    private String patientName;

    /**
     * 用户性别，详见 “性别” 必填
     */
    private Sex patientSex;

    /**
     * 用户年龄 必填
     */
    private int patientAge;

    /**
     * 查询状态类型 必填
     */
    private QueryType queryType;

    /**
     * 起始日期，格式：YYYY-MM-DD 必填
     */
    private Date beginDate;

    /**
     * 结束日期，格式：YYYY-MM-DD 必填
     */
    private Date endDate;

    /**
     * 门诊流水号,-1:查询全部,其他查询具体
     */
    private String clinicCode = String.valueOf(-1);


    /**
     * 患者手机号
     */
    private String mobile = "";

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

    public IDCardType getIdcardType() {
        return idcardType;
    }

    public void setIdcardType(IDCardType idcardType) {
        this.idcardType = idcardType;
    }

    public String getIdcardNo() {
        return idcardNo;
    }

    public void setIdcardNo(String idcardNo) {
        this.idcardNo = idcardNo;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public Date getBeginDate() {
        return safeClone(beginDate);
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = safeClone(beginDate);
    }

    public Date getEndDate() {
        return safeClone(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = safeClone(endDate);
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
