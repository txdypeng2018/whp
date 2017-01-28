package com.proper.enterprise.isj.pay.weixin.controller;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.pay.weixin.adapter.SignAdapter;
import com.proper.enterprise.isj.pay.weixin.constants.WeixinConstants;
import com.proper.enterprise.isj.pay.weixin.model.*;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.tasks.WeixinPayNotice2BusinessTask;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.http.HttpClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(path = "/pay/weixin")
public class WeixinPayController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinPayController.class);

    @Autowired
    Marshaller marshaller;

    @Autowired
    Map<String, Unmarshaller> unmarshallerMap;

    @Autowired
    WeixinService weixinService;

    @Autowired
    OrderService orderService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    private WeixinPayNotice2BusinessTask dealPayNotice2BusinessTask;

    /**
     * 获取微信预支付ID信息
     *
     * @param uoReq
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/prepayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> getWXPrepayInfo(@RequestBody UnifiedOrderReq uoReq, HttpServletRequest request)
            throws Exception {
        ResponseEntity<PayResultRes> resObj1 = getOrderCanPayTime(uoReq);
        if (resObj1 != null) {
            return resObj1;
        }
        
        uoReq.setNonceStr(RandomStringUtils.randomAlphabetic(WeixinConstants.RANDOM_LEN));
        uoReq.setSpbillCreateIp(request.getRemoteAddr());

        StringWriter writer = new StringWriter();
        marshaller.marshal(uoReq, new StreamResult(writer));
        String requestXML = writer.toString();

        LOGGER.debug("requestXML:" + requestXML);

        ResponseEntity<byte[]> response = HttpClient.post(WeixinConstants.UNIFIED_ORDER_URL,
                MediaType.APPLICATION_FORM_URLENCODED, requestXML);
        UnifiedOrderRes res = (UnifiedOrderRes) unmarshallerMap.get("unmarshallUnifiedOrder")
                .unmarshal(new StreamSource(new ByteArrayInputStream(response.getBody())));

        PayResultRes resObj = new PayResultRes();
        WeixinPay payObj = new WeixinPay();

        LOGGER.debug("return_msg:" + res.getReturnMsg());

        // 以下字段在return_code为SUCCESS的时候有返回
        if ("SUCCESS".equals(res.getReturnCode()) && "SUCCESS".equals(res.getResultCode())) {
            LOGGER.debug("result_code:SUCCESS");
            // 返回给请求客户端处理结果
            resObj.setResultCode("0");
            // 返回给请求客户端处理结果消息
            resObj.setResultMsg("SUCCESS");

            // 设置签名参数
            // 预支付Id
            payObj.setPrepayid(res.getPrepayId());
            // 随机字符串
            payObj.setNoncestr(RandomStringUtils.randomAlphabetic(WeixinConstants.RANDOM_LEN));
            // 时间戳
            payObj.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
            // 进行签名
            SignAdapter signAdapter = new SignAdapter();
            String retSign = signAdapter.marshalObject(payObj, WeixinPay.class);

            // -------------返回给客户端调用支付接口需要的参数-----------------------
            // 应用ID
            resObj.setAppid(res.getAppId());
            // 商户号
            resObj.setPartnerid(res.getMchId());
            // 预支付交易会话ID
            resObj.setPrepayid(payObj.getPrepayid());
            // 扩展字段
            resObj.setWxpackage(payObj.getPapackage());
            // 随机字符串
            resObj.setNoncestr(payObj.getNoncestr());
            // 时间戳
            resObj.setTimestamp(payObj.getTimestamp());
            // 签名
            resObj.setSign(retSign);

            // 获取预支付ID信息失败
            try {
                Order order = orderService.findByOrderNo(uoReq.getOutTradeNo());
                if (order != null) {
                    if (order.getFormClassInstance().equals(RecipeOrderDocument.class.getName())) {
                        RecipeOrderDocument recipe = recipeService
                                .getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
                        if (recipe == null) {
                            resObj.setResultCode("-1");
                            resObj.setResultMsg(CenterFunctionUtils.ORDER_NON_RECIPE_ERR);
                        } else {
                            boolean flag = recipeService.checkRecipeAmount(uoReq.getOutTradeNo(),
                                    String.valueOf(uoReq.getTotalFee()), PayChannel.WECHATPAY);
                            recipe = recipeService.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
                            if (!flag || (StringUtil.isEmpty(recipe.getRecipeNonPaidDetail().getPayChannelId()))) {
                                resObj.setResultCode("-1");
                                resObj.setResultMsg(CenterFunctionUtils.ORDER_DIFF_RECIPE_ERR);
                            }
                        }
                    } else {
                        RegistrationDocument reg = registrationService.getRegistrationDocumentById(order.getFormId());
                        if (reg != null) {
                            String payWay = reg.getPayChannelId();
                            boolean paidFlag = orderService.checkOrderIsPay(payWay, reg.getOrderNum());
                            if (reg.getRegistrationOrderReq() != null
                                    && StringUtil.isNotEmpty(reg.getRegistrationOrderReq().getPayChannelId())) {
                                paidFlag = true;
                            }
                            if (!paidFlag) {
                                reg.setPayChannelId(String.valueOf(PayChannel.WECHATPAY.getCode()));
                                registrationService.saveRegistrationDocument(reg);
                                order.setPayWay(String.valueOf(PayChannel.WECHATPAY.getCode()));
                                orderService.save(order);
                            } else {
                                resObj.setResultCode("-1");
                                resObj.setResultMsg(CenterFunctionUtils.ORDER_ALREADY_PAID_ERR);
                            }
                        } else {
                            resObj.setResultCode("-1");
                            resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
                        }
                    }
                } else {
                    resObj.setResultCode("-1");
                    resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
                }
            } catch (Exception e) {
                LOGGER.debug("微信预支付异常", e);
                resObj.setResultCode("-1");
                resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
            }

        } else {
            // 返回给请求客户端处理结果
            resObj.setResultCode("-1");
            // 返回给请求客户端处理结果消息
            resObj.setResultMsg("微信预支付校验出现异常");
        }
        return responseOfPost(resObj);
    }


    /**
     * 设置订单有效时间
     * @param uoReq
     * @return
     */
    private ResponseEntity<PayResultRes> getOrderCanPayTime(@RequestBody UnifiedOrderReq uoReq) {
        PayResultRes resObj = new PayResultRes();
        Order order = orderService.findByOrderNo(uoReq.getOutTradeNo());
        if (order == null) {
            resObj.setResultCode("-1");
            resObj.setResultMsg(CenterFunctionUtils.ORDER_NON_DATA_ERR);
            return responseOfPost(resObj);
        }
        Date cTime = DateUtil.toDate(order.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.setTime(cTime);
        cal.add(Calendar.MINUTE, CenterFunctionUtils.ORDER_COUNTDOWN);
        uoReq.setTimeStart(DateUtil.toString(cTime, "yyyyMMddHHmmss"));
        uoReq.setTimeExpire(DateUtil.toString(cal.getTime(), "yyyyMMddHHmmss"));
        if (cal.getTime().compareTo(new Date()) < 0) {
            resObj.setResultCode("-1");
            resObj.setResultMsg(CenterFunctionUtils.ORDER_OVERTIME_INVALID);
            return responseOfPost(resObj);
        }
        return null;
    }

    /**
     * 接收微信异步通知结果
     *
     * @param request
     * @throws Exception
     */
    @AuthcIgnore
    @PostMapping(value = "/noticeInfo")
    public void receiveWeixinNoticeInfo(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        LOGGER.debug("-------------微信异步通知---------------");
        boolean ret = false;

        request.setCharacterEncoding("UTF-8");
        InputStream inStream = request.getInputStream();
        UnifiedNoticeRes noticeRes = (UnifiedNoticeRes) unmarshallerMap.get("unmarshallUnifiedNotice")
                .unmarshal(new StreamSource(inStream));
        inStream.close();

        // 进行签名验证操作
        if (weixinService.isValid(noticeRes) && "SUCCESS".equalsIgnoreCase(noticeRes.getReturnCode())
                && "SUCCESS".equalsIgnoreCase(noticeRes.getResultCode())) {
            LOGGER.debug("sign_verify:SUCCESS");
            LOGGER.debug("result_code:SUCCESS");
            // 保存微信异步通知信息
            dealPayNotice2BusinessTask.run(noticeRes);
            ret = true;
        }

        PrintWriter out = resp.getWriter();
        String resultMsg = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml> ";
        if (ret) {
            resultMsg = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml> ";
        }
        out.write(resultMsg);
        out.flush();
        out.close();
    }

    /**
     * 微信退款
     *
     * @param wxRefReq
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/refund", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> refundPayment(@RequestBody WeixinRefundReq wxRefReq) throws Exception {
        // 处理微信退款
        PayResultRes refundRet = weixinService.saveWeixinRefund(wxRefReq);
        // 退款结果
        PayResultRes resObj = new PayResultRes();

        // 以下字段在return_code为SUCCESS的时候有返回
        if ("0".equals(refundRet.getResultCode())) {
            LOGGER.debug("weixin_refund_result_code:SUCCESS");
            // 获取预支付ID信息失败
        } else {
            LOGGER.debug("weixin_refund_result_code:FAIL");
        }
        return responseOfPost(resObj);
    }

}
