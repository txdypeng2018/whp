package com.proper.enterprise.isj.pay.cmb.document;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 招商银行用户协议关联表
 */
@Document(collection = "cmb_protocol_info")
public class CmbProtocolDocument extends BaseDocument {

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 协议号
     */
    private String protocolNo;

    /**
     * 是否签约成功
     * 0 : 未成功
     * 1 : 成功
     */
    private String sign;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
