package com.proper.enterprise.isj.proxy.service.rule.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.rule.CheckPersonRegistrationFunction;
import com.proper.enterprise.isj.proxy.service.rule.RegistrationRuleService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * Created by think on 2016/9/27 0027.
 */
@Service
public class RegistrationRuleServiceImpl extends AbstractService implements RegistrationRuleService {

    @Override
    public String checkPersonRegistration(String deptId, String idCard) {
        return toolkit.executeFunction(CheckPersonRegistrationFunction.class, deptId, idCard);
    }

}
