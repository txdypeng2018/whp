package com.proper.enterprise.isj.pay.ali.controller
import com.proper.enterprise.isj.order.repository.OrderRepository
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class AliPayControllerTest extends AbstractTest {

    @Autowired
    OrderRepository orderRepository

    @Test
    public void prepay() {
        println 1
        //post('/pay/ali/prepayInfo', '{"orderNo":"hOpTChleZacRtNEpFVQEErZTlyNzwZyU", "subject": "挂号费", "body": "门诊挂号费用", "totalFee": "0.01"}', HttpStatus.CREATED)
    }

    @Test
    public void noticeInfo() {
        def count = 10
        count.times { idx ->
            def orderNo = "2016112214104819158269$idx"
            post("/pay/ali/noticeInfo?sign_type=RSA&sign=ZTdx&body=诊间缴费&buyer_email=markr512@163.com&buyer_id=2088102124299026&discount=0.00&gmt_create=2016-11-22 14:10:59&gmt_payment=2016-11-22 14:10:59&is_total_fee_adjust=N&notify_id=56a10b335eaea55a1d4e7e8c4928df8g5m&notify_time=2016-11-22 14:11:00&notify_type=trade_status_sync&out_trade_no=${orderNo}&payment_type=1&price=1.00&quantity=1&seller_email=zfb_sj@sj-hospital.org&seller_id=2088021705112890&subject=诊间缴费&total_fee=1.00&trade_no=2016112221001004020279287509&trade_status=TRADE_SUCCESS&use_coupon=N", '', HttpStatus.CREATED)
        }
        assert orderRepository.count() < count

        waitExecutorDone()
        assert orderRepository.count() == count
    }
    
}
