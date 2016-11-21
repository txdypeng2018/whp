package com.proper.enterprise.isj.proxy.service.rule.impl

import com.proper.enterprise.isj.proxy.service.rule.RegistrationRuleService
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class RegistrationRuleServiceImplTest extends AbstractTest {

    @Autowired
    RegistrationRuleService service

    @Test
    @Sql
    public void checkPersonRegistration() {
        assert service.checkPersonRegistration('M149', "210101${new Date().format('yyyMMdd')}6133") == '当前科室不能选择儿童做为就诊人'
        assert service.checkPersonRegistration('0204', "210101${new Date().format('yyyMMdd')}6133") == '当前科室不能选择儿童做为就诊人'
        assert service.checkPersonRegistration('0168', '210101190001016133') == '当前科室不能选择男性就诊人'
        assert service.checkPersonRegistration('0175', '21010119000101571X') == '当前科室不能选择成人做为就诊人'
        assert service.checkPersonRegistration('0204', '210101190001015823') == '当前科室不能选择女性就诊人'
        assert service.checkPersonRegistration('9014', '21010119000101571X') == ''
    }

}
