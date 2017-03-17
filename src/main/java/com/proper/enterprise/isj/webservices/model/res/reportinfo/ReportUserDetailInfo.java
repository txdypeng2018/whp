package com.proper.enterprise.isj.webservices.model.res.reportinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;

import java.io.Serializable;

/**
 * 用户报告信息
 */
@XmlRootElement(name = "REPORT_INFO")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportUserDetailInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

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
     * 诊断
     */
    @XmlElement(name = "DIAGNOSIS")
    private String diagnosis;

    /**
     * 检查/检验项目
     */
    @XmlElement(name = "ITEM_NAME")
    private String itemName;

    /**
     * 标本名称
     */
    @XmlElement(name = "SPECIMEN_NAME")
    private String specimenName;

    /**
     * 标本号
     */
    @XmlElement(name = "SPECIMEN_ID")
    private String specimenId;

    /**
     * 报告时间，格式 YYYY-MM-DD HH24:MI:SS
     */
    @XmlElement(name = "REPORT_TIME")
    private String reportTime;

    /**
     * 科室名称
     */
    @XmlElement(name = "DEPT_NAME")
    private String deptName;

    /**
     * 医生名称
     */
    @XmlElement(name = "DOCTOR_NAME")
    private String doctorName;

    /**
     * 审核者
     */
    @XmlElement(name = "REVIEW_NAME")
    private String reviewName;

    /**
     * 审核时间，格式 YYYY-MM-DD HH24:MI:SS
     */
    @XmlElement(name = "REVIEW_TIME")
    private String reviewTime;

    /**
     * 备注
     */
    @XmlElement(name = "REMARK")
    private String remark;

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

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpecimenName() {
        return specimenName;
    }

    public void setSpecimenName(String specimenName) {
        this.specimenName = specimenName;
    }

    public String getSpecimenId() {
        return specimenId;
    }

    public void setSpecimenId(String specimenId) {
        this.specimenId = specimenId;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getReviewName() {
        return reviewName;
    }

    public void setReviewName(String reviewName) {
        this.reviewName = reviewName;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
