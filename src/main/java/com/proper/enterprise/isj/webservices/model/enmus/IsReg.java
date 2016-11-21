package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

public enum IsReg implements IntEnum {

    /**
     * 当天挂号
     */
    TODAY(1),

    /**
     * 预约挂号（直接挂号）
     */
    APPOINT_DIR(2),

    /**
     * 预约挂号（锁号挂号）
     */
    APPOINT_LOCK(3);

    private int code;

    IsReg(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
