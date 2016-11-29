package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 单笔订单查询接口响应对象
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuerySingleOrderRes {

    /**
     * 响应head
     */
    @XmlElement(name = "Head")
    private CommonHeadRes head;

    /**
     * 响应body
     */
    @XmlElement(name = "Body")
    private QuerySingleOrderBodyRes body;

    public CommonHeadRes getHead() {
        return head;
    }

    public void setHead(CommonHeadRes head) {
        this.head = head;
    }

    public QuerySingleOrderBodyRes getBody() {
        return body;
    }

    public void setBody(QuerySingleOrderBodyRes body) {
        this.body = body;
    }
}
