package com.proper.enterprise.isj.pay.weixin.controller

import com.proper.enterprise.platform.test.AbstractTest
import com.proper.enterprise.platform.test.annotation.NoTx
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class WeixinPayControllerTest extends AbstractTest {

    @Test
    public void prepay() {
        println 1
//        String outTradeNo = new Date().getTime();
//        post('/pay/weixin/prepayInfo', '{"outTradeNo": ' + outTradeNo + ', "detail":"挂号费", "body": "门诊挂号费用",  "totalFee": 1}', HttpStatus.CREATED)
    }

    @Test
    @NoTx
    public void testReceiveWeixinNoticeInfo() throws Exception {
        println 1
//        String xml = "<xml><appid><![CDATA[wxc7825f86d99f3d90]]></appid>\n" +
//                "<bank_type><![CDATA[CFT]]></bank_type>\n" +
//                "<cash_fee><![CDATA[1]]></cash_fee>\n" +
//                "<device_info><![CDATA[WEB]]></device_info>\n" +
//                "<fee_type><![CDATA[CNY]]></fee_type>\n" +
//                "<is_subscribe><![CDATA[N]]></is_subscribe>\n" +
//                "<mch_id><![CDATA[1379027502]]></mch_id>\n" +
//                "<nonce_str><![CDATA[XEvuHhlpbwqtfrsS]]></nonce_str>\n" +
//                "<openid><![CDATA[otNUJwrZN986eIejGuBbk2Opn-NY]]></openid>\n" +
//                "<out_trade_no><![CDATA[20160911234633576811334546748476]]></out_trade_no>\n" +
//                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
//                "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
//                "<sign><![CDATA[7929D0D2E30319935D947905776F1D0D]]></sign>\n" +
//                "<time_end><![CDATA[20160911234650]]></time_end>\n" +
//                "<total_fee>1</total_fee>\n" +
//                "<trade_type><![CDATA[APP]]></trade_type>\n" +
//                "<transaction_id><![CDATA[4001922001201609113720112951]]></transaction_id>\n" +
//                "</xml>";
//        post('/pay/weixin/noticeInfo',MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON_UTF8,xml,HttpStatus.CREATED);
    }
}
