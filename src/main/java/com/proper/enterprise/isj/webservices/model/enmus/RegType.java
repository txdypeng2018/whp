package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

public enum RegType implements IntEnum {

    /**
     * 为本人挂号
     */
    SELF(1),

    /**
     * 为子女挂号
     */
    CHILDREN(2),

    /**
     * 为他人挂号
     */
    OTHERS(3);

    private int code;

    RegType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
