package com.proper.enterprise.isj.app.business;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.context.VersionContext;
import com.proper.enterprise.platform.core.api.IBusiness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "pep")
public class AppVersionGetLatestVersionBusiness implements IBusiness<Integer, VersionContext<Integer>> {

    private static final String CACHE_KEY = "'latestVer'";

    @SuppressWarnings("rawtypes")
    @Autowired
    AppVersionGetLatestVersionInfoBusiness appVersionGetLatestVersionInfoBusiness;

    @Cacheable(key = CACHE_KEY)
    public int getLatestVersion() {
        AppVersionDocument version = appVersionGetLatestVersionInfoBusiness.getLatestVersionInfo();
        return version == null ? -1 : version.getVer();
    }

    @Override
    public void process(VersionContext<Integer> ctx) throws Exception {
        ctx.setResult(Integer.valueOf(getLatestVersion()));
    }

}
