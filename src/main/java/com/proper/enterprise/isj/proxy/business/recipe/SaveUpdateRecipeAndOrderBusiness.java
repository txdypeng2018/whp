package com.proper.enterprise.isj.proxy.business.recipe;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.context.ChannelIdContext;
import com.proper.enterprise.isj.context.InfoObjContext;
import com.proper.enterprise.isj.context.OrderNumContext;
import com.proper.enterprise.isj.context.RecipeOrderDocIdContext;
import com.proper.enterprise.isj.context.RecipeOrderDocumentContext;
import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.function.message.SaveSingleDetailMessageFunction;
import com.proper.enterprise.isj.function.message.SendRecipeMsgFunction;
import com.proper.enterprise.isj.function.recipe.CheckRecipeFailCanRefundFunction;
import com.proper.enterprise.isj.function.recipe.ConvertAppInfo2PayOrderFunction;
import com.proper.enterprise.isj.function.recipe.FetchRecipeRequestOrderNoMapFunction;
import com.proper.enterprise.isj.function.recipe.RefundMoney2UserFunction;
import com.proper.enterprise.isj.function.recipe.SaveOrUpdateOrderFailInfoFunction;
import com.proper.enterprise.isj.function.recipe.UpdateRegistrationAndOrderFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.logger.PayStepEnum;
import com.proper.enterprise.isj.payment.logger.utils.PayLogUtils;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderReqDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.enmus.QueryType;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.saveUpdateRecipeAndOrder(String, String, Object)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 * @param <M>
 */
@Service
public class SaveUpdateRecipeAndOrderBusiness<M extends OrderNumContext<Order> & ChannelIdContext<Order> & InfoObjContext<Order> & ModifiedResultBusinessContext<Order>>
        implements IBusiness<Order, M>, ILoggable {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    BusinessToolkit toolkitx;
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    OrderService orderService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(saveUpdateRecipeAndOrder(ctx.getOrderNum(), ctx.getChannelId(), ctx.getInfoObj()));
    }

    /**
     * 保存缴费及订单信息(异步通知调用).
     *
     * @param orderNo 订单号.
     * @param channelId 支付方式.
     * @param infoObj 支付对象.
     * @return order 订单信息.
     * @throws Exception 异常.
     */
    public Order saveUpdateRecipeAndOrder(String orderNo, String channelId, Object infoObj) throws Exception {
        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            debug("未查到缴费订单,调用缴费前校验,订单号:{}", orderNo);
            return null;
        }
        RecipeOrderDocument regBack = toolkitx.execute(FetchRecipeOrderDocumentByIdBusiness.class, (c) -> {
            ((RecipeOrderDocIdContext<?>) c).setRecipeOrderDocId(orderNo);
        });
        if (regBack == null) {
            debug("未查到缴费单,调用缴费前校验,订单号:{}", orderNo);
            return null;
        }
        BasicInfoDocument basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(regBack.getCreateUserId(),
                regBack.getPatientId());
        if (basicInfo == null) {
            debug("未查到患者信息,调用缴费前校验,门诊流水号:{}", regBack.getClinicCode());
            return null;
        }
        if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
            debug("患者信息的病历号为空,不能进行调用,调用缴费前校验,门诊流水号:{},患者Id:", regBack.getClinicCode(), basicInfo.getId());
            return null;
        }
        // 手动清空患者缴费缓存
        for (QueryType queryType : QueryType.values()) {
            webService4HisInterfaceCacheUtil.evictCachePayListRes(regBack.getPatientId(), queryType.name(),
                    basicInfo.getMedicalNum());
        }
        Map<String, String> requestOrderNoMap = toolkitx.executeFunction(FetchRecipeRequestOrderNoMapFunction.class,
                regBack);
        if (requestOrderNoMap.containsKey(order.getOrderNo())) {
            debug("订单号重复调用缴费接口,直接返回,不对HIS接口进行调用,门诊流水号:{}", regBack.getClinicCode());
            return null;
        }
        try {
            PayOrderReq payOrderReq = toolkitx.executeSimpleFunction(ConvertAppInfo2PayOrderFunction.class, order,
                    infoObj);
            if (payOrderReq == null) {
                debug("诊间缴费请求参数转换异常,订单号:{}", order.getOrderNo());
                throw new RecipeException("诊间缴费请求参数转换异常,订单号:".concat(order.getOrderNo()));
            }
            RecipeOrderReqDocument payReq = new RecipeOrderReqDocument();
            BeanUtils.copyProperties(payOrderReq, payReq);
            Map<String, RecipeOrderReqDocument> reqMap = regBack.getRecipeOrderReqMap();
            reqMap.put(payOrderReq.getOrderId(), payReq);
            regBack.setRecipeOrderReqMap(reqMap);
            toolkitx.execute(SaveRecipeOrderDocumentBusiness.class, (c) -> {
                ((RecipeOrderDocumentContext<?>) c).setRecipeOrderDocument(regBack);
            });
            toolkitx.executeFunction(UpdateRegistrationAndOrderFunction.class, order, payOrderReq, regBack, channelId);
        } catch (Exception e) {

            if (e instanceof DelayException) {
                PayLogUtils.log(PayStepEnum.UNKNOWN, order, ((DelayException) e).getPosition());
                Map<String, Object> tmp = new HashMap<>();
                tmp.put("doc", regBack);
                tmp.put("order", order);

                toolkitx.executeFunction(SendRecipeMsgFunction.class, regBack,
                        SendPushMsgEnum.RECIPE_REFUND_FAIL_CAUSE_BY_NET, tmp);
            } else {

                info("诊间缴费出现异常", e);
                String refundNo = order.getOrderNo() + "001";
                if (order.getPayWay().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                    refundNo = order.getOrderNo().substring(0, 18).concat("01");
                }
                RecipePaidDetailDocument detail = regBack.getRecipeNonPaidDetail();
                if (detail == null) {
                    detail = new RecipePaidDetailDocument();
                }
                detail.setRefundNum(refundNo);
                debug("退款单号:{}{}", refundNo, e);
                if (StringUtil.isEmpty(detail.getDescription())) {
                    detail.setDescription("");
                }
                debug("保存异常消息前,退款单号:{}", refundNo);
                if (StringUtil.isNotEmpty(e.getMessage())) {
                    detail.setDescription(detail.getDescription().concat(",").concat(e.getMessage()));
                }
                debug("保存异常消息后,退款单号:{}", refundNo);
                requestOrderNoMap.put(order.getOrderNo(), String.valueOf(order.getPayWay()));
                debug("将订单号添加到计算平台缴费情况的Map中,退款单号:{},订单号:{}", refundNo, order.getOrderNo());
                if (!toolkitx.<Boolean>executeFunction(CheckRecipeFailCanRefundFunction.class, order, regBack,
                        requestOrderNoMap, basicInfo, detail)) {
                    debug("诊间缴费失败后,核对平台与HIS已缴金额不一致,不能进行退款,退款单号:{},订单号:{}", refundNo, order.getOrderNo());
                    return null;
                }
                try {
                    boolean refundFlag = toolkitx.executeFunction(RefundMoney2UserFunction.class, order, refundNo,
                            false);
                    order = toolkitx.executeFunction(SaveOrUpdateOrderFailInfoFunction.class, order, channelId, regBack,
                            refundNo, detail, refundFlag);
                    regBack.getRecipePaidFailDetailList().add(detail);
                    RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
                    regBack.setRecipeNonPaidDetail(nonPaid);
                    toolkitx.execute(SaveRecipeOrderDocumentBusiness.class, (c) -> {
                        ((RecipeOrderDocumentContext<?>) c).setRecipeOrderDocument(regBack);
                    });
                } catch (Exception e2) {
                    debug("RecipeServiceNotxImpl.saveUpdateRecipeAndOrder[Exception]:", e2);
                    debug("诊间缴费HIS抛异常,调用支付平台退费失败,订单号:{},退费单号:{}", order.getOrderNo(), refundNo);
                    detail.setDescription(detail.getDescription().concat(",诊间缴费HIS抛异常,调用支付平台退费失败,订单号:"
                            .concat(order.getOrderNo()).concat(",退费单号:").concat(refundNo)));
                    regBack.getRecipePaidFailDetailList().add(detail);
                    RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
                    regBack.setRecipeNonPaidDetail(nonPaid);
                    toolkitx.execute(SaveRecipeOrderDocumentBusiness.class, (c) -> {
                        ((RecipeOrderDocumentContext<?>) c).setRecipeOrderDocument(regBack);
                    });
                    toolkitx.executeFunction(SaveSingleDetailMessageFunction.class, regBack,
                            SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
                }
            }
        }
        // 手动清空患者缴费缓存
        for (QueryType queryType : QueryType.values()) {
            webService4HisInterfaceCacheUtil.evictCachePayListRes(regBack.getPatientId(), queryType.name(),
                    basicInfo.getMedicalNum());
        }
        return order;
    }

}
