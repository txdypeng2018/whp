package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 退款接口请求对象
 */
@XmlRootElement(name = "Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundNoDupReq implements Serializable {

    /**
     * 请求head
     */
    @XmlElement(name = "Head")
    private RefundNoDupHeadReq head;

    /**
     * 请求body
     */
    @XmlElement(name = "Body")
    private RefundNoDupBodyReq body;

    /**
     * Hash
     */
    @XmlElement(name = "Hash")
    private String hash;

    public RefundNoDupHeadReq getHead() {
        return head;
    }

    public void setHead(RefundNoDupHeadReq head) {
        this.head = head;
    }

    public RefundNoDupBodyReq getBody() {
        return body;
    }

    public void setBody(RefundNoDupBodyReq body) {
        this.body = body;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
