package com.proper.enterprise.isj.payment.service.impl;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.api.pay.service.NoticeService;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.pay.ali.entity.AliEntity;
import com.proper.enterprise.platform.pay.ali.service.AliPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付宝异步通知业务处理类.
 */
@Service("pay_notice_ali")
public class AliNoticeServiceImpl implements NoticeService<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliNoticeServiceImpl.class);

    @Autowired
    AliPayService aliPayService;

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RecipeService recipeService;

    /**
     * 异步通知业务处理代码
     *
     * @param params 支付宝异步通知处理参数
     */
    @Override
    public void saveNoticeProcess(Map<String, String>  params) {
        LOGGER.debug("-------------支付宝异步通知相关业务处理--------处理开始---------");
        try {
            if (params == null || !params.containsKey("out_trade_no")) {
                LOGGER.debug("支付宝异步通知参数解析异常:{}", JSONUtil.toJSON(params));
            } else {
                // 取得订单号
                String outTradeNo = params.get("out_trade_no");
                // 获取异步通知参数
                AliEntity aliInfo = aliPayService.getAliNoticeInfo(params);
                // 取得订单信息
                Order orderinfo = orderService.findByOrderNo(outTradeNo);
                // 没有处理过订单
                if (orderinfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay")) {
                    // 处理支付宝异步通知信息
                    synchronized (orderinfo.getOrderNo()) {
                        try {
                            // 挂号
                            if (orderinfo.getFormClassInstance().equals(RegistrationDocument.class.getName())) {
                                PayRegReq payReg = registrationService.convertAppInfo2PayReg(aliInfo, orderinfo.getFormId());
                                // 向HIS发送异步通知结果之前保存请求对象
                                if (payReg != null) {
                                    registrationService.saveUpdateRegistrationAndOrder(payReg);
                                } else {
                                    LOGGER.debug("未查到已支付的挂号单信息,订单号:{}", outTradeNo);
                                }
                                // 缴费
                            } else {
                                orderinfo = recipeService.saveUpdateRecipeAndOrder(orderinfo.getOrderNo(),
                                        String.valueOf(PayChannel.ALIPAY.getCode()), aliInfo);
                                if (orderinfo == null) {
                                    LOGGER.debug("缴费异常,订单号:{}", outTradeNo);
                                } else {
                                    orderService.save(orderinfo);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.debug("支付成功后,调用HIS接口中发生了错误,订单号:{}{}", outTradeNo, e);
                        }
                    }
                    Order orderinfoRet = orderService.findByOrderNo(outTradeNo);
                    LOGGER.debug("更新订单状态为:{}", orderinfoRet.getPaymentStatus());
                }
            }
            LOGGER.debug("-------------支付宝异步通知相关业务处理-----正常结束------------");
        } catch (Exception e) {
            LOGGER.debug("支付宝异步通知业务处理异常:{}", e);
        }
    }
}
