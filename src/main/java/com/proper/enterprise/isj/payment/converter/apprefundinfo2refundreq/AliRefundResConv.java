package com.proper.enterprise.isj.payment.converter.apprefundinfo2refundreq;

import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AliRefundResConv extends AbstractAppRefundInfo2RefundReqConv<AliRefundRes> {

    @Override
    protected void fillTarget(AliRefundRes source, RefundReq target) {
        target.setRefundSerialNum(source.getTradeNo());
        BigDecimal bigDecimal = new BigDecimal(source.getRefundFee());
        bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
        target.setTotalFee(bigDecimal.intValue());
        target.setRefundFee(bigDecimal.intValue());
        target.setRefundDate(source.getGmtRefundPay().split(" ")[0]);
        target.setRefundTime(source.getGmtRefundPay().split(" ")[1]);
        if (StringUtil.isNotEmpty(source.getMsg())) {
            target.setRefundResCode(source.getMsg());
        } else {
            target.setRefundResCode("");
        }
    }

}
