package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;
import com.proper.enterprise.platform.core.controller.BaseController;

/**
 * Created by think on 2016/8/15 0015. 医院简介信息
 */
@RestController
@RequestMapping(path = "/hospitalIntroduce")
public class HospitalIntroduceController extends BaseController {

    @Autowired
    HospitalIntroduceService hospitalIntroduceService;

    /**
     * 取得医院简介信息
     * 
     * @return 医院简介信息
     */
    @JWTIgnore
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getHospitalIntroduce() {
        HosInfo info = null;
        try {
            info = hospitalIntroduceService.getHospitalInfoFromHis();
        } catch (UnmarshallingFailureException e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(info.getDesc());
    }
}
