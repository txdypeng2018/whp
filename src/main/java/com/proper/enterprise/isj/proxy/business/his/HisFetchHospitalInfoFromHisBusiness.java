package com.proper.enterprise.isj.proxy.business.his;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class HisFetchHospitalInfoFromHisBusiness<M extends ModifiedResultBusinessContext<HosInfo>>
        implements IBusiness<HosInfo, M> {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Override
    public void process(M ctx) throws Exception {
        HosInfo info;
        ResModel<HosInfo> hosInfoRes = webServicesClient.getHosInfo(CenterFunctionUtils.getHosId());
        if (hosInfoRes.getReturnCode() == ReturnCode.SUCCESS) {
            info = hosInfoRes.getRes();
        } else {
            throw new HisReturnException(hosInfoRes.getReturnMsg());
        }
        ctx.setResult(info);
    }
}
