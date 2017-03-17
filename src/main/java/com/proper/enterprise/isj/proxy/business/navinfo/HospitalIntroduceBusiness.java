package com.proper.enterprise.isj.proxy.business.navinfo;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class HospitalIntroduceBusiness<M extends ModifiedResultBusinessContext<String>> implements IBusiness<String, M>, ILoggable {

    @Autowired
    HospitalIntroduceService hospitalIntroduceService;

    @Override
    public void process(M ctx) throws Exception {
        HosInfo info;
        try {
            info = hospitalIntroduceService.getHospitalInfoFromHis();
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
        ctx.setResult(info.getDesc());
    }
}
