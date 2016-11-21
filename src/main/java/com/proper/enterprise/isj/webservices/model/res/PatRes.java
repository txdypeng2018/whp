package com.proper.enterprise.isj.webservices.model.res;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by think on 2016/9/14 0014.
 */
@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatRes implements Serializable {
    /**
     * 医院ID号 必填
     */
    @XmlElement(name = "HOS_ID")
    private String hosId;

    /**
     * 卡类型,传身份证
     */
    @XmlElement(name = "CARD_TYPE")
    private String cardType;

    /**
     * 身份证号
     */
    @XmlElement(name = "MARK_NO")
    private String markNo;

    /**
     * 病历号
     */
    @XmlElement(name = "CARD_NO")
    private String cardNo;

    /**
     * 姓名
     */
    @XmlElement(name = "NAME")
    private String name;

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getMarkNo() {
        return markNo;
    }

    public void setMarkNo(String markNo) {
        this.markNo = markNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
