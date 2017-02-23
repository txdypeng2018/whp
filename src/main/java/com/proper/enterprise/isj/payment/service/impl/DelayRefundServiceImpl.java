package com.proper.enterprise.isj.payment.service.impl;

import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.logger.PayLogConstrants;
import com.proper.enterprise.isj.payment.logger.entity.DefaultPayLogRecordEntity;
import com.proper.enterprise.isj.payment.logger.repository.PayLogRecordRepository;
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils;
import com.proper.enterprise.isj.payment.service.DelayRefundService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class DelayRefundServiceImpl implements DelayRefundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayRefundServiceImpl.class);

    private static final Object UPDATE_STATUS_LOCK = new Object();

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    PayLogRecordRepository repo;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    OrderService orderService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Override
    public void doDelayRefund(long delayTime) {
        List<DefaultPayLogRecordEntity> list = repo.findByStepAndStepStatus(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY, PayLogConstrants.STATUS_DEFAULT);

        for (DefaultPayLogRecordEntity record : list) {
            if (validateCanBeHandle(record, delayTime)) {
                handleRecord(record);
            }
        }
    }

    public static final int FUNC_HANDLERECORD = 0xA0020000;
    public static final int FUNC_HANDLERECORD_NULL_OBJ = FUNC_HANDLERECORD | PayLogUtils.CAUSE_TYPE_FAIL | 0x1;
    public static final int FUNC_HANDLERECORD_WRONG_TYPE = FUNC_HANDLERECORD | PayLogUtils.CAUSE_TYPE_FAIL | 0x2;
    public static final int FUNC_HANDLERECORD_NOT_OBTIAN_REG_TIME = FUNC_HANDLERECORD | PayLogUtils.CAUSE_TYPE_FAIL | 0x3;

    protected void handleRecord(DefaultPayLogRecordEntity record) {
        try {
            Object obj = fetchRecordObject(record);
            if (obj != null) {
                if (obj instanceof PayRegReq) {
                    PayRegReq req = (PayRegReq) obj;
                    Order order = orderService.findByOrderNo(req.getOrderId());
                    RegistrationDocument doc = getRegistrationDocumentById(order.getFormId());
                    try {
                        registrationService.saveOrRemoveCacheRegKey(doc, "1"); // 占号点
                        registrationService.saveUpdateRegistrationAndOrder(req);
                    } catch (RegisterException e) {
                        // 占不上号点的时候会捕获此异常
                        LOGGER.error("延时退款是无法获得号点：" + JSONUtil.toJSON(record));
                        updateStatusAsFail(record, FUNC_HANDLERECORD_NOT_OBTIAN_REG_TIME);
                        // 调用退费，如果仍然抛出异常则到本方法最后Throwable的捕获处理中，恢复处理标记，等待下次执行
                        refund(order, doc);
                    }
                    success(record);
                } else {
                    LOGGER.error("延时退款日志中业务对象类型异常：" + JSONUtil.toJSON(record));
                    updateStatusAsFail(record, FUNC_HANDLERECORD_WRONG_TYPE);
                }
            } else {
                LOGGER.error("延时退款日志中业务对象为空：" + JSONUtil.toJSON(record));
                updateStatusAsFail(record, FUNC_HANDLERECORD_NULL_OBJ);
            }
        } catch (Throwable t) {
            restoreStatus(record);
            LOGGER.error(t.getMessage(), t);
        }
    }

    private void refund(Order order, RegistrationDocument regBack) throws Exception {

        RegistrationOrderHisDocument his = regBack.getRegistrationOrderHis();
        regBack.setRegistrationOrderHis(his);
        registrationRepository.save(regBack);
        webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
        try {
            if (regBack.getRegistrationOrderHis() != null && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospPayId())) {
                registrationService.saveCancelRegistration(regBack.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
            } else {
                registrationServiceImpl.saveCancelRegistrationImpl(regBack.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                try {
                    registrationService.saveRegRefund(regBack.getId());
                } catch (Exception e3) {
                    LOGGER.debug("com.proper.enterprise.isj.payment.service.impl.DelayRefundServiceImpl.refund(Order, RegistrationDocument)[Exception]:", e3);
                    LOGGER.debug("延时退款,退费发生异常,订单号:" + order.getOrderNo());
                    throw e3;
                }
                try {
                    regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                    regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                    registrationService.saveRegistrationDocument(regBack);
                    order.setPayWay(regBack.getPayChannelId());
                    // 订单状态 3:退费成功
                    order.setOrderStatus("3");
                    order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_PLATFORM_MSG);
                    order.setCancelDate(DateUtil.toTimestamp(new Date()));
                    // 更新订单状态
                    order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                    orderService.save(order);
                    registrationService.sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_REFUND_SUCCESS);
                } catch (Exception e3) {
                    LOGGER.debug("com.proper.enterprise.isj.payment.service.impl.DelayRefundServiceImpl.refund(Order, RegistrationDocument)[Exception]:", e3);
                    LOGGER.debug("延时退款预约挂号发生异常情况,订单号:{}", order.getOrderNo());
                    throw e3;
                }
            }
        } catch (Exception e2) {
            LOGGER.debug("com.proper.enterprise.isj.payment.service.impl.DelayRefundServiceImpl.refund(Order, RegistrationDocument)[Exception]:", e2);
            throw e2;
        }

    }

    private void success(DefaultPayLogRecordEntity record) {
        synchronized (UPDATE_STATUS_LOCK) {
            DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
            cur.setStep(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY);
            cur.setStepStatus(PayLogConstrants.STATUS_MASK_SUCCESS);
            repo.save(cur);
            BeanUtils.copyProperties(cur, record);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Object fetchRecordObject(DefaultPayLogRecordEntity record) {
        Object res = null;
        String className = record.getJavaType();
        String content = record.getContent();
        try {
            Class clz = Class.forName(className);
            res = JSONUtil.parse(content, clz);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return res;
    }

    /**
     * 延时退款日志无法被处理(处理失败时)修改失败位置.
     *
     * @param record 被影响的记录.
     */
    protected void updateStatusAsFail(DefaultPayLogRecordEntity record, int position) {
        synchronized (UPDATE_STATUS_LOCK) {
            DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
            cur.setStep(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY);
            cur.setStepStatus(PayLogConstrants.STATUS_MASK_FAIL);
            cur.setCause(position);
            repo.save(cur);
            BeanUtils.copyProperties(cur, record);
        }
    }

    /**
     * 发生异常，恢复到可以重新执行的状态.
     *
     * @param record 被影响的记录.
     */
    protected void restoreStatus(DefaultPayLogRecordEntity record) {
        synchronized (UPDATE_STATUS_LOCK) {
            DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
            cur.setStep(PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY);
            cur.setStepStatus(PayLogConstrants.STATUS_DEFAULT);
            repo.save(cur);
            BeanUtils.copyProperties(cur, record);
        }
    }

    protected boolean validateCanBeHandle(DefaultPayLogRecordEntity record, long delayTime) {
        boolean res = false;

        Date logDate = DateUtil.toDateTime(record.getLogTm());
        long logTm = logDate.getTime();
        long curTm = System.currentTimeMillis();

        res = logTm + delayTime * 1000 < curTm;
        if (res) {
            synchronized (UPDATE_STATUS_LOCK) {
                DefaultPayLogRecordEntity cur = repo.findOne(record.getId());
                res = cur.getStep() == PayLogConstrants.STEP_MASK_UNKNOWN_AND_RETRY && cur.getStepStatus() == PayLogConstrants.STATUS_DEFAULT;
                if (res) {
                    cur.setStepStatus(PayLogConstrants.STATUS_MASK_START);
                    repo.save(cur);
                    record.setStepStatus(PayLogConstrants.STATUS_MASK_START);
                }
            }
        }
        return res;
    }

    public RegistrationDocument getRegistrationDocumentById(String id) {
        return registrationServiceImpl.getRegistrationDocumentById(id);
    }

}
