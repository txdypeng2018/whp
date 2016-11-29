package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 公共相应Head对象
 */
@XmlRootElement(name = "Head")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommonHeadRes {

    /**
     * 响应Code
     */
    @XmlElement(name = "Code")
    private String code;

    /**
     * 响应ErrMsg
     */
    @XmlElement(name = "ErrMsg")
    private String errMsg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
