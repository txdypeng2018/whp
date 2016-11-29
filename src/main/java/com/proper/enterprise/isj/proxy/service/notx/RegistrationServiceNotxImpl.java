package com.proper.enterprise.isj.proxy.service.notx;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.entity.AliEntity;
import com.proper.enterprise.isj.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.isj.pay.ali.model.AliRefundRes;
import com.proper.enterprise.isj.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.pay.weixin.entity.WeixinEntity;
import com.proper.enterprise.isj.pay.weixin.model.WeixinPayQueryRes;
import com.proper.enterprise.isj.pay.weixin.model.WeixinRefundReq;
import com.proper.enterprise.isj.pay.weixin.model.WeixinRefundRes;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationRefundLogDocument;
import com.proper.enterprise.isj.proxy.document.registration.*;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRefundLogRepository;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/9/16 0016.
 */
@Service
public class RegistrationServiceNotxImpl implements RegistrationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RegistrationServiceNotxImpl.class);

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;

    @Autowired
    MessagesService messagesService;

    @Autowired
    AliService aliService;

    @Autowired
    WeixinService weixinService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;
    
    @Autowired
    RegistrationRefundLogRepository registrationRefundLogRepository;


    /**
     * 创建挂号单
     * 
     * @param reg
     *            挂号单信息
     * @param isAppointment
     * @return
     * @throws Exception
     */
    @Override
    public RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument reg, String isAppointment)
            throws Exception {
        RegistrationDocument saveReg = null;
        try {
            saveReg = registrationServiceImpl.saveCreateRegistration(reg);
            saveReg = registrationServiceImpl.saveCreateRegistrationAndOrder(saveReg, isAppointment);
            if (isAppointment.equals(String.valueOf(1))) {
                webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(saveReg.getDoctorId(),
                        saveReg.getRegDate());
                saveOrRemoveRegCache(String.valueOf(1), saveReg.getId(),
                        DateUtil.toDate(saveReg.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.debug(e.getMessage());
            if (saveReg != null) {
                if (isAppointment.equals(String.valueOf(1))) {
                    try {
                        if(saveReg.getRegistrationOrderHis()!=null&&StringUtil.isNotEmpty(saveReg.getRegistrationOrderHis().getHospOrderId())){
                            registrationServiceImpl.saveCancelRegistrationImpl(saveReg.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        LOGGER.debug(e2.getCause().getMessage());
                    }
                }
                if(StringUtil.isNotEmpty(saveReg.getId())){
                    registrationRepository.delete(saveReg.getId());
                }
            }
            throw e;
        }
        return saveReg;
    }

    @Override
    public void saveUpdateRegistrationAndOrder(PayRegReq payRegReq) throws Exception {
        if (payRegReq != null) {
            try {
                synchronized (payRegReq.getOrderId()) {
                    Order order = orderService.findByOrderNo(payRegReq.getOrderId());
                    if (order == null) {
                        LOGGER.debug("挂号单支付回调,失败原因:未找到订单,订单号:" + payRegReq.getOrderId());
                        return;
                    }
                    RegistrationDocument regBack = this.getRegistrationDocumentById(order.getFormId());
                    RegistrationOrderReqDocument payOrderRegDocument = regBack.getRegistrationOrderReq();
                    BeanUtils.copyProperties(payRegReq, payOrderRegDocument);
                    if (regBack.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
                        LOGGER.debug("挂号单是未支付状态,进行调用,订单号:" + payRegReq.getOrderId());
                        if (regBack.getIsAppointment().equals(String.valueOf(1))) {
                            updateGTtodayRegAndOrder(payRegReq, order, regBack, payOrderRegDocument);
                        } else {
                            updateEqualtodayRegAndOrder(payRegReq, order, regBack, payOrderRegDocument);
                        }
                    } else {
                        LOGGER.debug("挂号单是已支付状态,不进行调用,订单号:" + payRegReq.getOrderId());
                        webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                                regBack.getRegDate());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.debug("更新挂号订单失败,订单号:" + payRegReq.getOrderId());
            }

        }
    }

    /**
     * 更新当日挂号
     * 
     * @param payRegReq
     * @param order
     * @param regBack
     * @param payOrderRegDocument
     * @throws Exception
     */
    private void updateEqualtodayRegAndOrder(PayRegReq payRegReq, Order order, RegistrationDocument regBack,
            RegistrationOrderReqDocument payOrderRegDocument) throws Exception {
        OrderRegReq orderReq = registrationServiceImpl.convertRegistration2OrderReg(regBack);
        BeanUtils.copyProperties(orderReq, payOrderRegDocument);
        regBack.setRegistrationOrderReq(payOrderRegDocument);
        regBack = this.saveRegistrationDocument(regBack);
        try {
            PayOrderRegReq payOrderRegReq = new PayOrderRegReq();
            BeanUtils.copyProperties(payOrderRegDocument, payOrderRegReq);
            registrationServiceImpl.updateRegistrationAndOrder(payOrderRegReq);
            sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_SUCCESS);
            saveOrRemoveRegCache(String.valueOf(0), order.getFormId(), null);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
        } catch (Exception e) {
            RegistrationOrderHisDocument his = regBack.getRegistrationOrderHis();
            his.setClientReturnMsg(e.getMessage());
            regBack.setRegistrationOrderHis(his);
            regBack = registrationRepository.save(regBack);
            payBackTodayReg(payRegReq, order, regBack);
            LOGGER.info(e.getMessage());
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
            throw e;
        }

    }

    /**
     * 更新预约挂号
     * 
     * @param payRegReq
     * @param order
     * @param regBack
     * @param payOrderRegDocument
     * @throws Exception
     */
    private void updateGTtodayRegAndOrder(PayRegReq payRegReq, Order order, RegistrationDocument regBack,
            RegistrationOrderReqDocument payOrderRegDocument) throws Exception {
        regBack.setRegistrationOrderReq(payOrderRegDocument);
        regBack = this.saveRegistrationDocument(regBack);
        try {
            registrationServiceImpl.updateRegistrationAndOrder(payRegReq);
            regBack = this.getRegistrationDocumentById(regBack.getId());
            sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_SUCCESS);
            saveOrRemoveRegCache(String.valueOf(0), order.getFormId(), null);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
        } catch (Exception e) {
            RegistrationOrderHisDocument his = regBack.getRegistrationOrderHis();
            his.setClientReturnMsg(e.getMessage());
            regBack.setRegistrationOrderHis(his);
            registrationRepository.save(regBack);
            e.printStackTrace();
            LOGGER.info(e.getMessage());
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
            try {
                if (regBack.getRegistrationOrderHis() != null
                        && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospPayId())) {
                    this.saveCancelRegistration(regBack.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                } else {
                    registrationServiceImpl.saveCancelRegistrationImpl(regBack.getId(),
                            OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                    try {
                        this.saveRegRefund(regBack.getId());
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        LOGGER.debug("预约挂号失败后,退费发生异常,订单号:" + order.getOrderNo());
                        throw e;
                    }
                    try {
                        regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                        regBack.setStatus(CenterFunctionUtils
                                .getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                        this.saveRegistrationDocument(regBack);

                        order.setPayWay(regBack.getPayChannelId());
                        order.setOrderStatus(String.valueOf(3));
                        order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_PLATFORM_MSG);
                        order.setCancelDate(DateUtil.toTimestamp(new Date()));
                        // 更新订单状态
                        order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                        orderService.save(order);
                        sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_REFUND_SUCCESS);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        LOGGER.debug("预约挂号发生异常情况,订单号:" + order.getOrderNo());
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            throw e;
        }
    }

    /**
     * 当日挂号,异常情况进行退费
     * 
     * @param payRegReq
     * @param order
     * @param regBack
     * @throws RegisterException
     */
    private void payBackTodayReg(PayRegReq payRegReq, Order order, RegistrationDocument regBack)
            throws RegisterException {
        try {
            RefundReq req = this.saveRegRefund(regBack.getId());
            if (req == null) {
                order.setOrderStatus(String.valueOf(5));
                // 更新订单状态
                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                order.setPayWay(String.valueOf(payRegReq.getPayChannelId()));
                orderService.save(order);

                regBack.setStatusCode(RegistrationStatusEnum.REFUND_FAIL.getValue());
                regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(regBack.getStatusCode()));
                regBack.setNum("F" + DateUtil.toString(new Date(), "yyMMddHHmmssSSS"));
                registrationRepository.save(regBack);
                sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_HIS_RETURNERR_MSG);
                throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
            } else {
                order.setOrderStatus(String.valueOf(3));
                // 更新订单状态
                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                order.setPayWay(String.valueOf(payRegReq.getPayChannelId()));
                orderService.save(order);

                regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(regBack.getStatusCode()));
                regBack.setNum("F" + DateUtil.toString(new Date(), "yyMMddHHmmssSSS"));
                registrationRepository.save(regBack);
                sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_HIS_RETURNMSG);
            }
        } catch (Exception refundException) {
            refundException.printStackTrace();
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
    }

    /**
     * 清除或保存30分钟倒计时的挂号单
     *
     * @param type
     *            1:添加,0:清除
     * @param regId
     */
    private synchronized void saveOrRemoveRegCache(String type, String regId, Date regCreateTime) {
        Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP);
        Cache.ValueWrapper valueWrapper = tempCache.get(CenterFunctionUtils.CACHE_KEY_REGITRATION_SCHEDULER_TASK);
        Map<String, Date> regMap = new HashMap<>();
        if (valueWrapper != null && valueWrapper.get() != null) {
            regMap = (Map<String, Date>) valueWrapper.get();
        }
        if (type.equals(String.valueOf(1))) {
            regMap.put(regId, regCreateTime);
        } else {
            regMap.remove(regId);
        }
        tempCache.put(CenterFunctionUtils.CACHE_KEY_REGITRATION_SCHEDULER_TASK, regMap);
    }

    @Override
    public RefundReq saveRegRefund(String registrationId) throws Exception {
        RegistrationDocument reg = this.getRegistrationDocumentById(registrationId);
        if (reg == null) {
            LOGGER.debug("未查到挂号单信息,挂号单Id:"+registrationId);
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
        Order order = orderService.getByFormId(registrationId);
        RefundReq refundReq = null;
        if (order == null) {
            LOGGER.debug("未查到订单表信息,挂号单Id:"+registrationId);
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
        if (StringUtil.isEmpty(reg.getPayChannelId())) {
            LOGGER.debug("未查到挂号单的支付方式,订单号:" + order.getOrderNo());
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
        if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            RegistrationTradeRefundDocument trade = null;
            if (reg.getRegistrationTradeRefund() == null) {
                trade = new RegistrationTradeRefundDocument();
                trade.setOutRefundNo(CenterFunctionUtils.createRegOrOrderNo(2));
                trade.setRefundFee(String.valueOf(reg.getAmount()));
                trade.setTotalFee(String.valueOf(reg.getAmount()));
                trade.setOutTradeNo(reg.getOrderNum());
                reg.setRegistrationTradeRefund(trade);
                this.saveRegistrationDocument(reg);
            } else {
                trade = reg.getRegistrationTradeRefund();
            }
            WeixinRefundReq req = new WeixinRefundReq();
            req.setOutRefundNo(trade.getOutRefundNo());
            req.setRefundFee(Integer.parseInt(trade.getRefundFee()));
            req.setTotalFee(Integer.parseInt(trade.getTotalFee()));
            req.setOutTradeNo(trade.getOutTradeNo());
            PayResultRes resultRes = weixinService.saveWeixinRefund(req);
            if (resultRes.getResultCode().equals(String.valueOf(0))) {
                req.setNonceStr(resultRes.getResultCode());
                req.setTransactionId(resultRes.getPrepayid());
                refundReq = this.convertAppRefundInfo2RefundReq(req, order.getOrderNo(), trade.getOutRefundNo());
                RegistrationRefundReqDocument refundHisReq = new RegistrationRefundReqDocument();
                BeanUtils.copyProperties(refundReq, refundHisReq);
                reg.setRegistrationRefundReq(refundHisReq);
                reg.setRefundApplyType(String.valueOf(1));
                this.saveRegistrationDocument(reg);
            } else {
                LOGGER.debug("退号(微信退费失败),订单号:" + trade.getOutTradeNo());
                throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
            }
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            RegistrationTradeRefundDocument trade = null;
            if (reg.getRegistrationTradeRefund() == null) {
                trade = new RegistrationTradeRefundDocument();
                trade.setOutTradeNo(reg.getOrderNum());
                trade.setOutRequestNo(CenterFunctionUtils.createRegOrOrderNo(2));
                trade.setRefundAmount(reg.getAmount());
                reg.setRegistrationTradeRefund(trade);
                this.saveRegistrationDocument(reg);
            } else {
                trade = reg.getRegistrationTradeRefund();
            }
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(reg.getAmount()));
            bigDecimal = bigDecimal.divide(new BigDecimal("100"));
            AliRefundRes refundRes = aliService.saveAliRefundResProcess(trade.getOutTradeNo(), trade.getOutRequestNo(),
                    bigDecimal.toString(), null);
            if (refundRes != null) {
                if (refundRes.getCode().equals("10000") && refundRes.getMsg().equals("Success")) {
                    RegistrationRefundReqDocument refundHisReq = new RegistrationRefundReqDocument();
                    try {
                        refundReq = this.convertAppRefundInfo2RefundReq(refundRes, order.getOrderNo(),
                                trade.getOutRequestNo());
                        BeanUtils.copyProperties(refundReq, refundHisReq);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType(String.valueOf(1));
                    this.saveRegistrationDocument(reg);
                } else {
                    LOGGER.debug("退号(支付宝退费失败),订单号:" + trade.getOutTradeNo());
                    throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
                }
            } else {
                LOGGER.debug("退号(支付宝退费失败),订单号:" + trade.getOutTradeNo());
                throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
            }
        }
        LOGGER.debug("退费请求参数--------------------->>>");
        if (refundReq != null) {
            LOGGER.debug(JSONUtil.toJSON(refundReq));
        }
        return refundReq;
    }

    /**
     * 付款成功后,发送挂号成功短信
     *
     * @param registrationId
     * @throws Exception
     */
    private void sendRegistrationMsg(String registrationId, SendPushMsgEnum pushType) throws Exception {
        RegistrationDocument updateReg = this.getRegistrationDocumentById(registrationId);
        /*---------挂号具体信息-------*/
        sendRegistrationMsg(pushType, updateReg);
    }

    private void sendRegistrationMsg(SendPushMsgEnum pushType, RegistrationDocument updateReg) throws Exception {
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(pushType, updateReg));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(updateReg.getCreateUserId());
        regMsg.setUserName(updateReg.getOperatorPhone());
        messagesService.saveMessage(regMsg);
    }

    @Override
    public void saveUpdateRegistrationAndOrderRefund(RefundReq refundReq) throws Exception {
        Order order = orderService.findByOrderNo(refundReq.getOrderId());
        if (order != null) {
            RegistrationDocument regBack = this.getRegistrationDocumentById(order.getFormId());
            RegistrationRefundReqDocument refund = new RegistrationRefundReqDocument();
            BeanUtils.copyProperties(refundReq, refund);
            regBack.setRegistrationRefundReq(refund);
            regBack = this.saveRegistrationDocument(regBack);
            try {
                registrationServiceImpl.saveUpdateRegistrationAndOrderRefund(refundReq);
            } catch (Exception e) {
                RegistrationRefundHisDocument refundHis = new RegistrationRefundHisDocument();
                refundHis.setClientReturnMsg(e.getMessage());
                regBack.setRegistrationRefundHis(refundHis);
                registrationRepository.save(regBack);
                LOGGER.info(e.getMessage());
                throw e;
            }
            try {
                this.sendRegistrationMsg(SendPushMsgEnum.REG_REFUND_SUCCESS, regBack);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.debug("退费成功后,发送推送抛出异常,异常信息:" + e.getMessage() + ",订单号:" + regBack.getOrderNum());
            }
        }

    }

    @Override
    public PayRegReq convertAppInfo2PayReg(Object infoObj, String regId) {
        String hosId = CenterFunctionUtils.getHosId();
        PayRegReq payRegReq = new PayRegReq();
        payRegReq.setHosId(hosId);
        RegistrationDocument reg = this.getRegistrationDocumentById(regId);
        if (reg == null) {
            return null;
        }
        try {
            int fee = Integer.parseInt(reg.getAmount());
            if (infoObj instanceof AliEntity) {
                AliEntity aliEntity = (AliEntity) infoObj;
                payRegReq.setOrderId(reg.getOrderNum());
                payRegReq.setSerialNum(aliEntity.getTradeNo());
                payRegReq.setPayDate(aliEntity.getNotifyTime().split(" ")[0]);
                payRegReq.setPayTime(aliEntity.getNotifyTime().split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(2));
                payRegReq.setPayResCode(aliEntity.getTradeStatus());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(aliEntity.getBuyerId());
            } else if (infoObj instanceof AliPayTradeQueryRes) {
                AliPayTradeQueryRes payTradeQuery = (AliPayTradeQueryRes) infoObj;
                payRegReq.setOrderId(reg.getOrderNum());
                payRegReq.setSerialNum(payTradeQuery.getTradeNo());
                payRegReq.setPayDate(payTradeQuery.getSendPayDate().split(" ")[0]);
                payRegReq.setPayTime(payTradeQuery.getSendPayDate().split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(2));
                payRegReq.setPayResCode(payTradeQuery.getTradeStatus());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(payTradeQuery.getBuyerUserId());
            } else if (infoObj instanceof WeixinEntity) {
                WeixinEntity weixinEntity = (WeixinEntity) infoObj;
                payRegReq.setOrderId(reg.getOrderNum());
                payRegReq.setSerialNum(weixinEntity.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinEntity.getTimeEnd(), "yyyyMMddHHmmss");

                payRegReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payRegReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(1));
                payRegReq.setPayResCode(weixinEntity.getResultCode());
                payRegReq.setMerchantId(weixinEntity.getMchId());
                payRegReq.setTerminalId(weixinEntity.getDeviceInfo());
                payRegReq.setPayAccount(weixinEntity.getAppid());
            } else if (infoObj instanceof WeixinPayQueryRes) {
                WeixinPayQueryRes weixinPayQuery = (WeixinPayQueryRes) infoObj;
                payRegReq.setOrderId(reg.getOrderNum());
                payRegReq.setSerialNum(weixinPayQuery.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinPayQuery.getTimeEnd(), "yyyyMMddHHmmss");

                payRegReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payRegReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(1));
                payRegReq.setPayResCode(weixinPayQuery.getResultCode());
                payRegReq.setMerchantId(weixinPayQuery.getMchId());
                payRegReq.setTerminalId(weixinPayQuery.getDeviceInfo());
                payRegReq.setPayAccount(weixinPayQuery.getAppid());
            }
            payRegReq.setBankNo("");
            payRegReq.setPayResDesc("");
            payRegReq.setPayTotalFee(fee);
            payRegReq.setPayCopeFee(fee);
            payRegReq.setPayFee(fee);
            payRegReq.setOperatorId(CenterFunctionUtils.convertDistrictId2OperatorId(reg.getDistrictId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return payRegReq;
    }

    @Override
    public RefundReq convertAppRefundInfo2RefundReq(Object infoObj, String orderNo, String refundId) {
        String hosId = CenterFunctionUtils.getHosId();
        RefundReq refundReq = new RefundReq();
        refundReq.setHosId(hosId);
        int fee = 0;
        Order order = orderService.findByOrderNo(orderNo);
        if (orderNo != null) {
            RegistrationDocument reg = this.getRegistrationDocumentById(order.getFormId());
            if (reg != null) {
                if (infoObj instanceof AliRefundRes) {
                    AliRefundRes refund = (AliRefundRes) infoObj;
                    refundReq.setOrderId(reg.getOrderNum());
                    refundReq.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                    refundReq.setRefundId(refundId);
                    refundReq.setRefundSerialNum(refund.getTradeNo());
                    BigDecimal bigDecimal = new BigDecimal(refund.getRefundFee());
                    bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                    refundReq.setTotalFee(bigDecimal.intValue());
                    refundReq.setRefundFee(bigDecimal.intValue());
                    refundReq.setRefundDate(refund.getGmtRefundPay().split(" ")[0]);
                    refundReq.setRefundTime(refund.getGmtRefundPay().split(" ")[1]);
                    if (StringUtil.isNotEmpty(refund.getMsg())) {
                        refundReq.setRefundResCode(refund.getMsg());
                    } else {
                        refundReq.setRefundResCode("");
                    }
                    refundReq.setRefundResDesc("");
                    refundReq.setRefundRemark("");
                } else if (infoObj instanceof WeixinRefundReq) {
                    WeixinRefundReq refund = (WeixinRefundReq) infoObj;
                    refundReq.setOrderId(reg.getOrderNum());
                    refundReq.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                    refundReq.setRefundId(refundId);
                    refundReq.setRefundSerialNum(refund.getTransactionId());
                    refundReq.setTotalFee(refund.getTotalFee());
                    refundReq.setRefundFee(refund.getTotalFee());
                    refundReq.setRefundDate(DateUtil.toDateString(new Date()));
                    refundReq.setRefundTime(DateUtil.toTimestamp(new Date(), false).split(" ")[1]);
                    if (StringUtil.isNotEmpty(refund.getNonceStr())) {
                        refundReq.setRefundResCode(refund.getNonceStr());
                    } else {
                        refundReq.setRefundResCode("");
                    }
                    refundReq.setRefundResDesc("");
                    refundReq.setRefundRemark("");
                }
            }
        }
        return refundReq;
    }

    @Override
    public List<RegistrationDocument> findRegistrationByCreateUserIdAndPayStatus(String userId, String status,
            String isAppointment) {
        return registrationRepository.findByCreateUserIdAndStatusCodeAndIsAppointment(userId, status, isAppointment);
    }

    @Override
    public RegistrationDocument getRegistrationDocumentById(String id) {
        return registrationServiceImpl.getRegistrationDocumentById(id);
    }

    @Override
    public RegistrationDocument saveQueryPayTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception {
        if (registrationDocument.getIsAppointment().equals(String.valueOf(1))) {
            String payWay = registrationDocument.getPayChannelId();
            if (StringUtil.isNotEmpty(payWay)) {
                PayRegReq payReg = null;
                if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                    AliPayTradeQueryRes query = aliService.getAliPayTradeQueryRes(registrationDocument.getOrderNum());
                    if (query != null && query.getCode().equals("10000")
                            && query.getTradeStatus().equals("TRADE_SUCCESS")) {
                        payReg = this.convertAppInfo2PayReg(query, registrationDocument.getId());
                    }
                } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                    WeixinPayQueryRes wQuery = weixinService.getWeixinPayQueryRes(registrationDocument.getOrderNum());
                    if (wQuery != null && wQuery.getResultCode().equals("SUCCESS")
                            && wQuery.getTradeState().equals("SUCCESS")) {
                        payReg = this.convertAppInfo2PayReg(wQuery, registrationDocument.getId());
                    }
                }
                if (payReg != null) {
                    this.saveUpdateRegistrationAndOrder(payReg);
                    registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
                }
            }
        } else {
            PayRegReq payReg = null;
            AliPayTradeQueryRes query = aliService.getAliPayTradeQueryRes(registrationDocument.getOrderNum());
            if (query != null && query.getCode().equals("10000") && query.getTradeStatus().equals("TRADE_SUCCESS")) {
                payReg = this.convertAppInfo2PayReg(query, registrationDocument.getId());

            }
            if (payReg == null) {
                WeixinPayQueryRes wQuery = weixinService.getWeixinPayQueryRes(registrationDocument.getOrderNum());
                if (wQuery != null && wQuery.getResultCode().equals("SUCCESS")) {
                    payReg = this.convertAppInfo2PayReg(wQuery, registrationDocument.getId());
                }
            }
            if (payReg == null) {
                synchronized (registrationDocument.getId()) {
                    registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
                    registrationDocument.setStatusCode(RegistrationStatusEnum.EXCHANGE_CLOSED.getValue());
                    registrationDocument.setStatus(
                            CenterFunctionUtils.getRegistrationStatusName(registrationDocument.getStatusCode()));
                    registrationDocument.setNum("F" + DateUtil.toString(new Date(), "yyMMddHHmmssSSS"));
                    registrationDocument = registrationRepository.save(registrationDocument);
                    sendRegistrationMsg(registrationDocument.getId(), SendPushMsgEnum.REG_TODAY_NOT_PAY_HIS_MSG);
                }
            } else {
                this.saveUpdateRegistrationAndOrder(payReg);
                registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
            }
        }

        return registrationDocument;
    }

    @Override
    public RegistrationDocument saveQueryRefundTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception {
        boolean queryFlag = false;
        RegistrationTradeRefundDocument refund = registrationDocument.getRegistrationTradeRefund();
        if (StringUtil.isNotEmpty(refund.getOutRequestNo())) {
            AliRefundTradeQueryRes refundQuery = aliService.getAliRefundTradeQueryRes(refund.getOutTradeNo(),
                    refund.getOutRequestNo());
            if (refundQuery != null && refundQuery.getCode().equals("10000")) {
                queryFlag = true;
            }
        } else {
            WeixinRefundRes weixinRefundQuery = weixinService.getWeixinRefundRes(refund.getOutTradeNo());
            if (weixinRefundQuery != null) {
                queryFlag = true;
            }
        }
        if (queryFlag) {
            RegistrationRefundReqDocument req = registrationDocument.getRegistrationRefundReq();
            RefundReq refundReq = new RefundReq();
            BeanUtils.copyProperties(req, refundReq);
            this.saveUpdateRegistrationAndOrderRefund(refundReq);
            registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
        }
        return registrationDocument;
    }

    @Override
    public RegistrationDocument getRegistrationDocumentByNum(String num) {
        return registrationServiceImpl.getRegistrationDocumentByNum(num);
    }

    @Override
    public void deleteRegistrationDocument(RegistrationDocument reg) {
        registrationServiceImpl.deleteRegistrationDocument(reg);
    }

    @Override
    public RegistrationDocument saveRegistrationDocument(RegistrationDocument reg) {
        return registrationServiceImpl.saveRegistrationDocument(reg);
    }

    @Override
    public List<RegistrationDocument> findRegistrationDocumentList(String patientId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("patientId").is(patientId));
        query.with(new Sort(Sort.Direction.DESC, "apptDate").and(new Sort(Sort.Direction.DESC, "createTime")));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    @Override
    public List<RegistrationDocument> findOverTimeRegistrationDocumentList() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -CenterFunctionUtils.ORDER_COUNTDOWN);
        Query query = new Query();
        query.addCriteria(Criteria.where("createTime")
                .lte(DateUtil.toString(cal.getTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT)).and("statusCode")
                .is(RegistrationStatusEnum.NOT_PAID.getValue()));
        query.with(new Sort(Sort.Direction.DESC, "apptDate"));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    @Override
    public List<RegistrationDocument> findAlreadyCancelRegAndRefundErrRegList() {
        Query query = new Query();
        Pattern cancelHisReturnMsgPattern = Pattern.compile("^.*" + ReturnCode.SUCCESS + ".*$",
                Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("statusCode").is(RegistrationStatusEnum.PAID.getValue())
                .and("cancelHisReturnMsg").regex(cancelHisReturnMsgPattern));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    @Override
    public void saveCancelRegistration(String registrationId, OrderCancelTypeEnum cancelType) throws Exception {

        if (StringUtil.isEmpty(registrationId)) {
            return;
        }
        RegistrationDocument regBack = this.getRegistrationDocumentById(registrationId);
        if (regBack == null) {
            return;
        }
        if (cancelType == null) {
            cancelType = OrderCancelTypeEnum.CANCEL_OVERTIME;
        }
        List<RegistrationRefundLogDocument> refundLogDocumentList = registrationRefundLogRepository
                .findByNum(regBack.getNum());
        RegistrationRefundLogDocument newRefund = null;
        if (refundLogDocumentList.size() == 0) {
            newRefund = new RegistrationRefundLogDocument();
        }
        synchronized (regBack.getNum()){
            if (regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.NOT_PAID.getValue()))
                    || (regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.PAID.getValue()))
                    && regBack.getRegistrationTradeRefund() == null)
                    || regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.SUSPEND_MED.getValue()))) {
                try {
                    regBack.setCancelRegToHisTime(DateUtil.toTimestamp(new Date()));
                    regBack = registrationRepository.save(regBack);
                    registrationServiceImpl.saveCancelRegistrationImpl(registrationId, cancelType);
                    saveOrRemoveRegCache(String.valueOf(0), registrationId, null);
                    regBack = this.getRegistrationDocumentById(registrationId);
                    try {
                        webService4HisInterfaceCacheUtil.cacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
                        if (cancelType == OrderCancelTypeEnum.CANCEL_OVERTIME) {
                            if (regBack.getRegistrationOrderHis() != null
                                    && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospOrderId())) {
                                sendRegistrationMsg(SendPushMsgEnum.REG_OVERTIME_CANCEL, regBack);
                            }
                        }
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(-1),
                                String.valueOf(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.debug("医生号点信息缓存释放失败,或者是超时推送出现异常:" + regBack.getNum() + ",异常" + e.getMessage());
                    }
                } catch (Exception e) {
                    if(newRefund!=null){
                        newRefund.setDescription(e.getMessage());
                    }
                    try {
                        regBack.setCancelRegErrMsg(e.getMessage());
                        registrationRepository.save(regBack);
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(0), String.valueOf(-1),
                                String.valueOf(0));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        LOGGER.debug("保存错误消息时出现异常信息:" + regBack.getNum() + ",异常" + e2.getMessage());
                    }
                    LOGGER.debug("挂号单号:"+regBack.getNum()+",退号时抛出异常"+e.getMessage());
                    throw e;
                }
            }

            if (cancelType != OrderCancelTypeEnum.CANCEL_OVERTIME
                    && (regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.PAID.getValue())) || regBack
                    .getStatusCode().equals(String.valueOf(RegistrationStatusEnum.SUSPEND_MED.getValue())))) {
                regBack = this.getRegistrationDocumentById(registrationId);
                RefundReq refundReq = null;
                try {
                    if(StringUtil.isEmpty(regBack.getOrderNum())){
                        throw  new Exception("挂号单中订单号字段信息为空,退费失败,挂号单号:"+regBack.getNum());
                    }
                    if (regBack.getRegistrationRefundReq() == null
                            || StringUtil.isEmpty(regBack.getRegistrationRefundReq().getRefundId())) {
                        refundReq = this.saveRegRefund(registrationId);
                    } else {
                        refundReq = new RefundReq();
                        BeanUtils.copyProperties(regBack.getRegistrationRefundReq(), refundReq);
                        LOGGER.debug("调用HIS退费接口异常,再次调用退费,不执行支付平台的退费,订单号:" + regBack.getOrderNum());
                    }
                    LOGGER.debug("挂号完成退费到支付平台,订单号:" + regBack.getOrderNum());
                    try {
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(1),
                                String.valueOf(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.debug("退费后,保存日志信息出现异常,订单号:" + regBack.getOrderNum() + ",异常信息:" + e.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.debug("挂号退费到支付平台失败,支付平台:" + regBack.getPayChannelId() + ",订单号:" + regBack.getOrderNum());
                    if(newRefund!=null){
                        newRefund.setDescription(e.getMessage());
                    }
                    try {
                        regBack.setRefundErrMsg(e.getMessage());
                        registrationRepository.save(regBack);
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(0),
                                String.valueOf(0));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        LOGGER.debug("保存挂号退费日志时出现异常,订单号:" + regBack.getOrderNum() + ",异常信息:" + e2.getMessage());
                    }
                    LOGGER.debug(e.getMessage());
                    throw e;
                }
                if (refundReq != null&&StringUtil.isNotEmpty(refundReq.getRefundId())) {
                    this.saveUpdateRegistrationAndOrderRefund(refundReq);
                    LOGGER.debug("完成退费通知HIS,订单号:" + regBack.getOrderNum());
                    try {
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(1),
                                String.valueOf(1));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.debug("保存挂号退费日志时出现异常,订单号:" + regBack.getOrderNum() + ",异常信息:" + e.getMessage());
                    }

                }
            }
        }
    }

    @Override
    public void saveOrUpdateRegRefundLog(RegistrationDocument regBack, RegistrationRefundLogDocument refundLogDocument,
            String cancelRegStatus, String refundStatus, String refundHisStatus) {
        if(refundLogDocument!=null){
            refundLogDocument.setNum(regBack.getNum());
            refundLogDocument.setOrderNum(regBack.getOrderNum());
            refundLogDocument.setPayChannelId(regBack.getPayChannelId());
            refundLogDocument.setRegistrationId(regBack.getId());
            refundLogDocument.setCancelRegStatus(cancelRegStatus);
            refundLogDocument.setRefundStatus(refundStatus);
            refundLogDocument.setRefundHisStatus(refundHisStatus);
            registrationRefundLogRepository.save(refundLogDocument);
        }
    }

    @Override
    public List<RegistrationDocument> findRegistrationDocumentByStopReg(Map<String, String> paramMap) {
        Query query = new Query();
        query.addCriteria(Criteria.where("doctorId").is(paramMap.get("doctorId")).and("deptId")
                .is(paramMap.get("deptId")).and("regDate").is(paramMap.get("regDate")).and("beginTime")
                .gte(paramMap.get("beginTime")).lte(paramMap.get("endTime")).and("statusCode")
                .in(RegistrationStatusEnum.NOT_PAID.getValue(), RegistrationStatusEnum.PAID.getValue()));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    @Override
    public void saveRefundAndUpdateRegistrationDocument(RegistrationDocument reg) throws Exception {
        RegistrationTradeRefundDocument trade = reg.getRegistrationTradeRefund();
        boolean refundFlag = false;
        if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            if (trade == null) {
                trade = new RegistrationTradeRefundDocument();
                trade.setOutTradeNo(reg.getOrderNum());
                trade.setOutRefundNo(CenterFunctionUtils.createRegOrOrderNo(2));
                trade.setRefundFee(reg.getAmount());
                trade.setTotalFee(reg.getAmount());
                reg.setRegistrationTradeRefund(trade);
                reg = this.saveRegistrationDocument(reg);
            }
            WeixinRefundReq req = new WeixinRefundReq();
            req.setOutRefundNo(trade.getOutRefundNo());
            req.setRefundFee(Integer.parseInt(trade.getRefundFee()));
            req.setTotalFee(Integer.parseInt(trade.getTotalFee()));
            req.setOutTradeNo(trade.getOutTradeNo());
            PayResultRes resultRes = weixinService.saveWeixinRefund(req);
            if (resultRes.getResultCode().equals("0")) {
                refundFlag = true;
            } else {
                LOGGER.debug("挂号退费失败,挂号单号:" + reg.getNum() + ",订单号:" + reg.getOrderNum() + ",失败原因:"
                        + resultRes.getResultMsg());
            }
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            if (trade == null) {
                trade = new RegistrationTradeRefundDocument();
                trade.setOutTradeNo(reg.getOrderNum());
                trade.setOutRequestNo(CenterFunctionUtils.createRegOrOrderNo(2));
                trade.setRefundAmount(reg.getAmount());
                reg.setRegistrationTradeRefund(trade);
                reg = this.saveRegistrationDocument(reg);
            }
            BigDecimal bigDecimal = new BigDecimal(reg.getAmount());
            bigDecimal = bigDecimal.divide(new BigDecimal("100"));
            AliRefundRes refunRes = aliService.saveAliRefundResProcess(trade.getOutTradeNo(), trade.getOutRequestNo(),
                    bigDecimal.toString(), null);
            if (refunRes != null) {
                if (refunRes.getCode().equals("10000")) {
                    refundFlag = true;
                } else {
                    LOGGER.debug(
                            "挂号退费失败,挂号单号:" + reg.getNum() + ",订单号:" + reg.getOrderNum() + ",失败原因:" + refunRes.getMsg());
                }
            } else {
                LOGGER.debug("挂号退费失败,挂号单号:" + reg.getNum() + ",订单号:" + reg.getOrderNum() + ",失败原因:返回为空");
            }
        }
        if (refundFlag && !reg.getStatusCode().equals(RegistrationStatusEnum.REFUND.getValue())) {
            reg.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
            reg.setRefundApplyType(String.valueOf(2));
            reg.setStatus(CenterFunctionUtils.getRegistrationStatusName(reg.getStatusCode()));
            this.saveRegistrationDocument(reg);
            updateOrderEntity(reg);
            sendRegistrationMsg(reg.getId(), SendPushMsgEnum.REG_REFUND_SUCCESS);
        }
    }

    private void updateOrderEntity(RegistrationDocument reg) {
        Order order = orderService.findByOrderNo(reg.getOrderNum());
        if (order != null) {
            order.setOrderStatus(String.valueOf(3));
            order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_PLATFORM_MSG);
            order.setCancelDate(DateUtil.toTimestamp(new Date()));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            orderService.save(order);
        }
    }

    @Override
    public List<RegistrationDocument> findRegistrationDocumentByCreateUserIdAndPatientIdCard(String createUserId,
            String patientIdCard) {
        return registrationRepository.findRegistrationDocumentByCreateUserIdAndPatientIdCard(createUserId,
                patientIdCard);
    }
}
