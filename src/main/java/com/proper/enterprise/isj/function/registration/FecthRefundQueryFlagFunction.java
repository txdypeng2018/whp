package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationTradeRefundDocument;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQueryRefundRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundQueryRes;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getRefundQueryFlag(RegistrationDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FecthRefundQueryFlagFunction implements IFunction<Boolean> {

    @Autowired
    PayFactory payFactory;

    @Override
    public Boolean execute(Object... params) throws Exception {
        return getRefundQueryFlag((RegistrationDocument) params[0]);
    }

    /**
     * 校验退款单号是否已退款.
     *
     * @param registrationDocument 挂号单.
     * @return 退款单号是否已退款.
     * @throws Exception 异常.
     */
    public boolean getRefundQueryFlag(RegistrationDocument registrationDocument) throws Exception {
        boolean queryFlag = false;
        RegistrationTradeRefundDocument refund = registrationDocument.getRegistrationTradeRefund();
        if (refund != null) {
            // 支付宝退款号
            if (StringUtil.isNotEmpty(refund.getOutRequestNo())) {
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                AliRefundTradeQueryRes refundQuery = payService.queryRefund(refund.getOutTradeNo(),
                        refund.getOutRequestNo());
                if (refundQuery != null && refundQuery.getCode().equals("10000")) {
                    queryFlag = true;
                }
                // 微信退款好
            } else if (StringUtil.isNotEmpty(refund.getOutRefundNo())) {
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                WechatRefundQueryRes refundQuery = payService.queryRefund(refund.getOutTradeNo(),
                        refund.getOutRefundNo());
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

}
