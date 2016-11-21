package com.proper.enterprise.isj.pay.ali.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 支付宝即时到账无密退款同步通知结果
 */
@XmlRootElement(name = "alipay")
@XmlAccessorType(XmlAccessType.FIELD)
public class AliRefundPreRes {
    /**
     * 是否成功过 必填
     */
    @XmlElement(name = "is_success")
    private String isSuccess;

    /**
     * 错误信息
     */
    @XmlElement(name = "error")
    private String error;

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
