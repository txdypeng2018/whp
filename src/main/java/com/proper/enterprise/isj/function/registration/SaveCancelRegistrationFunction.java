package com.proper.enterprise.isj.function.registration;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.function.message.SendRegistrationMsgFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationRefundLogDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRefundLogRepository;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.saveCancelRegistration(String,
 * OrderCancelTypeEnum)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveCancelRegistrationFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;

    @Autowired
    OrderService orderService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Override
    public Object execute(Object... params) throws Exception {
        saveCancelRegistration((String) params[0], (OrderCancelTypeEnum) params[1]);
        return null;
    }

    public void saveCancelRegistration(String registrationId, OrderCancelTypeEnum cancelType) throws Exception {
        // 校验挂号单号
        if (StringUtil.isEmpty(registrationId)) {
            return;
        }
        // 挂号单
        RegistrationDocument regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                registrationId);
        if (regBack == null) {
            return;
        }
        // 超时退号
        if (cancelType == null) {
            cancelType = OrderCancelTypeEnum.CANCEL_OVERTIME;
        }
        // 查询退款LOG信息
        List<RegistrationRefundLogDocument> refundLogDocumentList = toolkitx
                .executeRepositoryFunction(RegistrationRefundLogRepository.class, "findByNum", regBack.getNum());
        RegistrationRefundLogDocument newRefund = null;
        if (refundLogDocumentList.size() == 0) {
            newRefund = new RegistrationRefundLogDocument();
        }
        synchronized (regBack.getNum()) {
            // 未支付 或者 已支付并且没有退款 或者 超时
            if (regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.NOT_PAID.getValue()))
                    || (regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.PAID.getValue()))
                            && regBack.getRegistrationTradeRefund() == null)
                    || regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.SUSPEND_MED.getValue()))) {
                try {
                    regBack.setCancelRegToHisTime(DateUtil.toTimestamp(new Date()));
                    regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
                    // 进行退号操作
                    registrationServiceImpl.saveCancelRegistrationImpl(registrationId, cancelType);
                    toolkitx.executeFunction(SaveOrRemoveRegCacheFunction.class, "0", registrationId, null);
                    regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                            registrationId);
                    try {
                        webService4HisInterfaceCacheUtil.cacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                                regBack.getRegDate());
                        if (cancelType == OrderCancelTypeEnum.CANCEL_OVERTIME) {
                            if (regBack.getRegistrationOrderHis() != null
                                    && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospOrderId())) {
                                toolkitx.executeFunction(SendRegistrationMsgFunction.class,
                                        SendPushMsgEnum.REG_OVERTIME_CANCEL, regBack);
                            }
                        }
                        toolkitx.executeFunction(SaveOrUpdateRegRefundLogFunction.class, regBack, newRefund, "1", "-1",
                                "0");
                    } catch (Exception e) {
                        debug("医生号点信息缓存释放失败,或者是超时推送出现异常:{}{}", regBack.getNum(), e);
                    }
                } catch (Exception e) {
                    if (newRefund != null) {
                        newRefund.setDescription(e.getMessage());
                    }
                    try {
                        regBack.setCancelRegErrMsg(e.getMessage());
                        toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
                        toolkitx.executeFunction(SaveOrUpdateRegRefundLogFunction.class, regBack, newRefund, "0", "-1",
                                "0");
                    } catch (Exception e2) {
                        debug("RegistrationServiceNotxImpl.saveCancelRegistration[Exception]:", e2);
                        debug("保存错误消息时出现异常信息:{},异常:{}", regBack.getNum(), e2.getMessage());
                    }
                    debug("挂号单号:{}{}", regBack.getNum(), e);
                    throw e;
                }
            }
            regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne", registrationId);
            RefundReq refundReq = null;
            try {
                if (StringUtil.isEmpty(regBack.getOrderNum())) {
                    throw new Exception("挂号单中订单号字段信息为空,退费失败,挂号单号:".concat(regBack.getNum()));
                }
                if (regBack.getRegistrationOrderReq() != null
                        && StringUtil.isNotNull(regBack.getRegistrationOrderReq().getSerialNum())) {
                    toolkitx.executeFunction(SaveRegRefundFunction.class, registrationId);
                }
                regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne", registrationId);
                if (regBack.getRegistrationRefundReq() != null
                        && StringUtil.isNotEmpty(regBack.getRegistrationRefundReq().getRefundId())) {
                    refundReq = new RefundReq();
                    BeanUtils.copyProperties(regBack.getRegistrationRefundReq(), refundReq);
                }
                debug("挂号完成退费到支付平台,订单号:{}", regBack.getOrderNum());
                try {
                    toolkitx.executeFunction(SaveOrUpdateRegRefundLogFunction.class, regBack, newRefund, "1", "1", "0");
                } catch (Exception e) {
                    debug("退费后,保存日志信息出现异常,订单号:{}", regBack.getOrderNum(), e);
                }
            } catch (Exception e) {
                debug("挂号退费到支付平台失败,支付平台:{},订单号:{}", regBack.getPayChannelId(), regBack.getOrderNum(), e);
                if (newRefund != null) {
                    newRefund.setDescription(e.getMessage());
                }
                try {
                    regBack.setRefundErrMsg(e.getMessage());
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
                    toolkitx.executeFunction(SaveOrUpdateRegRefundLogFunction.class, regBack, newRefund, "1", "0", "0");
                } catch (Exception e2) {
                    debug("RegistrationServiceNotxImpl.saveCancelRegistration[Exception]:", e2);
                    debug("保存挂号退费日志时出现异常,订单号:{},异常信息:{}", regBack.getOrderNum(), e2.getMessage());
                }
                debug(e.getMessage());
                throw e;
            }
            if (refundReq != null && StringUtil.isNotEmpty(refundReq.getRefundId())) {
                if (regBack.getRegistrationRefundHis() == null
                        || StringUtil.isEmpty(regBack.getRegistrationRefundHis().getRefundFlag())) {
                    if (regBack.getRegistrationOrderHis() != null
                            && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospPayId())
                            && "1".equals(regBack.getIsAppointment())) {
                        toolkitx.executeFunction(SaveUpdateRegistrationAndOrderRefundFunction.class, refundReq);
                    } else {
                        try {
                            Order order = orderService.findByOrderNo(refundReq.getOrderId());
                            if (order != null) {
                                regBack = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                                        order.getFormId());
                                regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                                regBack.setStatus(CenterFunctionUtils
                                        .getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                                regBack.setRefundApplyType("1");
                                toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
                                order.setOrderStatus("3");
                                order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG);
                                order.setCancelDate(DateUtil.toTimestamp(new Date()));
                                // 更新订单状态
                                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                                orderService.save(order);
                            }
                        } catch (Exception e) {
                            debug("修改订单状态失败,订单号:{}", regBack.getOrderNum(), e);
                        }
                        try {
                            toolkitx.executeFunction(SendRegistrationMsgFunction.class,
                                    SendPushMsgEnum.REG_REFUND_SUCCESS, regBack);
                        } catch (Exception e) {
                            debug("退费成功后,发送推送抛出异常,订单号:{}", regBack.getOrderNum(), e);
                        }
                    }
                    debug("完成退费通知HIS,订单号:{}", regBack.getOrderNum());
                    try {
                        toolkitx.executeFunction(SaveOrUpdateRegRefundLogFunction.class, regBack, newRefund, "1", "1",
                                "1");
                    } catch (Exception e) {
                        debug("保存挂号退费日志时出现异常,订单号:{}", regBack.getOrderNum(), e);
                    }
                }
            }
        }
    }

}
