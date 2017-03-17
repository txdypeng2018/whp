package com.proper.enterprise.isj.proxy.document;

import java.io.Serializable;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * Created by think on 2016/8/16 0016. 医生排班时间列表（只返回有号日期）
 */
public class DoctorScheduleDocument implements Serializable {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    private String id;
    /**
     * 排班时间,example:yyyy-MM-dd
     */
    private String date;

    /**
     * 可挂号总数
     */
    private Integer total = 0;

    /**
     * 已挂号数
     */
    private Integer overCount = 0;

    /**
     * 类别编码
     */
    private String categoryCode;

    /**
     * 类别名称
     */
    private String category;

    /**
     * 挂号金额,example:7.00
     */
    private String amount;

    private String deptId;

    private String dept;

    private String districtId;

    private String district;

    private String doctorId;

    private String name;

    private String sexCode;

    private String title;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOverCount() {
        return overCount;
    }

    public void setOverCount(Integer overCount) {
        this.overCount = overCount;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
