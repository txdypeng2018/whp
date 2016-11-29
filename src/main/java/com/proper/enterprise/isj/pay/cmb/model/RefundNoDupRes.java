package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 退款接口接口响应对象
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundNoDupRes {

    /**
     * 响应head
     */
    @XmlElement(name = "Head")
    private CommonHeadRes head;

    /**
     * 响应body
     */
    @XmlElement(name = "Body")
    private RefundNoDupBodyRes body;

    public CommonHeadRes getHead() {
        return head;
    }

    public void setHead(CommonHeadRes head) {
        this.head = head;
    }

    public RefundNoDupBodyRes getBody() {
        return body;
    }

    public void setBody(RefundNoDupBodyRes body) {
        this.body = body;
    }
}
