package com.proper.enterprise.isj.pay.ali.constants;

import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * 支付宝常量类
 */
public class AliConstants {

    // 支付宝支付信息
    // 商户PID
    public final static String ALI_PAY_PARTNER_ID = ConfCenter.get("isj.pay.ali.partnerId");
    // 商户收款账号
    public final static String ALI_PAY_SELLER_ID = ConfCenter.get("isj.pay.ali.sellerId");
    // 商户私钥，pkcs8格式
    public final static String ALI_PAY_RSA_PRIVATE = ConfCenter.get("isj.pay.ali.privateKey");
    // 支付宝公钥
    public final static String ALI_PAY_RSA_PUBLIC = ConfCenter.get("isj.pay.ali.aliPublicKey");
    // 支付宝异步通知地址
    public final static String ALI_PAY_NOTICE_URL = ConfCenter.get("isj.pay.ali.notifyurl");
    // 支付宝消息验证地址
    public final static String ALI_PAY_NOTICE_HTTPS_VERIFY_URL = ConfCenter.get("isj.pay.ali.httpsVerifyUrl");
    // 支付宝签名方式
    public final static String ALI_PAY_SIGN_TYPE = ConfCenter.get("isj.pay.ali.signType");
    // 服务接口名称， 固定值
    public final static String ALI_PAY_MOBILE_SECURITYPAY_PAY=ConfCenter.get("isj.pay.ali.sucuritypay");
    // 支付类型， 固定值
    public final static String ALI_PAY_PAYMENT_TYPE = ConfCenter.get("isj.pay.ali.paymentType");
    // 参数编码
    public final static String ALI_PAY_INPUT_CHARSET = ConfCenter.get("isj.pay.ali.inputCharset");
    // 设置未付款交易的超时时间
    public final static String ALI_PAY_IT_B_PAY = ConfCenter.get("isj.pay.ali.itBPay");
    // 即时到账有密退款接口
    public final static String ALI_PAY_TRADE_REFUND = ConfCenter.get("isj.pay.ali.refund");
    // 即时到账有密退款接口请求地址
    public final static String ALI_PAY_TRADE_REFUND_URL = ConfCenter.get("isj.pay.ali.refundUrl");
//    // 统一收单交易退款接口地址
//    public final static String ALI_PAY_TRADE_REFUND_URL = ConfCenter.get("isj.pay.ali.refundUrl");
//    // 统一收单交易退款接口
//    public final static String ALI_PAY_TRADE_REFUND = ConfCenter.get("isj.pay.ali.refund");
    // 支付宝应用窗APPID
    public final static String ALI_PAY_APPID = ConfCenter.get("isj.pay.ali.appId");

    // 订单支付成功
    public final static String ALI_PAY_RESULT_SUCCESS = "9000";
    // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
    public final static String ALI_PAY_RESULT_DEALING = "8000";
    // 订单支付失败
    public final static String ALI_PAY_RESULT_FAIL = "4000";
    // 用户中途取消
    public final static String ALI_PAY_RESULT_CANCEL = "6001";
    // 网络连接出错
    public final static String ALI_PAY_RESULT_NETWORK_ERROR = "6002";
    // 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
    public final static String ALI_PAY_RESULT_UNKOWN = "6004";

    // 通知交易状态
    // 未知交易状态
    public final static String ALI_PAY_NOTICE_TARDESTATUS_UNKONWN = "UNKONWN";
    // 交易创建，等待买家付款。
    public final static String ALI_PAY_NOTICE_TARDESTATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    // 在指定时间段内未支付时关闭的交易；
    // 在交易完成全额退款成功时关闭的交易。
    public final static String ALI_PAY_NOTICE_TARDESTATUS_TRADE_CLOSED = "TRADE_CLOSED";
    // 交易成功，且可对该交易做操作，如：多级分润、退款等。
    public final static String ALI_PAY_NOTICE_TARDESTATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
    // 交易成功且结束，即不可再做任何操作。
    public final static String ALI_PAY_NOTICE_TARDESTATUS_TRADE_FINISHED = "TRADE_FINISHED";
    // 退费成功
    public final static String ALI_PAY_NOTICE_REFUND_STATUS_SUCCESS = "REFUND_SUCCESS";

    /*支付宝退款状态取值范围 */
    // 支付宝退款状态取值  未知
    public static final String APP_ALIPAY_REFUND_STATUS_UNKNOWN_VALUE = "UNKOWN";
    // 支付宝退款状态取值  退款成功
    public static final String APP_ALIPAY_REFUND_STATUS_REFUND_SUCCESS_VALUE = "REFUND_SUCCESS";
    // 支付宝退款状态取值 退款关闭
    public static final String APP_REFUND_TRADE_STATUS_REFUND_CLOSED_VALUE = "REFUND_CLOSED";

}
