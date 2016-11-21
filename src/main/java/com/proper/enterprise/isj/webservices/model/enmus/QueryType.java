package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * 查询状态类型
 */
public enum QueryType implements IntEnum {

    /**
     * 所有
     */
    ALL(0),

    /**
     * 未缴费
     */
    TO_PAY(1),

    /**
     * 已缴费
     */
    PAYED(2),

    /**
     * 已退款
     */
    REFUND(3);

    private int code;

    QueryType(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
