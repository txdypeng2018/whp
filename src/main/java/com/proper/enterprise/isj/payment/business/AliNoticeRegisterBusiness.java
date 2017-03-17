package com.proper.enterprise.isj.payment.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.AliEntityContext;
import com.proper.enterprise.isj.context.OrderInfoEntityContext;
import com.proper.enterprise.isj.context.OutTradeNoContext;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.utils.ConfCenter;

@Service
public class AliNoticeRegisterBusiness<T, M extends OrderInfoEntityContext<T> & AliEntityContext<T> & OutTradeNoContext<T>>
        implements IBusiness<T, M> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliNoticeRegisterBusiness.class);

    @Autowired
    RegistrationService registrationService;

    @Autowired
    OrderService orderService;

    @Override
    public void process(M ctx) throws Exception {
        Order orderinfo = ctx.getOrderinfo();

        if (orderinfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay")) {

            try {
                if (orderinfo.getFormClassInstance().equals(RegistrationDocument.class.getName())) {
                    PayRegReq payReg = registrationService.convertAppInfo2PayReg(ctx.getAliInfo(),
                            orderinfo.getFormId());
                    // 向HIS发送异步通知结果之前保存请求对象
                    if (payReg != null) {
                        registrationService.saveUpdateRegistrationAndOrder(payReg);
                    } else {
                        LOGGER.debug("未查到已支付的挂号单信息,订单号:{}", ctx.getOutTradeNo());
                    }

                    Order orderinfoRet = orderService.findByOrderNo(ctx.getOutTradeNo());
                    LOGGER.debug("更新订单状态为:{}", orderinfoRet.getPaymentStatus());
                }
            } catch (Exception e) {
                LOGGER.debug("支付成功后,调用HIS接口中发生了错误,订单号:{}{}", ctx.getOutTradeNo(), e);
            }

        }
    }
}
