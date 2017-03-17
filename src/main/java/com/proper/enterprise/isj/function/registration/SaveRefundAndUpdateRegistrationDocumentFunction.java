package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.function.message.SendRegistrationMsgFunction;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationTradeRefundDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;

@Service
public class SaveRefundAndUpdateRegistrationDocumentFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    PayFactory payFactory;

    @Override
    public Object execute(Object... params) throws Exception {
        saveRefundAndUpdateRegistrationDocument((RegistrationDocument) params[0]);
        return null;
    }

    /**
     * 线下申请退费,根据支付平台返还支付费用,修改相应的数据为已退费.
     *
     * @param reg 挂号单.
     */
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
            reg = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", reg);
        }
        // 请求退款对象
        com.proper.enterprise.platform.api.pay.model.RefundReq refundInfo = new com.proper.enterprise.platform.api.pay.model.RefundReq();
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
                LOGGER.debug("挂号退费失败,挂号单号:{},订单号:{},失败原因:{}", reg.getNum(), reg.getOrderNum(),
                        refundRes.getReturnMsg());
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
                    LOGGER.debug("挂号退费失败,挂号单号:{},订单号:{},失败原因:{}", reg.getNum(), reg.getOrderNum(),
                            cmbRefundRes.getHead().getErrMsg());
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
            reg = toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", reg);
            toolkitx.executeFunction(UpdateOrderEntityFunction.class, reg);

            toolkitx.executeFunction(SendRegistrationMsgFunction.class, SendPushMsgEnum.REG_REFUND_SUCCESS,
                    reg.getId());

        }
    }

}
