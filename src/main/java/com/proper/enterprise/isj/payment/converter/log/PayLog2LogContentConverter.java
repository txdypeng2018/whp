package com.proper.enterprise.isj.payment.converter.log;

import com.proper.enterprise.isj.payment.converter.AbstractWithTargetFactoryConverter;
import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.isj.payment.logger.entity.LogContent;

/**
 * .
 *
 * @param <T>
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class PayLog2LogContentConverter<T> extends AbstractWithTargetFactoryConverter<PayLog<T>, LogContent> {

    @Override
    protected void doConvert(PayLog<T> source, LogContent target) {
        target.setContent(source.businessObject2String());
        target.setJavaType(source.getBusinessObjectType());
    }


}
