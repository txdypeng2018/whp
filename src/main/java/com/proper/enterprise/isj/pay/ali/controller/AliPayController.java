package com.proper.enterprise.isj.pay.ali.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.constants.AliConstants;
import com.proper.enterprise.isj.pay.ali.model.AliRefundDetailReq;
import com.proper.enterprise.isj.pay.ali.model.UnifiedOrderReq;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.pay.model.PayResultRes;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.cipher.RSA;

@RestController
@RequestMapping(value = "/pay/ali")
public class AliPayController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayController.class);

    @Autowired
    AliService aliService;
    
    @Autowired
    WeixinService weixinService;

    @Autowired
    OrderService orderService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    Map<String, Unmarshaller> unmarshallerMap;

    @Autowired
    @Qualifier("aliRSA")
    RSA rsa;

    @PostMapping(value = "/prepayInfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> getPrepayinfo(@RequestBody UnifiedOrderReq uoReq) throws Exception {

        // 取得订单信息
        String orderInfo = aliService.getOrderInfo(uoReq, UnifiedOrderReq.class);
        // 获取秘钥
        String privateKey = AliConstants.ALI_PAY_RSA_PRIVATE;
        // 对订单信息进行签名
        String sign = rsa.sign(orderInfo, privateKey);
        sign = URLEncoder.encode(sign, PEPConstants.DEFAULT_CHARSET.name());
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
        // -------------返回给客户端调用支付接口需要的参数-----------------------
        // 返回给请求客户端处理结果
        PayResultRes resObj = new PayResultRes();
        resObj.setResultCode("0");
        resObj.setResultMsg("SUCCESS");
        resObj.setPayInfo(payInfo);
        resObj.setSign(privateKey);
        LOGGER.debug("resultCode:" + resObj.getResultCode());
        LOGGER.debug("payInfo:" + payInfo);
        LOGGER.debug("privatekey:" + StringUtil.abbreviate(privateKey, 15, privateKey.length()));
        // ------------------------------------------------------------------
        try {
            Order order = orderService.findByOrderNo(uoReq.getOutTradeNo());
            if(order!=null){
                if(order.getFormClassInstance().equals(RecipeOrderDocument.class.getName())){
                    String totalFee = (new BigDecimal(uoReq.getTotalFee()).multiply(new BigDecimal("100"))).toString();
                    boolean flag = recipeService.checkRecipeAmount(uoReq.getOutTradeNo(), totalFee, PayChannel.ALIPAY);
                    RecipeOrderDocument recipe = recipeService.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
                    if (!flag
                            || (recipe != null && StringUtil.isEmpty(recipe.getRecipeNonPaidDetail().getPayChannelId()))) {
                        resObj.setResultCode("-1");
                        resObj.setResultMsg(CenterFunctionUtils.ORDER_DIFF_RECIPE_ERR);
                    }
                }else{
                    RegistrationDocument reg = registrationService.getRegistrationDocumentById(order.getFormId());
                    if (reg != null) {
                        String payWay = reg.getPayChannelId();
                        boolean paidFlag = orderService.checkOrderIsPay(payWay, reg.getOrderNum());
                        if (!paidFlag) {
                            reg.setPayChannelId(String.valueOf(PayChannel.ALIPAY.getCode()));
                            registrationService.saveRegistrationDocument(reg);
                            order.setPayWay(String.valueOf(PayChannel.ALIPAY.getCode()));
                            orderService.save(order);
                        } else {
                            resObj.setResultCode("-1");
                            resObj.setResultMsg(CenterFunctionUtils.ORDER_ALREADY_PAID_ERR);
                        }
                    }else{
                        resObj.setResultCode("-1");
                        resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
                    }
                }
            }else{
                resObj.setResultCode("-1");
                resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resObj.setResultCode("-1");
            resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
        }
        return responseOfPost(resObj);
    }

    @JWTIgnore
    @PostMapping(value = "/noticeInfo")
    public ResponseEntity<String> dealNoticeInfo(HttpServletRequest request) throws Exception {
        LOGGER.debug("-----------支付宝异步通知---------------------");

        // 返回给支付宝服务器的异步通知结果
        boolean ret = false;

        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            params.put(name, StringUtil.join(entry.getValue(), ","));
        }
        LOGGER.debug("notice_msg:" + params.toString());
        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        // 商户订单号
        String outTradeNo = request.getParameter("out_trade_no");
        // 交易状态
        String tradeStatus = request.getParameter("trade_status");

        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        if (aliService.verify(params)) {// 验证成功
            // 移动支付异步通知
            if (StringUtil.isNotNull(outTradeNo)) {
                // 取得交易状态
                if (tradeStatus.equals(AliConstants.ALI_PAY_NOTICE_TARDESTATUS_TRADE_FINISHED)) {
                    // 判断该笔订单是否在商户网站中已经做过处理
                    // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    // 如果有做过处理，不执行商户的业务程序

                    // 付款异步通知内部处理
                    ret = aliService.saveAliNoticeProcess(outTradeNo, params, "pay");

                    // 注意：
                    // 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                } else if (tradeStatus.equals(AliConstants.ALI_PAY_NOTICE_TARDESTATUS_TRADE_SUCCESS)) {
                    // 判断该笔订单是否在商户网站中已经做过处理
                    // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    // 如果有做过处理，不执行商户的业务程序

                    // 付款异步通知内部处理
                    ret = aliService.saveAliNoticeProcess(outTradeNo, params, "pay");

                    // 注意：
                    // 该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
                    // 付款完成后，支付宝系统发送该交易状态通知
                    // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的

                    // 如果是退费异步通知
                } else if (tradeStatus.equals(AliConstants.ALI_PAY_NOTICE_TARDESTATUS_TRADE_CLOSED) && request
                        .getParameter("refund_status").equals(AliConstants.ALI_PAY_NOTICE_REFUND_STATUS_SUCCESS)) {
                    // 判断该笔订单是否在商户网站中已经做过处理
                    // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    // 如果有做过处理，不执行商户的业务程序

                    ret = aliService.saveAliNoticeProcess(outTradeNo, params, "refund");
                    // 注意：
                    // 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                }
                // 退款异步通知结果
            }
        }

        if (ret) {
            return responseOfPost("SUCCESS");
        } else {
            return responseOfPost("FAIL");
        }
    }

    /**
     * 支付宝退款
     *
     * @param reDetailReq
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/refund", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PayResultRes> refundPayment(@RequestBody AliRefundDetailReq reDetailReq) throws Exception {
        boolean ret = aliService.saveRefundProcess(reDetailReq);
        // 返回给请求客户端处理结果
        PayResultRes resObj = new PayResultRes();
        // 处理返回结果
        if (ret) {
            LOGGER.debug("refund success!");
            resObj.setResultCode("0");
            resObj.setResultMsg("SUCCESS");
        } else {
            LOGGER.debug("refund fail!");
            resObj.setResultCode("-1");
            resObj.setResultMsg("FAIL");
        }
        return responseOfPost(resObj);
    }

}
