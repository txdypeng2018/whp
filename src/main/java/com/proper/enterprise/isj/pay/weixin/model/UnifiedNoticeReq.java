package com.proper.enterprise.isj.pay.weixin.model;

import javax.xml.bind.annotation.*;

/**
 * 微信移动支付返回异步通知处理结果_XML_Model
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnifiedNoticeReq {

    /**
     * 返回码
     */
    @XmlElement(name = "return_code")
    private String returnCode;

    /**
     * 返回消息
     */
    @XmlElement(name = "return_msg")
    private String returnMsg;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
}
