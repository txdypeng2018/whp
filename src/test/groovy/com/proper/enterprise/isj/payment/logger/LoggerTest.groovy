package com.proper.enterprise.isj.payment.logger

import org.springframework.beans.factory.annotation.Autowired

import com.proper.enterprise.isj.exception.DelayException
import com.proper.enterprise.isj.payment.logger.repository.PayLogRecordRepository
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl
import com.proper.enterprise.isj.webservices.model.req.PayRegReq

class LoggerTest {

    @Autowired
    RegistrationServiceImpl service;

    @Autowired
    PayLogRecordRepository repo;

    //@Test 2017-3-14 王东石 重构，临时屏蔽，调整通过后需要恢复
    public void test() {

        LoggerTestAdvice.active = true;

        PayRegReq req = new PayRegReq();
        try {
            req.setOrderId(UUID.randomUUID().toString());
            service.updateRegistrationAndOrder(req)
        } catch (DelayException e) {
            PayLogUtils.log(PayStepEnum.UNKNOWN, req, e.getPosition());
        }
        /*
        PayOrderRegReq orderreq = new PayOrderRegReq();
        try {
            orderreq.setOrderId(UUID.randomUUID().toString());
            service.updateRegistrationAndOrder(orderreq)
        } catch (DelayException e) {
            PayLogUtils.log(PayStepEnum.UNKNOWN, orderreq, e.getPosition());
        }
        */
        sleep 5000

        def list = repo.findAll()
        assert list.size() == 2

        LoggerTestAdvice.active = false;

    }
}
