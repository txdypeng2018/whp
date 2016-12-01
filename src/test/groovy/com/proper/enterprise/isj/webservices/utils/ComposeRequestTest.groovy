package com.proper.enterprise.isj.webservices.utils
import com.proper.enterprise.isj.webservices.model.req.ReqModel
import com.proper.enterprise.platform.core.PEPConstants
import com.proper.enterprise.platform.core.utils.ConfCenter
import com.proper.enterprise.platform.core.utils.cipher.AES
import com.proper.enterprise.platform.core.utils.digest.MD5
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.oxm.Marshaller

import javax.xml.transform.stream.StreamResult

class ComposeRequestTest extends AbstractTest {

    @Autowired
    Marshaller marshaller

    @Test
    public void marshallReq() {
        def funCode = '1111'
        def userId = ConfCenter.get('isj.his.userId')
        def req = [HOST_ID: '123', IP: '456']

        def writer = new StringWriter()
        def m = new ReqModel()
        m.setFunCode(funCode)
        m.setUserId(userId)
        m.setReq(req)
        marshaller.marshal(m, new StreamResult(writer))

        def xml = new XmlParser().parseText(writer.toString())
        assert xml.FUN_CODE.text() == "<![CDATA[$funCode]]>"
        assert xml.USER_ID.text() == "<![CDATA[$userId]]>"
        assert xml.SIGN_TYPE.text() == "<![CDATA[MD5]]>"

        def reqEnc = '<REQ>'
        req.each { key, value ->
            reqEnc += "<$key><![CDATA[$value]]></$key>"
        }
        reqEnc += '</REQ>'
        AES aes = new AES(
                ConfCenter.get("isj.his.aes.mode"),
                ConfCenter.get("isj.his.aes.padding"),
                ConfCenter.get("isj.his.aes.key"));
        reqEnc = new String(aes.encrypt(reqEnc.getBytes(PEPConstants.DEFAULT_CHARSET)), PEPConstants.DEFAULT_CHARSET);
        assert xml.REQ_ENCRYPTED.text() == "<![CDATA[" + reqEnc + "]]>"

        def sign = "FUN_CODE=$funCode&REQ_ENCRYPTED=$reqEnc&USER_ID=$userId&KEY=${ConfCenter.get('isj.his.aes.key')}"
        assert xml.SIGN.text() == "<![CDATA[${MD5.md5Hex(sign).toUpperCase()}]]>"
    }

}
