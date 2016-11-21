package com.proper.enterprise.isj.webservices.model.res.orderreg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "CONCESSIONS")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concession implements Serializable {

    /**
     * 本次挂号优惠的总金额，无优惠返回0，单位：分
     * 必填
     */
    @XmlElement(name = "CONCESSIONS_FEE")
    private long concessionsFee;

    /**
     * 在院内优惠后实际需要支付的挂号费，无优惠返回原挂号费，单位：分
     * 必填
     */
    @XmlElement(name = "REAL_REG_FEE")
    private long realRegFee;

    /**
     * 在院内优惠后实际需要支付的诊疗费，无优惠返回原诊疗费，单位：分
     * 必填
     */
    @XmlElement(name = "REAL_TREAT_FEE")
    private long realTreatFee;

    /**
     * 在院内优惠后实际需要支付的总费用，无优惠返回原挂号费用，单位：分
     * 必填
     */
    @XmlElement(name = "REAL_TOTAL_FEE")
    private long realTotalFee;

    /**
     * 优惠类型，例：老人优惠，军人优惠等
     */
    @XmlElement(name = "CONCESSIONS_TYPE")
    private String concessionsType;

    public long getConcessionsFee() {
        return concessionsFee;
    }

    public void setConcessionsFee(long concessionsFee) {
        this.concessionsFee = concessionsFee;
    }

    public long getRealRegFee() {
        return realRegFee;
    }

    public void setRealRegFee(long realRegFee) {
        this.realRegFee = realRegFee;
    }

    public long getRealTreatFee() {
        return realTreatFee;
    }

    public void setRealTreatFee(long realTreatFee) {
        this.realTreatFee = realTreatFee;
    }

    public long getRealTotalFee() {
        return realTotalFee;
    }

    public void setRealTotalFee(long realTotalFee) {
        this.realTotalFee = realTotalFee;
    }

    public String getConcessionsType() {
        return concessionsType;
    }

    public void setConcessionsType(String concessionsType) {
        this.concessionsType = concessionsType;
    }
}
