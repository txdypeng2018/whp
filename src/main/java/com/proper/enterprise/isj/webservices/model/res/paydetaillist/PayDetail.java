package com.proper.enterprise.isj.webservices.model.res.paydetaillist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;

import java.io.Serializable;

/**
 * 待缴费订单明细集合
 */
@XmlRootElement(name = "PAY_DETAIL_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayDetail implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 明细费别，例：药品费，注射费等
     * 必填
     */
    @XmlElement(name = "DETAIL_TYPE")
    private String detailType;

    /**
     * 明细名称
     * 必填
     */
    @XmlElement(name = "DETAIL_NAME")
    private String detailName;

    /**
     * 明细流水号，唯一
     * 必填
     */
    @XmlElement(name = "DETAIL_ID")
    private String detailId;

    /**
     * 明细单位
     * 必填
     */
    @XmlElement(name = "DETAIL_UNIT")
    private String detailUnit;

    /**
     * 明细数量
     * 必填
     */
    @XmlElement(name = "DETAIL_COUNT")
    private String detailCount;

    /**
     * 明细单价，单位：分
     * 必填
     */
    @XmlElement(name = "DETAIL_PRICE")
    private String detailPrice;

    /**
     * 明细规格，药品必填
     */
    @XmlElement(name = "DETAIL_SPEC")
    private String detailSpec;

    /**
     * 明细总金额
     * 必填
     */
    @XmlElement(name = "DETAIL_AMOUT")
    private int detailAmout;

    /**
     * 明细医保金额
     * 必填
     */
    @XmlElement(name = "DETAIL_MI")
    private int detailMi;

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getDetailUnit() {
        return detailUnit;
    }

    public void setDetailUnit(String detailUnit) {
        this.detailUnit = detailUnit;
    }

    public String getDetailCount() {
        return detailCount;
    }

    public void setDetailCount(String detailCount) {
        this.detailCount = detailCount;
    }

    public String getDetailPrice() {
        return detailPrice;
    }

    public void setDetailPrice(String detailPrice) {
        this.detailPrice = detailPrice;
    }

    public String getDetailSpec() {
        return detailSpec;
    }

    public void setDetailSpec(String detailSpec) {
        this.detailSpec = detailSpec;
    }

    public int getDetailAmout() {
        return detailAmout;
    }

    public void setDetailAmout(int detailAmout) {
        this.detailAmout = detailAmout;
    }

    public int getDetailMi() {
        return detailMi;
    }

    public void setDetailMi(int detailMi) {
        this.detailMi = detailMi;
    }

}
