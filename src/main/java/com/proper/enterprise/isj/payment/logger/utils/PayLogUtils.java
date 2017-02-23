package com.proper.enterprise.isj.payment.logger.utils;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.payment.logger.LoggerApplicationContextHelper;
import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.isj.payment.logger.PayStepEnum;
import com.proper.enterprise.isj.payment.logger.utils.codec.PayLogEncoder;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.proper.enterprise.isj.payment.logger.PayLog.DEFAULT_CAUSE;

/**
 * 支付日志工具类.
 * <p>
 * 支付日志的调用接口,推荐使用此工具类记录日志.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public final class PayLogUtils {

    public static final int MASK_CAUSE_FUNC = 0xFFFF0000;
    public static final int MASK_CAUSE_TYPE = 0x0000FF00;
    public static final int MASK_CAUSE_NUMBER = 0x000000FF;
    public static final int CAUSE_TYPE_START = 0x00000100;
    public static final int CAUSE_TYPE_EXCUTE = 0x00000200;
    public static final int CAUSE_TYPE_SUCCESS = 0x00002000;
    public static final int CAUSE_TYPE_FAIL = 0x00004000;
    public static final int CAUSE_TYPE_EXCEPTION = 0x00008000;

    /**
     * 用于记录交易日志的日志对象.
     *
     * @since 0.1.0
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerApplicationContextHelper.getDelayLoggerName());

    /**
     * 记录交易日志的方法.
     *
     * @param <T>      业务对象的类型.
     * @param step     交易阶段.
     *                 <p>
     *                 参见：{@link PayStepEnum}和{@link PayLogConstrants}。
     *                 </p>
     * @param business 业务对象.
     * @since 0.1.0
     */
    @SuppressWarnings("unchecked")
    public static <T> void log(PayStepEnum step, T business, int logPos) {
        if (business != null) {
            ApplicationContext ctx = LoggerApplicationContextHelper.findAppContext();
            if (LOGGER.isInfoEnabled()) {
                PayLog<T> paylog = ctx.getBean(PayLog.class);
                paylog.setStep(step.step());
                paylog.setCause(logPos);
                paylog.setBusinessObject(business);
                PayLogEncoder<T, ? super PayLog<T>> encoder = ctx.getBean(PayLogEncoder.class);
                String msg = encoder.encode(paylog);
                LOGGER.info(msg);
            }
        }
    }

    /**
     * 记录交易日志的方法.
     * <p>
     * 日志原因参数为{@link DEFAULT_CAUSE}。
     * </p>
     *
     * @param <T>      业务对象的类型.
     * @param step     交易阶段.
     *                 <p>
     *                 参见：{@link PayStepEnum}和{@link PayLogConstrants}。
     *                 </p>
     * @param business 业务对象.
     * @see #log(PayStepEnum, Object, int)
     * @since 0.1.0
     */
    public static <T> void log(PayStepEnum step, T business) {
        log(step, business, DEFAULT_CAUSE);
    }

    public static class RegBizObject {

        private RegistrationOrderReqDocument payOrderRegDocument;
        private PayRegReq payRegReg;
        private Order order;
        private RegistrationDocument regBack;

        public RegistrationOrderReqDocument getPayOrderRegDocument() {
            return payOrderRegDocument;
        }

        public void setPayOrderRegDocument(RegistrationOrderReqDocument payOrderRegDocument) {
            this.payOrderRegDocument = payOrderRegDocument;
        }

        public PayRegReq getPayRegReg() {
            return payRegReg;
        }

        public void setPayRegReg(PayRegReq payRegReg) {
            this.payRegReg = payRegReg;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public RegistrationDocument getRegBack() {
            return regBack;
        }

        public void setRegBack(RegistrationDocument regBack) {
            this.regBack = regBack;
        }

    }

    public static class RecipeBizObject<T> {
        private String orderNo;
        private String channelId;
        private T infoObj;

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public T getInfoObj() {
            return infoObj;
        }

        public void setInfoObj(T infoObj) {
            this.infoObj = infoObj;
        }

    }

    public static class PayOrderReqSource<T> {
        private Order order;
        private T infoObj;

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public T getInfoObj() {
            return infoObj;
        }

        public void setInfoObj(T infoObj) {
            this.infoObj = infoObj;
        }

    }

    public static class RegistrationAndOrderBizObject {
        private Order order;
        private PayOrderReq payOrderReq;
        private RecipeOrderDocument regBack;
        private String channelId;

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public PayOrderReq getPayOrderReq() {
            return payOrderReq;
        }

        public void setPayOrderReq(PayOrderReq payOrderReq) {
            this.payOrderReq = payOrderReq;
        }

        public RecipeOrderDocument getRegBack() {
            return regBack;
        }

        public void setRegBack(RecipeOrderDocument regBack) {
            this.regBack = regBack;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

    }

    public static class CheckRecipeFailCanRefundBizObject {

        private Order order;
        private RecipeOrderDocument regBack;
        private Map<String, String> requestOrderNoMap;
        private RecipePaidDetailDocument detail;
        private BasicInfoDocument basicInfo;

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public RecipeOrderDocument getRegBack() {
            return regBack;
        }

        public void setRegBack(RecipeOrderDocument regBack) {
            this.regBack = regBack;
        }

        public Map<String, String> getRequestOrderNoMap() {
            return requestOrderNoMap;
        }

        public void setRequestOrderNoMap(Map<String, String> requestOrderNoMap) {
            this.requestOrderNoMap = requestOrderNoMap;
        }

        public RecipePaidDetailDocument getDetail() {
            return detail;
        }

        public void setDetail(RecipePaidDetailDocument detail) {
            this.detail = detail;
        }

        public BasicInfoDocument getBasicInfo() {
            return basicInfo;
        }

        public void setBasicInfo(BasicInfoDocument basicInfo) {
            this.basicInfo = basicInfo;
        }
    }

    public static RegBizObject generateRegBizObject(PayRegReq payRegReq, Order order, RegistrationDocument regBack,
                                                    RegistrationOrderReqDocument payOrderRegDocument) {
        RegBizObject o = new PayLogUtils.RegBizObject();
        o.payRegReg = payRegReq;
        o.order = order;
        o.regBack = regBack;
        o.payOrderRegDocument = payOrderRegDocument;
        return o;
    }

    public static <T> RecipeBizObject<T> generateRecipeBizObject(String orderNo, String channelId, T infoObj) {
        RecipeBizObject<T> o = new PayLogUtils.RecipeBizObject<T>();
        o.orderNo = orderNo;
        o.channelId = channelId;
        o.infoObj = infoObj;
        return o;
    }

    public static RegistrationAndOrderBizObject generateRegistrationAndOrderBizObject(Order order,
                                                                                      PayOrderReq payOrderReq, RecipeOrderDocument regBack, String channelId) {
        RegistrationAndOrderBizObject o = new RegistrationAndOrderBizObject();
        o.order = order;
        o.payOrderReq = payOrderReq;
        o.regBack = regBack;
        o.channelId = channelId;
        return o;
    }

    public static <T> PayOrderReqSource<T> generatePayOrderReqSource(Order order, T info) {
        PayOrderReqSource<T> o = new PayLogUtils.PayOrderReqSource<T>();
        o.order = order;
        o.infoObj = info;
        return o;
    }

    public static CheckRecipeFailCanRefundBizObject generateCheckRecipeFailCanRefundBizObject(Order order,
                                                                                              RecipeOrderDocument regBack, Map<String, String> requestOrderNoMap, BasicInfoDocument basicInfo,
                                                                                              RecipePaidDetailDocument detail) {
        CheckRecipeFailCanRefundBizObject o = new CheckRecipeFailCanRefundBizObject();
        o.order = order;
        o.regBack = regBack;
        o.requestOrderNoMap = requestOrderNoMap;
        o.detail = detail;
        o.basicInfo = basicInfo;
        return o;
    }
}
