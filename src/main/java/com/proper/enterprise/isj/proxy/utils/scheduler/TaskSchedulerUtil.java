package com.proper.enterprise.isj.proxy.utils.scheduler;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.tasks.InitData2CacheTask;
import com.proper.enterprise.isj.proxy.tasks.RefundFromHospitalTask;
import com.proper.enterprise.isj.proxy.tasks.RegistrationCancelTask;
import com.proper.enterprise.isj.proxy.tasks.StopRegTask;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * Created by think on 2016/9/20 0020.
 */
@Component
@Lazy(false)
public class TaskSchedulerUtil {

    @Autowired
    TaskScheduler scheduler;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    RegistrationCancelTask registrationCancelTask;

    @Autowired
    StopRegTask stopRegTask;

    @Autowired
    RefundFromHospitalTask refundFromHospitalTask;

    @Autowired
    InitData2CacheTask initData2CacheTask;



    @PostConstruct
    public void initMethod() {
        String canInit = ConfCenter.get("isj.info.scheduling.init");
        if (canInit.equals(String.valueOf(1))) {
            Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP);
            tempCache.clear();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            scheduler.scheduleWithFixedDelay(initData2CacheTask, calendar.getTime(), 30 * 60 * 1000);
            calendar.add(Calendar.SECOND, 10);
            scheduler.scheduleWithFixedDelay(registrationCancelTask, calendar.getTime(), 60 * 1000);
            calendar.add(Calendar.SECOND, 10);
            scheduler.scheduleWithFixedDelay(refundFromHospitalTask, calendar.getTime(), 30 * 60 * 1000);
            calendar.add(Calendar.SECOND, 10);
            scheduler.scheduleWithFixedDelay(stopRegTask, calendar.getTime(), 60 * 60 * 1000);
        }
    }

}