package com.proper.enterprise.isj.webservices.model.res;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 检查/检验报告列表
 */
@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 医院ID
     */
    @XmlElement(name = "HOS_ID")
    private String hosId;

    /**
     * 用户院内ID
     */
    @XmlElement(name = "HOSP_PATIENT_ID")
    private String hospPatientId;

    /**
     * 用户证件类型
     */
    @XmlElement(name = "PATIENT_IDCARD_TYPE")
    private int patientIdcardType;

    /**
     * 用户证件号码
     */
    @XmlElement(name = "PATIENT_IDCARD_NO")
    private String patientIdcardNo;

    /**
     * 用户卡类型
     */
    @XmlElement(name = "PATIENT_CARD_TYPE")
    private int patientCardType;

    /**
     * 用户卡号
     */
    @XmlElement(name = "PATIENT_CARD_NO")
    private String patientCardNo;

    /**
     * 用户姓名
     */
    @XmlElement(name = "PATIENT_NAME")
    private String patientName;

    /**
     * 用户性别
     */
    @XmlElement(name = "PATIENT_SEX")
    private String patientSex;

    /**
     * 用户年龄
     */
    @XmlElement(name = "PATIENT_AGE")
    private int patientAge;

    /**
     * 住院次数
     */
    @XmlElement(name = "VISIT_NUMBER")
    private int visitNumber;

    /**
     * 医疗保险类型（默认为自费），例：医保，自费等
     */
    @XmlElement(name = "MEDICAL_INSURANNCE_TYPE")
    private String medicalInsurannceType;

    /**
     * 用户检查/检验报告列表
     */
    @XmlElement(name = "REPORT_LIST")
    private ReportList reportList;

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

    public int getPatientIdcardType() {
        return patientIdcardType;
    }

    public void setPatientIdcardType(int patientIdcardType) {
        this.patientIdcardType = patientIdcardType;
    }

    public String getPatientIdcardNo() {
        return patientIdcardNo;
    }

    public void setPatientIdcardNo(String patientIdcardNo) {
        this.patientIdcardNo = patientIdcardNo;
    }

    public int getPatientCardType() {
        return patientCardType;
    }

    public void setPatientCardType(int patientCardType) {
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

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public int getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(int visitNumber) {
        this.visitNumber = visitNumber;
    }

    public String getMedicalInsurannceType() {
        return medicalInsurannceType;
    }

    public void setMedicalInsurannceType(String medicalInsurannceType) {
        this.medicalInsurannceType = medicalInsurannceType;
    }

    public ReportList getReportList() {
        return reportList;
    }

    public void setReportList(ReportList reportList) {
        this.reportList = reportList;
    }

}
