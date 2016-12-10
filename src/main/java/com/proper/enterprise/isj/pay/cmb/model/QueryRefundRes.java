package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 按订单号查询退款订单接口响应对象
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryRefundRes {

    /**
     * 响应head
     */
    @XmlElement(name = "Head")
    private CommonHeadRes head;

    /**
     * 响应body
     */
    @XmlElement(name = "Body")
    private QueryRefundBodyRes body;

    public CommonHeadRes getHead() {
        return head;
    }

    public void setHead(CommonHeadRes head) {
        this.head = head;
    }

    public QueryRefundBodyRes getBody() {
        return body;
    }

    public void setBody(QueryRefundBodyRes body) {
        this.body = body;
    }
}
