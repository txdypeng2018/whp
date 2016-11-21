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
    @NoTx
    public void notice() {
        println 1
       // post('/pay/ali/noticeInfo?sign_type=RSA&sign=GF1Jyd0SwSA2TrrFBV+TObpzylxLYMvLY4diuKjJbhv/EjwCH2yfMDkfv+W7aSrg/V9epuvvPTEkdoFraDWch2HuFhYPqcU2dbetSfIF1ClIUyb8IMrVU0nMty5Y1XL1W1lJzNNpw4lR+Z4ouKA0NB2EWQJMBXaQ1othJ5yoYxw=&body=挂号&buyer_email=markr512@163.com&buyer_id=2088102124299026&discount=0.00&gmt_create=2016-09-12 00:45:38&gmt_payment=2016-09-12 00:45:39&is_total_fee_adjust=N&notify_id=080b83096c49c7351bb9634a140cc4eg5m&notify_time=2016-09-12 00:45:39&notify_type=trade_status_sync&out_trade_no=20160912004515318357019411284993&payment_type=1&price=0.01&quantity=1&seller_email=zfb_sj@sj-hospital.org&seller_id=2088021705112890&subject=费用&total_fee=0.01&trade_no=2016091221001004020265276627&trade_status=TRADE_SUCCESS&use_coupon=N', '', HttpStatus.CREATED)
    }
    
}
