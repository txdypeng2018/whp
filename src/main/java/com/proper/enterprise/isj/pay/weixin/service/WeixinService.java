package com.proper.enterprise.isj.pay.weixin.service;

import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.pay.weixin.model.*;
import org.springframework.http.ResponseEntity;

/**
 * 微信支付服务接口
 *
 */
public interface WeixinService {

    Weixin save(Weixin weixin);

    Weixin findByOutTradeNo(String outTradeNo);

    WeixinRefund save(WeixinRefund weixinRefund);

    WeixinRefund findRefundByOutTradeNo(String outTradeNo);

    boolean saveWeixinNoticeProcess(UnifiedNoticeRes res) throws Exception;

    PayResultRes saveWeixinRefund(WeixinRefundReq wxRefReq) throws Exception;

    WeixinPayQueryRes getWeixinPayQueryRes(String outTradeNo);

    ResponseEntity<byte[]> getWeixinResponseEntity(String outTradeNo);

    WeixinRefundRes getWeixinRefundRes(String outTradeNo);

}
