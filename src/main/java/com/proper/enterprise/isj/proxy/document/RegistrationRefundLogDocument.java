package com.proper.enterprise.isj.proxy.document;

import org.springframework.data.mongodb.core.mapping.Document;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/11/9 0009.
 */
@Document(collection = "registration_refund_log")
public class RegistrationRefundLogDocument extends BaseDocument {

    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 挂号单号
     */
    private String num;

    /**
     * 订单号
     */
    private String orderNum;

    /**
     * 挂号单Id
     */
    private String registrationId;

    /**
     * 支付方式
     */
    private String payChannelId;

    /**
     * 退号状态 -1:未退号,1:成功,0:失败
     */
    private String cancelRegStatus = String.valueOf(-1);

    /**
     * 退费状态 -1:未退费,1:成功,0:失败
     */
    private String refundStatus = String.valueOf(-1);

    /**
     * 退费通知HIS 1:成功,0:失败或者未通知,需要具体看refundStatus参数
     */
    private String refundHisStatus;

    /**
     * 报错日志
     */
    private String description;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(String payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getCancelRegStatus() {
        return cancelRegStatus;
    }

    public void setCancelRegStatus(String cancelRegStatus) {
        this.cancelRegStatus = cancelRegStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundHisStatus() {
        return refundHisStatus;
    }

    public void setRefundHisStatus(String refundHisStatus) {
        this.refundHisStatus = refundHisStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
