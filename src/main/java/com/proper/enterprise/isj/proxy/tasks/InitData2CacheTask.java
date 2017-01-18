package com.proper.enterprise.isj.proxy.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;

/**
 * 初始化数据到缓存任务.
 * Created by think on 2016/9/24 0024.
 */
@Component
public class InitData2CacheTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitData2CacheTask.class);

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;

    @Override
    public void run() {
        try {
            webService4HisInterfaceCacheUtil.cacheHospitalInfoRes();
            webService4HisInterfaceCacheUtil.cacheDeptInfo();
            webService4HisInterfaceCacheUtil.cacheDoctorInfoRes();
            webServiceDataSecondCacheUtil.cacheOA2HISInfo();
            webServiceDataSecondCacheUtil.cacheSubjectMap();
            webServiceDataSecondCacheUtil.cacheSubjectDocument();
            webServiceCacheUtil.cacheDoctorDocument();
            webServiceCacheUtil.cacheDoctorInfoLike();
            webServiceCacheUtil.cacheDoctorSubjectRelMap();
        } catch (Exception e) {
            LOGGER.debug("定时任务异常", e);
        }

    }
}
