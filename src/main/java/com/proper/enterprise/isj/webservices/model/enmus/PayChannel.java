package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * 支付渠道
 */
public enum PayChannel implements IntEnum {

    /**
     * 微信支付
     */
    WECHATPAY(1),

    /**
     * 支付宝支付
     */
    ALIPAY(2),

    /**
     * 手机银联支付
     */
    UNIONPAY(3),

    /**
     * 互联网银联支付
     */
    WEB_UNION(4),

    /**
     * 终端银联支付
     */
    TERMINATE(5),

    /**
     * 医院窗口支付
     */
    HOS_WI(6);

    private int code;

    PayChannel(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static PayChannel codeOf(int code) {
        for (PayChannel v : values()) {
            if (v.getCode() == code) {
                return v;
            }
        }
        return null;
    }

}
