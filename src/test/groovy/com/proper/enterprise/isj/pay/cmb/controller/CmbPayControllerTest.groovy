package com.proper.enterprise.isj.pay.cmb.controller

import com.proper.enterprise.isj.pay.cmb.document.CmbProtocolDocument
import com.proper.enterprise.isj.pay.cmb.model.UnifiedOrderReq
import com.proper.enterprise.isj.pay.cmb.repository.CmbProtocolRepository
import com.proper.enterprise.isj.pay.cmb.service.CmbService
import com.proper.enterprise.isj.user.repository.UserInfoRepository
import com.proper.enterprise.isj.user.service.UserInfoPublicServiceTest
import com.proper.enterprise.isj.user.service.UserInfoService
import com.proper.enterprise.platform.api.auth.model.User
import com.proper.enterprise.platform.auth.common.repository.UserRepository
import com.proper.enterprise.platform.core.utils.ConfCenter
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult

/**
 * 一网通Controller测试类
 */
class CmbPayControllerTest extends AbstractTest {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserInfoPublicServiceTest userInfoPublicServiceTest;

    @Autowired
    CmbService cmbService;

    @Autowired
    CmbProtocolRepository cmbRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Test
    public void prepay() {
        userRepo.deleteAll();
        userInfoRepository.deleteAll();
        User user = userInfoPublicServiceTest.saveUser();
        String token = userInfoService.userLogin(user);
        mockRequest.addHeader("Authorization", token);
        UnifiedOrderReq uoReq = new UnifiedOrderReq();
        uoReq.setBillNo("1234567890");
        uoReq.setAmount("0.01");
        MvcResult result = post("/pay/cmb/prepayInfo", JSONUtil.toJSON(uoReq), HttpStatus.CREATED);
        String obj = result.getResponse().getContentAsString();
        Map<String, Object> doc = JSONUtil.parse(obj, Object.class);
        assert doc.get("resultCode") == "0"
        assert doc.get("cmbBillNo").toString().length() == 10
        assert doc.get("pay_info") != null
    }

    @Test
    public void noticeProtocolInfo() {
        cmbRepo.deleteAll();
        CmbProtocolDocument userProtocolInfo = new CmbProtocolDocument();
        userProtocolInfo.setUserId("57ee2736ae65e2531aad70fa");
        userProtocolInfo.setSign(ConfCenter.get("isj.pay.cmb.protocolResFail"));
        cmbService.saveUserProtocolInfo(userProtocolInfo);
        post('/pay/cmb/noticeProtocolInfo?RequestData={"NTBNBR":"P0015244","TRSCOD":"BKQY","DATLEN":"686","COMMID":"121Y612010001068xRGGZNFLKSMWXQIUNZMOCHB","BUSDAT":"PHhtbD48Y3VzdF9hcmdubz4yMDE2MTIwMTE2MTQyMzQ5MTAyMjwvY3VzdF9hcmdubz48cmVzcGNvZD5DTUJNQjk5PC9yZXNwY29kPjxyZXNwbXNnPuetvue9suWNj+iuruaIkOWKnzwvcmVzcG1zZz48bm90aWNlcGFyYT5wbm89MjAxNjEyMDExNjE0MjM0OTEwMjJ8dXNlcmlkPTU3ZWUyNzM2YWU2NWUyNTMxYWFkNzBmYTwvbm90aWNlcGFyYT48Y3VzdF9uby8+PGN1c3RfcGlkdHk+MTwvY3VzdF9waWR0eT48Y3VzdF9vcGVuX2RfcGF5Pk48L2N1c3Rfb3Blbl9kX3BheT48Y3VzdF9waWRfdj4xNzc0Njg1MjU0MzAyNTQ1NjcwMzA3NzUxNzgwODU8L2N1c3RfcGlkX3Y+PC94bWw+","SIGTIM":"201612011614550360","SIGDAT":"JoAABjRU+yMRHZ7vUZgWwHuykp6jyU3z1rHqg6JFeKKawY2+BAP/mMZ8dETZNly6GoyYuiDXG+fSt/oiuBnqBJIAaWKovg8lebe4qOUR11nElXbC3ZHml0fZetsM3RfTqwTbXKXIpfh1d8g0AZCXdoGGpBmRYF4mrLfVUR1du1Q="}', '', HttpStatus.OK)
    }

    @Test
    public void noticePayInfo() {
        get("/pay/cmb/noticePayInfo?Succeed=Y&CoNo=000062&BillNo=7253638968&Amount=0.01&Date=20161201&MerchantPara=pno=20161201161423491022|userid=57ee2736ae65e2531aad70fa&Msg=00240000622016120116320165000000000020&Signature=85|43|105|137|32|31|227|23|199|171|181|183|148|207|191|96|207|12|22|128|229|187|215|238|158|145|70|128|27|64|53|5|180|174|217|159|84|159|103|251|223|64|47|197|103|109|199|71|182|41|67|250|141|170|90|66|113|83|156|168|239|225|186|232|", HttpStatus.OK);
    }
}
