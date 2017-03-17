package com.proper.enterprise.isj.webservices.model.res.reginfo;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.utils.DateUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "REG_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reg implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 出诊日期，格式：YYYY-MM-DD
     * 必填
     */
    @XmlElement(name = "REG_DATE")
    private String regDate;

    /**
     * 出诊日期对应星期，如：星期五
     * 必填
     */
    @XmlElement(name = "REG_WEEKDAY")
    private String regWeekday;

    /**
     *出诊院区,返回值:1,2,6
     */
    @XmlElement(name = "REG_DISTRICT")
    private String regDistrict;
    /**
     * 出诊科室名称
     */
    @XmlElement(name = "REG_DEPTNAME")
    private String regDeptname;

    /**
     * 出诊科室的code
     */
    @XmlElement(name = "REG_DEPTCODE")
    private String regDeptcode;

    /**
     * 医生上午、下午、晚上排班信息集合
     * 必填
     */
    @XmlElement(name = "REG_TIME_LIST")
    private List<RegTime> regTimeList = new ArrayList<>();

    public Date getRegDate() {
        return DateUtil.toDate(regDate);
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegWeekday() {
        return regWeekday;
    }

    public void setRegWeekday(String regWeekday) {
        this.regWeekday = regWeekday;
    }

    public List<RegTime> getRegTimeList() {
        return regTimeList;
    }

    public void setRegTimeList(List<RegTime> regTimeList) {
        this.regTimeList = regTimeList;
    }

    public String getRegDistrict() {
        return regDistrict;
    }

    public void setRegDistrict(String regDistrict) {
        this.regDistrict = regDistrict;
    }

    public String getRegDeptname() {
        return regDeptname;
    }

    public void setRegDeptname(String regDeptname) {
        this.regDeptname = regDeptname;
    }

    public String getRegDeptcode() {
        return regDeptcode;
    }

    public void setRegDeptcode(String regDeptcode) {
        this.regDeptcode = regDeptcode;
    }
}
