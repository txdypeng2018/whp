package com.proper.enterprise.isj.function.recipe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.function.message.SaveSingleDetailMessageFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliPayTradeQueryRes;
import com.proper.enterprise.platform.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQueryRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQuerySingleOrderRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayQueryRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundQueryRes;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.checkRecipeFailCanRefund(Order, RecipeOrderDocument, Map<String, String>, BasicInfoDocument, RecipePaidDetailDocument)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class CheckRecipeFailCanRefundFunction implements IFunction<Boolean>, ILoggable{

    @Autowired
    FunctionToolkit toolkitx;
    
    @Autowired
    PayFactory payFactory;
    

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    
    @SuppressWarnings("unchecked")
    @Override
    public Boolean execute(Object... params) throws Exception {
        return checkRecipeFailCanRefund((Order) params[0], (RecipeOrderDocument) params[1],
                (Map<String, String>) params[2], (BasicInfoDocument) params[3], (RecipePaidDetailDocument) params[4]);
    }
    

    /**
     * 通知HIS缴费成功,HIS返回失败后,校验支付平台与HIS已缴费的金额是否相等,相等的条件在将多余的金额进行退款.
     *
     * @param order 订单.
     * @param regBack 挂号退款信息.
     * @param requestOrderNoMap 订单请求.
     * @param basicInfo 基本信息.
     * @param detail 详细信息.
     * @return 结果.
     * @throws Exception 异常.
     */
    private boolean checkRecipeFailCanRefund(Order order, RecipeOrderDocument regBack,
            Map<String, String> requestOrderNoMap, BasicInfoDocument basicInfo, RecipePaidDetailDocument detail)
            throws Exception {
        if (regBack == null || StringUtil.isEmpty(regBack.getClinicCode())) {
            debug("缴费项目为空,或者是缴费流水号为空,订单号:{}", order.getOrderNo());
            return false;
        }
        ResModel<PayList> paidRes = toolkitx.executeFunction(FindPayListModelFunction.class, basicInfo, regBack.getClinicCode(), "1", null, null, false);
        List<Pay> refundPayList = new ArrayList<>();
        if (paidRes == null) {
            debug("查询已缴费信息返回值不能进行解析,门诊流水号:{},订单号:{}", regBack.getClinicCode(), order.getOrderNo());
            toolkitx.executeFunction(SaveSingleDetailMessageFunction.class, regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }
        if (paidRes.getReturnCode() == ReturnCode.EMPTY_RETURN) {
            debug("HIS未查到已缴费项目,HIS返回信息:{}", paidRes.getReturnMsg());
        } else if (paidRes.getReturnCode() == ReturnCode.SUCCESS) {
            refundPayList = paidRes.getRes().getPayList();
        } else {
            debug("HIS查询已缴费信息出现异常,HIS返回信息:{}", paidRes.getReturnMsg());
            detail.setDescription("门诊流水号:".concat(regBack.getClinicCode()).concat("HIS查询已缴费信息出现异常,不进行自动退费,HIS返回信息:")
                    .concat(paidRes.getReturnMsg()));
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeOrderRepository.save(regBack);
            toolkitx.executeFunction(SaveSingleDetailMessageFunction.class, regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }
        List<RecipePaidDetailDocument> paidSuccessList = regBack.getRecipePaidDetailList();
        Set<String> seqSet = new HashSet<>();
        for (RecipePaidDetailDocument recipePaidDetailDocument : paidSuccessList) {
            debug("HIS查询记录的已缴项目的序号:{}", recipePaidDetailDocument.getHospSequence());
            seqSet.add(recipePaidDetailDocument.getHospSequence());
        }
        BigDecimal hisPaidBig = new BigDecimal("0");
        for (Pay pay : refundPayList) {
            if (!seqSet.contains(pay.getHospSequence())) {
                continue;
            }
            debug("HIS查询记录的已缴项目:{},缴费金额:{}", pay.getItemCode(), pay.getOwnCost());
            hisPaidBig = hisPaidBig.add(new BigDecimal(pay.getOwnCost()).abs());
        }
        BigDecimal queryBig = new BigDecimal("0");
        try {
            for (Map.Entry<String, String> paramEntry : requestOrderNoMap.entrySet()) {
                String orderNo = paramEntry.getKey();
                if (paramEntry.getValue().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
                    PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                    AliPayTradeQueryRes query = payService.queryPay(orderNo);
                    if (query != null) {
                        queryBig = queryBig.add(new BigDecimal(query.getTotalAmount()).multiply(new BigDecimal("100")));
                    } else {
                        debug("未查到支付宝支付信息,订单号:{}", orderNo);
                    }
                    AliRefundTradeQueryRes refundQuery = payService.queryRefund(orderNo, orderNo.concat("001"));
                    if (refundQuery != null && StringUtil.isNotEmpty(refundQuery.getRefundAmount())) {
                        queryBig = queryBig.subtract(
                                new BigDecimal(refundQuery.getRefundAmount()).multiply(new BigDecimal("100")));
                    }
                } else if (paramEntry.getValue().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
                    PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                    WechatPayQueryRes query = payService.queryPay(orderNo);
                    if (query != null) {
                        queryBig = queryBig.add(new BigDecimal(query.getTotalFee()));
                    } else {
                        debug("未查到微信支付信息,订单号:{}", orderNo);
                    }
                    WechatRefundQueryRes refundQuery = payService.queryRefund(orderNo, orderNo.concat("001"));
                    if (refundQuery != null && StringUtil.isNotEmpty(refundQuery.getRefundFee())) {
                        queryBig = queryBig.subtract(new BigDecimal(refundQuery.getRefundFee()));
                    }
                } else if (paramEntry.getValue().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
                    // 查询缴费订单信息
                    PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
                    CmbQuerySingleOrderRes query = payService.queryPay(orderNo);
                    // 查询支付成功
                    if (query != null && StringUtil.isEmpty(query.getHead().getCode())) {
                        queryBig = queryBig
                                .add(new BigDecimal(query.getBody().getBillAmount()).multiply(new BigDecimal("100")));
                    } else {
                        debug("未查到一网通支付信息,订单号:{}", orderNo);
                    }
                    // 退款信息查询
                    CmbQueryRefundRes refund = payService.queryRefund(orderNo, orderNo.concat("01"));
                    if (refund != null && refund.getBody().getBillRecord() != null
                            && StringUtil.isNotEmpty(refund.getBody().getBillRecord().get(0).getAmount())) {
                        queryBig = queryBig.subtract(new BigDecimal(refund.getBody().getBillRecord().get(0).getAmount())
                                .multiply(new BigDecimal("100")));
                    }
                }
            }
        } catch (Exception e) {
            debug("计算支付平台已缴金额发生异常,门诊流水号:{},订单号:{}{}", regBack.getClinicCode(), order.getOrderNo(), e);
            toolkitx.executeFunction(SaveSingleDetailMessageFunction.class, regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }

        debug("支付平台结余金额:{},当前缴费金额:{},HIS已缴费金额:{}", queryBig, order.getOrderAmount(), hisPaidBig);
        detail.setDescription(
                detail.getDescription().concat(",支付平台结余金额:".concat(String.valueOf(queryBig)).concat(",当前缴费金额:")
                        .concat(order.getOrderAmount()).concat(",HIS已缴费金额:").concat(String.valueOf(hisPaidBig))));
        if (queryBig.subtract(new BigDecimal(order.getOrderAmount())).compareTo(hisPaidBig) != 0) {
            debug("缴费失败后,HIS已缴费金额与支付平台的金额不相等,不能进行退款,门诊流水号:{},订单号:{}", regBack.getClinicCode(),
                    order.getOrderNo());
            regBack.getRecipePaidFailDetailList().add(detail);
            RecipePaidDetailDocument nonPaid = new RecipePaidDetailDocument();
            regBack.setRecipeNonPaidDetail(nonPaid);
            recipeOrderRepository.save(regBack);
            toolkitx.executeFunction(SaveSingleDetailMessageFunction.class, regBack, SendPushMsgEnum.RECIPE_PAID_REFUND_FAIL, regBack);
            return false;
        }
        return true;
    }

}
