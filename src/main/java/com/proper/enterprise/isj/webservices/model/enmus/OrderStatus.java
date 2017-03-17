package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * 缴费订单状态
 */
public enum OrderStatus implements IntEnum {

    /**
     * 未缴费
     */
    TO_PAY(0),

    /**
     * 已缴费
     */
    PAYED(1),

    /**
     * 已退款
     */
    REFUND(2);

    private int code;

    OrderStatus(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    public static OrderStatus codeOf(int code) {
        for (OrderStatus v : values()) {
            if (v.getCode() == code) {
                return v;
            }
        }
        return null;
    }

}
