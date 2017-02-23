package com.proper.enterprise.isj.user.model.enums;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * Created by think on 2016/8/13 0013. ֤证件类型
 */
public enum CredentialsEnum implements IntEnum {

    /**
     * 身份证
     */
    IDCARD(1),

    /**
     * 港澳居民来往内地通行证
     */
    HK(2),

    /**
     * 台湾居民来往大陆通行证
     */
    TW(3),

    /**
     * 护照
     */
    PASSPORT(5),

    /**
     * 军官证
     */
    OFFICER(6),

    /**
     * 出生证
     */
    BIRTH(7),

    /**
     * 驾驶证
     */
    DRIVE(8),

    /**
     * 残疾证
     */
    DISABLE(9),

    /**
     * 医保卡
     */
    MED_INS(10),

    /**
     * 市民卡
     */
    CITIZEN(11),

    /**
     * 其它
     */
    OTHERS(99);

    private int code;

    CredentialsEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
