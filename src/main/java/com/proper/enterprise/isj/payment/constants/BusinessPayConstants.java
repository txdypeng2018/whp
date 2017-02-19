package com.proper.enterprise.isj.payment.constants;

import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * 支付常量类.
 */
public class BusinessPayConstants {

    // 支付宝支付
    public final static String ISJ_PAY_WAY_ALI = ConfCenter.get("isj.pay.way.ali");

    // 微信支付
    public final static String ISJ_PAY_WAY_WECHAT = ConfCenter.get("isj.pay.way.wechat");

    // 一网通支付
    public final static String ISJ_PAY_WAY_CMB = ConfCenter.get("isj.pay.way.cmb");
}
