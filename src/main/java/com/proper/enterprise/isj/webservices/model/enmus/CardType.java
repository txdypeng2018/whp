package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

/**
 * 卡类型
 */
public enum CardType implements IntEnum {

    /**
     * 虚拟卡
     */
    NONCARD(0),

    /**
     * 健康卡
     */
    HEALTH(1),

    /**
     * 市民卡
     */
    CITIZEN(2),

    /**
     * 社保卡/医保卡
     */
    SM_INS(3),

    /**
     * 银行卡
     */
    BANK(4),

    /**
     * 公费医疗证
     */
    FREE_HEAL(5),

    /**
     * 农合证
     */
    COUNTRY(6),

    /**
     * 院内诊疗卡
     */
    INSIDE(7),

    /**
     * 就诊卡
     */
    CARD(8),

    /**
     * 系统内部号
     */
    INNER_NO(9),

    /**
     * 其它卡
     */
    OTHERS(99);

    private int code;

    CardType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CardType codeOf(int code) {
        for (CardType v : values()) {
            if (v.getCode() == code) {
                return v;
            }
        }
        return null;
    }

}
