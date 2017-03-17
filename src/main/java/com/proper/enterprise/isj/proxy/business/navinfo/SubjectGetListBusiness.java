package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.TypeContext;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;

@Service
public class SubjectGetListBusiness<T, M extends DistrictIdContext<Object> & TypeContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {
    @Autowired
    SubjectService subjectService;

    @Override
    public void process(M ctx) throws Exception {
        String districtId = ctx.getDistrictId();
        String type = ctx.getType();
        List<SubjectDocument> list;
        try {
            list = subjectService.findSubjectDocumentListFromHis(districtId, "2".equals(type));
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            throw new RuntimeException(CenterFunctionUtils.HIS_DATALINK_ERR, e);
        } catch (Exception e) {
            debug("系统错误", e);
            throw new RuntimeException(e.getMessage(), e);
        }
        ctx.setResult(list);
    }
}
