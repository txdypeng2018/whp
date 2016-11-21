package com.proper.enterprise.isj.webservices.model.res.reportinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 检查/检验报告详情
 */
@XmlRootElement(name = "REPORT_INFO")
@XmlAccessorType(XmlAccessType.FIELD)
public class Report implements Serializable {

    /**
     * 检查/检验报告单号
     */
    @XmlElement(name = "REPORT_ID")
    private String reportId;

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
     * 报告类型：0-普通检验报告
     *          1-药敏检验报告
     *          2-检查报告
     */
    @XmlElement(name = "REPORT_TYPE")
    private int reportType;

    /**
     * 备注
     */
    @XmlElement(name = "REMARK")
    private String remark;

    /**
     * 报告状态
     */
    @XmlElement(name = "STATE")
    private String state;


    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
