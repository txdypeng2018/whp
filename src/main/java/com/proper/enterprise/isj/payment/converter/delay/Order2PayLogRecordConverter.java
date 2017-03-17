package com.proper.enterprise.isj.payment.converter.delay;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.isj.payment.logger.entity.PayLogRecord;

@SuppressWarnings("rawtypes")
public class Order2PayLogRecordConverter extends AbstractManagedConverter<Class, Order, PayLogRecord> {

    @Override
    public PayLogRecord convert(Order source, PayLogRecord target) {
        target.setOrderId(source.getOrderNo());
        target.setPayWay(source.getPayWay());
        target.setRefundAmount("0");
        target.setRefundWay("");
        target.setTotalFee(source.getOrderAmount());
        target.setUserId(source.getLastModifyUserId());
        return target;
    }

}