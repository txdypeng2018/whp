package com.proper.enterprise.isj.user.model.enums;

/**
 * 性别枚举类型.
 * Created by think on 2016/9/2 0002.
 */
public enum SexTypeEnum {
    /**
     * 女
     */
    FEMALE(0),

    /**
     * 男
     */
    MALE(1),

    /**
     * 保密
     */
    SECRET(2),

    /**
     * 其他
     */
    OTHERS(3);

    private int code;

    SexTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
