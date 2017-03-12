package com.proper.enterprise.isj.user.service.impl
import com.proper.enterprise.isj.log.repository.WSLogRepository
import com.proper.enterprise.isj.user.service.SMSService
import com.proper.enterprise.platform.core.utils.DateUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SMSServiceImplTest extends AbstractTest {

    @Autowired
    SMSService service

    @Autowired
    WSLogRepository repository

    @Before
    public void setUp() {
        repository.deleteAll()
    }

    @Test
    public void sendSMS() {
        def phone = '15640567780'
        def result = service.sendSMS(phone, "测试短信 - ${DateUtil.timestamp}")
        assert result
        while(repository.count() != 1) {
            println "sleep 100 milliseconds to wait until write log done"
            sleep(100)
        }
        def logged = false
        repository.findAll().each {
            logged = logged || it.param['phone'] == phone
        }
        assert logged
    }

}
