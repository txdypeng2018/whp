package com.proper.enterprise.isj.app.service.impl;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.app.repository.AppVersionRepository;
import com.proper.enterprise.isj.app.service.AppVersionService;
import com.proper.enterprise.isj.support.service.AbstractService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "pep")
public class AppVersionServiceImpl extends AbstractService implements AppVersionService {

    private static final String CACHE_KEY = "'latestVer'";

    @Autowired
    AppVersionRepository repository;

    @Override
    @CacheEvict(key = CACHE_KEY)
    public AppVersionDocument save(AppVersionDocument appVersion) {
        
        return toolkit.executeRepositoryFunction(AppVersionRepository.class, "save", appVersion);
    }

    @Override
    public AppVersionDocument getLatestVersionInfo() {
        return toolkit.executeRepositoryFunction(AppVersionRepository.class, "findTopByOrderByVerDesc");
    }

    @Override
    @Cacheable(key = CACHE_KEY)
    public int getLatestVersion() {
        AppVersionDocument version = getLatestVersionInfo();
        return version == null ? -1 : version.getVer();
    }

    @Override
    public AppVersionDocument getCertainVersion(int version){
        return toolkit.executeRepositoryFunction(AppVersionRepository.class, "findByVer", version);
    }

}
