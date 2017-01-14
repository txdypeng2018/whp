package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.controller.BaseController;

/**
 * Created by think on 2016/8/16 0016. 指定科室详细
 */
@RestController
@RequestMapping(path = "/organization")
public class OrganizationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    SubjectService subjectService;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    /**
     * 取得院区列表
     * 
     * @return 院区列表
     */
    @AuthcIgnore
    @RequestMapping(path = "/districts", method = RequestMethod.GET)
    public ResponseEntity<List<SubjectDocument>> getDistricts() throws Exception {
        List<SubjectDocument> disList = null;
        try {
            disList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                    .get(String.valueOf(DeptLevel.CHILD.getCode())).get("0");
            // disList = subjectService.findDistrictListFromHis();
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            LOGGER.debug("HIS接口返回错误", e);
            throw new HisLinkException(e.getMessage());
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            throw new Exception(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        return responseOfGet(disList);
    }

}
