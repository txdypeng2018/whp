package com.proper.enterprise.isj.proxy.document;

import java.io.Serializable;

/**
 * Created by think on 2016/8/16 0016. 医生列表
 */
public class DoctorDocument implements Serializable {

    private static final long serialVersionUID = -1l;

    private String id;

    public DoctorDocument() { }

    public DoctorDocument(String name) {
        this.name = name;
    }

    /**
     * 姓名
     */
    private String name = "";

    /**
     * 性别编码 0:女,1:男,2:保密,3:其他
     */
    private String sexCode = "";

    /**
     * 职称名称
     */
    private String title = "";

    /**
     * 院区Id
     */
    private String districtId = "";

    /**
     * 院区名称
     */
    private String district = "";

    /**
     * 出诊科室Id(需要单独查询)
     */
    private String deptId = "";

    /**
     * 出诊科室诊室(需要单独查询)
     */
    private String dept = "";

    /**
     * 擅长
     */
    private String skill = "";

    /**
     * 简介
     */
    private String summary = "";

    /**
     *
     */
    private String hospital = "";

    /**
     * 身份证号
     */
    private String idCard = "";

    /**
     * 挂号费用，单位：分
     */
    private String regFee = "";

    /**
     * 状态，详见 “状态” 1:正常 2:注销
     * 
     */
    private String status = "";

    /**
     * 出生日期,格式：YYYY-MM-DD
     */
    private String birthday = "";

    /**
     * 手机号
     */
    private String phone = "";

    /**
     * 座机号
     */
    private String workPhone = "";

    /**
     * 照片
     */
    private String personPic = "";

    /**
     *有号点不能挂号时,此字段进行赋值
     */
    private String unallowed = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSexCode() {
        return sexCode;
    }

    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRegFee() {
        return regFee;
    }

    public void setRegFee(String regFee) {
        this.regFee = regFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getPersonPic() {
        return personPic;
    }

    public void setPersonPic(String personPic) {
        this.personPic = personPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnallowed() {
        return unallowed;
    }

    public void setUnallowed(String unallowed) {
        this.unallowed = unallowed;
    }
}
