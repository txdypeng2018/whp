package com.proper.enterprise.isj.proxy.tasks

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.jdbc.Sql

import com.proper.enterprise.platform.test.AbstractTest


@Sql
class DelayRefundTaskTest  extends AbstractTest{

    @Autowired
    TaskScheduler scheduler;

    @Autowired
    DelayRefundTask delayRefundTask;

    Map result = new HashMap();

    @Test
    void testDelayRefundDefaultSetting(){
        DelayRefundTask task = wac.getBean(DelayRefundTask.class);
        assert task.fetchCronExpression()==DelayRefundTask.DEFAULT_CRON_EXPRESSION
        assert Long.toString(task.fetchDelayTime())==DelayRefundTask.DEFAULT_DELAY_TIME_SECOND

        scheduler.schedule((Runnable)new Runnable(){
                    public void run(){}
                }, new CronTrigger(delayRefundTask.fetchCronExpression()));


        scheduler.schedule((Runnable)new Runnable(){
                    public void run(){
                        result.put("result", true);
                    }
                }, new CronTrigger("* * * * * ?"));

        sleep(2000)
        assert result["result"]
    }
}
