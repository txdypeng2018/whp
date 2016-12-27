package com.proper.enterprise.isj.pay.weixin.service.impl;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.pay.weixin.adapter.SignAdapter;
import com.proper.enterprise.isj.pay.weixin.constants.WeixinConstants;
import com.proper.enterprise.isj.pay.weixin.entity.WeixinEntity;
import com.proper.enterprise.isj.pay.weixin.entity.WeixinRefundEntity;
import com.proper.enterprise.isj.pay.weixin.model.*;
import com.proper.enterprise.isj.pay.weixin.repository.WeixinRefundRepository;
import com.proper.enterprise.isj.pay.weixin.repository.WeixinRepository;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.http.HttpClient;
import com.proper.enterprise.platform.core.utils.http.HttpsClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class WeixinServiceImpl implements WeixinService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinServiceImpl.class);

    @Autowired
    Marshaller marshaller;

    @Autowired
    Map<String, Unmarshaller> unmarshallerMap;

    @Autowired
    WeixinRepository weixinRepo;

    @Autowired
    WeixinRefundRepository weixinRefundRepo;

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RecipeService recipeService;

    @Override
    public Weixin save(Weixin weixin) {
        return weixinRepo.save((WeixinEntity) weixin);
    }

    @Override
    public Weixin findByOutTradeNo(String outTradeNo) {
        return weixinRepo.findByOutTradeNo(outTradeNo);
    }

    @Override
    public WeixinRefund save(WeixinRefund weixinRefund) {
        return weixinRefundRepo.save((WeixinRefundEntity) weixinRefund);
    }

    @Override
    public WeixinRefund findRefundByOutTradeNo(String outTradeNo) {
        return weixinRefundRepo.findByOutTradeNo(outTradeNo);
    }

    /**
     * 微信异步通知处理
     *
     * @param res
     * @return
     */
    @Override
    public boolean saveWeixinNoticeProcess(UnifiedNoticeRes res) throws Exception {
        // 返回结果
        boolean ret = false;
        // 取得订单号
        String orderNo = res.getOutTradeNo();
        // 查询订单
        Order orderInfo = orderService.findByOrderNo(orderNo);
        // 存在订单
        if (orderInfo != null) {
            // 保存微信异步通知信息
            WeixinEntity weixinInfo = getWeixinNoticeInfo(res);
            // 没有处理过订单
            if (orderInfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay")) {
                synchronized (orderInfo.getOrderNo()) {
                    try {
                        if (orderInfo.getFormClassInstance().equals(RegistrationDocument.class.getName())) {
                            PayRegReq payReg = registrationService.convertAppInfo2PayReg(weixinInfo,
                                    orderInfo.getFormId());
                            if (payReg != null) {
                                registrationService.saveUpdateRegistrationAndOrder(payReg);
                            }
                        } else {
                            orderInfo = recipeService.saveUpdateRecipeAndOrder(orderInfo.getOrderNo(),
                                    String.valueOf(PayChannel.WECHATPAY.getCode()), weixinInfo);
                            if (orderInfo == null) {
                                LOGGER.debug("缴费异常,订单号:" + orderNo);
                            } else {
                                orderService.save(orderInfo);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.debug("微信通知接口返回接口异常", e);
                    }
                    
                }
                ret = true;
                // 已经成功处理过的订单
            } else if (orderInfo.getPaymentStatus() == ConfCenter.getInt("isj.pay.paystatus.payed")) {
                ret = true;
            }
            if(weixinInfo!=null){
                Weixin weixin = this.findByOutTradeNo(weixinInfo.getOutTradeNo());
                if (weixin == null) {
                    logAliEntity(weixinInfo);
                    // 保存微信异步通知信息
                    save(weixinInfo);
                }
            }
        }
        return ret;
    }

    /**
     * 保存微信异步通知信息到数据库
     *
     * @param weixinNoticeInfo
     * @return
     */
    private WeixinEntity getWeixinNoticeInfo(UnifiedNoticeRes weixinNoticeInfo) {
        WeixinEntity weixinInfo = new WeixinEntity();
        BeanUtils.copyProperties(weixinNoticeInfo, weixinInfo);
        weixinInfo.setIsdel(ConfCenter.get("isj.pay.isdel.nomarl"));
        return weixinInfo;
    }

    // TODO
    private void logAliEntity(WeixinEntity weixininfo) throws Exception {
        Field[] fields = WeixinEntity.class.getDeclaredFields();
        Set<String> set = new HashSet<>();
        for (Field field : fields) {
            if (field != null && !field.getName().equals("$jacocoData")) {
                set.add(field.getName());
            }
        }
        Object value;
        for (String fieldName : set) {
            value = WeixinEntity.class.getMethod("get" + StringUtil.capitalize(fieldName)).invoke(weixininfo);
            LOGGER.debug("AliEntity[" + fieldName + "]:" + String.valueOf(value));
        }
    }

    /**
     * 微信支付退款处理
     *
     * @param wxRefReq
     * @return
     * @throws Exception
     */
    public PayResultRes saveWeixinRefund(WeixinRefundReq wxRefReq) throws Exception {
        // 返回值
        PayResultRes ret = new PayResultRes();
        // 随机字符串
        wxRefReq.setNonceStr(RandomStringUtils.randomAlphabetic(WeixinConstants.RANDOM_LEN));
        // 退单号 采用和支付一样的生成方式:精确到毫秒的时间 + 15位随机数字
        // StringBuilder sbOutRefundNo = new StringBuilder();
        // wxRefReq.setOutRefundNo(sbOutRefundNo.append(DateUtil.toString(new
        // Date(), "yyyyMMddHHmmssSSS"))
        // .append(RandomStringUtils.randomNumeric(15)).toString());
        // 签名
        SignAdapter signAdapter = new SignAdapter();
        String retSign = signAdapter.marshalObject(wxRefReq, WeixinRefundReq.class);
        wxRefReq.setSign(retSign);
        // 将请求参数转换为xml格式
        StringWriter writer = new StringWriter();
        marshaller.marshal(wxRefReq, new StreamResult(writer));
        String requestXML = writer.toString();
        LOGGER.debug("requestXML:" + requestXML);
        // 使用证书
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("weixincert/cert.p12");
        // 使用httsClient通过证书请求微信退款
        HttpsClient hc = HttpsClient.withKeyStore(inputStream, "PKCS12", WeixinConstants.WEIXIN_PAY_MCH_ID);
        ResponseEntity<byte[]> response = hc.post(WeixinConstants.REFUND_URL, MediaType.APPLICATION_FORM_URLENCODED,
                requestXML);
        WeixinRefundRes res = (WeixinRefundRes) unmarshallerMap.get("unmarshallWeixinRefund")
                .unmarshal(new StreamSource(new ByteArrayInputStream(response.getBody())));
        // 输出LOG
        logEntity(res, WeixinRefundRes.class);
        // 以下字段在return_code为SUCCESS的时候有返回
        if ("SUCCESS".equalsIgnoreCase(res.getReturnCode()) && "SUCCESS".equalsIgnoreCase(res.getResultCode())) {
            // TODO 根据业务需求获取相应参数
            ret.setResultCode("0");
            ret.setResultMsg(res.getReturnMsg());
            ret.setPrepayid(res.getTransactionId());
            WeixinRefund refund = this.findRefundByOutTradeNo(res.getOutTradeNo());
            if (refund == null) {
                WeixinRefundEntity weixinRefundInfo = new WeixinRefundEntity();
                BeanUtils.copyProperties(res, weixinRefundInfo);
                logEntity(weixinRefundInfo, WeixinRefundEntity.class);
                this.save(weixinRefundInfo);
            }
//            // 获取Entity信息
//            WeixinRefundEntity weixinRefundInfo = new WeixinRefundEntity();
//            BeanUtils.copyProperties(res, weixinRefundInfo);
//            logEntity(weixinRefundInfo, WeixinRefundEntity.class);
//
//            this.save(weixinRefundInfo);

        } else if ("SUCCESS".equalsIgnoreCase(res.getReturnCode()) && "FAIL".equalsIgnoreCase(res.getResultCode())) {
            // TODO 失败时不返回订单信息,仅返回支付状态信息
            ret.setResultCode("-1");
            ret.setResultMsg(res.getReturnMsg());
        }

        return ret;
    }

    @Override
    public WeixinPayQueryRes getWeixinPayQueryRes(String outTradeNo) {
        WeixinQueryReq req = new WeixinQueryReq();
        req.setOutTradeNo(outTradeNo);
        req.setNonceStr(CenterFunctionUtils.createRegOrOrderNo(2));
        SignAdapter signAdapter = new SignAdapter();
        WeixinPayQueryRes queryRes = null;
        try {
            String sign = signAdapter.marshalObject(req, WeixinQueryReq.class);
            req.setSign(sign);
            StringWriter writer = new StringWriter();
            marshaller.marshal(req, new StreamResult(writer));
            String requestXML = writer.toString();
            LOGGER.debug("requestXML:" + requestXML);
            ResponseEntity<byte[]> response = HttpClient.post(WeixinConstants.CHECK_ORDER_URL,
                    MediaType.APPLICATION_FORM_URLENCODED, requestXML);
            queryRes = (WeixinPayQueryRes) unmarshallerMap.get("unmarshallWeixinPayQueryRes")
                    .unmarshal(new StreamSource(new ByteArrayInputStream(response.getBody())));
            if (!queryRes.getReturnCode().equals("SUCCESS") || !queryRes.getResultCode().equals("SUCCESS")) {
                queryRes = null;
            }
        } catch (Exception e) {
            LOGGER.debug("微信查询接口返回接口异常", e);
        }

        return queryRes;
    }

    @Override
    public WeixinRefundRes getWeixinRefundRes(String outTradeNo) {
        WeixinRefundRes res = null;
        ResponseEntity<byte[]> response = getWeixinResponseEntity(outTradeNo);
        if (response != null) {
            try {
                res = (WeixinRefundRes) unmarshallerMap.get("unmarshallWeixinRefund")
                        .unmarshal(new StreamSource(new ByteArrayInputStream(response.getBody())));
                if (!res.getReturnCode().equals("SUCCESS") || !res.getResultCode().equals("SUCCESS")) {
                    res = null;
                }
            } catch (IOException e) {
                LOGGER.debug("微信退费接口返回接口异常", e);
            }
        }
        return res;
    }

    @Override
    public boolean isValid(UnifiedNoticeRes noticeRes) {
        String sign;
        try {
            sign = new SignAdapter().marshalObject(noticeRes, UnifiedNoticeRes.class);
            LOGGER.debug("notice_sign: {}; verify_sign: {}", noticeRes.getSign(), sign);
            return noticeRes.getSign().equals(sign);
        } catch (Exception e) {
            LOGGER.debug("Check validation of sign occurs error!", e);
            return false;
        }
    }

    @Override
    public ResponseEntity<byte[]> getWeixinResponseEntity(String outTradeNo) {
        WeixinQueryReq req = new WeixinQueryReq();
        req.setOutRefundNo(outTradeNo);
        req.setNonceStr(CenterFunctionUtils.createRegOrOrderNo(2));
        SignAdapter signAdapter = new SignAdapter();

        ResponseEntity<byte[]> response = null;
        try {
            String sign = signAdapter.marshalObject(req, WeixinQueryReq.class);
            req.setSign(sign);
            StringWriter writer = new StringWriter();
            marshaller.marshal(req, new StreamResult(writer));
            String requestXML = writer.toString();
            LOGGER.debug("requestXML:" + requestXML);
            response = HttpClient.post(WeixinConstants.CHECK_REFUND_URL,
                    MediaType.APPLICATION_FORM_URLENCODED, requestXML);

        } catch (Exception e) {
            LOGGER.debug("微信公共接口返回接口异常", e);
        }
        return response;
    }

    /**
     * 输出BeanLog
     *
     * @param t
     * @param clz
     * @param <T>
     * @throws Exception
     */
    private <T> void logEntity(T t, Class<T> clz) throws Exception {
        Field[] fields = clz.getDeclaredFields();
        Set<String> set = new HashSet<>();
        for (Field field : fields) {
            if (field != null) {
                set.add(field.getName());
            }
        }
        Object value;
        for (String fieldName : set) {
            if (!StringUtil.startsWith(fieldName, "$")) {
                value = clz.getMethod("get" + StringUtil.capitalize(fieldName)).invoke(t);
                LOGGER.debug(t.getClass().getName() + "[" + fieldName + "]:" + String.valueOf(value));
            }
        }
    }
}
