package com.proper.enterprise.isj.user.document
import com.proper.enterprise.isj.user.repository.UserInfoRepository
import com.proper.enterprise.platform.core.mongo.dao.MongoDAO
import com.proper.enterprise.platform.test.AbstractTest
import org.apache.commons.lang3.RandomStringUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class UserInfoDocumentTest extends AbstractTest {

    @Autowired
    UserInfoRepository repository

    @Autowired
    MongoDAO dao

    @Test
    public void secretData() {
        def collection = 'user_info'
        def uname = '用户名'
        def phone = '13000000000'
        def idcard = '210101101010101010'

        def doc = new UserInfoDocument()
        doc.setName(uname)
        doc.setPhone(phone)
        doc.setIdCard(idcard)
        doc.setUserId(RandomStringUtils.randomAscii(16))
        doc = repository.save(doc)

        assert doc.getName() == uname
        assert doc.getPhone() == phone
        assert doc.getIdCard() == idcard

        def newdoc = dao.queryById(collection, doc.getId())
        assert newdoc.get('name') > '' && newdoc.get('name') != uname
        assert newdoc.get('phone') > '' && newdoc.get('phone') != phone
        assert newdoc.get('idCard') > '' && newdoc.get('idCard') != idcard

        dao.deleteById(collection, doc.getId())
    }

}
