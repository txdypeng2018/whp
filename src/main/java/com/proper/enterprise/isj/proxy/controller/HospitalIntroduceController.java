package com.proper.enterprise.isj.proxy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.navinfo.HospitalIntroduceBusiness;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * 医院简介信息.
 * Created by think on 2016/8/15 0015.
 */
@RestController
@RequestMapping(path = "/hospitalIntroduce")
public class HospitalIntroduceController extends IHosBaseController {
    /**
     * 取得医院简介信息.
     *
     * @return 医院简介信息.
     */
    @AuthcIgnore
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getHospitalIntroduce() {
        return responseOfGet((String) toolkit.execute(HospitalIntroduceBusiness.class, c -> {
        }));

    }
}
