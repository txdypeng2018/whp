package com.proper.enterprise.isj.proxy.business.his;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ReportIdContext;
import com.proper.enterprise.isj.exception.IHosException;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4FileCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class HisFetchRepostsDetailsInfoAsStringBusiness<M extends ReportIdContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {

    @Autowired
    WebService4FileCacheUtil webServiceFileCacheUtil;

    @Override
    public void process(M ctx) throws Exception {
        try {
            ctx.setResult(webServiceFileCacheUtil.getCacheReportPhoto(ctx.getReportId()));
        } catch (IHosException ie) {
            debug("MedicalReportsServiceImpl.getRepostsDetailsInfo[Exception]:", ie);
            throw ie;
        }

    }

}
