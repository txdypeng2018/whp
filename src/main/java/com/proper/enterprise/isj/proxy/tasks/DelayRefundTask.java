package com.proper.enterprise.isj.proxy.tasks;

import com.proper.enterprise.isj.payment.entity.DelayRefundConfigEntity;
import com.proper.enterprise.isj.payment.repository.DelayRefundConfigEntityRepository;
import com.proper.enterprise.isj.payment.service.DelayRefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DelayRefundTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayRefundTask.class);

    public static final String DEFAULT_CRON_EXPRESSION = "15 0/10 1,2,3,4,5,6,7,11,12,13,14,15,16,17,18,19,20,21,22 * * ?";

    /**
     * 延时时间默认值，4小时=60*60*4=14400.
     */
    public static final String DEFAULT_DELAY_TIME_SECOND = "14400";

    private static final String PK_CRON_EXPRESSION = "PK_CRON_EXPRESSION";
    private static final String PK_DELAY_TIME = "PK_DELAY_TIME";


    @Autowired
    DelayRefundService service;

    @Autowired
    DelayRefundConfigEntityRepository repo;

    @Override
    public void run() {
        try {
            service.doDelayRefund(this.fetchDelayTime());
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public String fetchCronExpression() {
        String res = DEFAULT_CRON_EXPRESSION;
        DelayRefundConfigEntity record = repo.findOne(PK_CRON_EXPRESSION);
        res = record.getText();
        return res;
    }

    public long fetchDelayTime() {
        String value = DEFAULT_DELAY_TIME_SECOND;
        DelayRefundConfigEntity record = repo.findOne(PK_DELAY_TIME);
        value = record.getText();
        return Long.parseLong(value);
    }



}
