package com.proper.enterprise.isj.webservices.model.res.timereglist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;

import java.io.Serializable;

@XmlRootElement(name = "TIME_REG_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeReg implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 时段 1:上午 (06:00-12:00),2:下午 (12:00-18:00),3:晚上(18:00-次日06:00)
     * 
     */
    @XmlElement(name = "TIME_FLAG")
    private String timeFlag;

    /**
     * 分时开始时间，格式：HH24:MI
     */
    @XmlElement(name = "BEGIN_TIME")
    private String beginTime;

    /**
     * 分时结束时间，格式：HH24:MI
     */
    @XmlElement(name = "END_TIME")
    private String endTime;

    /**
     * 该时段可挂号源总数，不限号源数量默认传99
     */
    @XmlElement(name = "TOTAL")
    private int total = 99;

    /**
     * 该时段剩余号源数，不限号源数量默认传99
     */
    @XmlElement(name = "OVER_COUNT")
    private int overCount = 99;

    /**
     * 排班ID
     */
    @XmlElement(name = "REG_ID")
    private String regId;

    /**
     * 号点序号
     */
    @XmlElement(name = "REG_NUM")
    private String regNum;

    public String getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(String timeFlag) {
        this.timeFlag = timeFlag;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOverCount() {
        return overCount;
    }

    public void setOverCount(int overCount) {
        this.overCount = overCount;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }
}
