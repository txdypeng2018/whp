package com.proper.enterprise.isj.proxy.document.recipe;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by think on 2016/10/6 0006.
 */
public class RecipeRefundDetailDocument implements Serializable {

    /**
     * 序号
     */
    private String id;

    @Transient
    private String clinicCode;

    /**
     * 类别,1:挂号,2:处方
     */
    private String type;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 处方号
     */
    private String recipeNo;
    /**
     * 处方内流水号
     */
    private String sequenceNo;

    /**
     * 金额
     */
    private String cost;

    /**
     * 状态
     */
    private String status;

    /**
     * 名称
     */
    private String name;

    /**
     * 缴费退费单号
     */
    private String refundNo;

    /**
     * 缴费退费返回的msg
     */
    private String refundReturnMsg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRecipeNo() {
        return recipeNo;
    }

    public void setRecipeNo(String recipeNo) {
        this.recipeNo = recipeNo;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getRefundReturnMsg() {
        return refundReturnMsg;
    }

    public void setRefundReturnMsg(String refundReturnMsg) {
        this.refundReturnMsg = refundReturnMsg;
    }
}
