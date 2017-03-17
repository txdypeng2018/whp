package com.proper.enterprise.isj.webservices.model.res.doctorinfo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * Created by think on 2016/8/24 0024.
 * 
 */
@XmlRootElement(name = "DOCTOR_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class Doctor implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 科室ID
     */
    @XmlElement(name = "DEPT_ID")
    private String deptId;

    /**
     * 医生ID，HIS系统中医生唯一ID
     */
    @XmlElement(name = "DOCTOR_ID")
    private String doctorId;

    /**
     * 医生名称
     */
    @XmlElement(name = "NAME")
    private String name;

    /**
     * 医生身份证号
     */
    @XmlElement(name = "IDCARD")
    private String idCard;

    /**
     * 介绍
     */
    @XmlElement(name = "DESC")
    private String desc;

    /**
     * 医生擅长
     */
    @XmlElement(name = "SPECIAL")
    private String special;

    /**
     * 医生职称
     */
    @XmlElement(name = "JOB_TITLE")
    private String jobTitle;

    /**
     * 挂号费用，单位：分
     */
    @XmlElement(name = "REG_FEE")
    private BigDecimal regFee;

    /**
     * 状态
     */
    @XmlElement(name = "STATUS")
    private Integer status;

    /**
     * 性别
     */
    @XmlElement(name = "SEX")
    private Integer sex;

    /**
     * 出生日期，格式：YYYY-MM-DD
     */
    @XmlElement(name = "BIRTHDAY")
    private String birthday;

    /**
     * 手机号码
     */
    @XmlElement(name = "MOBILE")
    private String mobile;

    /**
     * 办公室号码
     */
    @XmlElement(name = "TEL")
    private String tel;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public BigDecimal getRegFee() {
        return regFee;
    }

    public void setRegFee(BigDecimal regFee) {
        this.regFee = regFee;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
