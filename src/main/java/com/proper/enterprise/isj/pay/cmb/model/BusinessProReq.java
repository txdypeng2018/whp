package com.proper.enterprise.isj.pay.cmb.model;

import com.proper.enterprise.platform.core.utils.ConfCenter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 招商银行校验码(签约+支付)
 */
@XmlRootElement(name = "Protocol")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessProReq implements Serializable {

    //------------以下字段必填--------------
    /**
     * 客户协议号
     */
    @XmlElement(name = "PNo")
    private String pno;

    /**
     * 交易时间
     */
    @XmlElement(name = "TS")
    private String ts;

    /**
     * 协议商户企业编号
     */
    @XmlElement(name = "MchNo")
    private String mchNo = ConfCenter.get("isj.pay.cmb.mchNo");

    //------以下字段“签约+支付”时必填----------
    /**
     * 协议开通请求流水号
     */
    @XmlElement(name = "Seq")
    private String seq;

    /**
     * 协议开通结果通知命令请求地址
     */
    @XmlElement(name = "URL")
    private String url;

    //------------以下字段选填-----------------
    /**
     * 协议开通结果通知命令参数
     */
    @XmlElement(name = "Para")
    private String para;

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getMchNo() {
        return mchNo;
    }

    public void setMchNo(String mchNo) {
        this.mchNo = mchNo;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }
}
