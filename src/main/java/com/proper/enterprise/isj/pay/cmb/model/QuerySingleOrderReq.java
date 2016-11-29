package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 单笔订单查询接口请求对象
 */
@XmlRootElement(name = "Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuerySingleOrderReq implements Serializable {

    /**
     * 请求head
     */
    @XmlElement(name = "Head")
    private QuerySingleOrderHeadReq head;

    /**
     * 请求body
     */
    @XmlElement(name = "Body")
    private QuerySingleOrderBodyReq body;

    /**
     * Hash
     */
    @XmlElement(name = "Hash")
    private String hash;

    public QuerySingleOrderHeadReq getHead() {
        return head;
    }

    public void setHead(QuerySingleOrderHeadReq head) {
        this.head = head;
    }

    public QuerySingleOrderBodyReq getBody() {
        return body;
    }

    public void setBody(QuerySingleOrderBodyReq body) {
        this.body = body;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
