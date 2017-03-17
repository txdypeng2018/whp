package com.proper.enterprise.isj.function.recipe;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;
import com.proper.enterprise.platform.api.pay.factory.PayFactory;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.pay.ali.model.AliRefundTradeQueryRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbBillRecordRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbQueryRefundRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundQueryRes;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.checkRecipeAndRefundIsEqual(RecipeOrderDocument, RecipePaidDetailDocument, RefundByHis)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class CheckRecipeAndRefundIsEqualFunction implements IFunction<String>, ILoggable{

    @Autowired
    FunctionToolkit toolkitx;
    
    @Autowired
    UserInfoService userInfoService;
    
    @Autowired
    PayFactory payFactory;
    
    @Override
    public String execute(Object... params) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * 支付平台某个订单的退款总数小于等于HIS的退款总数
     *
     * @param recipe 退款报文.
     * @param recipePaidOrder 已付款信息.
     * @param refund 退款历史.
     * @return 退款总数.
     * @throws Exception 异常.
     */
    public String checkRecipeAndRefundIsEqual(RecipeOrderDocument recipe, RecipePaidDetailDocument recipePaidOrder,
            RefundByHis refund) throws Exception {
        BasicInfoDocument basic = userInfoService.getFamilyMemberByUserIdAndMemberId(recipe.getCreateUserId(),
                recipe.getPatientId());
        ResModel<PayList> refundPayRes = toolkitx.executeFunction(FindPayListModelFunction.class, basic, recipe.getClinicCode(), "2", null, null, false);
        List<Pay> refundPayList = refundPayRes.getRes().getPayList();
        debug("HIS查询记录的退费集合数:{},退款Id:{},订单号:{}", refundPayList.size(), refund.getId(),
                recipePaidOrder.getOrderNum(), recipe.getClinicCode());
        BigDecimal hisRefundBig = new BigDecimal("0");
        for (Pay pay : refundPayList) {
            debug("HIS查询记录的退费项目:{},退费金额:{}", pay.getItemCode(), pay.getOwnCost());
            hisRefundBig = hisRefundBig.add(new BigDecimal(pay.getOwnCost()).abs());
        }
        BigDecimal finishBig = new BigDecimal("0");
        // 支付宝以及微信
        DecimalFormat df = new DecimalFormat("000");
        // 一网通因为只允许20为退款流水号,所以format为00
        DecimalFormat dfCmb = new DecimalFormat("00");
        String refundNo = null;
        if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.ALIPAY.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                refundNo = recipePaidOrder.getOrderNum() + df.format(tempIndex);
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_ALI);
                AliRefundTradeQueryRes refundQuery = payService.queryRefund(recipePaidOrder.getOrderNum(), refundNo);
                // 退款查询结果
                if (refundQuery != null) {
                    if (refundQuery.getCode().equals("10000")) {
                        if (StringUtil.isNotEmpty(refundQuery.getRefundAmount())) {
                            finishBig = finishBig
                                    .add(new BigDecimal(refundQuery.getRefundAmount()).multiply(new BigDecimal("100")));
                        } else {
                            break;
                        }
                    } else {
                        debug("支付宝缴费查询失败,订单号:{},退费单号:{},失败消息:{}", recipePaidOrder.getOrderNum(), refundNo,
                                refundQuery.getMsg());
                        return null;
                    }
                } else {
                    debug("支付宝缴费查询失败,订单号:{},退费单号:{},查询返回值为空", recipePaidOrder.getOrderNum(), refundNo);
                    return null;
                }
            }
        } else if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.WECHATPAY.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                refundNo = recipePaidOrder.getOrderNum() + df.format(tempIndex);
                // 微信退款查询
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
                WechatRefundQueryRes refundQuery = payService.queryRefund(recipePaidOrder.getOrderNum(), refundNo);
                if (refundQuery != null) {
                    if (refundQuery.getReturnCode().equals("SUCCESS")) {
                        if (StringUtil.isNotEmpty(refundQuery.getRefundFee())) {
                            finishBig = finishBig.add(new BigDecimal(refundQuery.getRefundFee()));
                        } else {
                            break;
                        }
                    } else {
                        debug("微信缴费查询失败,订单号:{},退费单号:{},未找到退费的标签", recipePaidOrder.getOrderNum(), refundNo);
                        return null;
                    }
                } else {
                    debug("微信缴费查询失败,订单号:{},退费单号:{},查询返回值为空", recipePaidOrder.getOrderNum(), refundNo);
                    return null;
                }
            }
        } else if (recipePaidOrder.getPayChannelId().equals(String.valueOf(PayChannel.WEB_UNION.getCode()))) {
            int tempIndex = 0;
            while (tempIndex <= 500) {
                tempIndex++;
                // 一网通退款信息查询
                refundNo = recipePaidOrder.getOrderNum().substring(0, 18).concat(dfCmb.format(tempIndex));
                PayService payService = payFactory.newPayService(BusinessPayConstants.ISJ_PAY_WAY_CMB);
                CmbQueryRefundRes refundQuery = payService.queryRefund(recipePaidOrder.getOrderNum(), refundNo);
                if (refundQuery != null) {
                    // 如果查询有该订单的退款信息
                    if (StringUtil.isEmpty(refundQuery.getHead().getCode())) {
                        CmbBillRecordRes billRecord = refundQuery.getBody().getBillRecord().get(0);
                        // 如果订单已经直接退款成功
                        if (billRecord.getBillState().equals("210")) {
                            if (StringUtil.isNotEmpty(billRecord.getAmount())) {
                                finishBig = finishBig
                                        .add(new BigDecimal(billRecord.getAmount()).multiply(new BigDecimal("100")));
                            } else {
                                break;
                            }
                        } else {
                            debug("一网通缴费查询失败,订单号:{},退费单号:{},失败消息:{}", recipePaidOrder.getOrderNum(), refundNo,
                                    refundQuery.getHead().getErrMsg());
                            return null;
                        }
                    } else {
                        debug("一网通退款查询失败,无此退款订单号");
                        break;
                    }
                } else {
                    debug("一网通缴费查询失败,订单号:{},退费单号:{},查询返回值为空", recipePaidOrder.getOrderNum(), refundNo);
                    return null;
                }
            }
        }
        debug("查询支付平台结束,获得对应的退款钱数:{}", finishBig.toString());
        if (finishBig.add(new BigDecimal(refund.getCost()).abs()).compareTo(hisRefundBig) > 0) {
            debug("已退金额:{},待退金额:{},退款总额:{}", finishBig.intValue(),
                    new BigDecimal(refund.getCost()).abs().intValue(), hisRefundBig.intValue());
            debug("平台退费Id:{},门诊流水号:{},处方号:{},退款金额大于总退款金额", refund.getId(), refund.getClinicCode(),
                    refund.getRecipeNo());
            return null;
        } else {
            debug("退费金额校验成功后,返回的支付平台的退款单号:{}", refundNo);
            return refundNo;
        }
    }

}
