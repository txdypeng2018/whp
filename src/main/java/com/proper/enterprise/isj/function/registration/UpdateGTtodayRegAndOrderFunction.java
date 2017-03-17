package com.proper.enterprise.isj.function.registration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.function.message.SendRegistrationMsgFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class UpdateGTtodayRegAndOrderFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Override
    public Object execute(Object... params) throws Exception {
        updateGTtodayRegAndOrder((PayRegReq) params[0], (Order) params[1], (RegistrationDocument) params[2],
                (RegistrationOrderReqDocument) params[3]);
        return null;
    }

    /**
     * 更新预约挂号.
     *
     * @param payRegReq 支付请求.
     * @param order 订单.
     * @param regBack 挂号记录.
     * @param payOrderRegDocument 支付报文.
     * @throws Exception 异常.
     */
    public void updateGTtodayRegAndOrder(PayRegReq payRegReq, Order order, RegistrationDocument regBack,
            RegistrationOrderReqDocument payOrderRegDocument) throws Exception {
        // 支付结果向his请求对象
        regBack.setRegistrationOrderReq(payOrderRegDocument);
        // 更新挂号信息
        regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
        try {
            registrationServiceImpl.updateRegistrationAndOrder(payRegReq);
            regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne", regBack.getId());
            toolkitx.executeFunction(SendRegistrationMsgFunction.class, SendPushMsgEnum.REG_PAY_SUCCESS,
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne", order.getFormId()));

            toolkitx.executeFunction(SaveOrRemoveRegCacheFunction.class, "0", order.getFormId(), null);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
        }catch (DelayException e) {
            throw e;
        }  catch (Exception e) {
            RegistrationOrderHisDocument his = regBack.getRegistrationOrderHis();
            his.setClientReturnMsg(e.getMessage());
            regBack.setRegistrationOrderHis(his);
            toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
            info("预约挂号出现异常", e);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
            try {
                if (regBack.getRegistrationOrderHis() != null
                        && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospPayId())) {
                    toolkitx.executeFunction(SaveCancelRegistrationFunction.class, regBack.getId(),
                            OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                } else {
                    registrationServiceImpl.saveCancelRegistrationImpl(regBack.getId(),
                            OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                    try {
                        toolkitx.executeFunction(SaveRegRefundFunction.class, regBack.getId());
                    } catch (Exception e3) {
                        debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e3);
                        debug("预约挂号失败后,退费发生异常,订单号:" + order.getOrderNo());
                        throw e;
                    }
                    try {
                        regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                        regBack.setStatus(CenterFunctionUtils
                                .getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));

                        toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
                        order.setPayWay(regBack.getPayChannelId());
                        // 订单状态 3:退费成功
                        order.setOrderStatus("3");
                        order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_PLATFORM_MSG);
                        order.setCancelDate(DateUtil.toTimestamp(new Date()));
                        // 更新订单状态
                        order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                        orderService.save(order);

                        toolkitx.executeFunction(SendRegistrationMsgFunction.class,
                                SendPushMsgEnum.REG_PAY_SUCCESS, toolkitx
                                .executeRepositoryFunction(RegistrationRepository.class, "findOne", order.getFormId()));

                    } catch (Exception e3) {
                        debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e3);
                        debug("预约挂号发生异常情况,订单号:{}", order.getOrderNo());
                    }
                }
            } catch (Exception e2) {
                debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e2);
            }
            throw e;
        }
    }
}
