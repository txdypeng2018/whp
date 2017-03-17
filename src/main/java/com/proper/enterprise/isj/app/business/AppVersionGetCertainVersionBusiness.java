package com.proper.enterprise.isj.app.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.app.repository.AppVersionRepository;
import com.proper.enterprise.isj.context.VersionContext;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
@CacheConfig(cacheNames = "pep")
public class AppVersionGetCertainVersionBusiness<M extends VersionContext<AppVersionDocument>>
        implements IBusiness<AppVersionDocument, M> {
    @Autowired
    AppVersionRepository repository;

    public AppVersionDocument getCertainVersion(int version) {
        return repository.findByVer(version);
    }

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(getCertainVersion(Integer.parseInt(ctx.getVersion())));
    }

}
