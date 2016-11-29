package com.proper.enterprise.isj.pay.cmb.model;

import com.proper.enterprise.platform.core.utils.ConfCenter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 退款接口请求Head对象
 */
@XmlRootElement(name = "Head")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundNoDupHeadReq implements Serializable {

    /**
     * 4位分行号
     */
    @XmlElement(name = "BranchNo")
    private String branchNo = ConfCenter.get("isj.pay.cmb.branchId");

    /**
     * 6位商户号
     */
    @XmlElement(name = "MerchantNo")
    private String merchantNo = ConfCenter.get("isj.pay.cmb.cono");

    /**
     * 操作员号，例如9999
     */
    @XmlElement(name = "Operator")
    private String operator = ConfCenter.get("isj.pay.cmb.operator");

    /**
     * 操作员号，例如9999
     */
    @XmlElement(name = "Password")
    private String password = ConfCenter.get("isj.pay.cmb.operatorPassword");

    /**
     * 请求发起的时间，精确到毫秒
     */
    @XmlElement(name = "TimeStamp")
    private String timeStamp;

    /**
     * 接口名称
     */
    @XmlElement(name = "Command")
    private String command = ConfCenter.get("isj.pay.cmb.refundNoDup");

    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(String branchNo) {
        this.branchNo = branchNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
