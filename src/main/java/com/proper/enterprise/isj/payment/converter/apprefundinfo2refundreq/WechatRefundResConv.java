package com.proper.enterprise.isj.payment.converter.apprefundinfo2refundreq;

import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WechatRefundResConv extends AbstractAppRefundInfo2RefundReqConv<WechatRefundRes> {
    @Override
    protected void fillTarget(WechatRefundRes source, RefundReq target) {
        target.setRefundSerialNum(source.getTransactionId());
        target.setTotalFee(Integer.parseInt(source.getTotalFee()));
        target.setRefundFee(Integer.parseInt(source.getTotalFee()));
        target.setRefundDate(DateUtil.toDateString(new Date()));
        target.setRefundTime(DateUtil.toTimestamp(new Date(), false).split(" ")[1]);
        if (StringUtil.isNotEmpty(source.getNonceStr())) {
            target.setRefundResCode(source.getNonceStr());
        } else {
            target.setRefundResCode("");
        }
    }
}
