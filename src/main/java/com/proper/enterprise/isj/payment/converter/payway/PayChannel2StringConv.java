package com.proper.enterprise.isj.payment.converter.payway;

import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.converter.Converter;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import org.springframework.stereotype.Service;

@Service
public class PayChannel2StringConv implements Converter<PayChannel, String> {

    @Override
    public String convert(PayChannel source, String target) {
        String payway;
        switch (source) {
            case ALIPAY:
                payway = BusinessPayConstants.ISJ_PAY_WAY_ALI;
                break;
            case WECHATPAY:
                payway = BusinessPayConstants.ISJ_PAY_WAY_WECHAT;
                break;
            case WEB_UNION:
                payway = BusinessPayConstants.ISJ_PAY_WAY_CMB;
                break;
            default:
                throw new RuntimeException("不支持的支付渠道:" + source.toString());
        }
        return payway;
    }

}
