package com.proper.enterprise.isj.pay.weixin.constants;

import com.proper.enterprise.platform.core.utils.ConfCenter;

public class WeixinConstants {

    /**
     * 服务号相关信息
     */
    //服务号的应用号
    public final static String WEIXIN_PAY_APPID = ConfCenter.get("isj.pay.wx.appId");
    //服务号的应用密码
    public final static String WEIXIN_PAY_APP_SECRECT = ConfCenter.get("isj.pay.wx.appSecret");
    //服务号的配置token
    public final static String WEIXIN_PAY_TOKEN = "weixinCourse";
    //商户号
    public final static String WEIXIN_PAY_MCH_ID = ConfCenter.get("isj.pay.wx.mchId");
    //API密钥
    public final static String WEIXIN_PAY_API_KEY = ConfCenter.get("isj.pay.wx.apiKey");
    //签名加密方式
    public final static String WEIXIN_PAY_SIGN_TYPE = "MD5";
    //微信支付统一接口的回调action
    public final static String NOTIFY_URL = "http://14.117.25.80:8016/wxweb/config/pay!paySuccess.action";
    //微信支付成功支付后跳转的地址
    public final static String SUCCESS_URL = "http://14.117.25.80:8016/wxweb/contents/config/pay_success.jsp";
    //oauth2授权时回调action
    public final static String REDIRECT_URI = "http://14.117.25.80:8016/GoMyTrip/a.jsp?port=8016";
    //可用余额退款/基本账户
    public final static String REFUND_SOURCE_RECHARGE_FUNDS = "REFUND_SOURCE_RECHARGE_FUNDS";
    //未结算资金退款
    public final static String REFUND_SOURCE_UNSETTLED_FUNDS = "REFUND_SOURCE_UNSETTLED_FUNDS";
    /**
     * 微信基础接口地址
     */
    //获取token接口(GET)
    public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    //oauth2授权接口(GET)
    public final static String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    //刷新access_token接口（GET）
    public final static String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    // 菜单创建接口（POST）
    public final static String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    // 菜单查询（GET）
    public final static String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    // 菜单删除（GET）
    public final static String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
    /**
     * 微信支付接口地址
     */
    //微信支付统一接口(POST)
    public final static String UNIFIED_ORDER_URL = ConfCenter.get("isj.pay.wx.url.unified");
    //微信退款接口(POST)
    public final static String REFUND_URL = ConfCenter.get("isj.pay.wx.refund.url");
    //订单查询接口(POST)
    public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    //关闭订单接口(POST)
    public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
    //退款查询接口(POST)
    public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
    //对账单接口(POST)
    public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    //短链接转换接口(POST)
    public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
    //接口调用上报接口(POST)
    public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";

    public final static String TIME_FORMAT = ConfCenter.get("isj.pay.wx.timeFormat");
    public final static int RANDOM_LEN = ConfCenter.getInt("isj.pay.wx.randomLen", 32);

}
