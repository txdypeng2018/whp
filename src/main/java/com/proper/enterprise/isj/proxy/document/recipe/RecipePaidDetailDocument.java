package com.proper.enterprise.isj.proxy.document.recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by think on 2016/10/16 0016.
 */
public class RecipePaidDetailDocument implements Serializable {

    private static final long serialVersionUID = -1;

    private String orderNum;

    /**
     * 发生退款产生的退款单号
     */
    private String refundNum;

    /**
     * 退款成功标志位 1:退款成功,0:退款失败
     */
    private String refundStatus;

    /**
     * 退款说明(失败/成功说明)
     */
    private String description;

    /**
     * 缴费批次号
     */
    private String hospSequence;

    private String amount;

    private String payChannelId;

    private List<RecipeDetailAllDocument> detailList = new ArrayList<>();

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<RecipeDetailAllDocument> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<RecipeDetailAllDocument> detailList) {
        this.detailList = detailList;
    }

    public String getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(String payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getHospSequence() {
        return hospSequence;
    }

    public void setHospSequence(String hospSequence) {
        this.hospSequence = hospSequence;
    }


    public String getRefundNum() {
        return refundNum;
    }

    public void setRefundNum(String refundNum) {
        this.refundNum = refundNum;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
