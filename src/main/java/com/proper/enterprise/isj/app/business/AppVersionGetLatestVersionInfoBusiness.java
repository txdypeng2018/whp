package com.proper.enterprise.isj.app.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.app.repository.AppVersionRepository;
import com.proper.enterprise.isj.context.AppVersionDocumentContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
@CacheConfig(cacheNames = "pep")
public class AppVersionGetLatestVersionInfoBusiness<M extends AppVersionDocumentContext<AppVersionDocument> & ModifiedResultBusinessContext<AppVersionDocument>>
        implements IBusiness<AppVersionDocument, M> {
    @Autowired
    AppVersionRepository repository;

    public AppVersionDocument getLatestVersionInfo() {
        return repository.findTopByOrderByVerDesc();
    }

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(getLatestVersionInfo());
    }

}
