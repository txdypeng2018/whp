package com.proper.enterprise.isj.proxy.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.report.MedicalGuideGetListBusiness;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * 就医指南信息.
 * Created by think on 2016/8/15 0015.
 */
@RestController
@RequestMapping(path = "/medicalGuide")
public class MedicalGuideController extends IHosBaseController {

    /**
     * 取得就医指南信息.
     * 
     * @return 就医指南信息.
     */
    @AuthcIgnore
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getMedicalGuide() {
        return responseOfGet((String) toolkit.execute(MedicalGuideGetListBusiness.class, c -> {
        }));

    }
}
