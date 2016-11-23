package com.proper.enterprise.isj.pay.ali.controller

import com.proper.enterprise.platform.test.AbstractTest
import com.proper.enterprise.platform.test.annotation.NoTx
import org.junit.Test
import org.springframework.http.HttpStatus

class AliPayControllerTest extends AbstractTest {

    @Test
    public void prepay() {
        println 1
        //post('/pay/ali/prepayInfo', '{"orderNo":"hOpTChleZacRtNEpFVQEErZTlyNzwZyU", "subject": "挂号费", "body": "门诊挂号费用", "totalFee": "0.01"}', HttpStatus.CREATED)
    }

    @Test
    //@Sql
    //@NoTx
    public void notice() {
        println 1
        //post('/pay/ali/noticeInfo?sign_type=RSA&sign=ZTdatdofVuuavFVvZv7WIQdjDI5s4vcycR0gBCVu/UstfiZWUbJEC0PF5BymaWRuwycVrmnpQT4KJmj6gdGeSOBPhkmEaKNGF5andDxVbvicotID2WY8ZzD362SlzcBw0tM9ADLK6hjn65Gie8P9gqfNuWN8j5XnbCXA7qPACmQ=&body=诊间缴费&buyer_email=markr512@163.com&buyer_id=2088102124299026&discount=0.00&gmt_create=2016-11-22 14:10:59&gmt_payment=2016-11-22 14:10:59&is_total_fee_adjust=N&notify_id=56a10b335eaea55a1d4e7e8c4928df8g5m&notify_time=2016-11-22 14:11:00&notify_type=trade_status_sync&out_trade_no=2016112214104819158269&payment_type=1&price=1.00&quantity=1&seller_email=zfb_sj@sj-hospital.org&seller_id=2088021705112890&subject=诊间缴费&total_fee=1.00&trade_no=2016112221001004020279287509&trade_status=TRADE_SUCCESS&use_coupon=N', '', HttpStatus.CREATED)
    }
    
}
