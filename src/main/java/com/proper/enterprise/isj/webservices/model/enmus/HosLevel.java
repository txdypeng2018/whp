package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * 医院等级
 */
public enum HosLevel implements IntEnum {

    /**
     * 其他
     */
    OTHER(0),

    /**
     * 一级
     */
    ONE(1),

    /**
     * 二级
     */
    TWO(2),

    /**
     * 三级
     */
    THREE(3),

    /**
     * 特级
     */
    TOP(4),

    /**
     * 三甲
     */
    THREE_A(5),

    /**
     * 三乙
     */
    THREE_B(6),

    /**
     * 三丙
     */
    THREE_C(7),

    /**
     * 二甲
     */
    TWO_A(8),

    /**
     * 二乙
     */
    TWO_B(9),

    /**
     * 二丙
     */
    TWO_C(10),

    /**
     * 一甲
     */
    ONE_A(11),

    /**
     * 一乙
     */
    ONE_B(12),

    /**
     * 一丙
     */
    ONE_C(13);

    private int code;

    HosLevel(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static HosLevel codeOf(int code) {
        for (HosLevel level : values()) {
            if (level.getCode() == code) {
                return level;
            }
        }
        return null;
    }
}
