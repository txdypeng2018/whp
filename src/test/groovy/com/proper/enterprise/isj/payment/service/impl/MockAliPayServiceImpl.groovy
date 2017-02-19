package com.proper.enterprise.isj.payment.service.impl

import com.proper.enterprise.platform.api.pay.enums.PayResType
import com.proper.enterprise.platform.api.pay.model.OrderReq
import com.proper.enterprise.platform.api.pay.model.PayResultRes
import com.proper.enterprise.platform.core.PEPConstants
import com.proper.enterprise.platform.core.utils.ConfCenter
import com.proper.enterprise.platform.core.utils.DateUtil
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.core.utils.http.HttpClient
import com.proper.enterprise.platform.pay.ali.constants.AliConstants
import com.proper.enterprise.platform.pay.ali.model.AliOrderReq
import com.proper.enterprise.platform.pay.ali.model.AliPayResultRes
import com.proper.enterprise.platform.pay.ali.service.AliPayResService
import com.proper.enterprise.platform.pay.ali.service.impl.AliPayServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Primary
@Service
class MockAliPayServiceImpl extends AliPayServiceImpl {

    @Autowired
    AliPayResService aliPayResService;

    @Override
    boolean verify(Map<String, String> params) throws Exception {
        true
    }

    @Override
    protected <T extends PayResultRes, R extends OrderReq> T savePrepayImpl(R req) throws Exception {
        AliPayResultRes resObj = new AliPayResultRes();
        AliOrderReq uoReq = (AliOrderReq)req;
        uoReq.setNotifyUrl(AliConstants.ALI_PAY_NOTICE_URL);
        String orderInfo = getOrderInfo(uoReq, AliOrderReq.class);
        String privateKey = AliConstants.ALI_PAY_RSA_PRIVATE;
        String sign = orderInfo;
        sign = URLEncoder.encode(sign, PEPConstants.DEFAULT_CHARSET.name());
        final StringBuilder payInfo = new StringBuilder();
        payInfo.append(orderInfo).append("&sign=\"").append(sign).append("\"&sign_type=\"RSA\"");
        resObj.setResultCode(PayResType.SUCCESS);
        resObj.setResultMsg("SUCCESS");
        resObj.setPayInfo(payInfo.toString());
        resObj.setSign(privateKey);
        return (T)resObj;
    }

    @Override
    protected Object getAliRequestRes(Object res, Map<String, String> bizContentMap, String method, String responseKey) {
        String appId = AliConstants.ALI_PAY_APPID;
        String tradeUrl = ConfCenter.get("pay.ali.tradeUrl");
        StringBuilder reqUrl = new StringBuilder();
        StringBuilder paramStr = new StringBuilder();
        reqUrl = reqUrl.append(tradeUrl);
        paramStr.append("app_id=").append(appId);
        paramStr.append("&biz_content=");
        paramStr.append(JSONUtil.toJSON(bizContentMap));
        paramStr.append("&charset=");
        paramStr.append(PEPConstants.DEFAULT_CHARSET.name());
        paramStr.append("&method=");
        paramStr.append(method);
        paramStr.append("&sign_type=RSA");
        paramStr.append("&timestamp=").append(DateUtil.toTimestamp(new Date()));
        paramStr.append("&version=1.0");
        String sign = paramStr.toString();
        sign = URLEncoder.encode(sign, PEPConstants.DEFAULT_CHARSET.name());
        paramStr.append("&sign=").append(sign);
        reqUrl = reqUrl.append("?").append(paramStr);
        ResponseEntity<byte[]> entity = HttpClient.get(reqUrl.toString());
        String strRead = new String(entity.getBody(), PEPConstants.DEFAULT_CHARSET.name());
        res = aliPayResService.convertMap2AliPayRes(strRead, responseKey, res);
        return res;
    }

}
