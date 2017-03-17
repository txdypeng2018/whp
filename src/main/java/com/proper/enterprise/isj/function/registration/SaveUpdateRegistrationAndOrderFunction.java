package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.logger.PayStepEnum;
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SaveUpdateRegistrationAndOrderFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    OrderService orderService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;
    
    @Override
    public Object execute(Object... params) throws Exception {
        saveUpdateRegistrationAndOrder((PayRegReq)params[0]);
        return null;
    }
    
    public void saveUpdateRegistrationAndOrder(PayRegReq payRegReq) throws Exception {
        try {
            Order order = orderService.findByOrderNo(payRegReq.getOrderId());
            if (order == null) {
                debug("挂号单支付回调,失败原因:未找到订单,订单号:{}", payRegReq.getOrderId());
                return;
            }
            RegistrationDocument regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                    order.getFormId());
            RegistrationOrderReqDocument payOrderRegDocument = regBack.getRegistrationOrderReq();
            if (payOrderRegDocument == null) {
                return;
            }
            boolean canNoticeHisFlag = false;
            if (StringUtil.isEmpty(payOrderRegDocument.getSerialNum())) {
                canNoticeHisFlag = true;
            }
            debug("通知his前确认订单状态,订单号:{},支付状态:{}", payRegReq.getOrderId(), regBack.getStatusCode());
            // 请求his前保存请求对象
            BeanUtils.copyProperties(payRegReq, payOrderRegDocument);
            if (regBack.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue()) && canNoticeHisFlag) {
                debug("挂号单是未支付状态,进行调用,订单号:{}", payRegReq.getOrderId());
                // 预约挂号
                if (regBack.getIsAppointment().equals("1")) {
                    if (regBack.getRegistrationRefundReq() == null
                            || StringUtil.isEmpty(regBack.getRegistrationRefundReq().getOrderId())) {
                        // 预约挂号与his业务处理
                        toolkitx.executeFunction(UpdateGTtodayRegAndOrderFunction.class, payRegReq, order, regBack,
                                payOrderRegDocument);
                    }
                    // 单日挂号
                } else {
                    // 当日挂号与his业务处理
                    toolkitx.executeFunction(UpdateEqualtodayRegAndOrderFunction.class, payRegReq, order, regBack,
                            payOrderRegDocument);
                }
            } else {
                debug("挂号单是已支付状态,不进行调用,订单号:{}", payRegReq.getOrderId());
                webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                        regBack.getRegDate());
            }
        } catch (DelayException e) {
            PayLogUtils.log(PayStepEnum.UNKNOWN, payRegReq, e.getPosition());
        } catch (Exception e) {
            debug("更新挂号订单失败,订单号:{}{}", payRegReq.getOrderId(), e);
        }
    }

}
