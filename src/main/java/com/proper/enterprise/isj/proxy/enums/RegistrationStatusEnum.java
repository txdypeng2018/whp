package com.proper.enterprise.isj.proxy.enums;

/**
 * Created by think on 2016/9/11 0011.
 */
public enum RegistrationStatusEnum {
    /**
     * 未支付
     */
    NOT_PAID("0"),
    /**
     * 已支付
     */
    PAID("1"),

    /**
     * 已完成
     */
    FINISH("2"),
    /**
     * 已退费
     */
    REFUND("6"),
    /**
     * 已停诊
     */
    SUSPEND_MED("7"),

    /**
     * 已取消
     */
    CANCEL("8"),
    /**
     * 交易关闭
     */
    EXCHANGE_CLOSED("9"),

    /**
     * 退费失败
     */
    REFUND_FAIL("10");


    private String value;

    private RegistrationStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
