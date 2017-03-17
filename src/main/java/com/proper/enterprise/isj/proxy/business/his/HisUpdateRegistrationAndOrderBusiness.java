package com.proper.enterprise.isj.proxy.business.his;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.PayOrderRegReqContext;
import com.proper.enterprise.isj.context.PayRegReqContext;
import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.function.registration.UpdateRegistrationAndOrderStatusFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.res.PayReg;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.utils.JSONUtil;

@Service
public class HisUpdateRegistrationAndOrderBusiness<M extends PayRegReqContext<Object> & PayOrderRegReqContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    public static final int FUNC_REGSVCIMPL_UPDATE_REG_AND_ODR = 0x00010000;
    public static final int FUNC_REGSVCIMPL_UPDATE_REG_AND_ODR_APPOINTMENT_NET_ERROR = FUNC_REGSVCIMPL_UPDATE_REG_AND_ODR
            | PayLogUtils.CAUSE_TYPE_EXCEPTION | 0x1;
    public static final int FUNC_REGSVCIMPL_UPDATE_REG_AND_ODR_TODAY_NET_ERROR = FUNC_REGSVCIMPL_UPDATE_REG_AND_ODR
            | PayLogUtils.CAUSE_TYPE_EXCEPTION | 0x2;

    @Autowired
    OrderService orderService;

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    UpdateRegistrationAndOrderStatusFunction updateRegistrationAndOrderStatusFunction;

    @Override
    public void process(M ctx) throws Exception {

        String channelId;
        ResModel<PayReg> payRegRes = null;
        Order order;
        // 预约挂号
        PayRegReq payRegReq = ctx.getPayRegReq();
        PayOrderRegReq payOrderRegReq = ctx.getPayOrderRegReq();
        if (payRegReq != null) {
            channelId = payRegReq.getPayChannelId();
            synchronized (payRegReq.getOrderId()) {
                order = orderService.findByOrderNo(payRegReq.getOrderId());
            }
            if (order != null) {
                debug("预约挂号缴费请求参数----------->>>:{}", JSONUtil.toJSON(payRegReq));
                try {
                    payRegRes = webServicesClient.payReg(payRegReq);
                } catch (InvocationTargetException ite) {
                    if (ite.getCause() != null && ite.getCause() instanceof RemoteAccessException) {
                        debug("预约挂号缴费网络连接异常", ite);
                        throw new DelayException(FUNC_REGSVCIMPL_UPDATE_REG_AND_ODR_APPOINTMENT_NET_ERROR);
                    }
                    throw ite;
                }
            }
        } else {
            channelId = payOrderRegReq.getPayChannelId();
            synchronized (payOrderRegReq.getOrderId()) {
                order = orderService.findByOrderNo(payOrderRegReq.getOrderId());
            }
            if (order != null) {
                debug("当日挂号缴费请求参数----------->>>");
                debug(JSONUtil.toJSON(payOrderRegReq));
                try {
                    payRegRes = webServicesClient.payOrderReg(payOrderRegReq);
                } catch (InvocationTargetException ite) {
                    if (ite.getCause() != null && ite.getCause() instanceof RemoteAccessException) {
                        debug("当日挂号缴费网络连接异常", ite);
                        throw new DelayException(FUNC_REGSVCIMPL_UPDATE_REG_AND_ODR_TODAY_NET_ERROR);
                    }
                    throw ite;
                }
            }
        }
        if (order != null) {
            FunctionUtils.invoke(updateRegistrationAndOrderStatusFunction, channelId, payRegRes, order);
        }
    }
}
