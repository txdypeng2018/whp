package com.proper.enterprise.isj.function.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.model.RefundReq;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;

/**
 * old:com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.refundMoney2User(Order, String, boolean)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class RefundMoney2UserFunction implements IFunction<Boolean>, ILoggable{


    @Autowired
    PayFactory payFactory;
    
    @Override
    public Boolean execute(Object... params) throws Exception {
        return refundMoney2User((Order)params[0], (String)params[1], (Boolean)params[2]);
    }
    
    /**
     * 调用支付平台的退款接口.
     *
     * @param order 订单.
     * @param refundNo 退款编号.
     * @param refundFlag 退款标识.
     * @return 是否成功退款.
     * @throws Exception 异常.
     */
    private boolean refundMoney2User(Order order, String refundNo, boolean refundFlag) throws Exception {
        if (order.getPayWay().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            // 退款请求对象
            RefundReq refundInfo = new RefundReq();
            // 商户订单号
            refundInfo.setOutTradeNo(order.getOrderNo());
            // 退款流水号
            refundInfo.setOutRequestNo(refundNo);
            // 退款金额
            refundInfo.setRefundAmount(order.getOrderAmount());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            AliRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes != null && refundRes.getCode().equals("10000")) {
                refundFlag = true;
            } else {
                debug("未查到需要退费的项目,或者退费接口返回异常,支付宝订单号:{}", order.getOrderNo());
            }
        } else if (order.getPayWay().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            // 请求退款对象
            RefundReq refundInfo = new RefundReq();
            // 商户订单号
            refundInfo.setOutTradeNo(order.getOrderNo());
            // 退款流水号
            refundInfo.setOutRequestNo(refundNo);
            // 退款金额
            refundInfo.setRefundAmount(order.getOrderAmount());
            // 退款总金额
            refundInfo.setTotalFee(order.getOrderAmount());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            WechatRefundRes refundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (refundRes.getResultCode().equals("0")) {
                refundFlag = true;
            } else {
                debug("未查到需要退费的项目,或者退费接口返回异常,微信订单号:{}", order.getOrderNo());
            }
            // 一网通缴费退款操作
        } else if (order.getPayWay().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            // 请求退款对象
            RefundReq refundInfo = new RefundReq();
            // 商户订单号
            refundInfo.setOutTradeNo(order.getOrderNo());
            // 退款流水号
            refundInfo.setOutRequestNo(refundNo);
            // 退款金额
            refundInfo.setRefundAmount(order.getOrderAmount());
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            CmbRefundNoDupRes cmbRefundRes = payService.refundPay(refundInfo);
            // 退款结果
            if (cmbRefundRes != null && StringUtil.isNull(cmbRefundRes.getHead().getCode())) {
                refundFlag = true;
            } else {
                debug("未查到需要退费的项目,或者退费接口返回异常,一网通订单号:{}", order.getOrderNo());
            }
        } else {
            debug("未查到订单号对应的支付平台,订单号:{}", order.getOrderNo());
        }
        return refundFlag;
    }

}
