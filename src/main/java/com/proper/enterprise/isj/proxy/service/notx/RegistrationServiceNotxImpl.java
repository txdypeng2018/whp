package com.proper.enterprise.isj.proxy.service.notx;

import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.entity.AliEntity;
import com.proper.enterprise.isj.pay.ali.model.Ali;
import com.proper.enterprise.isj.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.isj.pay.ali.model.AliRefundRes;
import com.proper.enterprise.isj.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.entity.CmbQueryRefundEntity;
import com.proper.enterprise.isj.pay.cmb.model.QueryRefundRes;
import com.proper.enterprise.isj.pay.cmb.model.QuerySingleOrderRes;
import com.proper.enterprise.isj.pay.cmb.model.RefundNoDupBodyReq;
import com.proper.enterprise.isj.pay.cmb.model.RefundNoDupRes;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.pay.weixin.entity.WeixinEntity;
import com.proper.enterprise.isj.pay.weixin.model.Weixin;
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
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

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
    CmbService cmbService;

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
            LOGGER.debug("挂号出现异常", e);
            if (saveReg != null) {
                if (isAppointment.equals(String.valueOf(1))) {
                    try {
                        if(saveReg.getRegistrationOrderHis()!=null&&StringUtil.isNotEmpty(saveReg.getRegistrationOrderHis().getHospOrderId())){
                            registrationServiceImpl.saveCancelRegistrationImpl(saveReg.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                        }
                    } catch (Exception e2) {
                        LOGGER.debug("RegistrationServiceNotxImpl.saveCreateRegistrationAndOrder[Exception]:", e);
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
                    if (payOrderRegDocument == null) {
                        return;
                    }
                    boolean canNoticeHisFlag = false;
                    if (StringUtil.isEmpty(payOrderRegDocument.getSerialNum())) {
                        canNoticeHisFlag = true;
                    }
                    LOGGER.debug("通知his前确认订单状态,订单号:" + payRegReq.getOrderId() + ",支付状态:" + regBack.getStatusCode());
                    BeanUtils.copyProperties(payRegReq, payOrderRegDocument);
                    if (regBack.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())
                            && canNoticeHisFlag) {
                        LOGGER.debug("挂号单是未支付状态,进行调用,订单号:" + payRegReq.getOrderId());
                        if (regBack.getIsAppointment().equals(String.valueOf(1))) {
                            if (regBack.getRegistrationRefundReq() == null
                                    || StringUtil.isEmpty(regBack.getRegistrationRefundReq().getOrderId())) {
                                updateGTtodayRegAndOrder(payRegReq, order, regBack, payOrderRegDocument);
                            }
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
                LOGGER.debug("更新挂号订单失败,订单号:" + payRegReq.getOrderId(), e);
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
            LOGGER.info("当日挂号更新出现异常", e);
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
            LOGGER.info("预约挂号出现异常", e);
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
                        LOGGER.debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e3);
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
                        LOGGER.debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e3);
                        LOGGER.debug("预约挂号发生异常情况,订单号:" + order.getOrderNo());
                    }
                }
            } catch (Exception e2) {
                LOGGER.debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e2);
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
            regBack = this.getRegistrationDocumentById(regBack.getId());
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
            LOGGER.debug("RegistrationServiceNotxImpl.payBackTodayReg[Exception]:", refundException);
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

    /**
     * 退款操作
     *
     * @param registrationId
     * @return
     * @throws Exception
     */
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
                trade.setOutRefundNo(reg.getOrderNum()+"001");
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
                LOGGER.debug("退号(微信退费失败),微信返回的错误消息:" + resultRes.getResultMsg() + ",订单号:" + trade.getOutTradeNo());
                //throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
            }
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            RegistrationTradeRefundDocument trade = null;
            if (reg.getRegistrationTradeRefund() == null) {
                trade = new RegistrationTradeRefundDocument();
                trade.setOutTradeNo(reg.getOrderNum());
                trade.setOutRequestNo(reg.getOrderNum()+"001");
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
                        LOGGER.debug("挂号退费转换成HIS需要的参数时异常", e);
                    }
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType(String.valueOf(1));
                    this.saveRegistrationDocument(reg);
                } else {
                    LOGGER.debug("退号(支付宝退费失败),支付宝返回Code" + refundRes.getCode() + ",支付宝返回消息:" + refundRes.getMsg()
                            + ",订单号:" + trade.getOutTradeNo());
                    //throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
                }
            } else {
                LOGGER.debug("退号(支付宝退费失败),支付宝返回对象为空,订单号:" + trade.getOutTradeNo());
                //throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
            }
        }  else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            RegistrationTradeRefundDocument trade = null;
            if (reg.getRegistrationTradeRefund() == null) {
                trade = new RegistrationTradeRefundDocument();
                trade.setOutTradeNo(reg.getOrderNum());
                trade.setCmbRefundNo(reg.getOrderNum().substring(0, 18) + "01");
                trade.setRefundAmount(reg.getAmount());
                reg.setRegistrationTradeRefund(trade);
                this.saveRegistrationDocument(reg);
            } else {
                trade = reg.getRegistrationTradeRefund();
            }
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(reg.getAmount()));
            bigDecimal = bigDecimal.divide(new BigDecimal("100"));
            // 生成一网通退款请求对象
            RefundNoDupBodyReq refundInfo = new RefundNoDupBodyReq();
            CmbPayEntity cmbInfo = cmbService.getQueryInfo(trade.getOutTradeNo());
            // 原订单号
            refundInfo.setBillNo(cmbInfo.getBillNo());
            // 交易日期
            refundInfo.setDate(cmbInfo.getDate());
            // 退款流水号
            refundInfo.setRefundNo(trade.getCmbRefundNo());
            // 退款金额
            refundInfo.setAmount(bigDecimal.toString());
            RefundNoDupRes cmbRefundRes = cmbService.saveRefundResult(refundInfo);
            if (cmbRefundRes != null) {
                if (StringUtil.isNull(cmbRefundRes.getHead().getCode())) {
                    RegistrationRefundReqDocument refundHisReq = new RegistrationRefundReqDocument();
                    try {
                        refundReq = this.convertAppRefundInfo2RefundReq(cmbRefundRes, order.getOrderNo(),
                                trade.getCmbRefundNo());
                        BeanUtils.copyProperties(refundReq, refundHisReq);
                    } catch (Exception e) {
                        LOGGER.debug("挂号退费转换成HIS需要的参数时异常", e);
                    }
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType(String.valueOf(4));
                    this.saveRegistrationDocument(reg);
                } else {
                    LOGGER.debug("退号(一网通退费失败),订单号:" + trade.getOutTradeNo());
                }
            } else {
                LOGGER.debug("退号(一网通退费失败),支付宝返回对象为空,订单号:" + trade.getOutTradeNo());
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
                LOGGER.info("挂号退款异常", e);
                throw e;
            }
            try {
                this.sendRegistrationMsg(SendPushMsgEnum.REG_REFUND_SUCCESS, regBack);
            } catch (Exception e) {
                LOGGER.debug("退费成功后,发送推送抛出异常,订单号:" + regBack.getOrderNum(), e);
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
            } else if (infoObj instanceof CmbPayEntity) {
                CmbPayEntity cmbEntity = (CmbPayEntity) infoObj;
                payRegReq.setOrderId(reg.getOrderNum());
                // 支付信息
                String account = cmbEntity.getMsg();
                payRegReq.setSerialNum(account.substring(account.length() - 20, account.length()));
                // 交易日期
                String date = DateUtil.toString(DateUtil.toDate(cmbEntity.getDate(), "yyyyMMdd"), "yyyy-MM-dd");

                payRegReq.setPayDate(date);
                payRegReq.setPayTime(cmbEntity.getTime());
                payRegReq.setPayChannelId(String.valueOf(4));
                payRegReq.setPayResCode(cmbEntity.getSucceed());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(cmbEntity.getBillNo());
            } else if (infoObj instanceof QuerySingleOrderRes) {
                QuerySingleOrderRes cmbPayQuery = (QuerySingleOrderRes) infoObj;
                payRegReq.setOrderId(reg.getOrderNum());
                payRegReq.setSerialNum(cmbPayQuery.getBody().getBankSeqNo());
                // 日期
                String date = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptDate(), "yyyyMMdd"), "yyyy-MM-dd");
                // 时间
                String time = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptTime(), "HHmmss"), "HH:mm:ss");
                payRegReq.setPayDate(date);
                payRegReq.setPayTime(time);
                payRegReq.setPayChannelId(String.valueOf(4));
                payRegReq.setPayResCode(cmbPayQuery.getHead().getCode());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(cmbPayQuery.getBody().getBillNo());
            }
            payRegReq.setBankNo("");
            payRegReq.setPayResDesc("");
            payRegReq.setPayTotalFee(fee);
            payRegReq.setPayCopeFee(fee);
            payRegReq.setPayFee(fee);
            payRegReq.setOperatorId(CenterFunctionUtils.convertDistrictId2OperatorId(reg.getDistrictId()));
        } catch (Exception e) {
            LOGGER.debug("挂号缴费参数转换成HIS需要的参数时异常", e);
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
                    // 招行退款请求对象
                } else if(infoObj instanceof RefundNoDupRes) {
                    RefundNoDupRes refund = (RefundNoDupRes) infoObj;
                    refundReq.setOrderId(reg.getOrderNum());
                    refundReq.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                    refundReq.setRefundId(refundId);
                    // 银行流水号
                    refundReq.setRefundSerialNum(refund.getBody().getBankSeqNo());
                    BigDecimal bigDecimal = new BigDecimal(refund.getBody().getAmount());
                    bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
                    refundReq.setTotalFee(bigDecimal.intValue());
                    refundReq.setRefundFee(bigDecimal.intValue());
                    // 日期
                    String date = DateUtil.toString(DateUtil.toDate(refund.getBody().getDate(), "yyyyMMdd"), "yyyy-MM-dd");
                    // 时间
                    String time = DateUtil.toString(DateUtil.toDate(refund.getBody().getTime(), "HHmmss"), "HH:mm:ss");
                    refundReq.setRefundDate(date);
                    refundReq.setRefundTime(time);
                    refundReq.setRefundResCode(refund.getHead().getErrMsg());
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
        PayRegReq payReg = getPayRegReq(registrationDocument);
        if (payReg == null) {
            if (registrationDocument.getIsAppointment().equals(String.valueOf(0))) {
                synchronized (registrationDocument.getId()) {
                    registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
                    registrationDocument.setStatusCode(RegistrationStatusEnum.EXCHANGE_CLOSED.getValue());
                    registrationDocument.setStatus(
                            CenterFunctionUtils.getRegistrationStatusName(registrationDocument.getStatusCode()));
                    registrationDocument.setNum("F" + DateUtil.toString(new Date(), "yyMMddHHmmssSSS"));
                    registrationDocument = registrationRepository.save(registrationDocument);
                    sendRegistrationMsg(registrationDocument.getId(), SendPushMsgEnum.REG_TODAY_NOT_PAY_HIS_MSG);
                }
            }
//        }else{
//            boolean canUpdate = false;
//            try {
//                 canUpdate = getPayPlatformRecordFlag(registrationDocument.getOrderNum());
//            } catch (Exception e) {
//                LOGGER.debug("查询支付平台异步回调的保存记录异常,订单号:" + registrationDocument.getOrderNum(), e);
//            }
//            if (canUpdate) {
//                this.saveUpdateRegistrationAndOrder(payReg);
//            }
            //registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
        }

        return registrationDocument;
    }

    /**
     * 检查是否已经保存异步消息
     * 
     * @param orderNum
     * @return
     * @throws Exception
     */
    private boolean getPayPlatformRecordFlag(String orderNum) throws Exception {
        boolean canUpdate = false;
        Order order = orderService.findByOrderNo(orderNum);
        String payWay = order.getPayWay();
        if (StringUtil.isNotEmpty(payWay)) {
            if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                Ali ali = aliService.findByOutTradeNo(orderNum);
                if (ali != null) {
                    canUpdate = true;
                }
            } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                Weixin weixin = weixinService.findByOutTradeNo(orderNum);
                if (weixin != null) {
                    canUpdate = true;
                }
            } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                CmbPayEntity queryPanInfo = cmbService.getQueryInfo(orderNum);
                if (queryPanInfo != null) {
                    canUpdate = true;
                }
            }
        }
        return canUpdate;
    }

    /**
     * 根据订单号获得支付信息,并转换为HIS的请求参数
     * @param registrationDocument
     * @return
     * @throws Exception
     */
    private PayRegReq getPayRegReq(RegistrationDocument registrationDocument) throws Exception {
        PayRegReq payReg = null;
        String payWay =  registrationDocument.getPayChannelId();
        if (StringUtil.isEmpty(payWay)) {
            payWay = "";
        }
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
        } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            QuerySingleOrderRes cmbQuery = cmbService.getCmbPayQueryRes(registrationDocument.getOrderNum());
            if (cmbQuery != null && StringUtil.isNull(cmbQuery.getHead().getCode())
                    && cmbQuery.getBody().getStatus().equals("0")) {
                payReg = this.convertAppInfo2PayReg(cmbQuery, registrationDocument.getId());
            }
        }
        return payReg;
    }

    /**
     *  查询挂号单退款信息
     *
     * @param registrationDocument
     * @return
     * @throws Exception
     */
    @Override
    public RegistrationDocument saveQueryRefundTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception {
        boolean queryFlag = getRefundQueryFlag(registrationDocument);
        if (queryFlag) {
            RegistrationRefundReqDocument req = registrationDocument.getRegistrationRefundReq();
            RefundReq refundReq = new RefundReq();
            BeanUtils.copyProperties(req, refundReq);
            this.saveUpdateRegistrationAndOrderRefund(refundReq);
            registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
        }
        return registrationDocument;
    }

    /**
     * 校验退款单号是否已退款
     * @param registrationDocument
     * @return
     * @throws Exception
     */
    private boolean getRefundQueryFlag(RegistrationDocument registrationDocument) throws Exception {
        boolean queryFlag = false;
        RegistrationTradeRefundDocument refund = registrationDocument.getRegistrationTradeRefund();
        if(refund!=null){
            if (StringUtil.isNotEmpty(refund.getOutRequestNo())) {
                AliRefundTradeQueryRes refundQuery = aliService.getAliRefundTradeQueryRes(refund.getOutTradeNo(),
                        refund.getOutRequestNo());
                if (refundQuery != null && refundQuery.getCode().equals("10000")) {
                    queryFlag = true;
                }
            } else if (StringUtil.isNotEmpty(refund.getOutRefundNo())){
                WeixinRefundRes weixinRefundQuery = weixinService.getWeixinRefundRes(refund.getOutRefundNo());
                if (weixinRefundQuery != null) {
                    queryFlag = true;
                }
                // 查询一网通退款信息
            } else if (StringUtil.isNotEmpty(refund.getCmbRefundNo())) {
                // 退款信息查询
                CmbQueryRefundEntity queryRefundInfo = new CmbQueryRefundEntity();
                CmbPayEntity cmbInfo = cmbService.getQueryInfo(refund.getOutTradeNo());
                // 订单号
                queryRefundInfo.setBillNo(cmbInfo.getBillNo());
                // 交易日期
                queryRefundInfo.setDate(cmbInfo.getDate());
                // 退款流水号
                queryRefundInfo.setRefundNo(refund.getCmbRefundNo());
                QueryRefundRes cmbRefundQuery = cmbService.queryRefundResult(queryRefundInfo);
                if(cmbRefundQuery != null && cmbRefundQuery.getBody().getBillRecord() != null
                        && StringUtil.isNotEmpty(cmbRefundQuery.getBody().getBillRecord().get(0).getAmount())) {
                    queryFlag = true;
                }
            }
        }
        return queryFlag;
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
    public List<RegistrationDocument> findOverTimeRegistrationDocumentList(int overTimeMinute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -overTimeMinute);
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
        synchronized (regBack.getNum()) {
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
                        webService4HisInterfaceCacheUtil.cacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                                regBack.getRegDate());
                        if (cancelType == OrderCancelTypeEnum.CANCEL_OVERTIME) {
                            if (regBack.getRegistrationOrderHis() != null
                                    && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospOrderId())) {
                                sendRegistrationMsg(SendPushMsgEnum.REG_OVERTIME_CANCEL, regBack);
                            }
                        }
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(-1),
                                String.valueOf(0));
                    } catch (Exception e) {
                        LOGGER.debug("医生号点信息缓存释放失败,或者是超时推送出现异常:" + regBack.getNum(), e);
                    }
                } catch (Exception e) {
                    if (newRefund != null) {
                        newRefund.setDescription(e.getMessage());
                    }
                    try {
                        regBack.setCancelRegErrMsg(e.getMessage());
                        registrationRepository.save(regBack);
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(0), String.valueOf(-1),
                                String.valueOf(0));
                    } catch (Exception e2) {
                        LOGGER.debug("RegistrationServiceNotxImpl.saveCancelRegistration[Exception]:", e2);
                        LOGGER.debug("保存错误消息时出现异常信息:" + regBack.getNum() + ",异常" + e2.getMessage());
                    }
                    LOGGER.debug("挂号单号:" + regBack.getNum(), e);
                    throw e;
                }
            }

            // if (cancelType != OrderCancelTypeEnum.CANCEL_OVERTIME
            // &&
            // (regBack.getStatusCode().equals(String.valueOf(RegistrationStatusEnum.PAID.getValue()))
            // || regBack
            // .getStatusCode().equals(String.valueOf(RegistrationStatusEnum.SUSPEND_MED.getValue()))))
            // {
            regBack = this.getRegistrationDocumentById(registrationId);
            RefundReq refundReq = null;
            try {
                if (StringUtil.isEmpty(regBack.getOrderNum())) {
                    throw new Exception("挂号单中订单号字段信息为空,退费失败,挂号单号:" + regBack.getNum());
                }
                this.saveRegRefund(registrationId);
                regBack = this.getRegistrationDocumentById(registrationId);
                if (regBack.getRegistrationRefundReq() != null
                        && StringUtil.isNotEmpty(regBack.getRegistrationRefundReq().getRefundId())) {
                    refundReq = new RefundReq();
                    BeanUtils.copyProperties(regBack.getRegistrationRefundReq(), refundReq);
                }
                LOGGER.debug("挂号完成退费到支付平台,订单号:" + regBack.getOrderNum());
                try {
                    saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(1),
                            String.valueOf(0));
                } catch (Exception e) {
                    LOGGER.debug("退费后,保存日志信息出现异常,订单号:" + regBack.getOrderNum(), e);
                }
            } catch (Exception e) {
                LOGGER.debug("挂号退费到支付平台失败,支付平台:" + regBack.getPayChannelId() + ",订单号:" + regBack.getOrderNum(), e);
                if (newRefund != null) {
                    newRefund.setDescription(e.getMessage());
                }
                try {
                    regBack.setRefundErrMsg(e.getMessage());
                    registrationRepository.save(regBack);
                    saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(0),
                            String.valueOf(0));
                } catch (Exception e2) {
                    LOGGER.debug("RegistrationServiceNotxImpl.saveCancelRegistration[Exception]:", e2);
                    LOGGER.debug("保存挂号退费日志时出现异常,订单号:" + regBack.getOrderNum() + ",异常信息:" + e2.getMessage());
                }
                LOGGER.debug(e.getMessage());
                throw e;
            }
            if (refundReq != null && StringUtil.isNotEmpty(refundReq.getRefundId())) {
                if (regBack.getRegistrationRefundHis() == null
                        || StringUtil.isEmpty(regBack.getRegistrationRefundHis().getRefundFlag())) {
                    if (regBack.getRegistrationOrderHis() != null
                            && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospPayId())
                            && "1".equals(regBack.getIsAppointment())) {
                        this.saveUpdateRegistrationAndOrderRefund(refundReq);
                    } else {
                        try {
                            Order order = orderService.findByOrderNo(refundReq.getOrderId());
                            if (order != null) {
                                regBack = this.getRegistrationDocumentById(order.getFormId());
                                regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                                regBack.setStatus(CenterFunctionUtils
                                        .getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                                regBack.setRefundApplyType(String.valueOf(1));
                                this.saveRegistrationDocument(regBack);
                                order.setOrderStatus(String.valueOf(3));
                                order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG);
                                order.setCancelDate(DateUtil.toTimestamp(new Date()));
                                // 更新订单状态
                                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                                orderService.save(order);
                            }
                        } catch (Exception e) {
                            LOGGER.debug("修改订单状态失败,订单号:" + regBack.getOrderNum(), e);
                        }
                        try {
                            this.sendRegistrationMsg(SendPushMsgEnum.REG_REFUND_SUCCESS, regBack);
                        } catch (Exception e) {
                            LOGGER.debug("退费成功后,发送推送抛出异常,订单号:" + regBack.getOrderNum(), e);
                        }
                    }
                    LOGGER.debug("完成退费通知HIS,订单号:" + regBack.getOrderNum());
                    try {
                        saveOrUpdateRegRefundLog(regBack, newRefund, String.valueOf(1), String.valueOf(1),
                                String.valueOf(1));
                    } catch (Exception e) {
                        LOGGER.debug("保存挂号退费日志时出现异常,订单号:" + regBack.getOrderNum(), e);
                    }
                }
            }
            // }
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
                trade.setOutRefundNo(reg.getOrderNum()+"001");
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
                trade.setOutRequestNo(reg.getOrderNum()+"001");
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
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            if (trade == null) {
                trade = new RegistrationTradeRefundDocument();
                trade.setOutTradeNo(reg.getOrderNum());
                trade.setCmbRefundNo(reg.getOrderNum().substring(0, 18) + "01");
                trade.setRefundAmount(reg.getAmount());
                reg.setRegistrationTradeRefund(trade);
                this.saveRegistrationDocument(reg);
            } else {
                trade = reg.getRegistrationTradeRefund();
            }
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(reg.getAmount()));
            bigDecimal = bigDecimal.divide(new BigDecimal("100"));
            // 生成一网通退款请求对象
            RefundNoDupBodyReq refundInfo = new RefundNoDupBodyReq();
            CmbPayEntity cmbInfo = cmbService.getQueryInfo(trade.getOutTradeNo());
            // 原订单号
            refundInfo.setBillNo(cmbInfo.getBillNo());
            // 交易日期
            refundInfo.setDate(cmbInfo.getDate());
            // 退款流水号
            refundInfo.setRefundNo(trade.getCmbRefundNo());
            // 退款金额
            refundInfo.setAmount(bigDecimal.toString());
            RefundNoDupRes cmbRefundRes = cmbService.saveRefundResult(refundInfo);
            if (cmbRefundRes != null) {
                if (StringUtil.isEmpty(cmbRefundRes.getHead().getCode())) {
                    refundFlag = true;
                } else {
                    LOGGER.debug(
                            "挂号退费失败,挂号单号:" + reg.getNum() + ",订单号:" + reg.getOrderNum() + ",失败原因:" + cmbRefundRes.getHead().getErrMsg());
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

    @Override
    public void setOrderProcess2Registration(RegistrationDocument registration) {
        List<RegistrationOrderProcessDocument> orders = registration.getOrders();
        getNotPaidProcess(registration, orders);
        getOrder2PayPlatform(registration, orders);
        if(orders.size()>1){
            getHospitalConfirm(registration, orders);
        }
        if ("1".equals(registration.getIsAppointment())) {
            getCancelReg2Hospital(registration, orders);
            String payStatus = String.valueOf(0);
            String confirmStatus = String.valueOf(0);
            if (orders.size() > 1) {
                payStatus = orders.get(1).getStatus();
            }
            if (orders.size() > 3) {
                confirmStatus = orders.get(2).getStatus();
            }
            if (orders.size() > 3 && payStatus.equals("1")) {
                getRefund2Patient(registration, orders);
            }
            if (orders.size() > 4 && confirmStatus.equals("1")) {
                getHospitalConfirmCancel(registration, orders);
            }
        } else if ("0".equals(registration.getIsAppointment())) {
            if (orders.size() > 2) {
                RegistrationOrderProcessDocument hospConfirmProcess = orders.get(2);
                if (hospConfirmProcess.getDetail().contains("挂号失败") || (hospConfirmProcess.getDetail().contains("未确认")
                        && !registration.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue()))) {
                    getRefund2Patient(registration, orders);
                }
            }
        }
    }

    /**
     * 订单流程(医院确认退号)
     * 
     * @param registration
     * @param orders
     */
    private void getHospitalConfirmCancel(RegistrationDocument registration,
            List<RegistrationOrderProcessDocument> orders) {
        RegistrationRefundReqDocument refundReq = registration.getRegistrationRefundReq();
        if (refundReq != null && StringUtil.isNotEmpty(refundReq.getRefundSerialNum())) {
            RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
            orderProcess.setName("医院确认退费");
            orderProcess.setStatus("1");
            StringBuilder detailStr = new StringBuilder();
            RegistrationRefundHisDocument refundHis = registration.getRegistrationRefundHis();
            if (refundHis == null || !"1".equals(refundHis.getRefundFlag())) {
                detailStr.append("医院确认失败");
            } else {
                detailStr.append("医院确认成功");
            }
            if(registration.getRegistrationRefundHis() != null) {
                String rt = registration.getRegistrationRefundHis().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setDetail(detailStr.toString());
            orderProcess.setImg("logo.png");
            orders.add(orderProcess);
        }
    }

    /**
     * 订单流程(退号)
     * 
     * @param registration
     * @param orders
     */
    private void getCancelReg2Hospital(RegistrationDocument registration,
            List<RegistrationOrderProcessDocument> orders) {
        boolean isShowCancel = false;
        boolean cancelRegFlag = false;
        if (StringUtil.isNotEmpty(registration.getCancelHisReturnMsg())) {
            if (StringUtil.isNotEmpty(registration.getCancelRegToHisTime())) {
                isShowCancel = true;
            }
            if (registration.getCancelHisReturnMsg().contains("交易成功")) {
                cancelRegFlag = true;
            }
        } else {
            RegistrationTradeRefundDocument refund = registration.getRegistrationTradeRefund();
            RegistrationRefundReqDocument refundReq = registration.getRegistrationRefundReq();
            if (refund != null) {
                cancelRegFlag = true;
            } else if (refundReq != null && StringUtil.isNotEmpty(refundReq.getOrderId())) {
                cancelRegFlag = true;
            }
        }
        RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setName("取消挂号");
        orderProcess.setStatus(String.valueOf(1));
        StringBuilder detailStr = new StringBuilder();
        if (isShowCancel) {
            if (cancelRegFlag) {
                detailStr.append("退号成功");
            } else {
                detailStr.append("退号失败");
            }
            if(registration.getRegistrationTradeRefund() != null) {
                String rt = registration.getRegistrationTradeRefund().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setDetail(detailStr.toString());
            orderProcess.setImg("user.png");
            orders.add(orderProcess);
        }
    }

    /**
     * 订单流程(退款)
     * 
     * @param registration
     * @param orders
     */
    private void getRefund2Patient(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setName("向支付平台申请退款");
        detailStr = new StringBuilder();
        RegistrationTradeRefundDocument refund = registration.getRegistrationTradeRefund();
        String img = "money.png";
        if(String.valueOf(PayChannel.ALIPAY.getCode()).equals(registration.getPayChannelId())){
            img = "alipay_icon.png";
        }else if(String.valueOf(PayChannel.WECHATPAY.getCode()).equals(registration.getPayChannelId())){
            img = "weixin.png";
        }else if(String.valueOf(PayChannel.WEB_UNION.getCode()).equals(registration.getPayChannelId())){
            img = "CMB_icon.jpg";
        }
        if (refund == null) {
            orderProcess.setStatus(String.valueOf(1));
            if (registration.getStatusCode().equals(RegistrationStatusEnum.REFUND.getValue())) {
                detailStr.append("申请退款成功");
            } else {
                detailStr.append("申请退款失败");
            }
            if(registration.getRegistrationTradeRefund() != null) {
                String rt = registration.getRegistrationRefundReq().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setImg(img);
            orderProcess.setDetail(detailStr.toString());
            orders.add(orderProcess);
        } else {
            boolean refundFlag = false;
            try {
                refundFlag = this.getRefundQueryFlag(registration);
            } catch (Exception e) {
                LOGGER.debug("流程定义查询退款信息出现异常", e);
            }
            if (refundFlag) {
                detailStr.append("申请退款成功");
            } else {
                detailStr.append("申请退款失败");
            }
            if(registration.getRegistrationTradeRefund() != null) {
                String rt = registration.getRegistrationRefundReq().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setStatus(String.valueOf(1));
            orderProcess.setImg(img);
            orderProcess.setDetail(detailStr.toString());
            orders.add(orderProcess);
        }
    }

    /**
     * 订单流程(医院确认)
     * 
     * @param registration
     * @param orders
     */
    private void getHospitalConfirm(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument payProcess = orders.get(1);
        if ("1".equals(payProcess.getStatus())) {
            RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
            orderProcess.setName("医院确认");
            orderProcess.setImg("logo.png");
            RegistrationOrderReqDocument orderReq = registration.getRegistrationOrderReq();
            if (orderReq != null && StringUtil.isNotEmpty(orderReq.getSerialNum())) {
                orderProcess.setStatus(String.valueOf(1));
                detailStr = new StringBuilder();
                RegistrationOrderHisDocument orderHis = registration.getRegistrationOrderHis();
                if (orderHis != null && StringUtil.isNotEmpty(orderHis.getHospPayId())) {
                    String ct = orderHis.getLastModifyTime();
                    String hct =  StringUtil.isEmpty(ct) ? "" : ct.substring(0, ct.length() - 4);
                    detailStr.append(hct);
                    if (StringUtil.isNotEmpty(orderHis.getHospRemark())) {
                        detailStr.append("<br/>");
                        detailStr.append(orderHis.getHospRemark());
                    }
                } else {
                    detailStr.append("挂号失败");
                    if (orderHis != null) {
                        if (StringUtil.isNotEmpty(orderHis.getClientReturnMsg())) {
                            detailStr.append("<br/>");
                            detailStr.append(orderHis.getClientReturnMsg());
                        }
                    }
                }
                orderProcess.setDetail(detailStr.toString());
                orders.add(orderProcess);
            }else{
                orderProcess.setStatus(String.valueOf(0));
                orderProcess.setDetail("未确认");
                orders.add(orderProcess);
            }
        }
    }

    /**
     * 订单流程(支付给支付平台)
     * 
     * @param registration
     * @param orders
     */
    private void getOrder2PayPlatform(RegistrationDocument registration,
            List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setName("订单未支付");
        RegistrationOrderReqDocument orderReq = registration.getRegistrationOrderReq();
        if (orderReq == null || StringUtil.isEmpty(orderReq.getSerialNum())) {
            PayRegReq payRegReq = null;
            try {
                payRegReq = this.getOrderProcessPayRegReq(registration);
            } catch (Exception e) {
                LOGGER.debug("流程定义查询支付信息出现异常", e);
            }
            if (payRegReq != null) {
                orderReq = new RegistrationOrderReqDocument();
                BeanUtils.copyProperties(payRegReq, orderReq);
            }
        }
        String img = "money.png";
        if (orderReq != null && StringUtil.isNotEmpty(orderReq.getSerialNum())) {
            orderProcess.setStatus(String.valueOf(1));
            detailStr = new StringBuilder();
            detailStr.append(orderReq.getSerialNum());
            detailStr.append("<br/>");
            detailStr.append(orderReq.getPayDate().concat(" ").concat(orderReq.getPayTime()));
            if (String.valueOf(PayChannel.WECHATPAY.getCode()).equals(orderReq.getPayChannelId())) {
                img = "weixin.png";
            } else if (String.valueOf(PayChannel.ALIPAY.getCode()).equals(orderReq.getPayChannelId())) {
                img = "alipay_icon.png";
            } else if (String.valueOf(PayChannel.WEB_UNION.getCode()).equals(orderReq.getPayChannelId())) {
                img = "CMB_icon.jpg";
            }

            orderProcess.setDetail(detailStr.toString());
            orderProcess.setName("订单已支付");
            orderProcess.setImg(img);
            orders.add(orderProcess);
        }else{
            orderProcess.setStatus(String.valueOf(0));
            orderProcess.setDetail("");
            orderProcess.setImg(img);
            orders.add(orderProcess);
        }
    }

    /**
     * 订单流程(待支付)
     * 
     * @param registration
     * @param orders
     */
    private void getNotPaidProcess(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess = null;
        orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setStatus(String.valueOf(1));
        orderProcess.setName("生成订单，待支付");
        detailStr = new StringBuilder();
        detailStr.append(registration.getNum());
        detailStr.append("<br/>");
        detailStr.append(DateUtil
                .toTimestamp(DateUtil.toDate(registration.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT)));
        orderProcess.setDetail(detailStr.toString());
        orderProcess.setImg("user.png");
        orders.add(orderProcess);
    }



    private PayRegReq getOrderProcessPayRegReq(RegistrationDocument registrationDocument) throws Exception {
        PayRegReq payReg = null;
        String payWay = registrationDocument.getPayChannelId();
        if (StringUtil.isEmpty(payWay)) {
            payWay = "";
        }
        if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            AliPayTradeQueryRes query = aliService.getAliPayTradeQueryRes(registrationDocument.getOrderNum());
            if (query != null && query.getCode().equals("10000")) {
                payReg = this.convertAppInfo2PayReg(query, registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            WeixinPayQueryRes wQuery = weixinService.getWeixinPayQueryRes(registrationDocument.getOrderNum());
            if (wQuery != null && wQuery.getResultCode().equals("SUCCESS")) {
                payReg = this.convertAppInfo2PayReg(wQuery, registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            QuerySingleOrderRes cmbQuery = cmbService.getCmbPayQueryRes(registrationDocument.getOrderNum());
            if (cmbQuery != null && StringUtil.isNull(cmbQuery.getHead().getCode())) {
                payReg = this.convertAppInfo2PayReg(cmbQuery, registrationDocument.getId());
            }
        }
        return payReg;
    }
}
