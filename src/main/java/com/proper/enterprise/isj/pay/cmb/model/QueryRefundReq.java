package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 按订单号查询退款订单接口请求对象
 */
@XmlRootElement(name = "Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryRefundReq implements Serializable {

    /**
     * 请求head
     */
    @XmlElement(name = "Head")
    private QueryRefundHeadReq head;

    /**
     * 请求body
     */
    @XmlElement(name = "Body")
    private QueryRefundBodyReq body;

    /**
     * Hash
     */
    @XmlElement(name = "Hash")
    private String hash;

    public QueryRefundHeadReq getHead() {
        return head;
    }

    public void setHead(QueryRefundHeadReq head) {
        this.head = head;
    }

    public QueryRefundBodyReq getBody() {
        return body;
    }

    public void setBody(QueryRefundBodyReq body) {
        this.body = body;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
