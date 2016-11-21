package com.proper.enterprise.isj.app.controller

import com.proper.enterprise.isj.app.document.AppVersionDocument
import com.proper.enterprise.isj.app.repository.AppVersionRepository
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.After
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class AppVersionControllerTest extends AbstractTest {

    @Autowired
    AppVersionRepository repository

    def newVer(int ver) {
        def version = new AppVersionDocument()
        version.setVer(ver)
        version.setUrl('http://test.com/test.apk')
        version.setNote("""Still in snapshot version
blah blah blah""")
        version
    }

    @Test
    public void getLatestVer() {
        assert get('/app/latest', HttpStatus.OK).getResponse().getContentLength() == 0

        put('/app/latest', JSONUtil.toJSON(newVer(1)), HttpStatus.OK)
        put('/app/latest', JSONUtil.toJSON(newVer(2)), HttpStatus.OK)
        def result = JSONUtil.parse(get('/app/latest', HttpStatus.OK).getResponse().getContentAsString(), AppVersionDocument.class)
        assert result.getVer() == 2

        assert get('/app/latest?current=2', HttpStatus.OK).getResponse().getContentLength() == 0
        get('/app/latest?current=1', HttpStatus.OK)
    }

    @Test
    public void getCertainVer() {
        assert get('/app/versions/99999', HttpStatus.OK).getResponse().getContentLength() == 0

        put('/app/latest', JSONUtil.toJSON(newVer(1)), HttpStatus.OK)
        put('/app/latest', JSONUtil.toJSON(newVer(2)), HttpStatus.OK)
        def result = JSONUtil.parse(get('/app/latest', HttpStatus.OK).getResponse().getContentAsString(), AppVersionDocument.class)
        assert result.getVer() == 2

        assert get('/app/versions/3', HttpStatus.OK).getResponse().getContentLength() == 0
        get('/app/versions/2', HttpStatus.OK)
        get('/app/versions/1', HttpStatus.OK)
    }

    @After
    public void tearDown() {
        repository.deleteAll()
    }

}
