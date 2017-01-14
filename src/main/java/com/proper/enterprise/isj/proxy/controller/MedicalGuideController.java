package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by think on 2016/8/15 0015. 就医指南信息
 */
@RestController
@RequestMapping(path = "/medicalGuide")
public class MedicalGuideController extends BaseController {

    @Autowired
    BaseInfoRepository baseRepo;

    /**
     * 取得就医指南信息
     * 
     * @return 就医指南信息
     */
    @AuthcIgnore
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getMedicalGuide() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.medicalGuide"));
        return responseOfGet(infoList.get(0).getInfo());
    }
}
