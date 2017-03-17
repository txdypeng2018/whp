package com.proper.enterprise.isj.function.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.wechat.entity.WechatEntity;
import com.proper.enterprise.platform.pay.wechat.model.WechatNoticeRes;
import com.proper.enterprise.platform.pay.wechat.service.WechatPayService;

@Service
public class WechatSaveNoticeProcessFunction implements IFunction<Object>, ILoggable{

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    WechatPayService wechatPayService;
    
    @Override
    public Object execute(Object... params) throws Exception {
        saveNoticeProcess((WechatNoticeRes)params[0]);
        return null;
    }


    /**
     * 异步通知业务处理代码
     *
     * @param noticeRes 微信异步通知处理参数
     */
    public void saveNoticeProcess(WechatNoticeRes  noticeRes) {
        debug("-------------微信异步通知相关业务处理--------处理开始---------");
        try {
            if (noticeRes == null || StringUtil.isEmpty(noticeRes.getOutTradeNo())) {
                debug("微信异步通知参数解析异常:{}", JSONUtil.toJSON(noticeRes));
            } else {
                // 取得订单号
                String orderNo = noticeRes.getOutTradeNo();
                // 查询订单
                Order orderInfo = orderService.findByOrderNo(orderNo);
                // 获取异步通知保存对象
                WechatEntity wechatInfo = wechatPayService.getWechatNoticeInfo(noticeRes);
                // 没有处理过订单
                if (orderInfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay")) {
                    // 处理微信异步通知信息
                    synchronized (orderInfo.getOrderNo()) {
                        try {
                            // 挂号
                            if (orderInfo.getFormClassInstance().equals(RegistrationDocument.class.getName())) {
                                PayRegReq payReg = registrationService.convertAppInfo2PayReg(wechatInfo,
                                        orderInfo.getFormId());
                                // 向HIS发送异步通知结果之前保存请求对象
                                if (payReg != null) {
                                    registrationService.saveUpdateRegistrationAndOrder(payReg);
                                } else {
                                    debug("未查到已支付的挂号单信息,订单号:{}", orderNo);
                                }
                                // 缴费
                            } else {
                                orderInfo = recipeService.saveUpdateRecipeAndOrder(orderInfo.getOrderNo(),
                                        String.valueOf(PayChannel.WECHATPAY.getCode()), wechatInfo);
                                if (orderInfo == null) {
                                    debug("缴费异常,订单号:{}", orderNo);
                                } else {
                                    orderService.save(orderInfo);
                                }
                            }
                        } catch (Exception e) {
                            debug("支付成功后,调用HIS接口中发生了错误,订单号:{}{}", orderNo, e);
                        }
                    }
                    Order orderinfoRet = orderService.findByOrderNo(orderNo);
                    debug("更新订单状态为:{}", orderinfoRet.getPaymentStatus());
                }
            }
            debug("-------------微信异步通知相关业务处理-----正常结束------------");
        } catch (Throwable e) {
            debug("微信异步通知业务处理异常:{}", e);
        }
    }

}
