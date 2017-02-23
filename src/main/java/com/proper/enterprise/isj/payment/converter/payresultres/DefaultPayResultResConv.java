package com.proper.enterprise.isj.payment.converter.payresultres;

import com.proper.enterprise.isj.payment.converter.Converter;
import com.proper.enterprise.platform.api.pay.enums.PayResType;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import org.springframework.stereotype.Service;

@Service
public class DefaultPayResultResConv<T extends PayResultRes> implements Converter<PayResultRes, T> {

    @SuppressWarnings("unchecked")
    @Override
    public T convert(PayResultRes source, T target) {
        // 判断预支付结果
        if (source.getResultCode().equals(PayResType.SUCCESS)) {
            target = (T) source;
        } else {
            target.setResultCode(source.getResultCode());
            target.setResultMsg(source.getResultMsg());
        }
        return target;
    }

}
