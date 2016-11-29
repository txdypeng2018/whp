package com.proper.enterprise.isj.pay.cmb.model;

import com.proper.enterprise.platform.core.utils.ConfCenter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 单笔订单查询接口请求Head对象
 */
@XmlRootElement(name = "Head")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuerySingleOrderHeadReq implements Serializable {

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
     * 请求发起的时间，精确到毫秒
     */
    @XmlElement(name = "TimeStamp")
    private String timeStamp;

    /**
     * 接口名称
     */
    @XmlElement(name = "Command")
    private String command = ConfCenter.get("isj.pay.cmb.querySingleOrder");

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
