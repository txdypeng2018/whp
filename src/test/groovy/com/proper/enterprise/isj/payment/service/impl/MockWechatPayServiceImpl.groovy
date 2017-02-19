package com.proper.enterprise.isj.payment.service.impl

import com.proper.enterprise.platform.api.pay.enums.PayResType
import com.proper.enterprise.platform.api.pay.model.PayResultRes
import com.proper.enterprise.platform.api.pay.model.PrepayReq
import com.proper.enterprise.platform.pay.wechat.model.WechatNoticeRes
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes
import com.proper.enterprise.platform.pay.wechat.model.WechatPayResultRes
import com.proper.enterprise.platform.pay.wechat.service.impl.WechatPayServiceImpl
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class MockWechatPayServiceImpl extends WechatPayServiceImpl {

    @Override
    boolean isValid(WechatNoticeRes noticeRes) {
        true
    }

    @Override
    def <T> T queryPay(String outTradeNo) {
        def res = new WechatPayQueryRes()
        (T) res
    }

    @Override
    def <T extends PayResultRes> T savePrepay(PrepayReq req) {
        def res = new WechatPayResultRes()
        res.setResultCode(req.totalFee > '0' ? PayResType.SUCCESS : PayResType.MONEYERROR)
        res.prepayid = 'prepay id'
        (T) res
    }

}
