package com.proper.enterprise.isj.payment.converter.apprefundinfo2refundreq;

import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CmbRefundNoDupResConv extends AbstractAppRefundInfo2RefundReqConv<CmbRefundNoDupRes> {

    @Override
    protected void fillTarget(CmbRefundNoDupRes source, RefundReq target) {
        // 银行流水号
        target.setRefundSerialNum(source.getBody().getBankSeqNo());
        BigDecimal bigDecimal = new BigDecimal(source.getBody().getAmount());
        bigDecimal = bigDecimal.multiply(new BigDecimal("100"));
        target.setTotalFee(bigDecimal.intValue());
        target.setRefundFee(bigDecimal.intValue());
        // 日期
        String date = DateUtil.toString(DateUtil.toDate(source.getBody().getDate(), "yyyyMMdd"), "yyyy-MM-dd");
        // 时间
        String time = DateUtil.toString(DateUtil.toDate(source.getBody().getTime(), "HHmmss"), "HH:mm:ss");
        target.setRefundDate(date);
        target.setRefundTime(time);
        target.setRefundResCode(source.getHead().getErrMsg());

    }

}
