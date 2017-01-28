package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.exception.HisReturnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;

/**
 * 医院简介服务实现.
 * Created by think on 2016/8/29 0029.
 */
@Service
public class HospitalIntroduceServiceImpl implements HospitalIntroduceService {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Override
    public HosInfo getHospitalInfoFromHis() throws Exception {
        HosInfo info;
        ResModel<HosInfo> hosInfoRes = webServicesClient.getHosInfo(CenterFunctionUtils.getHosId());
        if (hosInfoRes.getReturnCode() == ReturnCode.SUCCESS) {
            info = hosInfoRes.getRes();
        } else {
            throw new HisReturnException(hosInfoRes.getReturnMsg());
        }
        return info;
    }
}
