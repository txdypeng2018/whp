package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.platform.pay.ali.service.AliPayService;
import com.proper.enterprise.platform.pay.cmb.model.CmbQuerySingleOrderRes;
import com.proper.enterprise.platform.pay.cmb.service.CmbPayService;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes;
import com.proper.enterprise.platform.pay.wechat.service.WechatPayService;

@Service
public class CheckOrderIsPaidFunction implements IFunction<Boolean> {

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private CmbPayService cmbPayService;

    @Override
    public Boolean execute(Object... params) throws Exception {
        return checkOrderIsPay((String)params[0], (String)params[1]);
    }

    public boolean checkOrderIsPay(String payChannelId, String orderNum) throws Exception {
        boolean paidFlag = false;
        if (StringUtil.isNotEmpty(payChannelId)) {
            if (payChannelId.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                PayService payService = (PayService) aliPayService;
                AliPayTradeQueryRes res = payService.queryPay(orderNum);
                if (res != null && "10000".equals(res.getCode()) && "TRADE_SUCCESS".equals(res.getTradeStatus())) {
                    paidFlag = true;
                }
            } else if (payChannelId.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                PayService payService = (PayService) wechatPayService;
                WechatPayQueryRes res = payService.queryPay(orderNum);
                if (res != null && "SUCCESS".equals(res.getResultCode()) && "SUCCESS".equals(res.getTradeState())) {
                    paidFlag = true;
                }
            } else if (payChannelId.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                PayService payService = (PayService) cmbPayService;
                CmbQuerySingleOrderRes res = payService.queryPay(orderNum);
                if (res != null && StringUtil.isNull(res.getHead().getCode())) {
                    paidFlag = true;
                }
            }
        }
        return paidFlag;
    }
}
