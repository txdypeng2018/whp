package com.proper.enterprise.isj.proxy.document.registration;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/9/11 0011.
 */
public class RegistrationRefundHisDocument extends BaseDocument{

    /**
     * 医院退款单号
     */
    private String hospPayId;

    /**
     * 医院退款单号
     *
     */
    private String refundFlag;

    /**
     * 调用端口返回的消息(code)
     */
    private String clientReturnMsg;

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

    public String getClientReturnMsg() {
        return clientReturnMsg;
    }

    public void setClientReturnMsg(String clientReturnMsg) {
        this.clientReturnMsg = clientReturnMsg;
    }
}
