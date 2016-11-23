package com.proper.enterprise.isj.pay.ali.service.impl;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;

import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.constants.AliConstants;
import com.proper.enterprise.isj.pay.ali.entity.AliEntity;
import com.proper.enterprise.isj.pay.ali.entity.AliRefundEntity;
import com.proper.enterprise.isj.pay.ali.model.*;
import com.proper.enterprise.isj.pay.ali.repository.AliRefundRepository;
import com.proper.enterprise.isj.pay.ali.repository.AliRepository;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.cipher.RSA;
import com.proper.enterprise.platform.core.utils.http.HttpClient;

/**
 * 支付宝ServiceImpl
 */
@Service
public class AliServiceImpl implements AliService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliServiceImpl.class);

    @Autowired
    AliRepository aliRepo;

    @Autowired
    AliRefundRepository aliRefundRepo;

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    @Qualifier("aliRSA")
    RSA rsa;

    @Autowired
    Map<String, Unmarshaller> unmarshallerMap;

    @Override
    public Ali save(Ali ali) {
        return aliRepo.save((AliEntity) ali);
    }

    @Override
    public Ali findByOutTradeNo(String outTradeNo) {
        return aliRepo.findByOutTradeNo(outTradeNo);
    }

    @Override
    public AliRefund save(AliRefund aliRefund) {
        return aliRefundRepo.save((AliRefundEntity) aliRefund);
    }

//    @Override
//    public AliRefund findByTradeNo(String tradeNo) {
//        return aliRefundRepo.findByTradeNo(tradeNo);
//    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     *
     * @param params
     *            通知返回来的参数数组
     * @return 验证结果
     */
    public boolean verify(Map<String, String> params) throws Exception {
        // 判断responsetTxt是否为true，isSign是否为true
        // responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        // isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "false";
        if (params.get("notify_id") != null) {
            String notifyId = params.get("notify_id");
            responseTxt = verifyResponse(notifyId);
        }
        LOGGER.debug("验证是否为支付宝发出的数据。验证结果:" + responseTxt);
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
        }
        boolean isSign = getSignVeryfy(params, sign);
        LOGGER.debug("验证签名是否正确,验证结果:" + isSign);
        // 写日志记录（若要调试，请取消下面两行注释）
        // String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign +
        // "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
        // AlipayCore.logResult(sWord);

        return isSign && responseTxt.equals("true");
    }

    @Override
    public AliPayTradeQueryRes getAliPayTradeQueryRes(String outTradeNo) {
        AliPayTradeQueryRes res = new AliPayTradeQueryRes();
        Map<String, String> bizContentMap = new HashMap<String, String>();
        bizContentMap.put("out_trade_no", outTradeNo);
        String method = ConfCenter.get("isj.pay.ali.tradeQueryMethod");
        String responseKey = "alipay_trade_query_response";
        Object result = getAliRequestRes(res, bizContentMap, method, responseKey);
        if (result != null) {
            res = (AliPayTradeQueryRes) result;
        } else {
            res = null;
        }
        return res;
    }

    @Override
    public AliRefundTradeQueryRes getAliRefundTradeQueryRes(String outTradeNo, String outRequestNo) {
        AliRefundTradeQueryRes res = new AliRefundTradeQueryRes();
        Map<String, String> bizContentMap = new HashMap<String, String>();
        bizContentMap.put("out_trade_no", outTradeNo);
        bizContentMap.put("out_request_no", outRequestNo);
        String method = ConfCenter.get("isj.pay.ali.tradeRefundQueryMethod");
        String responseKey = "alipay_trade_fastpay_refund_query_response";
        Object result = getAliRequestRes(res, bizContentMap, method, responseKey);
        if (result != null) {
            res = (AliRefundTradeQueryRes) result;
        } else {
            res = null;
        }
        return res;
    }

    @Override
    public AliRefundRes saveAliRefundResProcess(String outTradeNo, String refundNo, String amount, String refundReason) {
        AliRefundRes res = new AliRefundRes();
        Map<String, String> bizContentMap = new HashMap<String, String>();
        bizContentMap.put("out_trade_no", outTradeNo);
        bizContentMap.put("out_request_no", refundNo);
        bizContentMap.put("refund_amount", amount);
        if(StringUtil.isNotEmpty(refundReason)){
            bizContentMap.put("refund_reason", refundReason);
        }
        String method = ConfCenter.get("isj.pay.ali.tradeRefundMethod");
        String responseKey = "alipay_trade_refund_response";
        Object result = getAliRequestRes(res, bizContentMap, method, responseKey);
        if (result != null) {
            res = (AliRefundRes) result;
            AliRefundEntity refund = new AliRefundEntity();
            BeanUtils.copyProperties(res, refund);
            AliRefund oldRefund =  aliRefundRepo.findByOutTradeNo(refund.getOutTradeNo());
            if (oldRefund == null) {
                aliRefundRepo.save(refund);
            }
        } else {
            res = null;
        }
        return res;
    }

    private Object getAliRequestRes(Object res, Map<String, String> bizContentMap, String method, String responseKey) {

        String privateKey = AliConstants.ALI_PAY_RSA_PRIVATE;
        String publicKey = ConfCenter.get("isj.pay.ali.openplatform.publicKey");
        String appId = AliConstants.ALI_PAY_APPID;
        String tradeUrl = ConfCenter.get("isj.pay.ali.tradeUrl");

        StringBuilder reqUrl = new StringBuilder();
        StringBuilder paramStr = new StringBuilder();

        try {
            reqUrl = reqUrl.append(tradeUrl);
            paramStr.append("app_id=").append(appId);
            paramStr.append("&biz_content=" + JSONUtil.toJSON(bizContentMap));
            paramStr.append("&charset=" + PEPConstants.DEFAULT_CHARSET.name());
            paramStr.append("&method=" + method);
            paramStr.append("&sign_type=RSA");
            paramStr.append("&timestamp=").append(DateUtil.toTimestamp(new Date()));
            paramStr.append("&version=1.0");
            String sign = rsa.sign(paramStr.toString(), privateKey);
            sign = URLEncoder.encode(sign, PEPConstants.DEFAULT_CHARSET.name());
            paramStr.append("&sign=").append(sign);
            reqUrl = reqUrl.append("?").append(paramStr);
            ResponseEntity<byte[]> entity = HttpClient.get(reqUrl.toString());
            String strRead = new String(entity.getBody(), PEPConstants.DEFAULT_CHARSET.name());
            Map<String, Object> queryMap = JSONUtil.parse(strRead, Map.class);
            Map<String, Object> resMap = (Map<String, Object>) queryMap.get(responseKey);
            String signStr = (String) queryMap.get("sign");
            boolean flag = rsa.verifySign(JSONUtil.toJSON(resMap), signStr, publicKey);
            if (flag) {
                res = this.convertMap2AliPayTradeQueryRes(resMap, res);
            } else {
                res = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private Object convertMap2AliPayTradeQueryRes(Map<String, Object> resMap, Object res) throws Exception {
        if (res instanceof AliPayTradeQueryRes) {
            res = new AliPayTradeQueryRes();
        } else if (res instanceof AliRefundRes) {
            res = new AliRefundRes();
        } else if (res instanceof AliRefundTradeQueryRes) {
            res = new AliRefundTradeQueryRes();
        }
        Field[] fields = res.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("serialVersionUID") || field.getName().equals("$jacocoData")) {
                continue;
            }
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), res.getClass());
            Method setMethod = pd.getWriteMethod();
            setMethod.invoke(res, resMap.get(StringUtil.camelToSnake(field.getName()))); // 执行get方法返回一个Object
        }
        return res;
    }

//    /**
//     * 支付宝即时到账异步通知处理
//     *
//     * @param params
//     * @return
//     */
//    public boolean saveAliRefundProcess(Map<String, String> params) throws Exception {
//        boolean ret = false;
//        // 获取结果集信息
//        String resultDetails[] = params.get("result_details").split("\\^");
//        // 支付宝交易号
//        String tradeNo = resultDetails[0];
//        params.put("trade_no", tradeNo);
//        // 退款金额
//        String totalFee = resultDetails[1];
//        params.put("total_fee", totalFee);
//        // 退款结果
//        String refundResult = resultDetails[2];
//        params.put("refund_result", refundResult);
//        params.remove("result_details");
//        // 获取支付宝即时到账退款无密异步通知对象
//        AliRefundEntity aliRefundInfo = getAliRefundNoticeInfo(params);
//        logAliEntity(aliRefundInfo, AliRefundEntity.class);
//        // 查找记录
//        AliRefund refundInfo = this.findByTradeNo(aliRefundInfo.getTradeNo());
//        if (refundInfo == null) {
//            this.save(aliRefundInfo);
//            ret = true;
//        }
//        return ret;
//    }

    /**
     * 支付宝异步通知内部逻辑处理
     *
     * @param orderNo
     * @param params
     * @param dealType
     *            如果是支付异步通知 dealType = pay 如果是退款异步通知 dealType = refund
     * @return
     */
    @Override
    public boolean saveAliNoticeProcess(String orderNo, Map<String, String> params, String dealType) throws Exception {
        boolean ret = false;
        LOGGER.debug("orderNo:" + orderNo);
        Order orderinfo = orderService.findByOrderNo(orderNo);
        LOGGER.debug("orderinfo != null:" + String.valueOf(orderinfo != null));
        // 查询订单号是否已经处理过了
        // 存在订单
        if (orderinfo != null) {
            // 支付异步通知处理
            if ("pay".equals(dealType)) {
                AliEntity aliInfo = getAliNoticeInfo(params);
                LOGGER.debug("获取支付宝Entity信息");
                // 没有处理过订单
                if (orderinfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay")) {
                    LOGGER.debug("没有处理过的订单");
                    // 保存支付宝异步通知信息
                    synchronized (orderinfo.getOrderNo()) {
                        try {
                            if (orderinfo.getFormClassInstance().equals(RegistrationDocument.class.getName())) {
                                PayRegReq payReg = registrationService.convertAppInfo2PayReg(aliInfo,
                                        orderinfo.getFormId());
                                if (payReg != null) {
                                    registrationService.saveUpdateRegistrationAndOrder(payReg);
                                }else{
                                    LOGGER.debug("未查到已支付的挂号单信息,订单号:" + orderNo);
                                }
                            } else {
                                orderinfo = recipeService.saveUpdateRecipeAndOrder(orderinfo.getOrderNo(),
                                        String.valueOf(PayChannel.ALIPAY.getCode()), aliInfo);
                                if (orderinfo == null) {
                                    LOGGER.debug("缴费异常,订单号:" + orderNo);
                                } else {
                                    orderService.save(orderinfo);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.debug("支付成功后,调用HIS接口中发生了错误,订单号:" + orderNo);
                        }
                    }
                    // // 更新订单状态
                    // orderinfo.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.payed"));
                    // orderinfo.setOrderStatus(String.valueOf(2));
                    // orderinfo.setPayWay(String.valueOf(2));
                    // orderService.save(orderinfo);
                    LOGGER.debug("更新订单状态为支付成功");
                    Order orderinfoRet = orderService.findByOrderNo(orderNo);
                    LOGGER.debug("orderinfoRet.getPaymentStatus():" + orderinfoRet.getPaymentStatus());
                    ret = true;
                    // 已经成功处理过的订单
                } else if (orderinfo.getPaymentStatus() == ConfCenter.getInt("isj.pay.paystatus.payed")) {
                    LOGGER.debug("已经成功处理过的支付订单");
                    ret = true;
                }
                if (aliInfo != null) {
                    logAliEntity(aliInfo, AliEntity.class);
                    // 保存支付宝异步通知信息
                    Ali ali = this.findByOutTradeNo(aliInfo.getOutTradeNo());
                    if (ali == null) {
                        save(aliInfo);
                    }
                }
                // 退款异步通知处理
            } else if ("refund".equals(dealType)) {
                // 没有处理过订单
                if (orderinfo.getPaymentStatus() == ConfCenter.getInt("isj.pay.paystatus.payed")) {
                    LOGGER.debug("没有处理过的退费订单");
                    // 保存支付宝异步通知信息
                    AliEntity aliInfo = getAliNoticeInfo(params);
                    LOGGER.debug("获取支付宝Entity信息");
                    // 更新订单状态 // TODO
                    // RefundReq refundReq =
                    // registrationService.convertAppRefundInfo2RefundReq(aliInfo,
                    // orderNo);
                    // registrationService.saveUpdateRegistrationAndOrderRefund(refundReq);
                    // orderinfo.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                    // orderService.save(orderinfo);
                    logAliEntity(aliInfo, AliEntity.class);
                    // 保存支付宝异步通知信息
                    save(aliInfo);
                    LOGGER.debug("更新订单状态为已退费");

                    Order orderinfoRet = orderService.findByOrderNo(orderNo);
                    LOGGER.debug("orderinfoRet.getPaymentStatus():" + orderinfoRet.getPaymentStatus());

                    ret = true;
                    // 已经成功处理过的订单
                } else if (orderinfo.getPaymentStatus() == ConfCenter.getInt("isj.pay.paystatus.refund")) {
                    LOGGER.debug("已经成功处理过的退费订单");
                    ret = true;
                }
            }
        }
        return ret;
    }

    /**
     * 支付宝即时到账无密退款
     *
     * @param reDetailReq
     * @return
     * @throws Exception
     */
    public boolean saveRefundProcess(AliRefundDetailReq reDetailReq) throws Exception {
        // 请求退款对象
        AliRefundReq rePriReq = new AliRefundReq();
        // 退款请求时间
        rePriReq.setRefundDate(DateUtil.toTimestamp(new Date(), false));
        // 退款批次号
        // StringBuilder sbBatchNo = new StringBuilder();
        // sbBatchNo.append(DateUtil.toString(new Date(),
        // "yyyyMMddHHmmssSSS")).append(RandomStringUtils.randomNumeric(15));
        // rePriReq.setBatchNo(sbBatchNo.toString());
        rePriReq.setBatchNo(reDetailReq.getBatchNo());
        // 单笔数据集
        StringBuilder sbDetailData = new StringBuilder();
        sbDetailData.append(reDetailReq.getTradeNo()).append("^").append(reDetailReq.getTotalFee()).append("^")
                .append(reDetailReq.getRefundeReason());
        rePriReq.setDetailData(sbDetailData.toString());

        // 取得退款信息
        String orderInfo = this.getRefundInfo(rePriReq, AliRefundReq.class, false);
        // 获取URLEncode退款信息
        String urlEncodeOrderInfo = this.getRefundInfo(rePriReq, AliRefundReq.class, true);
        // 获取秘钥
        String privateKey = AliConstants.ALI_PAY_RSA_PRIVATE;
        // 对订单信息进行签名
        String sign = rsa.sign(orderInfo, privateKey);
        LOGGER.debug("rsa:" + sign);
        sign = URLEncoder.encode(sign, PEPConstants.DEFAULT_CHARSET.name());

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = urlEncodeOrderInfo + "&sign=" + sign + "&" + "sign_type=RSA";
        LOGGER.debug("payInfo:" + payInfo);
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(AliConstants.ALI_PAY_TRADE_REFUND_URL).append("?").append(payInfo);
        LOGGER.debug("sbUrl:" + sbUrl.toString());
        // 发起退款请求
        ResponseEntity<byte[]> response = HttpClient.get(sbUrl.toString());
        // 解析请求结果
        AliRefundPreRes res = (AliRefundPreRes) unmarshallerMap.get("unmarshallAliRefundPre")
                .unmarshal(new StreamSource(new ByteArrayInputStream(response.getBody())));
        LOGGER.debug("res.getIsSuccess():" + res.getIsSuccess());

        // 处理返回结果
        if (StringUtil.isNotNull(res.getIsSuccess()) && "T".equals(res.getIsSuccess())) {
            LOGGER.debug("refund success!");
            return true;
        } else {
            LOGGER.debug("refund fail!");
            return false;
        }
    }

    /**
     * create the order info. 创建订单信息
     */
    public <T> String getOrderInfo(T t, Class<T> clz) throws Exception {
        Field[] fields = clz.getDeclaredFields();
        Set<String> set = new HashSet<>();
        for (Field field : fields) {
            set.add(field.getName());
        }
        StringBuilder sb = new StringBuilder();
        Object value;
        for (String fieldName : set) {
            if (!StringUtil.startsWith(fieldName, "$")) {
                value = clz.getMethod("get" + StringUtil.capitalize(fieldName)).invoke(t);
                if (value != null) {
                    if (fieldName.equals("inputCharset")) {
                        sb.append("&_").append(StringUtil.camelToSnake(fieldName)).append("=").append("\"")
                                .append(value).append("\"");
                    } else {
                        sb.append("&").append(StringUtil.camelToSnake(fieldName)).append("=").append("\"").append(value)
                                .append("\"");
                    }
                }
            }
        }
        return sb.deleteCharAt(0).toString();
    }

    public <T> String getRefundInfo(T t, Class<T> clz, boolean isURLEncoder) throws Exception {
        Field[] fields = clz.getDeclaredFields();
        Set<String> set = new TreeSet<>();
        for (Field field : fields) {
            set.add(field.getName());
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sbRet = new StringBuilder();
        Object value;
        for (String fieldName : set) {
            if (!StringUtil.startsWith(fieldName, "$")) {
                value = clz.getMethod("get" + StringUtil.capitalize(fieldName)).invoke(t);
                if (value != null) {
                    String strValue = null;
                    if (isURLEncoder) {
                        strValue = URLEncoder.encode(String.valueOf(value), PEPConstants.DEFAULT_CHARSET.name());
                    } else {
                        strValue = String.valueOf(value);
                    }
                    if (fieldName.equals("inputCharset")) {
                        sbRet.append("_").append(StringUtil.camelToSnake(fieldName)).append("=").append(strValue);
                    } else {
                        sb.append("&").append(StringUtil.camelToSnake(fieldName)).append("=").append(strValue);
                    }
                }
            }
        }
        return sbRet.append(sb.toString()).toString();
    }

    /**
     * 创建支付宝支付信息Entity
     *
     * @param params
     *            参数
     * @return alipayinfo 支付信息
     * @throws Exception
     */
    private AliEntity getAliNoticeInfo(Map<String, String> params) throws Exception {
        Field[] fields = AliEntity.class.getDeclaredFields();
        Set<String> set = new HashSet<>();
        for (Field field : fields) {
            set.add(field.getName());
        }
        String value;
        AliEntity alipayinfo = new AliEntity();
        for (String fieldName : set) {
            if (!fieldName.equals("isdel") && !StringUtil.startsWith(fieldName, "$")) {
                String paramName = StringUtil.camelToSnake(fieldName);
                value = params.get(paramName);
                // 交易状态 0:未知
                if (paramName.equals("trade_status") && StringUtil.isNull(params.get("trade_status"))) {
                    alipayinfo.setTradeStatus(AliConstants.ALI_PAY_NOTICE_TARDESTATUS_UNKONWN);
                    // 交易金额
                } else if (paramName.equals("total_fee") && StringUtil.isNull(params.get("total_fee"))) {
                    alipayinfo.setTotalFee("0");
                    // 购买数量
                } else if (paramName.equals("quantity") && StringUtil.isNull(params.get("quantity"))) {
                    alipayinfo.setTotalFee("1");
                    // 商品单价
                } else if (paramName.equals("price") && StringUtil.isNull(params.get("price"))) {
                    alipayinfo.setTotalFee("0");
                    // 退款状态 0:未知
                } else if (paramName.equals("refund_status") && StringUtil.isNull(params.get("refund_status"))) {
                    alipayinfo.setRefundStatus(AliConstants.APP_ALIPAY_REFUND_STATUS_UNKNOWN_VALUE);
                    // 其他情况
                } else {
                    AliEntity.class.getMethod("set" + StringUtil.capitalize(fieldName), String.class).invoke(alipayinfo,
                            value);
                }
            }
        }
        // 逻辑删除 0:正常
        alipayinfo.setIsdel(ConfCenter.get("isj.pay.isdel.nomarl"));
        return alipayinfo;
    }

    /**
     * <<<<<<< HEAD 创建支付宝退款异步通知信息Entity
     *
     * @param params
     *            参数
     * @return alipayinfo 支付信息
     * @throws Exception
     */
    private AliRefundEntity getAliRefundNoticeInfo(Map<String, String> params) throws Exception {
        Field[] fields = AliRefundEntity.class.getDeclaredFields();
        Set<String> set = new HashSet<>();
        for (Field field : fields) {
            set.add(field.getName());
        }
        String value;
        AliRefundEntity aliRefundinfo = new AliRefundEntity();
        for (String fieldName : set) {
            if (!StringUtil.startsWith(fieldName, "$")) {
                String paramName = StringUtil.camelToSnake(fieldName);
                value = params.get(paramName);
                AliRefundEntity.class.getMethod("set" + StringUtil.capitalize(fieldName), String.class)
                        .invoke(aliRefundinfo, value);
            }
        }
        return aliRefundinfo;
    }

    /**
     * 输出BeanLog
     *
     * @param t
     * @param clz
     * @param <T>
     * @throws Exception
     */
    private <T> void logAliEntity(T t, Class<T> clz) throws Exception {
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

    /**
     * 获取远程服务器ATN结果,验证返回URL
     *
     * @param notifyId
     *            通知校验ID
     * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
     *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private String verifyResponse(String notifyId) throws IOException {
        // 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

        String partner = AliConstants.ALI_PAY_PARTNER_ID;
        String veryfyUrl = AliConstants.ALI_PAY_NOTICE_HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id="
                + notifyId;

        return checkUrl(veryfyUrl);
    }

    /**
     * 获取远程服务器ATN结果
     *
     * @param urlvalue
     *            指定URL路径地址
     * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true
     *         返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private String checkUrl(String urlvalue) throws IOException {
        return new String(HttpClient.get(urlvalue).getBody(), PEPConstants.DEFAULT_CHARSET);
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     *
     * @param params
     *            通知返回来的参数数组
     * @param sign
     *            比对的签名结果
     * @return 生成的签名结果
     */
    private boolean getSignVeryfy(Map<String, String> params, String sign) throws Exception {
        // 过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = paraFilter(params);
        // 获取待签名字符串
        String preSignStr = createLinkString(sParaNew);
        LOGGER.debug("异步通知待签名的字符串:" + preSignStr);
        LOGGER.debug("异步通知的签名:" + sign);
        // 获得签名验证结果
        return AliConstants.ALI_PAY_SIGN_TYPE.equals("RSA")
                && rsa.verifySign(preSignStr, sign, AliConstants.ALI_PAY_RSA_PUBLIC);
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray
     *            签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (Map.Entry entry : sArray.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params
     *            需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder prestr = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr.append(key);
                prestr.append("=");
                prestr.append(value);
            } else {
                prestr.append(key);
                prestr.append("=");
                prestr.append(value);
                prestr.append("&");
            }
        }

        return prestr.toString();
    }

}
