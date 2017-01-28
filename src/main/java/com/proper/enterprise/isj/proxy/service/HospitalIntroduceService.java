package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.webservices.model.res.HosInfo;

/**
 * 医院简介服务.
 * Created by think on 2016/8/29 0029.
 */
public interface HospitalIntroduceService {

	HosInfo getHospitalInfoFromHis() throws Exception;
}
