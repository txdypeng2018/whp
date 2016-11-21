package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * 性别
 */
public enum Sex implements IntEnum {

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

    Sex(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
