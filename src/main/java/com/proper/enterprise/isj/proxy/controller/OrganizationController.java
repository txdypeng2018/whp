package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by think on 2016/8/16 0016. 指定科室详细
 */
@RestController
@RequestMapping(path = "/organization")
public class OrganizationController extends BaseController {

    @Autowired
    SubjectService subjectService;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    /**
     * 取得院区列表
     * 
     * @return 院区列表
     */
    @JWTIgnore
    @RequestMapping(path = "/districts", method = RequestMethod.GET)
    public ResponseEntity<List<SubjectDocument>> getDistricts() throws Exception {
        List<SubjectDocument> disList = null;
        try {
            disList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                    .get(String.valueOf(DeptLevel.CHILD.getCode())).get("0");
            // disList = subjectService.findDistrictListFromHis();
        } catch (UnmarshallingFailureException e) {
            e.printStackTrace();
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            e.printStackTrace();
            throw new HisLinkException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        return responseOfGet(disList);
    }

}
