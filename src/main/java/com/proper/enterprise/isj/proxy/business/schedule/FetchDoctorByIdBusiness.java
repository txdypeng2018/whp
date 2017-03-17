package com.proper.enterprise.isj.proxy.business.schedule;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ContextUtils;
import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchDoctorByIdBusiness<M extends IdContext<DoctorDocument> & ModifiedResultBusinessContext<DoctorDocument>>
        implements IBusiness<DoctorDocument, M>, ILoggable {
    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;

    @Override
    public void process(M ctx) throws Exception {
        String id = ContextUtils.getProperty(ctx, "id");
        DoctorDocument doc;
        try {
            doc = webServiceCacheUtil.getCacheDoctorDocument().get(id);
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            throw new RuntimeException(HIS_DATALINK_ERR, e);
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            debug("系统错误", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(doc);
    }
}
