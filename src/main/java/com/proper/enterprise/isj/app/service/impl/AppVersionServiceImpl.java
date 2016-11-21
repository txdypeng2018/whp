package com.proper.enterprise.isj.app.service.impl;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.app.repository.AppVersionRepository;
import com.proper.enterprise.isj.app.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "pep")
public class AppVersionServiceImpl implements AppVersionService {

    private static final String CACHE_KEY = "'latestVer'";

    @Autowired
    AppVersionRepository repository;

    @Override
    @CacheEvict(key = CACHE_KEY)
    public AppVersionDocument save(AppVersionDocument appVersion) {
        return repository.save(appVersion);
    }

    @Override
    public AppVersionDocument getLatestVersionInfo() {
        return repository.findTopByOrderByVerDesc();
    }

    @Override
    @Cacheable(key = CACHE_KEY)
    public int getLatestVersion() {
        AppVersionDocument version = getLatestVersionInfo();
        return version == null ? -1 : version.getVer();
    }

    @Override
    public AppVersionDocument getCertainVersion(int version){
        return repository.findByVer(version);
    }

}
