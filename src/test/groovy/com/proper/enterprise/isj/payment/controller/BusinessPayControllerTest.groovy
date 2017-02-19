package com.proper.enterprise.isj.payment.controller

import com.proper.enterprise.isj.order.entity.OrderEntity
import com.proper.enterprise.isj.order.repository.OrderRepository
import com.proper.enterprise.isj.proxy.document.RegistrationDocument
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository
import com.proper.enterprise.isj.user.document.UserInfoDocument
import com.proper.enterprise.isj.user.repository.UserInfoRepository
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel
import com.proper.enterprise.platform.core.utils.ConfCenter
import com.proper.enterprise.platform.core.utils.DateUtil
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.core.utils.StringUtil
import com.proper.enterprise.platform.pay.ali.model.AliOrderReq
import com.proper.enterprise.platform.pay.cmb.model.CmbOrderReq
import com.proper.enterprise.platform.test.AbstractTest
import org.apache.commons.lang3.RandomStringUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult

class BusinessPayControllerTest extends AbstractTest {

    @Autowired
    UserInfoRepository userInfoRepository

    @Autowired
    OrderRepository orderRepo

    @Autowired
    RegistrationRepository registrationRepo

    @Test
    public void testAliPrepay() {
        String outTradeNo = DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS").concat(RandomStringUtils.randomNumeric(15))
//        String outTradeNo = "2017020811524029763439"
        saveRegistrationInfo(outTradeNo, String.valueOf(PayChannel.ALIPAY.getCode()))
        AliOrderReq aliReq = new AliOrderReq()

        aliReq.setOutTradeNo(outTradeNo)
        aliReq.setTotalFee("1")
        aliReq.setBody("预约挂号缴费")
        MvcResult result = post('/pay/ali/prepay', JSONUtil.toJSON(aliReq), HttpStatus.CREATED)

        String obj = result.getResponse().getContentAsString()
        Map<String, Object> doc = (Map<String, Object>)JSONUtil.parse(obj, Object.class)

        assert doc.get("resultCode") == "SUCCESS"

        aliReq.setTotalFee("0.01")
        MvcResult resultErr = post('/pay/ali/prepay', JSONUtil.toJSON(aliReq), HttpStatus.CREATED)

        String objErr = resultErr.getResponse().getContentAsString()
        Map<String, Object> docErr = (Map<String, Object>)JSONUtil.parse(objErr, Object.class)

        assert docErr.get("resultCode") == "MONEYERROR"
    }

    @Test
    public void testWechatPrepay() {
        String outTradeNo = DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS").concat(RandomStringUtils.randomNumeric(15))
//        String outTradeNo = "2017020813353386856452"
        saveRegistrationInfo(outTradeNo, String.valueOf(PayChannel.WECHATPAY.getCode()))
        MvcResult result = post('/pay/wechat/prepay', '{"outTradeNo": ' + outTradeNo + ', "body": "当日挂号缴费",  "totalFee": 1}', HttpStatus.CREATED)
        String obj = result.getResponse().getContentAsString()
        Map<String, Object> doc = (Map<String, Object>)JSONUtil.parse(obj, Object.class)

        assert doc.get("resultCode") == "SUCCESS"
        assert StringUtil.isNotNull(String.valueOf(doc.get("prepayid")))

        MvcResult resultErr = post('/pay/wechat/prepay', '{"outTradeNo": ' + outTradeNo + ', "body": "当日挂号缴费",  "totalFee": 0}', HttpStatus.CREATED)
        String objErr = resultErr.getResponse().getContentAsString()
        Map<String, Object> docErr = (Map<String, Object>)JSONUtil.parse(objErr, Object.class)

        assert docErr.get("resultCode") == "MONEYERROR"
    }

    @Test
    public void testCmbPrepay() {
        // mock user
        UserInfoDocument userInfo = this.saveTestUserInfo();
        mockUser(userInfo.getUserId(), "13800000001");
        // cmbprepay
        CmbOrderReq cmbReq = new CmbOrderReq()
        String outTradeNo = DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS").concat(RandomStringUtils.randomNumeric(1))
//        String outTradeNo = "2017020808153683263476"
        saveRegistrationInfo(outTradeNo, String.valueOf(PayChannel.WEB_UNION.getCode()))
        cmbReq.setBillNo(outTradeNo)
        cmbReq.setAmount("1")
        cmbReq.setMerchantPara("诊间缴费")
        cmbReq.setPayUserId("testUser")
        MvcResult result = post('/pay/cmb/prepay', JSONUtil.toJSON(cmbReq), HttpStatus.CREATED)
        String obj = result.getResponse().getContentAsString()
        Map<String, Object> doc = (Map<String, Object>)JSONUtil.parse(obj, Object.class)

        assert doc.get("resultCode") == "SUCCESS"

        cmbReq.setAmount("-1")
        MvcResult resultErr = post('/pay/cmb/prepay', JSONUtil.toJSON(cmbReq), HttpStatus.CREATED)
        String objErr = resultErr.getResponse().getContentAsString()
        Map<String, Object> docErr = (Map<String, Object>)JSONUtil.parse(objErr, Object.class)

        assert docErr.get("resultCode") == "MONEYERROR"
    }

    /**
     * 新增用户进行在线建档 绑定成功
     */
    private UserInfoDocument saveTestUserInfo() {
        UserInfoDocument userInfo = new UserInfoDocument();
        userInfo.setUserId(DateUtil.toTimestamp(new Date(), true));
        userInfo.setName("210100199001010001");
        userInfo.setPhone("13800000001");
        userInfo.setIdCard("210100199001010001");
        return userInfoRepository.save(userInfo);
    }

    /**
     * 保存挂号订单信息
     *
     * @param outTradeNo
     * @param paychannelId
     */
    private void saveRegistrationInfo(String outTradeNo, String paychannelId) {
        // order
        OrderEntity orderInfo = new OrderEntity()
        orderInfo.setFormId("testRegistration")
        orderInfo.setOrderNo(outTradeNo)
        orderInfo.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.unpay"))
        orderInfo.setIsdel(ConfCenter.getInt("isj.pay.isdel.nomarl"))
        orderInfo.setOrderStatus("1")
        orderInfo.setFormClassInstance(RegistrationDocument.class.getName())
        orderRepo.save(orderInfo)
        // RegistrationDocument
        RegistrationDocument registrationDocument = new RegistrationDocument()
        registrationDocument.setNum("S1234567")
        registrationDocument.setId("testRegistration")
        registrationDocument.setPayChannelId(paychannelId)
        registrationDocument.setOrderNum(outTradeNo)
        registrationRepo.save(registrationDocument)
    }
}
