package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.navinfo.OrganizationGetDistrictsBusiness;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * 指定科室详细.
 * Created by think on 2016/8/16 0016.
 */
@RestController
@RequestMapping(path = "/organization")
public class OrganizationController extends IHosBaseController {

    /**
     * 取得院区列表
     * 
     * @return 院区列表
     */
    @SuppressWarnings("unchecked")
    @AuthcIgnore
    @RequestMapping(path = "/districts", method = RequestMethod.GET)
    public ResponseEntity<List<SubjectDocument>> getDistricts() throws Exception {
        return responseOfGet((List<SubjectDocument>) toolkit.execute(OrganizationGetDistrictsBusiness.class, c -> {
        }));
    }

}
