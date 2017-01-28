package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

/**
 * 医院简介信息.
 * Created by think on 2016/8/15 0015.
 */
@RestController
@RequestMapping(path = "/hospitalIntroduce")
public class HospitalIntroduceController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalIntroduceController.class);

    @Autowired
    HospitalIntroduceService hospitalIntroduceService;

    /**
     * 取得医院简介信息.
     *
     * @return 医院简介信息.
     */
    @AuthcIgnore
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getHospitalIntroduce() {
        HosInfo info;
        try {
            info = hospitalIntroduceService.getHospitalInfoFromHis();
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            throw new RuntimeException(HIS_DATALINK_ERR, e);
        } catch (HisReturnException e) {
            LOGGER.debug("HIS接口返回错误", e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        return responseOfGet(info.getDesc());
    }
}
