package com.proper.enterprise.isj.proxy.enums;

/**
 * 用户反馈信息枚举
 */
public enum FeedbackEnum {
    /**
     * 未反馈
     */
    UNREPLAY("0"),
    /**
     * 已反馈
     */
    REPLAYED("1"),
    /**
     * 已关闭
     */
    CLOSED("2");

    private String value;

    private FeedbackEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
