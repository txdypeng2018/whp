package com.proper.enterprise.isj.payment.converter.log;

import com.proper.enterprise.isj.payment.converter.Converter;
import com.proper.enterprise.isj.payment.logger.PayLog;

/**
 * .
 *
 * @param <T>
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class PayLog2BizObjConverter<T> implements Converter<PayLog<T>, T> {

    @Override
    public T convert(PayLog<T> source, T target) {
        return source.getBusinessObject();
    }

}