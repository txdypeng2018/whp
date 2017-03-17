package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundReqDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationTradeRefundDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.saveRegRefund(String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveRegRefundFunction implements IFunction<RefundReq>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    PayFactory payFactory;

    @Override
    public RefundReq execute(Object... params) throws Exception {
        return saveRegRefund((String) params[0]);
    }

    public RefundReq saveRegRefund(String registrationId) throws Exception {
        RegistrationDocument reg = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne",
                registrationId);
        if (reg == null) {
            LOGGER.debug("未查到挂号单信息,挂号单Id:{}", registrationId);
            throw new RegisterException(CenterFunctionUtils.ORDERREG_REFUND_ERR);
        }
        Order order = toolkitx.executeRepositoryFunction(OrderRepository.class, "getByFormId", registrationId);
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
            toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", reg);
        } else {
            trade = reg.getRegistrationTradeRefund();
        }
        // 订单号
        String orderNo = trade.getOutTradeNo();
        // 请求退款对象
        com.proper.enterprise.platform.api.pay.model.RefundReq refundInfo = new com.proper.enterprise.platform.api.pay.model.RefundReq();
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
                    refundReq = toolkitx.executeFunction(ConvertAppRefundInfo2RefundReqFuntion.class, refundRes,
                            order.getOrderNo(), trade.getOutRefundNo());
                    RegistrationRefundReqDocument refundHisReq = new RegistrationRefundReqDocument();
                    BeanUtils.copyProperties(refundReq, refundHisReq);
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType("1");
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", reg);
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
                        refundReq = toolkitx.executeFunction(ConvertAppRefundInfo2RefundReqFuntion.class, refundRes,
                                order.getOrderNo(), trade.getOutRequestNo());
                        BeanUtils.copyProperties(refundReq, refundHisReq);
                    } catch (Exception e) {
                        LOGGER.debug("挂号退费转换成HIS需要的参数时异常", e);
                    }
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType("1");
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", reg);
                } else {
                    LOGGER.debug("退号(支付宝退费失败),支付宝返回Code:{},支付宝返回消息:{},订单号:{}", refundRes.getCode(), refundRes.getMsg(),
                            orderNo);
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
                        refundReq = toolkitx.executeFunction(ConvertAppRefundInfo2RefundReqFuntion.class, cmbRefundRes,
                                order.getOrderNo(), trade.getCmbRefundNo());
                        BeanUtils.copyProperties(refundReq, refundHisReq);
                    } catch (Exception e) {
                        LOGGER.debug("挂号退费转换成HIS需要的参数时异常", e);
                    }
                    reg.setRegistrationRefundReq(refundHisReq);
                    reg.setRefundApplyType(String.valueOf(PayChannel.WEB_UNION.getCode()));
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", reg);
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

}
