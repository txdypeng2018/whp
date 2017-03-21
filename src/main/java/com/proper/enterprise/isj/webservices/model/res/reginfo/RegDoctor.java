package com.proper.enterprise.isj.webservices.model.res.reginfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "REG_DOCTOR_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegDoctor implements Serializable {


    /**
    * 
    */
    private static final long serialVersionUID = -5623354724628029691L;

    /**
     * 医生ID
     * 必填
     */
    @XmlElement(name = "DOCTOR_ID")
    private String doctorId;

    /**
     * 医生名称
     * 必填
     */
    @XmlElement(name = "NAME")
    private String name;

    /**
     * 医生职称
     * 必填
     */
    @XmlElement(name = "JOB_TITLE")
    private String jobTitle;

    /**
     * 出诊日期集合
     * 必填
     */
    @XmlElement(name = "REG_LIST")
    private List<Reg> regList = new ArrayList<>();

    public RegDoctor() { }

    public RegDoctor(String doctorId, String name, String jobTitle) {
        this.doctorId = doctorId;
        this.name = name;
        this.jobTitle = jobTitle;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public List<Reg> getRegList() {
        return regList;
    }

    public void setRegList(List<Reg> regList) {
        this.regList = regList;
    }
}
