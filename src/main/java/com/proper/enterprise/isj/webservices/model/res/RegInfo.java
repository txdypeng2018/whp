package com.proper.enterprise.isj.webservices.model.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegInfo implements Serializable {

    /**
     * 医院ID 必填
     */
    @XmlElement(name = "HOS_ID")
    private String hosId;

    /**
     * 科室ID 必填
     */
    @XmlElement(name = "DISTRICT")
    private String district;

    /**
     * 科室ID 必填
     */
    @XmlElement(name = "DEPT_ID")
    private String deptId;

    /**
     * 科室 如:中医科门诊
     */
    @XmlElement(name = "DEPT_NAME")
    private String deptName;

    /**
     * 诊室 如:中医科门诊11诊室
     */
    @XmlElement(name = "ROOM_NAME")
    private String roomName;

    /**
     * 有排班医生集合 必填
     */
    @XmlElement(name = "REG_DOCTOR_LIST")
    private List<RegDoctor> regDoctorList = new ArrayList<>();

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public List<RegDoctor> getRegDoctorList() {
        return regDoctorList;
    }

    public void setRegDoctorList(List<RegDoctor> regDoctorList) {
        this.regDoctorList = regDoctorList;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
