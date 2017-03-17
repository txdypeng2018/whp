package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class OrganizationGetDistrictsBusiness<M extends ModifiedResultBusinessContext<List<SubjectDocument>>>
        implements IBusiness<List<SubjectDocument>, M>, ILoggable {

    @Autowired
    SubjectService subjectService;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Override
    public void process(M ctx) throws Exception {
        List<SubjectDocument> disList;
        try {
            disList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                    .get(String.valueOf(DeptLevel.CHILD.getCode())).get("0");
            // disList = subjectService.findDistrictListFromHis();
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            throw new HisLinkException(e.getMessage());
        } catch (Exception e) {
            debug("系统错误", e);
            throw new Exception(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        ctx.setResult(disList);
    }
}
