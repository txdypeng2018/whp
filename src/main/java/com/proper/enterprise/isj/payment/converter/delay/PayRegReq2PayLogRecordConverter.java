package com.proper.enterprise.isj.payment.converter.delay;

import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.isj.payment.logger.entity.PayLogRecord;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;

@SuppressWarnings("rawtypes")
public class PayRegReq2PayLogRecordConverter extends AbstractManagedConverter<Class, PayRegReq, PayLogRecord> {

    @Override
    public PayLogRecord convert(PayRegReq source, PayLogRecord target) {
        target.setOrderId(source.getOrderId());
        target.setPayWay(source.getPayChannelId());
        target.setRefundAmount("0");
        target.setRefundWay("");
        target.setTotalFee(Integer.toString(source.getPayTotalFee()));
        target.setUserId(source.getOperatorId());
        return target;
    }

}
