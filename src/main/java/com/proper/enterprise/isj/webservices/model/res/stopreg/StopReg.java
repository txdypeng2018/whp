package com.proper.enterprise.isj.webservices.model.res.stopreg;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by think on 2016/10/2 0002.
 */
@XmlRootElement(name = "STOP_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class StopReg implements Serializable {

    @XmlElement(name = "DISTRICT")
    private String district;

    @XmlElement(name = "DEPT_ID")
    private String deptId;

    @XmlElement(name = "DEPT_NAME")
    private String deptName;

    @XmlElement(name = "DOCTOR_ID")
    private String doctorId;

    @XmlElement(name = "DOCTOR_NAME")
    private String doctorName;

    @XmlElement(name = "REG_DATE")
    private String regDate;

    @XmlElement(name = "TIME_FLAG")
    private String timeFlag;

    @XmlElement(name = "BEGIN_TIME")
    private String beginTime;

    @XmlElement(name = "END_TIME")
    private String endTime;

    @XmlElement(name = "STOP_REMARK")
    private String stopRemark;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(String timeFlag) {
        this.timeFlag = timeFlag;
    }

    public String getStopRemark() {
        return stopRemark;
    }

    public void setStopRemark(String stopRemark) {
        this.stopRemark = stopRemark;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
