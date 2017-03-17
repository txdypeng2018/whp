package com.proper.enterprise.isj.webservices.model.res.reginfo;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.webservices.model.enmus.RegStatus;
import com.proper.enterprise.isj.webservices.model.enmus.TimeFlag;
import com.proper.enterprise.platform.core.enums.WhetherType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "REG_TIME_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegTime implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 排班ID，如果存在分时，那么挂号的排班ID以分时接口里返回的排班ID为准 必填
     */
    @XmlElement(name = "REG_ID")
    private String regId;

    /**
     * 时段，详见 “时段” 必填
     */
    @XmlElement(name = "TIME_FLAG")
    private int timeFlag;

    /**
     * 出诊状态，详见 “出诊状态” 必填
     */
    @XmlElement(name = "REG_STATUS")
    private int regStatus;

    /**
     * 该时段可挂号源总数，不限号源数量默认传99 必填
     */
    @XmlElement(name = "TOTAL")
    private int total = 99;

    /**
     * 该时段剩余号源数，不限号源数量默认传99 必填
     */
    @XmlElement(name = "OVER_COUNT")
    private int overCount = 99;

    /**
     * 挂号级别，M、N 为特需
     */
    @XmlElement(name = "REG_LEVEL")
    private String regLevel;

    /**
     * 挂号费用，单位：分 必填
     */
    @XmlElement(name = "REG_FEE")
    private long regFee;

    /**
     * 诊疗费用，单位：分 必填
     */
    @XmlElement(name = "TREAT_FEE")
    private long treatFee;

    /**
     * 是否有分时，0-否 1-是 必填
     */
    @XmlElement(name = "ISTIME")
    private int isTime;

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public TimeFlag getTimeFlag() {
        return TimeFlag.codeOf(timeFlag);
    }

    public void setTimeFlag(int timeFlag) {
        this.timeFlag = timeFlag;
    }

    public RegStatus getRegStatus() {
        return RegStatus.codeOf(regStatus);
    }

    public void setRegStatus(int regStatus) {
        this.regStatus = regStatus;
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

    public String getRegLevel() {
        return regLevel;
    }

    public void setRegLevel(String regLevel) {
        this.regLevel = regLevel;
    }

    public long getRegFee() {
        return regFee;
    }

    public void setRegFee(long regFee) {
        this.regFee = regFee;
    }

    public long getTreatFee() {
        return treatFee;
    }

    public void setTreatFee(long treatFee) {
        this.treatFee = treatFee;
    }

    public WhetherType getIsTime() {
        return WhetherType.codeOf(isTime);
    }

    public void setIsTime(int isTime) {
        this.isTime = isTime;
    }
}
