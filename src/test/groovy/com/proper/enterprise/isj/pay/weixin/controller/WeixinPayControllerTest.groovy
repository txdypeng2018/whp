package com.proper.enterprise.isj.pay.weixin.controller

import com.proper.enterprise.isj.order.repository.OrderRepository
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class WeixinPayControllerTest extends AbstractTest {

    @Autowired
    OrderRepository orderRepository

    @Test
    public void prepay() {
        println 1
//        String outTradeNo = new Date().getTime();
//        post('/pay/weixin/prepayInfo', '{"outTradeNo": ' + outTradeNo + ', "detail":"挂号费", "body": "门诊挂号费用",  "totalFee": 1}', HttpStatus.CREATED)
    }

    @Test
    public void testReceiveWeixinNoticeInfo() throws Exception {
        def count = 30
        count.times { idx ->
            def orderNo = "2016112214104819158269$idx"
            def xml = """
<xml><appid><![CDATA[wxc7825f86d99f3d90]]></appid>
<bank_type><![CDATA[CFT]]></bank_type>
<cash_fee><![CDATA[1]]></cash_fee>
<device_info><![CDATA[WEB]]></device_info>
<fee_type><![CDATA[CNY]]></fee_type>
<is_subscribe><![CDATA[N]]></is_subscribe>
<mch_id><![CDATA[1379027502]]></mch_id>
<nonce_str><![CDATA[XEvuHhlpbwqtfrsS]]></nonce_str>
<openid><![CDATA[otNUJwrZN986eIejGuBbk2Opn-NY]]></openid>
<out_trade_no><![CDATA[${orderNo}]]></out_trade_no>
<result_code><![CDATA[SUCCESS]]></result_code>
<return_code><![CDATA[SUCCESS]]></return_code>
<sign><![CDATA[7929D0D2E30319935D947905776F1D0D]]></sign>
<time_end><![CDATA[20160911234650]]></time_end>
<total_fee>1</total_fee>
<trade_type><![CDATA[APP]]></trade_type>
<transaction_id><![CDATA[4001922001201609113720112951]]></transaction_id>
</xml>
"""
            post('/pay/weixin/noticeInfo',MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON_UTF8,xml,HttpStatus.OK)
        }
        assert orderRepository.count() < count

        waitExecutorDone()
        assert orderRepository.count() == count
    }

}
