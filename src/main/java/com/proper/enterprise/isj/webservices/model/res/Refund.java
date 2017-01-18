package com.proper.enterprise.isj.webservices.model.res;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class Refund implements Serializable {

    /**
     * 医院退款单号
     */
    @XmlElement(name = "HOSP_REFUND_ID")
    private String hospPayId;

    /**
     * 医院退款单号
     * 
     */
    @XmlElement(name = "REFUND_FLAG")
    private String refundFlag;

    public String getHospPayId() {
        return hospPayId;
    }

    public void setHospPayId(String hospPayId) {
        this.hospPayId = hospPayId;
    }

    public String getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(String refundFlag) {
        this.refundFlag = refundFlag;
    }
}
