package com.proper.enterprise.isj.webservices.model.res;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayOrder implements Serializable {

    /**
     * HIS缴费支付订单号，缴费成功时返回HIS系统生成的缴费支付流水，唯一
     */
    @XmlElement(name = "HOSP_ORDER_ID")
    private String hospOrderId;

    /**
     * 收据号
     */
    @XmlElement(name = "RECEIPT_ID")
    private String receiptId;

    /**
     * 备注，例如请到XX取药，请到XX做检查，请到XX打印发票，XX代表地址，尽量详细。
     */
    @XmlElement(name = "HOSP_REMARK")
    private String hospRemark;

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

}
