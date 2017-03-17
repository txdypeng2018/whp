package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQuerySingleOrderRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getPayRegReq(RegistrationDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchPayRegReqFunction implements IFunction<PayRegReq> {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    PayFactory payFactory;

    @Override
    public PayRegReq execute(Object... params) throws Exception {
        return getPayRegReq((RegistrationDocument) params[0]);
    }

    /**
     * 根据订单号获得支付信息,并转换为HIS的请求参数
     *
     * @param registrationDocument 挂号信息.
     * @return 支付信息.
     * @throws Exception 异常.
     */
    public PayRegReq getPayRegReq(RegistrationDocument registrationDocument) throws Exception {
        PayRegReq payReg = null;
        String payWay = registrationDocument.getPayChannelId();
        if (StringUtil.isEmpty(payWay)) {
            payWay = "";
        }
        if (payWay.equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            AliPayTradeQueryRes query = payService.queryPay(registrationDocument.getOrderNum());
            if (query != null && query.getCode().equals("10000") && query.getTradeStatus().equals("TRADE_SUCCESS")) {
                payReg = toolkitx.executeFunction(ConvertAppInfo2PayRegFunction.class, query,
                        registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            WechatPayQueryRes wQuery = payService.queryPay(registrationDocument.getOrderNum());
            if (wQuery != null && wQuery.getResultCode().equals("SUCCESS")
                    && wQuery.getTradeState().equals("SUCCESS")) {
                payReg = toolkitx.executeFunction(ConvertAppInfo2PayRegFunction.class, wQuery,
                        registrationDocument.getId());
            }
        } else if (payWay.equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            CmbQuerySingleOrderRes cmbQuery = payService.queryPay(registrationDocument.getOrderNum());
            if (cmbQuery != null && StringUtil.isNull(cmbQuery.getHead().getCode())
                    && cmbQuery.getBody().getStatus().equals("0")) {
                payReg = toolkitx.executeFunction(ConvertAppInfo2PayRegFunction.class, cmbQuery,
                        registrationDocument.getId());
            }
        }
        return payReg;
    }

}
