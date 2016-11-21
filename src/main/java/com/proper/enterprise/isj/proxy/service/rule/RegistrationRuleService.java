package com.proper.enterprise.isj.proxy.service.rule;

/**
 * Created by think on 2016/9/27 0027.
 */
public interface RegistrationRuleService {

    /**
     * 符合规则返回空串,不合规则返回相应提示信息
     * 
     * @param deptId
     * @param idCard
     * @return
     */
    String checkPersonRegistration(String deptId, String idCard);
}
