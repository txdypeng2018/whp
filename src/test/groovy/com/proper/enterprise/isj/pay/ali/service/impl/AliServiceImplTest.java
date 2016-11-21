package com.proper.enterprise.isj.pay.ali.service.impl;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.pay.ali.constants.AliConstants;
import com.proper.enterprise.isj.pay.ali.entity.AliEntity;
import com.proper.enterprise.isj.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.isj.pay.ali.entity.AliRefundEntity;
import com.proper.enterprise.isj.pay.ali.model.*;
import com.proper.enterprise.isj.pay.ali.repository.AliRefundRepository;
import com.proper.enterprise.isj.pay.ali.repository.AliRepository;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.cipher.RSA;
import com.proper.enterprise.platform.core.utils.http.HttpClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

/**
 * 支付宝ServiceImpl
 */
// @Primary
// @Service
public class AliServiceImplTest implements AliService {

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

    @Override
    public AliRefund findByTradeNo(String tradeNo) {
        return aliRefundRepo.findByTradeNo(tradeNo);
    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     *
     * @param params
     *            通知返回来的参数数组
     * @return 验证结果
     */
    public boolean verify(Map<String, String> params) throws Exception {
        return true;
    }

    @Override
    public AliPayTradeQueryRes getAliPayTradeQueryRes(String outTradeNo) {
        return null;
    }

    @Override
    public AliRefundTradeQueryRes getAliRefundTradeQueryRes(String outTradeNo, String outRequestNo) {
        return null;
    }

    @Override
    public AliRefundRes saveAliRefundResProcess(String outTradeNo, String refundNo, String amount, String refundReason) {
        return null;
    }

    /**
     * 支付宝即时到账异步通知处理
     *
     * @param params
     * @return
     */
    public boolean saveAliRefundProcess(Map<String, String> params) throws Exception {
        boolean ret = false;
        // 获取结果集信息
        String resultDetails[] = params.get("result_details").split("\\^");
        // 支付宝交易号
        String tradeNo = resultDetails[0];
        params.put("trade_no", tradeNo);
        // 退款金额
        String totalFee = resultDetails[1];
        params.put("total_fee", totalFee);
        // 退款结果
        String refundResult = resultDetails[2];
        params.put("refund_result", refundResult);
        params.remove("result_details");
        // 获取支付宝即时到账退款无密异步通知对象
        AliRefundEntity aliRefundInfo = getAliRefundNoticeInfo(params);
        logAliEntity(aliRefundInfo, AliRefundEntity.class);
        // 查找记录
        AliRefund refundInfo = this.findByTradeNo(aliRefundInfo.getTradeNo());
        if (refundInfo == null) {
            this.save(aliRefundInfo);
            ret = true;
        }
        return ret;
    }

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
                // 没有处理过订单
                if (orderinfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay")) {
                    LOGGER.debug("没有处理过的订单");
                    // 保存支付宝异步通知信息
                    AliEntity aliInfo = getAliNoticeInfo(params);
                    LOGGER.debug("获取支付宝Entity信息");
                    PayRegReq payReg = registrationService.convertAppInfo2PayReg(aliInfo, orderinfo.getFormId());
                    registrationService.saveUpdateRegistrationAndOrder(payReg);
                    // // 更新订单状态
                    // orderinfo.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.payed"));
                    // orderinfo.setOrderStatus(String.valueOf(2));
                    // orderinfo.setPayWay(String.valueOf(2));
                    // orderService.save(orderinfo);
                    logAliEntity(aliInfo, AliEntity.class);
                    // 保存支付宝异步通知信息
                    save(aliInfo);
                    LOGGER.debug("更新订单状态为支付成功");

                    Order orderinfoRet = orderService.findByOrderNo(orderNo);
                    LOGGER.debug("orderinfoRet.getPaymentStatus():" + orderinfoRet.getPaymentStatus());

                    ret = true;
                    // 已经成功处理过的订单
                } else if (orderinfo.getPaymentStatus() == ConfCenter.getInt("isj.pay.paystatus.payed")) {
                    LOGGER.debug("已经成功处理过的支付订单");
                    ret = true;
                }

                // 退款异步通知处理
            } else if ("refund".equals(dealType)) {
                // 没有处理过订单
                if (orderinfo.getPaymentStatus() == ConfCenter.getInt("isj.pay.paystatus.payed")) {
                    LOGGER.debug("没有处理过的退费订单");
                    // 保存支付宝异步通知信息
                    AliEntity aliInfo = getAliNoticeInfo(params);
                    LOGGER.debug("获取支付宝Entity信息");
                    // 更新订单状态
                    orderinfo.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                    orderService.save(orderinfo);
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
        StringBuilder sbBatchNo = new StringBuilder();
        sbBatchNo.append(DateUtil.toString(new Date(), "yyyyMMddHHmmssSSS"))
                .append(RandomStringUtils.randomNumeric(15));
        rePriReq.setBatchNo(sbBatchNo.toString());
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
     *
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