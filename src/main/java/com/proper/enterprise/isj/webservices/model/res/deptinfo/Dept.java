package com.proper.enterprise.isj.webservices.model.res.deptinfo;

import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.enmus.Status;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "DEPT_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dept implements Serializable {

    /**
     * ID；当 LEVEL 为 2 时此 ID 医生编码 必填
     */
    @XmlElement(name = "DEPT_ID")
    private String deptId;

    /**
     * 科室名称 必填
     */
    @XmlElement(name = "DEPT_NAME")
    private String deptName;

    /**
     * 上级科室ID，院区为 0 必填
     */
    @XmlElement(name = "PARENT_ID")
    private String parentId;

    /**
     * 备注介绍
     */
    @XmlElement(name = "DESC")
    private String desc;

    /**
     * 科室等级，0 上级科室；1 末端科室；2 代表医生
     */
    @XmlElement(name = "LEVEL")
    private int level;

    /**
     * 科室所在位置
     */
    @XmlElement(name = "ADDRESS")
    private String address;

    /**
     * 状态，0 停用；1 启用 必填
     */
    @XmlElement(name = "STATUS")
    private int status;


    /**
     * 排序号
     */
    @XmlElement(name = "SORT_ID")
    private int sortId;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DeptLevel getLevel() {
        return DeptLevel.codeOf(level);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Status getStatus() {
        return Status.codeOf(status);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }
}
