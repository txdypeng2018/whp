package com.proper.enterprise.isj.webservices.model.res.reportinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 检验项目列表详细
 */
@XmlRootElement(name = "DETAIL")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportCheckListDetail implements Serializable {
    /**
     * 检验项目
     */
    @XmlElement(name = "CHECK_NAME")
    private String checkName;

    /**
     * 检验结果
     */
    @XmlElement(name = "RESULT")
    private String result;

    /**
     * 单位
     */
    @XmlElement(name = "UNIT")
    private String unit;

    /**
     * 结果正常标志，例：正常、偏高、偏低等
     */
    @XmlElement(name = "NORMAL_FLAG")
    private String normalFlag;

    /**
     * 参考值
     */
    @XmlElement(name = "REFERENCE_VALUE")
    private String referenceValue;

    /**
     * 说明
     */
    @XmlElement(name = "DESC")
    private String desc;

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNormalFlag() {
        return normalFlag;
    }

    public void setNormalFlag(String normalFlag) {
        this.normalFlag = normalFlag;
    }

    public String getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(String referenceValue) {
        this.referenceValue = referenceValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
