package com.proper.enterprise.isj.user.service.impl

import com.proper.enterprise.isj.user.service.SMSService
import com.proper.enterprise.platform.core.utils.DateUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SMSServiceImplTest extends AbstractTest {

    @Autowired
    SMSService service

    @Test
    public void sendSMS() {
        def result = service.sendSMS('15640567780', "测试短信 - ${DateUtil.timestamp}")
        assert result
    }

}
