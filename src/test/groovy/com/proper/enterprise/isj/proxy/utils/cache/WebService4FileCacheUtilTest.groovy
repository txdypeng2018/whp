package com.proper.enterprise.isj.proxy.utils.cache

import com.proper.enterprise.isj.proxy.document.DoctorDocument
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired


class WebService4FileCacheUtilTest extends AbstractTest {

    @Autowired
    WebService4FileCacheUtil util

    @Test
    public void testCompressPhoto() {
        def doctor = new DoctorDocument('test photo')
        doctor.setId('testphoto')
        doctor.setPersonPic('8979bf9f-fe15-4106-93c6-dc995b61e87a')
        assert util.cacheDoctorPhoto(doctor) > ''
    }

}
