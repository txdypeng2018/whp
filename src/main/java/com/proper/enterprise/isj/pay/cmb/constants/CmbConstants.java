package com.proper.enterprise.isj.pay.cmb.constants;

import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * 招商银行支付常量
 */
public class CmbConstants {

    // 支付商户开户分行号
    public final static String CMB_PAY_BRANCHID = ConfCenter.get("isj.pay.cmb.branchId");

    // 支付商户号，6位长数字，由银行在商户开户时确定；//收单商户号
    public final static String CMB_PAY_CONO = ConfCenter.get("isj.pay.cmb.cono");

    // 商户请求通用接口
    public final static String CMB_PAY_DIRECT_REQUEST_X = ConfCenter.get("isj.pay.cmb.directRequestX");
}
