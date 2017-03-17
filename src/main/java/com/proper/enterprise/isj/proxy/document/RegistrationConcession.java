package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/9/4 0004.
 */

public class RegistrationConcession extends BaseDocument {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 本次挂号优惠的总金额，无优惠返回0，单位：分 必填
     */
    private long concessionsFee;

    /**
     * 在院内优惠后实际需要支付的挂号费，无优惠返回原挂号费，单位：分 必填
     */
    private long realRegFee;

    /**
     * 在院内优惠后实际需要支付的诊疗费，无优惠返回原诊疗费，单位：分 必填
     */
    private long realTreatFee;

    /**
     * 在院内优惠后实际需要支付的总费用，无优惠返回原挂号费用，单位：分 必填
     */
    private long realTotalFee;

    /**
     * 优惠类型，例：老人优惠，军人优惠等
     */
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
