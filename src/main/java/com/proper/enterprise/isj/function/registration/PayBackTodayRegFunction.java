package com.proper.enterprise.isj.function.registration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.function.message.SendRegistrationMsgFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.payBackTodayReg(PayRegReq, Order,
 * RegistrationDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class PayBackTodayRegFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Override
    public Object execute(Object... params) throws Exception {
        payBackTodayReg((PayRegReq) params[0], (Order) params[1], (RegistrationDocument) params[2]);
        return null;
    }

    /**
     * 当日挂号,异常情况进行退费.
     *
     * @param payRegReq 支付请求.
     * @param order 订单.
     * @param regBack 挂号信息.
     * @throws RegisterException 异常.
     */
    public void payBackTodayReg(PayRegReq payRegReq, Order order, RegistrationDocument regBack)
            throws RegisterException {
        RefundReq req = toolkitx.executeFunction(SaveRegRefundFunction.class, regBack.getId());
        regBack = toolkitx.executeFunction(FetchRegistrationDocumentByIdFunction.class, regBack.getId());
        if (req == null) {
            // 更新订单状态 5:退费失败
            order.setOrderStatus("5");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(payRegReq.getPayChannelId()));
            toolkitx.executeRepositoryFunction(OrderRepository.class, "save", order);

            regBack.setStatusCode(RegistrationStatusEnum.REFUND_FAIL.getValue());
            regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(regBack.getStatusCode()));
            regBack.setNum("F".concat(DateUtil.toString(new Date(), "yyMMddHHmmssSSS")));
            toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
            toolkitx.executeFunction(SendRegistrationMsgFunction.class, SendPushMsgEnum.REG_PAY_HIS_RETURNERR_MSG,
                    order.getFormId());
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        } else {
            // 更新订单状态 3:退费成功
            order.setOrderStatus("3");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            order.setPayWay(String.valueOf(payRegReq.getPayChannelId()));
            toolkitx.executeRepositoryFunction(OrderRepository.class, "save", order);

            regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
            regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(regBack.getStatusCode()));
            regBack.setNum("F".concat(DateUtil.toString(new Date(), "yyMMddHHmmssSSS")));
            toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
            toolkitx.executeFunction(SendRegistrationMsgFunction.class, SendPushMsgEnum.REG_PAY_HIS_RETURNMSG,
                    order.getFormId());
        }
    }

}
