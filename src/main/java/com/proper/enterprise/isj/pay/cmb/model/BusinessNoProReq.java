package com.proper.enterprise.isj.pay.cmb.model;

import com.proper.enterprise.platform.core.utils.ConfCenter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 招商银行校验码(支付)
 */
@XmlRootElement(name = "Protocol")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessNoProReq implements Serializable {

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

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }
}
