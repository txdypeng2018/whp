package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * 科室等级
 */
public enum DeptLevel implements IntEnum {

    /**
     * 上级科室
     */
    PARENT(0),

    /**
     * 末端科室
     */
    CHILD(1),

    /**
     * 医生
     */
    DOCTOR(2);

    private int code;

    DeptLevel(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static DeptLevel codeOf(int code) {
        for (DeptLevel level : values()) {
            if (level.getCode() == code) {
                return level;
            }
        }
        return null;
    }
}
