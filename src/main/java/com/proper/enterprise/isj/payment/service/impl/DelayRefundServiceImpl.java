package com.proper.enterprise.isj.payment.service.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.payment.DoDelayRefundFunction;
import com.proper.enterprise.isj.payment.service.DelayRefundService;
import com.proper.enterprise.isj.support.service.AbstractService;

@Service
public class DelayRefundServiceImpl extends AbstractService implements DelayRefundService {


    @Override
    public void doDelayRefund(long delayTime) {
        //TODO
        toolkit.executeFunction(DoDelayRefundFunction.class, delayTime);
    }

}
