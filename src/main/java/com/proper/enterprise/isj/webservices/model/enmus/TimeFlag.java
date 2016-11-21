package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

public enum TimeFlag implements IntEnum {

    /**
     * 上午 (06:00-12:00)
     */
    AM(1),

    /**
     * 下午 (12:00-18:00)
     */
    PM(2),

    /**
     * 晚上(18:00-次日06:00)
     */
    NIGHT(3);

    private int code;

    TimeFlag(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TimeFlag codeOf(int code) {
        for (TimeFlag timeFlag : values()) {
            if (timeFlag.getCode() == code) {
                return timeFlag;
            }
        }
        return null;
    }
}
