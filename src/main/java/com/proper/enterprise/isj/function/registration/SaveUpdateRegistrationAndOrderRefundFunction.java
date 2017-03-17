package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.function.message.SendRegistrationMsgFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundHisDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundReqDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.saveUpdateRegistrationAndOrderRefund(RefundReq)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveUpdateRegistrationAndOrderRefundFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;

    @Override
    public Object execute(Object... params) throws Exception {
        saveUpdateRegistrationAndOrderRefund((RefundReq) params[0]);
        return null;
    }

    public void saveUpdateRegistrationAndOrderRefund(RefundReq refundReq) throws Exception {
        Order order = toolkitx.executeRepositoryFunction(OrderRepository.class, "findByOrderNo",
                refundReq.getOrderId());
        if (order != null) {
            RegistrationDocument regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                    order.getFormId());
            RegistrationRefundReqDocument refund = new RegistrationRefundReqDocument();
            BeanUtils.copyProperties(refundReq, refund);
            regBack.setRegistrationRefundReq(refund);
            regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
            try {
                registrationServiceImpl.saveUpdateRegistrationAndOrderRefund(refundReq);
            } catch (Exception e) {
                RegistrationRefundHisDocument refundHis = new RegistrationRefundHisDocument();
                refundHis.setClientReturnMsg(e.getMessage());
                regBack.setRegistrationRefundHis(refundHis);
                toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
                info("挂号退款异常", e);
                throw e;
            }
            try {
                toolkitx.executeFunction(SendRegistrationMsgFunction.class, SendPushMsgEnum.REG_REFUND_SUCCESS,
                        regBack);
            } catch (Exception e) {
                debug("退费成功后,发送推送抛出异常,订单号:" + regBack.getOrderNum(), e);
            }
        }
    }

}
