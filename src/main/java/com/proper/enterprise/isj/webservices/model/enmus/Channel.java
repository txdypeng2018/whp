package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

public enum Channel implements IntEnum {

    /**
     * 微信公众号
     */
    WECHAT(1),

    /**
     * 支付宝窗口
     */
    ALIPAY(2),

    /**
     * 手机 APP
     */
    APP(3),

    /**
     * 网站
     */
    WEBSITE(4),

    /**
     * 中国电信
     */
    TELECOM(5),

    /**
     * 中国移动
     */
    MOBILE(6),

    /**
     * 中国联通
     */
    UNICOM(7),

    /**
     * 自助终端
     */
    SELF_TERM(8),

    /**
     * 医院窗口
     */
    HOS_WIN(9),

    /**
     * 其他
     */
    OTHERS(10),

    /**
     * 系统
     */
    SYS(11);


    private int code;

    Channel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
