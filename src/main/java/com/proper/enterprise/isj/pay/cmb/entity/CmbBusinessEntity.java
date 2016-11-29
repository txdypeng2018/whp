package com.proper.enterprise.isj.pay.cmb.entity;

import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 一网通用户协议异步通知结果
 */
@Entity
@Table(name = "ISJ_CMB_PROTOCOL_INFO")
@CacheEntity
public class CmbBusinessEntity extends BaseEntity {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 企业网银编号 必填
     */
    private String ntbnbr;

    /**
     * 功能交易码：本接口固定填“BKQY”  必填
     */
    private String trscod;

    /**
     * 字段BUSDAT长度  必填
     */
    private String datlen;

    /**
     * 通讯报文ID：返回的报文头会包含该信息，仅用作单次通讯的请求和响应报文的对应。银行业务数据不存储该ID。  必填
     */
    private String commid;

    /**
     * 业务数据包：报文数据必须经过base64编码。业务报文见后面说明。  必填
     */
    @Column(length = 2000000, nullable = false)
    private String busdat;

    /**
     * 签名时间：签名时间与服务器的时间差，不能超过1小时。格式：yyyyMMddHHmmssffff  必填
     */
    private String sigtim;

    /**
     * 签名数据的BASE64编码。签名的原文见如下说明，商户调用我行提供的验证签名api验证数据的合法性。   必填
     */
    @Column(length = 1024, nullable = false)
    private String sigdat;

    /**
     * 通讯报文ID：返回的报文头会包含该信息，仅用作单次通讯的请求和响应报文的对应。银行业务数据不存储该ID。  必填
     */

    /**
     * 互联网商户签约协议号 必填
     */
    @Column(nullable = false, unique = true)
    private String custArgno;

    /**
     * 签约请求时的附加参数 必填
     */
    private String noticepara;

    /**
     * 签约状态。“CMBMB99” 表示签约成功 必填
     */
    @Column(nullable = false)
    private String respcod;

    /**
     * 返回签约信息 必填
     */
    private String respmsg;

    /**
     * 互联网商户客户ID 必填
     */
    private String custNo;

    /**
     * 证件类型 目前只有1，表示身份证 必填
     */
    private String custPidty;

    /**
     * 招行后台生成的、全局唯一的用户ID，30位hash值，成功交易返回 必填
     */
    @Column(nullable = false)
    private String custPidV;

    /**
     * 必填
     */
    private String custOpenDPay;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNtbnbr() {
        return ntbnbr;
    }

    public void setNtbnbr(String ntbnbr) {
        this.ntbnbr = ntbnbr;
    }

    public String getTrscod() {
        return trscod;
    }

    public void setTrscod(String trscod) {
        this.trscod = trscod;
    }

    public String getDatlen() {
        return datlen;
    }

    public void setDatlen(String datlen) {
        this.datlen = datlen;
    }

    public String getCommid() {
        return commid;
    }

    public void setCommid(String commid) {
        this.commid = commid;
    }

    public String getBusdat() {
        return busdat;
    }

    public void setBusdat(String busdat) {
        this.busdat = busdat;
    }

    public String getSigtim() {
        return sigtim;
    }

    public void setSigtim(String sigtim) {
        this.sigtim = sigtim;
    }

    public String getSigdat() {
        return sigdat;
    }

    public void setSigdat(String sigdat) {
        this.sigdat = sigdat;
    }

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
