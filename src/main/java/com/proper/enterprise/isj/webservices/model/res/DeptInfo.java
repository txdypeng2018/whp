package com.proper.enterprise.isj.webservices.model.res;

import com.proper.enterprise.isj.webservices.model.res.deptinfo.Dept;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeptInfo implements Serializable {

    /**
     * 医院ID 必填
     */
    @XmlElement(name = "HOS_ID")
    private String hosId;

    /**
     * 科室信息集合 必填
     */
    @XmlElement(name = "DEPT_LIST")
    private List<Dept> deptList = new ArrayList<>();

    public List<Dept> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<Dept> deptList) {
        this.deptList = deptList;
    }

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }
}
