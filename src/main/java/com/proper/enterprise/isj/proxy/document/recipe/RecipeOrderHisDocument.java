package com.proper.enterprise.isj.proxy.document.recipe;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/9/17 0017.
 */
public class RecipeOrderHisDocument extends BaseDocument {

    /**
     * HIS缴费支付订单号，缴费成功时返回HIS系统生成的缴费支付流水，唯一
     */
    private String hospOrderId;

    /**
     * 收据号
     */
    private String receiptId;

    /**
     * 备注，例如请到XX取药，请到XX做检查，请到XX打印发票，XX代表地址，尽量详细。
     */
    private String hospRemark;

    /**
     * 调用端口返回的消息(code)
     */
    private String clientReturnMsg;

    public String getHospOrderId() {
        return hospOrderId;
    }

    public void setHospOrderId(String hospOrderId) {
        this.hospOrderId = hospOrderId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getHospRemark() {
        return hospRemark;
    }

    public void setHospRemark(String hospRemark) {
        this.hospRemark = hospRemark;
    }

    public String getClientReturnMsg() {
        return clientReturnMsg;
    }

    public void setClientReturnMsg(String clientReturnMsg) {
        this.clientReturnMsg = clientReturnMsg;
    }
}
