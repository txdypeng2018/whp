package com.proper.enterprise.isj.payment.converter.delay;

import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.isj.payment.logger.entity.PayLogRecord;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;

@SuppressWarnings("rawtypes")
public class PayOrderRegReq2PayLogRecordConverter extends AbstractManagedConverter<Class, PayOrderRegReq, PayLogRecord> {

    @Override
    public PayLogRecord convert(PayOrderRegReq source, PayLogRecord target) {
        target.setOrderId(source.getOrderId());
        target.setPayWay(source.getPayChannelId());
        target.setRefundAmount("0");
        target.setRefundWay("");
        target.setTotalFee(Integer.toString(source.getPayTotalFee()));
        target.setUserId(source.getOperatorId());
        return target;
    }

}