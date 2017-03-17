package com.proper.enterprise.isj.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.TypeCodeContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.customerservice.UserDBGetFamilyMenberTypesBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.UserDBGetFamilyMenberTypesByCodeBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.UserDBGetFeedbackTypesBusiness;
import com.proper.enterprise.isj.proxy.business.customerservice.UserDBGetSexTypesBusiness;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * Created by think on 2016/9/2 0002.
 */

@RestController
@RequestMapping(path = "/dataBase")
public class UserDataBaseController extends IHosBaseController {

    @RequestMapping(path = "/familyMenberTypes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Map<String, String>>> getFamilyMenberTypes() throws Exception {
        return responseOfGet(toolkit.execute(UserDBGetFamilyMenberTypesBusiness.class, c -> {
        }));
    }

    @AuthcIgnore
    @RequestMapping(path = "/sexTypes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Map<String, String>>> getSexTypes() {
        return responseOfGet(toolkit.execute(UserDBGetSexTypesBusiness.class, c -> {
        }));
    }

    @RequestMapping(path = "/familyMenberTypes/{typeCode}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getFamilyMenberTypes(@PathVariable String typeCode) throws Exception {
        return responseOfGet(toolkit.execute(UserDBGetFamilyMenberTypesByCodeBusiness.class, (c) -> {
            ((TypeCodeContext<?>) c).setTypeCode(typeCode);
        }));

    }

    @AuthcIgnore
    @RequestMapping(path = "/feedbackTypes", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, String>>> getFeedbackTypes() {
        return responseOfGet(toolkit.execute(UserDBGetFeedbackTypesBusiness.class, c -> {
        }));
    }
}
