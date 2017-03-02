package com.proper.enterprise.isj.proxy.service.notx;

import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.logger.PayStepEnum;
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils;
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
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.entity.AliEntity;
import com.proper.enterprise.platform.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.platform.pay.ali.service.AliPayService;
import com.proper.enterprise.platform.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.platform.pay.cmb.model.CmbQueryRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQuerySingleOrderRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.pay.cmb.service.CmbPayService;
import com.proper.enterprise.platform.pay.wechat.entity.WechatEntity;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundQueryRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;
import com.proper.enterprise.platform.pay.wechat.service.WechatPayService;
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
    AliPayService aliPayService;

    @Autowired
    WechatPayService wechatPayService;

    @Autowired
    CmbPayService cmbPayService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    RegistrationRefundLogRepository registrationRefundLogRepository;

    @Autowired
    PayFactory payFactory;

    /**
     * 通过ID获取挂号信息.
     *
     * @param id 挂号单ID.
     * @return 获取的挂号单对象.
     */
    @Override
    public RegistrationDocument getRegistrationDocumentById(String id) {
        return registrationServiceImpl.getRegistrationDocumentById(id);
    }

    /**
     * 保存或更新挂号单信息.
     *
     * @param reg 挂号报文.
     * @return 挂号报文.
     */
    @Override
    public RegistrationDocument saveRegistrationDocument(RegistrationDocument reg) {
        return registrationServiceImpl.saveRegistrationDocument(reg);
    }

    /**
     * 通过用户ID以及支付状态获取挂号单信息.
     *
     * @param userId        患者ID.
     * @param status        支付状态.
     * @param isAppointment 挂号类别.
     * @return 挂号单信息.
     */
    @Override
    public List<RegistrationDocument> findRegistrationByCreateUserIdAndPayStatus(String userId, String status, String isAppointment) {
        return registrationRepository.findByCreateUserIdAndStatusCodeAndIsAppointment(userId, status, isAppointment);
    }

    /**
     * 根据挂号单号查询挂号单.
     *
     * @param num 挂号单号.
     * @return 挂号单.
     */
    @Override
    public RegistrationDocument getRegistrationDocumentByNum(String num) {
        return registrationServiceImpl.getRegistrationDocumentByNum(num);
    }

    /**
     * 删除挂号信息.
     *
     * @param reg 挂号信息.
     */
    @Override
    public void deleteRegistrationDocument(RegistrationDocument reg) {
        registrationServiceImpl.deleteRegistrationDocument(reg);
    }

    /**
     * 通过患者ID查询挂号单信息.
     *
     * @param patientId 患者ID.
     * @return 挂号单列表.
     */
    @Override
    public List<RegistrationDocument> findRegistrationDocumentList(String patientId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("patientId").is(patientId));
        query.with(new Sort(Sort.Direction.DESC, "apptDate").and(new Sort(Sort.Direction.DESC, "createTime")));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 查询已支付,进行了退号操作的记录
     */
    @Override
    public List<RegistrationDocument> findAlreadyCancelRegAndRefundErrRegList() {
        Query query = new Query();
        Pattern cancelHisReturnMsgPattern = Pattern.compile("^.*" + ReturnCode.SUCCESS + ".*$", Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("statusCode").is(RegistrationStatusEnum.PAID.getValue())
                .and("cancelHisReturnMsg").regex(cancelHisReturnMsgPattern));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 获取订单超时未付款的挂号单.
     *
     * @return 挂号单.
     */
    @Override
    public List<RegistrationDocument> findOverTimeRegistrationDocumentList(int overTimeMinute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -overTimeMinute);
        Query query = new Query();
        query.addCriteria(Criteria.where("createTime").lte(DateUtil.toString(cal.getTime(),
                PEPConstants.DEFAULT_TIMESTAMP_FORMAT)).and("statusCode").is(RegistrationStatusEnum.NOT_PAID.getValue()));
        query.with(new Sort(Sort.Direction.DESC, "apptDate"));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 通过参数查询挂号单信息.
     *
     * @param paramMap 参数集合.
     * @return 查询结果.
     */
    @Override
    public List<RegistrationDocument> findRegistrationDocumentByStopReg(Map<String, String> paramMap) {
        Query query = new Query();
        query.addCriteria(Criteria.where("doctorId").is(paramMap.get("doctorId"))
                .and("deptId").is(paramMap.get("deptId")).and("regDate").is(paramMap.get("regDate"))
                .and("beginTime").gte(paramMap.get("beginTime")).lte(paramMap.get("endTime")).and("statusCode")
                .in(RegistrationStatusEnum.NOT_PAID.getValue(), RegistrationStatusEnum.PAID.getValue()));
        return mongoTemplate.find(query, RegistrationDocument.class);
    }

    /**
     * 通过创建挂号单用户ID以及患者身份证号查询挂号信息.
     *
     * @param createUserId  创建挂号单用户ID.
     * @param patientIdCard 患者身份证号.
     * @return 查询结果.
     */
    @Override
    public List<RegistrationDocument> findRegistrationDocumentByCreateUserIdAndPatientIdCard(String createUserId, String patientIdCard) {
        return registrationRepository.findRegistrationDocumentByCreateUserIdAndPatientIdCard(createUserId, patientIdCard);
    }

    /**
     * 更新挂号单以及订单信息.
     *
     * @param payRegReq 向HIS发送异步处理结果对象.
     * @throws Exception
     */
    @Override
    public void saveUpdateRegistrationAndOrder(PayRegReq payRegReq) throws Exception {
        try {
            synchronized (payRegReq.getOrderId()) {
                Order order = orderService.findByOrderNo(payRegReq.getOrderId());
                if (order == null) {
                    LOGGER.debug("挂号单支付回调,失败原因:未找到订单,订单号:{}", payRegReq.getOrderId());
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
                LOGGER.debug("通知his前确认订单状态,订单号:{},支付状态:{}", payRegReq.getOrderId(), regBack.getStatusCode());
                // 请求his前保存请求对象
                BeanUtils.copyProperties(payRegReq, payOrderRegDocument);
                if (regBack.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue()) && canNoticeHisFlag) {
                    LOGGER.debug("挂号单是未支付状态,进行调用,订单号:{}", payRegReq.getOrderId());
                    // 预约挂号
                    if (regBack.getIsAppointment().equals("1")) {
                        if (regBack.getRegistrationRefundReq() == null
                                || StringUtil.isEmpty(regBack.getRegistrationRefundReq().getOrderId())) {
                            // 预约挂号与his业务处理
                            this.updateGTtodayRegAndOrder(payRegReq, order, regBack, payOrderRegDocument);
                        }
                        // 单日挂号
                    } else {
                        // 当日挂号与his业务处理
                        this.updateEqualtodayRegAndOrder(payRegReq, order, regBack, payOrderRegDocument);
                    }
                } else {
                    LOGGER.debug("挂号单是已支付状态,不进行调用,订单号:{}", payRegReq.getOrderId());
                    webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
                }
            }
        } catch (DelayException e) {
            PayLogUtils.log(PayStepEnum.UNKNOWN, payRegReq, e.getPosition());
        } catch (Exception e) {
            LOGGER.debug("更新挂号订单失败,订单号:{}{}", payRegReq.getOrderId(), e);
        }
    }

    /**
     * 创建挂号单.
     *
     * @param reg           挂号单信息.
     * @param isAppointment 是否预约挂号.
     * @return 挂号单.
     * @throws Exception 异常.
     */
    @Override
    public RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument reg, String isAppointment) throws Exception {
        RegistrationDocument saveReg = null;
        try {
            saveReg = registrationServiceImpl.saveCreateRegistration(reg);
            saveReg = registrationServiceImpl.saveCreateRegistrationAndOrder(saveReg, isAppointment);
            if (isAppointment.equals("1")) {
                webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(saveReg.getDoctorId(), saveReg.getRegDate());
                saveOrRemoveRegCache("1", saveReg.getId(), DateUtil.toDate(saveReg.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT));
            }
        } catch (Exception e) {
            LOGGER.debug("挂号出现异常", e);
            if (saveReg != null) {
                if (isAppointment.equals("1")) {
                    try {
                        if (saveReg.getRegistrationOrderHis() != null
                                && StringUtil.isNotEmpty(saveReg.getRegistrationOrderHis().getHospOrderId())) {
                            registrationServiceImpl.saveCancelRegistrationImpl(saveReg.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                        }
                    } catch (Exception e2) {
                        LOGGER.debug("RegistrationServiceNotxImpl.saveCreateRegistrationAndOrder[Exception]:", e2);
                        LOGGER.debug(e2.getCause().getMessage());
                    }
                }
                if (StringUtil.isNotEmpty(saveReg.getId())) {
                    registrationRepository.delete(saveReg.getId());
                }
            }
            throw e;
        }
        return saveReg;
    }

    /**
     * 更新当日挂号.
     *
     * @param payRegReq           支付请求.
     * @param order               订单.
     * @param regBack             挂号信息.
     * @param payOrderRegDocument 订单支付报文.
     * @throws Exception 异常.
     */
    private void updateEqualtodayRegAndOrder(PayRegReq payRegReq, Order order, RegistrationDocument regBack,
                                             RegistrationOrderReqDocument payOrderRegDocument) throws Exception {
        // 转换当日挂号对象为his当日挂号请求对象
        OrderRegReq orderReq = registrationServiceImpl.convertRegistration2OrderReg(regBack);
        BeanUtils.copyProperties(orderReq, payOrderRegDocument);
        regBack.setRegistrationOrderReq(payOrderRegDocument);
        // 保存当日挂号请求信息
        regBack = this.saveRegistrationDocument(regBack);
        try {
            PayOrderRegReq payOrderRegReq = new PayOrderRegReq();
            BeanUtils.copyProperties(payOrderRegDocument, payOrderRegReq);
            registrationServiceImpl.updateRegistrationAndOrder(payOrderRegReq);
            sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_SUCCESS);
            saveOrRemoveRegCache("0", order.getFormId(), null);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
        } catch (DelayException e) {
            throw e;
        } catch (Exception e) {
            RegistrationOrderHisDocument his = regBack.getRegistrationOrderHis();
            his.setClientReturnMsg(e.getMessage());
            regBack.setRegistrationOrderHis(his);
            regBack = registrationRepository.save(regBack);
            payBackTodayReg(payRegReq, order, regBack);
            LOGGER.info("当日挂号更新出现异常", e);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
            throw e;
        }
    }

    /**
     * 更新预约挂号.
     *
     * @param payRegReq           支付请求.
     * @param order               订单.
     * @param regBack             挂号记录.
     * @param payOrderRegDocument 支付报文.
     * @throws Exception 异常.
     */
    private void updateGTtodayRegAndOrder(PayRegReq payRegReq, Order order, RegistrationDocument regBack,
                                          RegistrationOrderReqDocument payOrderRegDocument) throws Exception {
        // 支付结果向his请求对象
        regBack.setRegistrationOrderReq(payOrderRegDocument);
        // 更新挂号信息
        regBack = this.saveRegistrationDocument(regBack);
        try {
            registrationServiceImpl.updateRegistrationAndOrder(payRegReq);
            regBack = this.getRegistrationDocumentById(regBack.getId());
            sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_SUCCESS);
            saveOrRemoveRegCache("0", order.getFormId(), null);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
        } catch (DelayException e) {
            throw e;
        } catch (Exception e) {
            RegistrationOrderHisDocument his = regBack.getRegistrationOrderHis();
            his.setClientReturnMsg(e.getMessage());
            regBack.setRegistrationOrderHis(his);
            registrationRepository.save(regBack);
            LOGGER.info("预约挂号出现异常", e);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
            try {
                if (regBack.getRegistrationOrderHis() != null && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospPayId())) {
                    this.saveCancelRegistration(regBack.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                } else {
                    registrationServiceImpl.saveCancelRegistrationImpl(regBack.getId(), OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                    try {
                        this.saveRegRefund(regBack.getId());
                    } catch (Exception e3) {
                        LOGGER.debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e3);
                        LOGGER.debug("预约挂号失败后,退费发生异常,订单号:" + order.getOrderNo());
                        throw e;
                    }
                    try {
                        regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                        regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                        this.saveRegistrationDocument(regBack);
                        order.setPayWay(regBack.getPayChannelId());
                        // 订单状态 3:退费成功
                        order.setOrderStatus("3");
                        order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_PLATFORM_MSG);
                        order.setCancelDate(DateUtil.toTimestamp(new Date()));
                        // 更新订单状态
                        order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                        orderService.save(order);
                        sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_REFUND_SUCCESS);
                    } catch (Exception e3) {
                        LOGGER.debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e3);
                        LOGGER.debug("预约挂号发生异常情况,订单号:{}", order.getOrderNo());
                    }
                }
            } catch (Exception e2) {
                LOGGER.debug("RegistrationServiceNotxImpl.updateGTtodayRegAndOrder[Exception]:", e2);
            }
            throw e;
        }
    }

    /**
     * 当日挂号,异常情况进行退费.
     *
     * @param payRegReq 支付请求.
     * @param order     订单.
     * @param regBack   挂号信息.
     * @throws RegisterException 异常.
     */
    private void payBackTodayReg(PayRegReq payRegReq, Order order, RegistrationDocument regBack) throws RegisterException {
        try {
            RefundReq req = this.saveRegRefund(regBack.getId());
            regBack = this.getRegistrationDocumentById(regBack.getId());
            if (req == null) {
                // 更新订单状态 5:退费失败
                order.setOrderStatus("5");
                // 更新订单状态
                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                order.setPayWay(String.valueOf(payRegReq.getPayChannelId()));
                orderService.save(order);

                regBack.setStatusCode(RegistrationStatusEnum.REFUND_FAIL.getValue());
                regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(regBack.getStatusCode()));
                regBack.setNum("F".concat(DateUtil.toString(new Date(), "yyMMddHHmmssSSS")));
                registrationRepository.save(regBack);
                sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_HIS_RETURNERR_MSG);
                throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
            } else {
                // 更新订单状态 3:退费成功
                order.setOrderStatus("3");
                // 更新订单状态
                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                order.setPayWay(String.valueOf(payRegReq.getPayChannelId()));
                orderService.save(order);

                regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(regBack.getStatusCode()));
                regBack.setNum("F".concat(DateUtil.toString(new Date(), "yyMMddHHmmssSSS")));
                registrationRepository.save(regBack);
                sendRegistrationMsg(order.getFormId(), SendPushMsgEnum.REG_PAY_HIS_RETURNMSG);
            }
        } catch (Exception refundException) {
            LOGGER.debug("RegistrationServiceNotxImpl.payBackTodayReg[Exception]:", refundException);
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
    }

    /**
     * 三方支付平台退款操作.
     *
     * @param registrationId 挂号信息.
     * @return 退款请求.
     * @throws Exception 异常.
     */
    @Override
    public RefundReq saveRegRefund(String registrationId) throws Exception {
        RegistrationDocument reg = this.getRegistrationDocumentById(registrationId);
        if (reg == null) {
            LOGGER.debug("未查到挂号单信息,挂号单Id:{}", registrationId);
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
        Order order = orderService.getByFormId(registrationId);
        RefundReq refundReq = null;
        if (order == null) {
            LOGGER.debug("未查到订单表信息,挂号单Id:{}", registrationId);
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
        if (StringUtil.isEmpty(reg.getPayChannelId())) {
            LOGGER.debug("未查到挂号单的支付方式,订单号:{}", order.getOrderNo());
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
        RegistrationTradeRefundDocument trade;
        // 获取退款信息
        if (reg.getRegistrationTradeRefund() == null) {
            trade = new RegistrationTradeRefundDocument();
            // 订单号
            trade.setOutTradeNo(reg.getOrderNum());
            if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                // 微信退款单号
                trade.setOutRefundNo(reg.getOrderNum().concat("001"));
            } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                // 支付宝退款单号
                trade.setOutRequestNo(reg.getOrderNum().concat("001"));
            } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                // 一网通退款单号
                trade.setCmbRefundNo(reg.getOrderNum().substring(0, 18).concat("01"));
            }
            reg.setRegistrationTradeRefund(trade);
            this.saveRegistrationDocument(reg);
        } else {
            trade = reg.getRegistrationTradeRefund();
        }
        // 订单号
        String orderNo = trade.getOutTradeNo();
        // 请求退款对象
        com.proper.enterprise.platform.api.pay.model.RefundReq refundInfo =
                new com.proper.enterprise.platform.api.pay.model.RefundReq();
        // 商户订单号
        refundInfo.setOutTradeNo(orderNo);
        // 退款金额
        refundInfo.setRefundAmount(reg.getAmount());
        // 订单金额
        refundInfo.setTotalFee(reg.getAmount());
        // 微信退款
        if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            // 退款流水号
            refundInfo.setOutRequestNo(trade.getOutRefundNo());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            WechatRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes != null) {
                if (refundRes.getResultCode().equals("SUCCESS")) {
                    refundReq = this.convertAppRefundInfo2RefundReq(refundRes, order.getOrderNo(), trade.getOutRefundNo());
                    RegistrationRefundReqDocument refundHisReq = new RegistrationRefundReqDocument();
                    BeanUtils.copyProperties(refundReq, refundHisReq);
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType("1");
                    this.saveRegistrationDocument(reg);
                } else {
                    LOGGER.debug("退号(微信退费失败),微信返回的错误消息:{},订单号:{}", refundRes.getReturnMsg(), orderNo);
                }
            } else {
                LOGGER.debug("退号(微信退费失败),微信返回对象为空,订单号:{}", orderNo);
            }
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            // 退款流水号
            refundInfo.setOutRequestNo(trade.getOutRequestNo());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            AliRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes != null) {
                if (refundRes.getCode().equals("10000") && refundRes.getMsg().equals("Success")) {
                    RegistrationRefundReqDocument refundHisReq = new RegistrationRefundReqDocument();
                    try {
                        refundReq = this.convertAppRefundInfo2RefundReq(refundRes, order.getOrderNo(), trade.getOutRequestNo());
                        BeanUtils.copyProperties(refundReq, refundHisReq);
                    } catch (Exception e) {
                        LOGGER.debug("挂号退费转换成HIS需要的参数时异常", e);
                    }
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType("1");
                    this.saveRegistrationDocument(reg);
                } else {
                    LOGGER.debug("退号(支付宝退费失败),支付宝返回Code:{},支付宝返回消息:{},订单号:{}", refundRes.getCode(), refundRes.getMsg(), orderNo);
                }
            } else {
                LOGGER.debug("退号(支付宝退费失败),支付宝返回对象为空,订单号:{}", orderNo);
            }
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            // 退款流水号
            refundInfo.setOutRequestNo(trade.getCmbRefundNo());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            CmbRefundNoDupRes cmbRefundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (cmbRefundRes != null) {
                if (StringUtil.isNull(cmbRefundRes.getHead().getCode())) {
                    RegistrationRefundReqDocument refundHisReq = new RegistrationRefundReqDocument();
                    try {
                        refundReq = this.convertAppRefundInfo2RefundReq(cmbRefundRes, order.getOrderNo(), trade.getCmbRefundNo());
                        BeanUtils.copyProperties(refundReq, refundHisReq);
                    } catch (Exception e) {
                        LOGGER.debug("挂号退费转换成HIS需要的参数时异常", e);
                    }
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType(String.valueOf(PayChannel.WEB_UNION.getCode()));
                    this.saveRegistrationDocument(reg);
                } else {
                    LOGGER.debug("退号(一网通退费失败),订单号:{}", orderNo);
                }
            } else {
                LOGGER.debug("退号(一网通退费失败),支付宝返回对象为空,订单号:{}", orderNo);
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
     * @param registrationId 挂号ID.
     * @throws Exception 异常.
     */
    @Override
    public void sendRegistrationMsg(String registrationId, SendPushMsgEnum pushType) throws Exception {
        RegistrationDocument updateReg = this.getRegistrationDocumentById(registrationId);
        /*---------挂号具体信息-------*/
        sendRegistrationMsg(pushType, updateReg);
    }

    /**
     * 发送挂号推送消息.
     *
     * @param pushType  推送消息类别.
     * @param updateReg 推送消息对象.
     * @throws Exception 异常.
     */
    private void sendRegistrationMsg(SendPushMsgEnum pushType, RegistrationDocument updateReg) throws Exception {
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(pushType, updateReg));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(updateReg.getCreateUserId());
        regMsg.setUserName(updateReg.getOperatorPhone());
        messagesService.saveMessage(regMsg);
    }

    /**
     * 更新挂号退款信息.
     *
     * @param refundReq 退款对象.
     * @throws Exception 异常.
     */
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

    /**
     * 查询未付款订单,更新状态.
     *
     * @param registrationDocument 挂号报文.
     */
    @Override
    public RegistrationDocument saveQueryPayTradeStatusAndUpdateReg(RegistrationDocument registrationDocument) throws Exception {
        PayRegReq payReg = getPayRegReq(registrationDocument);
        if (payReg == null) {
            if (registrationDocument.getIsAppointment().equals("0")) {
                synchronized (registrationDocument.getId()) {
                    registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
                    registrationDocument.setStatusCode(RegistrationStatusEnum.EXCHANGE_CLOSED.getValue());
                    registrationDocument.setStatus(CenterFunctionUtils.getRegistrationStatusName(registrationDocument.getStatusCode()));
                    registrationDocument.setNum("F" + DateUtil.toString(new Date(), "yyMMddHHmmssSSS"));
                    registrationDocument = registrationRepository.save(registrationDocument);
                    sendRegistrationMsg(registrationDocument.getId(), SendPushMsgEnum.REG_TODAY_NOT_PAY_HIS_MSG);
                }
            }
        }
        return registrationDocument;
    }

    /**
     * 根据订单号获得支付信息,并转换为HIS的请求参数
     *
     * @param registrationDocument 挂号信息.
     * @return 支付信息.
     * @throws Exception 异常.
     */
    private PayRegReq getPayRegReq(RegistrationDocument registrationDocument) throws Exception {
        PayRegReq payReg = null;
        String payWay = registrationDocument.getPayChannelId();
        if (StringUtil.isEmpty(payWay)) {
            payWay = "";
        }
        if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            AliPayTradeQueryRes query = payService.queryPay(registrationDocument.getOrderNum());
            if (query != null && query.getCode().equals("10000") && query.getTradeStatus().equals("TRADE_SUCCESS")) {
                payReg = this.convertAppInfo2PayReg(query, registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            WechatPayQueryRes wQuery = payService.queryPay(registrationDocument.getOrderNum());
            if (wQuery != null && wQuery.getResultCode().equals("SUCCESS") && wQuery.getTradeState().equals("SUCCESS")) {
                payReg = this.convertAppInfo2PayReg(wQuery, registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            CmbQuerySingleOrderRes cmbQuery = payService.queryPay(registrationDocument.getOrderNum());
            if (cmbQuery != null && StringUtil.isNull(cmbQuery.getHead().getCode()) && cmbQuery.getBody().getStatus().equals("0")) {
                payReg = this.convertAppInfo2PayReg(cmbQuery, registrationDocument.getId());
            }
        }
        return payReg;
    }

    /**
     * 查询挂号单退款信息.
     *
     * @param registrationDocument 挂号.
     * @return 挂号单退款信息.
     * @throws Exception 异常.
     */
    @Override
    public RegistrationDocument saveQueryRefundTradeStatusAndUpdateReg(RegistrationDocument registrationDocument) throws Exception {
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
     * 校验退款单号是否已退款.
     *
     * @param registrationDocument 挂号单.
     * @return 退款单号是否已退款.
     * @throws Exception 异常.
     */
    private boolean getRefundQueryFlag(RegistrationDocument registrationDocument) throws Exception {
        boolean queryFlag = false;
        RegistrationTradeRefundDocument refund = registrationDocument.getRegistrationTradeRefund();
        if (refund != null) {
            // 支付宝退款号
            if (StringUtil.isNotEmpty(refund.getOutRequestNo())) {
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                AliRefundTradeQueryRes refundQuery = payService.queryRefund(refund.getOutTradeNo(), refund.getOutRequestNo());
                if (refundQuery != null && refundQuery.getCode().equals("10000")) {
                    queryFlag = true;
                }
                // 微信退款好
            } else if (StringUtil.isNotEmpty(refund.getOutRefundNo())) {
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                WechatRefundQueryRes refundQuery = payService.queryRefund(refund.getOutTradeNo(), refund.getOutRefundNo());
                if (refundQuery != null && refundQuery.getResultCode().equals("SUCCESS")) {
                    queryFlag = true;
                }
                // 一网通退款号
            } else if (StringUtil.isNotEmpty(refund.getCmbRefundNo())) {
                // 退款信息查询
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
                CmbQueryRefundRes refundQuery = payService.queryRefund(refund.getOutTradeNo(), refund.getCmbRefundNo());
                if (refundQuery != null && refundQuery.getBody().getBillRecord() != null
                        && StringUtil.isNotEmpty(refundQuery.getBody().getBillRecord().get(0).getAmount())) {
                    queryFlag = true;
                }
            }
        }
        return queryFlag;
    }

    /**
     * 清除或保存30分钟倒计时的挂号单.
     *
     * @param type          1:添加,0:清除.
     * @param regId         挂号ID.
     * @param regCreateTime 挂号时间.
     */
    private synchronized void saveOrRemoveRegCache(String type, String regId, Date regCreateTime) {
        Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP);
        Cache.ValueWrapper valueWrapper = tempCache.get(CenterFunctionUtils.CACHE_KEY_REGITRATION_SCHEDULER_TASK);
        Map<String, Date> regMap = new HashMap<>();
        if (valueWrapper != null && valueWrapper.get() != null) {
            //noinspection unchecked
            regMap = (Map<String, Date>) valueWrapper.get();
        }
        if (type.equals("1")) {
            regMap.put(regId, regCreateTime);
        } else {
            regMap.remove(regId);
        }
        tempCache.put(CenterFunctionUtils.CACHE_KEY_REGITRATION_SCHEDULER_TASK, regMap);
    }

    /**
     * 退号.
     *
     * @param registrationId 挂号单.
     * @param cancelType     取消方式 1:手动,2:超时自动.
     * @throws Exception 异常.
     */
    @Override
    public void saveCancelRegistration(String registrationId, OrderCancelTypeEnum cancelType) throws Exception {
        // 校验挂号单号
        if (StringUtil.isEmpty(registrationId)) {
            return;
        }
        // 挂号单
        RegistrationDocument regBack = this.getRegistrationDocumentById(registrationId);
        if (regBack == null) {
            return;
        }
        // 超时退号
        if (cancelType == null) {
            cancelType = OrderCancelTypeEnum.CANCEL_OVERTIME;
        }
        // 查询退款LOG信息
        List<RegistrationRefundLogDocument> refundLogDocumentList = registrationRefundLogRepository.findByNum(regBack.getNum());
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
                    regBack = registrationRepository.save(regBack);
                    // 进行退号操作
                    registrationServiceImpl.saveCancelRegistrationImpl(registrationId, cancelType);
                    saveOrRemoveRegCache("0", registrationId, null);
                    regBack = this.getRegistrationDocumentById(registrationId);
                    try {
                        webService4HisInterfaceCacheUtil.cacheDoctorTimeRegInfoRes(regBack.getDoctorId(), regBack.getRegDate());
                        if (cancelType == OrderCancelTypeEnum.CANCEL_OVERTIME) {
                            if (regBack.getRegistrationOrderHis() != null
                                    && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospOrderId())) {
                                sendRegistrationMsg(SendPushMsgEnum.REG_OVERTIME_CANCEL, regBack);
                            }
                        }
                        saveOrUpdateRegRefundLog(regBack, newRefund, "1", "-1", "0");
                    } catch (Exception e) {
                        LOGGER.debug("医生号点信息缓存释放失败,或者是超时推送出现异常:{}{}", regBack.getNum(), e);
                    }
                } catch (Exception e) {
                    if (newRefund != null) {
                        newRefund.setDescription(e.getMessage());
                    }
                    try {
                        regBack.setCancelRegErrMsg(e.getMessage());
                        registrationRepository.save(regBack);
                        saveOrUpdateRegRefundLog(regBack, newRefund, "0", "-1", "0");
                    } catch (Exception e2) {
                        LOGGER.debug("RegistrationServiceNotxImpl.saveCancelRegistration[Exception]:", e2);
                        LOGGER.debug("保存错误消息时出现异常信息:{},异常:{}", regBack.getNum(), e2.getMessage());
                    }
                    LOGGER.debug("挂号单号:{}{}", regBack.getNum(), e);
                    throw e;
                }
            }
            regBack = this.getRegistrationDocumentById(registrationId);
            RefundReq refundReq = null;
            try {
                if (StringUtil.isEmpty(regBack.getOrderNum())) {
                    throw new Exception("挂号单中订单号字段信息为空,退费失败,挂号单号:".concat(regBack.getNum()));
                }
                if (regBack.getRegistrationOrderReq() != null
                        && StringUtil.isNotNull(regBack.getRegistrationOrderReq().getSerialNum())) {
                    this.saveRegRefund(registrationId);
                }
                regBack = this.getRegistrationDocumentById(registrationId);
                if (regBack.getRegistrationRefundReq() != null
                        && StringUtil.isNotEmpty(regBack.getRegistrationRefundReq().getRefundId())) {
                    refundReq = new RefundReq();
                    BeanUtils.copyProperties(regBack.getRegistrationRefundReq(), refundReq);
                }
                LOGGER.debug("挂号完成退费到支付平台,订单号:{}", regBack.getOrderNum());
                try {
                    saveOrUpdateRegRefundLog(regBack, newRefund, "1", "1", "0");
                } catch (Exception e) {
                    LOGGER.debug("退费后,保存日志信息出现异常,订单号:{}", regBack.getOrderNum(), e);
                }
            } catch (Exception e) {
                LOGGER.debug("挂号退费到支付平台失败,支付平台:{},订单号:{}", regBack.getPayChannelId(), regBack.getOrderNum(), e);
                if (newRefund != null) {
                    newRefund.setDescription(e.getMessage());
                }
                try {
                    regBack.setRefundErrMsg(e.getMessage());
                    registrationRepository.save(regBack);
                    saveOrUpdateRegRefundLog(regBack, newRefund, "1", "0", "0");
                } catch (Exception e2) {
                    LOGGER.debug("RegistrationServiceNotxImpl.saveCancelRegistration[Exception]:", e2);
                    LOGGER.debug("保存挂号退费日志时出现异常,订单号:{},异常信息:{}", regBack.getOrderNum(), e2.getMessage());
                }
                LOGGER.debug(e.getMessage());
                throw e;
            }
            if (refundReq != null && StringUtil.isNotEmpty(refundReq.getRefundId())) {
                if (regBack.getRegistrationRefundHis() == null || StringUtil.isEmpty(regBack.getRegistrationRefundHis().getRefundFlag())) {
                    if (regBack.getRegistrationOrderHis() != null && StringUtil.isNotEmpty(regBack.getRegistrationOrderHis().getHospPayId()) && "1".equals(regBack.getIsAppointment())) {
                        this.saveUpdateRegistrationAndOrderRefund(refundReq);
                    } else {
                        try {
                            Order order = orderService.findByOrderNo(refundReq.getOrderId());
                            if (order != null) {
                                regBack = this.getRegistrationDocumentById(order.getFormId());
                                regBack.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                                regBack.setStatus(CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                                regBack.setRefundApplyType("1");
                                this.saveRegistrationDocument(regBack);
                                order.setOrderStatus("3");
                                order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG);
                                order.setCancelDate(DateUtil.toTimestamp(new Date()));
                                // 更新订单状态
                                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                                orderService.save(order);
                            }
                        } catch (Exception e) {
                            LOGGER.debug("修改订单状态失败,订单号:{}", regBack.getOrderNum(), e);
                        }
                        try {
                            this.sendRegistrationMsg(SendPushMsgEnum.REG_REFUND_SUCCESS, regBack);
                        } catch (Exception e) {
                            LOGGER.debug("退费成功后,发送推送抛出异常,订单号:{}", regBack.getOrderNum(), e);
                        }
                    }
                    LOGGER.debug("完成退费通知HIS,订单号:{}", regBack.getOrderNum());
                    try {
                        saveOrUpdateRegRefundLog(regBack, newRefund, "1", "1", "1");
                    } catch (Exception e) {
                        LOGGER.debug("保存挂号退费日志时出现异常,订单号:{}", regBack.getOrderNum(), e);
                    }
                }
            }
        }
    }

    /**
     * 更新挂号退款日志
     *
     * @param regBack           挂号记录.
     * @param refundLogDocument 退款日志信息.
     * @param cancelRegStatus   取消挂号单状态(-1:未退号,1:成功,0:失败).
     * @param refundStatus      退款状态(-1:未退费,1:成功,0:失败).
     * @param refundHisStatus   his退款状态(1:成功,0:失败或者未通知).
     */
    @Override
    public void saveOrUpdateRegRefundLog(RegistrationDocument regBack, RegistrationRefundLogDocument refundLogDocument,
                                         String cancelRegStatus, String refundStatus, String refundHisStatus) {
        if (refundLogDocument != null) {
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

    /**
     * 线下申请退费,根据支付平台返还支付费用,修改相应的数据为已退费.
     *
     * @param reg 挂号单.
     */
    @Override
    public void saveRefundAndUpdateRegistrationDocument(RegistrationDocument reg) throws Exception {
        // 获取退款信息
        RegistrationTradeRefundDocument trade = reg.getRegistrationTradeRefund();
        // 获取信息为空时创建并保存对象
        if (trade == null) {
            trade = new RegistrationTradeRefundDocument();
            // 订单号
            trade.setOutTradeNo(reg.getOrderNum());
            if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                // 微信退款单号
                trade.setOutRefundNo(reg.getOrderNum().concat("001"));
            } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                // 支付宝退款单号
                trade.setOutRequestNo(reg.getOrderNum().concat("001"));
            } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                // 一网通退款单号
                trade.setCmbRefundNo(reg.getOrderNum().substring(0, 18).concat("01"));
            }
            reg.setRegistrationTradeRefund(trade);
            reg = this.saveRegistrationDocument(reg);
        }
        // 请求退款对象
        com.proper.enterprise.platform.api.pay.model.RefundReq refundInfo =
                new com.proper.enterprise.platform.api.pay.model.RefundReq();
        // 商户订单号
        refundInfo.setOutTradeNo(trade.getOutTradeNo());
        // 退款金额
        refundInfo.setRefundAmount(reg.getAmount());
        // 退款总金额
        refundInfo.setTotalFee(reg.getAmount());
        // 退款标识位
        boolean refundFlag = false;
        // 微信退款
        if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            // 退款流水号
            refundInfo.setOutRequestNo(trade.getOutRefundNo());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            WechatRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes.getResultCode().equals("SUCCESS")) {
                refundFlag = true;
            } else {
                LOGGER.debug("挂号退费失败,挂号单号:{},订单号:{},失败原因:{}", reg.getNum(), reg.getOrderNum(), refundRes.getReturnMsg());
            }
            // 支付宝退款
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            // 退款流水号
            refundInfo.setOutRequestNo(trade.getOutRequestNo());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            AliRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes != null) {
                if (refundRes.getCode().equals("10000")) {
                    refundFlag = true;
                } else {
                    LOGGER.debug("挂号退费失败,挂号单号:{},订单号:{},失败原因:{}", reg.getNum(), reg.getOrderNum(), refundRes.getMsg());
                }
            } else {
                LOGGER.debug("挂号退费失败,挂号单号:{},订单号:{},失败原因:返回为空", reg.getNum(), reg.getOrderNum());
            }
            // 一网通退款
        } else if (reg.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            // 退款流水号
            refundInfo.setOutRequestNo(trade.getCmbRefundNo());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            CmbRefundNoDupRes cmbRefundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (cmbRefundRes != null) {
                if (StringUtil.isEmpty(cmbRefundRes.getHead().getCode())) {
                    refundFlag = true;
                } else {
                    LOGGER.debug("挂号退费失败,挂号单号:{},订单号:{},失败原因:{}", reg.getNum(), reg.getOrderNum(), cmbRefundRes.getHead().getErrMsg());
                }
            } else {
                LOGGER.debug("挂号退费失败,挂号单号:{},订单号:,失败原因:返回为空", reg.getNum(), reg.getOrderNum());
            }
        }
        // 通知HIS退款
        if (refundFlag && !reg.getStatusCode().equals(RegistrationStatusEnum.REFUND.getValue())) {
            // 已退费
            reg.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
            // 退费方式 2:线下申请
            reg.setRefundApplyType("2");
            reg.setStatus(CenterFunctionUtils.getRegistrationStatusName(reg.getStatusCode()));
            this.saveRegistrationDocument(reg);
            updateOrderEntity(reg);
            sendRegistrationMsg(reg.getId(), SendPushMsgEnum.REG_REFUND_SUCCESS);
        }
    }

    /**
     * 更新订单信息.
     *
     * @param reg 挂号单信息.
     */
    private void updateOrderEntity(RegistrationDocument reg) {
        Order order = orderService.findByOrderNo(reg.getOrderNum());
        if (order != null) {
            order.setOrderStatus("3");
            order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_PLATFORM_MSG);
            order.setCancelDate(DateUtil.toTimestamp(new Date()));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            orderService.save(order);
        }
    }

    //-----------------------------------订单流程相关---------------START--------------------------------------

    /**
     * 订单流程(待支付).
     *
     * @param registration 挂号信息.
     * @param orders       订单列表.
     */
    private void getNotPaidProcess(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess;
        orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setStatus("1");
        orderProcess.setName("生成订单，待支付");
        detailStr = new StringBuilder();
        detailStr.append(registration.getNum());
        detailStr.append("<br/>");
        detailStr.append(DateUtil.toTimestamp(DateUtil.toDate(registration.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT)));
        orderProcess.setDetail(detailStr.toString());
        orderProcess.setImg("user.png");
        orders.add(orderProcess);
    }

    /**
     * 订单流程(支付给支付平台).
     *
     * @param registration 挂号信息.
     * @param orders       订单列表.
     */
    private void getOrder2PayPlatform(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
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
            orderProcess.setStatus("1");
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
        } else {
            orderProcess.setStatus("0");
            orderProcess.setDetail("");
            orderProcess.setImg(img);
            orders.add(orderProcess);
        }
    }

    /**
     * 订单流程(医院确认)
     *
     * @param registration 挂号信息.
     * @param orders       订单列表.
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
                orderProcess.setStatus("1");
                detailStr = new StringBuilder();
                RegistrationOrderHisDocument orderHis = registration.getRegistrationOrderHis();
                if (orderHis != null && StringUtil.isNotEmpty(orderHis.getHospPayId())) {
                    String ct = orderHis.getLastModifyTime();
                    String hct = StringUtil.isEmpty(ct) ? "" : ct.substring(0, ct.length() - 4);
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
            } else {
                orderProcess.setStatus("0");
                orderProcess.setDetail("未确认");
                orders.add(orderProcess);
            }
        }
    }

    /**
     * 订单流程(退号).
     *
     * @param registration 挂号信息.
     * @param orders       订单列表.
     */
    private void getCancelReg2Hospital(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
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
        orderProcess.setStatus("1");
        StringBuilder detailStr = new StringBuilder();
        if (isShowCancel) {
            if (cancelRegFlag) {
                detailStr.append("退号成功");
            } else {
                detailStr.append("退号失败");
            }
            if (registration.getRegistrationTradeRefund() != null) {
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
     * @param registration 挂号信息.
     * @param orders       订单列表.
     */
    private void getRefund2Patient(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setName("向支付平台申请退款");
        detailStr = new StringBuilder();
        RegistrationTradeRefundDocument refund = registration.getRegistrationTradeRefund();
        String img = "money.png";
        if (String.valueOf(PayChannel.ALIPAY.getCode()).equals(registration.getPayChannelId())) {
            img = "alipay_icon.png";
        } else if (String.valueOf(PayChannel.WECHATPAY.getCode()).equals(registration.getPayChannelId())) {
            img = "weixin.png";
        } else if (String.valueOf(PayChannel.WEB_UNION.getCode()).equals(registration.getPayChannelId())) {
            img = "CMB_icon.jpg";
        }
        if (refund == null) {
            orderProcess.setStatus("1");
            if (registration.getStatusCode().equals(RegistrationStatusEnum.REFUND.getValue())) {
                detailStr.append("申请退款成功");
            } else {
                detailStr.append("申请退款失败");
            }
            if (registration.getRegistrationTradeRefund() != null) {
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
            if (registration.getRegistrationTradeRefund() != null) {
                String rt = registration.getRegistrationRefundReq().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setStatus("1");
            orderProcess.setImg(img);
            orderProcess.setDetail(detailStr.toString());
            orders.add(orderProcess);
        }
    }

    /**
     * 订单流程(医院确认退号).
     *
     * @param registration 挂号信息.
     * @param orders       订单列表.
     */
    private void getHospitalConfirmCancel(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
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
            if (registration.getRegistrationRefundHis() != null) {
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
     * 挂号单订单流程
     *
     * @param registration 挂号单信息.
     */
    @Override
    public void setOrderProcess2Registration(RegistrationDocument registration) {
        List<RegistrationOrderProcessDocument> orders = registration.getOrders();
        // 订单流程(待支付).
        getNotPaidProcess(registration, orders);
        getOrder2PayPlatform(registration, orders);
        if (orders.size() > 1) {
            getHospitalConfirm(registration, orders);
        }
        // 预约挂号
        if ("1".equals(registration.getIsAppointment())) {
            getCancelReg2Hospital(registration, orders);
            String payStatus = "0";
            String confirmStatus = "0";
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
            // 单日挂号
        } else if ("0".equals(registration.getIsAppointment())) {
            if (orders.size() > 2) {
                RegistrationOrderProcessDocument hospConfirmProcess = orders.get(2);
                if (hospConfirmProcess.getDetail().contains("挂号失败")
                        || (hospConfirmProcess.getDetail().contains("未确认")
                        && !registration.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue()))) {
                    getRefund2Patient(registration, orders);
                }
            }
        }
    }

    //-----------------------------------订单流程相关---------------END--------------------------------------

    /**
     * 获取三方支付平台查询支付结果.
     *
     * @param registrationDocument 挂号单信息.
     * @return payReg 返回对象.
     * @throws Exception 异常
     */
    private PayRegReq getOrderProcessPayRegReq(RegistrationDocument registrationDocument) throws Exception {
        PayRegReq payReg = null;
        String payWay = registrationDocument.getPayChannelId();
        payWay = StringUtil.isEmpty(payWay) ? "" : payWay;
        if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            AliPayTradeQueryRes query = payService.queryPay(registrationDocument.getOrderNum());
            if (query != null && query.getCode().equals("10000")) {
                payReg = this.convertAppInfo2PayReg(query, registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            WechatPayQueryRes wQuery = payService.queryPay(registrationDocument.getOrderNum());
            if (wQuery != null && wQuery.getResultCode().equals("SUCCESS")) {
                payReg = this.convertAppInfo2PayReg(wQuery, registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            CmbQuerySingleOrderRes cmbQuery = payService.queryPay(registrationDocument.getOrderNum());
            if (cmbQuery != null && StringUtil.isNull(cmbQuery.getHead().getCode())) {
                payReg = this.convertAppInfo2PayReg(cmbQuery, registrationDocument.getId());
            }
        }
        return payReg;
    }

    /**
     * 将支付平台异步通知对象转换为HIS请求对象.
     *
     * @param infoObj 支付平台异步通知对象.
     * @param regId   挂号信息ID.
     * @return 转换后的请求对象.
     */
    @Override
    public PayRegReq convertAppInfo2PayReg(Object infoObj, String regId) {
        // HIS请求对象
        PayRegReq payRegReq = new PayRegReq();
        // 医院ID
        payRegReq.setHosId(CenterFunctionUtils.getHosId());
        // 取得挂号单信息
        RegistrationDocument reg = this.getRegistrationDocumentById(regId);
        // 如果未获取到挂号单信息则返回null
        if (reg == null) {
            return null;
        }
        try {
            // HIS请求参数金额(以分为单位)
            int fee = Integer.parseInt(reg.getAmount());
            payRegReq.setOrderId(reg.getOrderNum());
            // 支付宝异步通知保存对象
            if (infoObj instanceof AliEntity) {
                AliEntity aliEntity = (AliEntity) infoObj;
                payRegReq.setSerialNum(aliEntity.getTradeNo());
                payRegReq.setPayDate(aliEntity.getNotifyTime().split(" ")[0]);
                payRegReq.setPayTime(aliEntity.getNotifyTime().split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.ALIPAY.getCode()));
                payRegReq.setPayResCode(aliEntity.getTradeStatus());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(aliEntity.getBuyerId());
                // 支付宝单笔订单查询结果对象
            } else if (infoObj instanceof AliPayTradeQueryRes) {
                AliPayTradeQueryRes payTradeQuery = (AliPayTradeQueryRes) infoObj;
                payRegReq.setSerialNum(payTradeQuery.getTradeNo());
                payRegReq.setPayDate(payTradeQuery.getSendPayDate().split(" ")[0]);
                payRegReq.setPayTime(payTradeQuery.getSendPayDate().split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.ALIPAY.getCode()));
                payRegReq.setPayResCode(payTradeQuery.getTradeStatus());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(payTradeQuery.getBuyerUserId());
                // 微信异步通知保存对象
            } else if (infoObj instanceof WechatEntity) {
                WechatEntity weixinEntity = (WechatEntity) infoObj;
                payRegReq.setSerialNum(weixinEntity.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinEntity.getTimeEnd(), "yyyyMMddHHmmss");
                payRegReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payRegReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WECHATPAY.getCode()));
                payRegReq.setPayResCode(weixinEntity.getResultCode());
                payRegReq.setMerchantId(weixinEntity.getMchId());
                payRegReq.setTerminalId(weixinEntity.getDeviceInfo());
                payRegReq.setPayAccount(weixinEntity.getAppid());
                // 微信单笔订单查询结果对象
            } else if (infoObj instanceof WechatPayQueryRes) {
                WechatPayQueryRes weixinPayQuery = (WechatPayQueryRes) infoObj;
                payRegReq.setSerialNum(weixinPayQuery.getTransactionId());
                Date timeEnd = DateUtil.toDate(weixinPayQuery.getTimeEnd(), "yyyyMMddHHmmss");
                payRegReq.setPayDate(DateUtil.toTimestamp(timeEnd).split(" ")[0]);
                payRegReq.setPayTime(DateUtil.toTimestamp(timeEnd).split(" ")[1]);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WECHATPAY.getCode()));
                payRegReq.setPayResCode(weixinPayQuery.getResultCode());
                payRegReq.setMerchantId(weixinPayQuery.getMchId());
                payRegReq.setTerminalId(weixinPayQuery.getDeviceInfo());
                payRegReq.setPayAccount(weixinPayQuery.getAppid());
                // 一网通异步通知保存对象
            } else if (infoObj instanceof CmbPayEntity) {
                CmbPayEntity cmbEntity = (CmbPayEntity) infoObj;
                // 支付信息
                String account = cmbEntity.getMsg();
                // 一网通20位银行流水号
                payRegReq.setSerialNum(account.substring(account.length() - 20, account.length()));
                // 交易日期
                String date = DateUtil.toString(DateUtil.toDate(cmbEntity.getDate(), "yyyyMMdd"), "yyyy-MM-dd");
                payRegReq.setPayDate(date);
                payRegReq.setPayTime(cmbEntity.getTime());
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WEB_UNION.getCode()));
                payRegReq.setPayResCode(cmbEntity.getSucceed());
                payRegReq.setMerchantId("");
                payRegReq.setTerminalId("");
                payRegReq.setPayAccount(cmbEntity.getBillNo());
                // 一网通单笔订单查询结果对象
            } else if (infoObj instanceof CmbQuerySingleOrderRes) {
                CmbQuerySingleOrderRes cmbPayQuery = (CmbQuerySingleOrderRes) infoObj;
                payRegReq.setSerialNum(cmbPayQuery.getBody().getBankSeqNo());
                // 日期
                String date = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptDate(), "yyyyMMdd"), "yyyy-MM-dd");
                // 时间
                String time = DateUtil.toString(DateUtil.toDate(cmbPayQuery.getBody().getAcceptTime(), "HHmmss"), "HH:mm:ss");
                payRegReq.setPayDate(date);
                payRegReq.setPayTime(time);
                payRegReq.setPayChannelId(String.valueOf(PayChannel.WEB_UNION.getCode()));
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

    /**
     * 将支付平台异步通知对象转换为HIS请求对象.
     *
     * @param infoObj  支付平台异步通知对象.
     * @param orderNo  订单号.
     * @param refundId 退款流水号.
     * @return 转换后的请求对象.
     */
    @Override
    public RefundReq convertAppRefundInfo2RefundReq(Object infoObj, String orderNo, String refundId) {
        String hosId = CenterFunctionUtils.getHosId();
        RefundReq refundReq = new RefundReq();
        refundReq.setHosId(hosId);
        //int fee = 0;
        Order order = orderService.findByOrderNo(orderNo);
        if (orderNo != null) {
            RegistrationDocument reg = this.getRegistrationDocumentById(order.getFormId());
            if (reg != null) {
                refundReq.setOrderId(reg.getOrderNum());
                refundReq.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                refundReq.setRefundId(refundId);
                refundReq.setRefundResDesc("");
                refundReq.setRefundRemark("");
                // 支付宝退款响应信息
                if (infoObj instanceof AliRefundRes) {
                    AliRefundRes refund = (AliRefundRes) infoObj;
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
                    // 微信退款响应信息
                } else if (infoObj instanceof WechatRefundRes) {
                    WechatRefundRes refund = (WechatRefundRes) infoObj;
                    refundReq.setRefundSerialNum(refund.getTransactionId());
                    refundReq.setTotalFee(Integer.parseInt(refund.getTotalFee()));
                    refundReq.setRefundFee(Integer.parseInt(refund.getTotalFee()));
                    refundReq.setRefundDate(DateUtil.toDateString(new Date()));
                    refundReq.setRefundTime(DateUtil.toTimestamp(new Date(), false).split(" ")[1]);
                    if (StringUtil.isNotEmpty(refund.getNonceStr())) {
                        refundReq.setRefundResCode(refund.getNonceStr());
                    } else {
                        refundReq.setRefundResCode("");
                    }
                    // 招行退款响应信息
                } else if (infoObj instanceof CmbRefundNoDupRes) {
                    CmbRefundNoDupRes refund = (CmbRefundNoDupRes) infoObj;
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
                }
            }
        }
        return refundReq;
    }

    @Override
    public void saveOrRemoveCacheRegKey(RegistrationDocument reg, String cacheType) throws RegisterException {
        String cacheRegKey = "registration_" + reg.getDoctorId().concat("_").concat(reg.getRegisterDate());
        Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_60);
        Cache.ValueWrapper valueWrapper = tempCache.get(cacheRegKey);
        if (valueWrapper != null && valueWrapper.get() != null) {
            if (cacheType.equals("1")) {
                String patientId = (String) valueWrapper.get();
                if (StringUtil.isEmpty(patientId) || !reg.getPatientId().equals(patientId)) {
                    throw new RegisterException(CenterFunctionUtils.REG_IS_ABSENCE_ERROR);
                }
            } else {
                tempCache.evict(cacheRegKey);
            }
        } else {
            tempCache.put(cacheRegKey, reg.getPatientId());
        }

    }
}
