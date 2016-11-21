package com.proper.enterprise.isj.proxy.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;

/**
 * Created by think on 2016/9/24 0024.
 */
@Component
public class InitData2CacheTask implements Runnable {

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
            e.printStackTrace();
        }

    }
}
