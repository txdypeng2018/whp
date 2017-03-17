package com.proper.enterprise.isj.webservices.model.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundByHisInfo  implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    @XmlElement(name = "REFUNDLIST")
    private List<RefundByHis> refundlist = new ArrayList<>();

    public List<RefundByHis> getRefundlist() {
        return refundlist;
    }

    public void setRefundlist(List<RefundByHis> refundlist) {
        this.refundlist = refundlist;
    }
}
