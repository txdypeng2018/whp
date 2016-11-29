package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 一网通移动支付接收签约异步通知信息_XML_Model
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessRes implements Serializable {

    /**
     * 互联网商户签约协议号 必填
     */
    @XmlElement(name = "cust_argno")
    private String custArgno;

    /**
     * 签约请求时的附加参数 必填
     */
    @XmlElement(name = "noticepara")
    private String noticepara;

    /**
     * 签约状态。“CMBMB99” 表示签约成功 必填
     */
    @XmlElement(name = "respcod")
    private String respcod;

    /**
     * 返回签约信息 必填
     */
    @XmlElement(name = "respmsg")
    private String respmsg;

    /**
     * 互联网商户客户ID 必填
     */
    @XmlElement(name = "cust_no")
    private String custNo;

    /**
     * 证件类型 目前只有1，表示身份证 必填
     */
    @XmlElement(name = "cust_pidty")
    private String custPidty;

    /**
     * 招行后台生成的、全局唯一的用户ID，30位hash值，成功交易返回 必填
     */
    @XmlElement(name = "cust_pid_v")
    private String custPidV;

    /**
     * 必填
     */
    @XmlElement(name = "cust_open_d_pay")
    private String custOpenDPay;

    public String getCustArgno() {
        return custArgno;
    }

    public void setCustArgno(String custArgno) {
        this.custArgno = custArgno;
    }

    public String getNoticepara() {
        return noticepara;
    }

    public void setNoticepara(String noticepara) {
        this.noticepara = noticepara;
    }

    public String getRespcod() {
        return respcod;
    }

    public void setRespcod(String respcod) {
        this.respcod = respcod;
    }

    public String getRespmsg() {
        return respmsg;
    }

    public void setRespmsg(String respmsg) {
        this.respmsg = respmsg;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustPidty() {
        return custPidty;
    }

    public void setCustPidty(String custPidty) {
        this.custPidty = custPidty;
    }

    public String getCustPidV() {
        return custPidV;
    }

    public void setCustPidV(String custPidV) {
        this.custPidV = custPidV;
    }

    public String getCustOpenDPay() {
        return custOpenDPay;
    }

    public void setCustOpenDPay(String custOpenDPay) {
        this.custOpenDPay = custOpenDPay;
    }
}
