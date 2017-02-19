package com.proper.enterprise.isj.payment.service.impl;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.service.BusinessPayService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.pay.enums.PayResType;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 支付ServiceImpl.
 */
@Service
public class BusinessPayServiceImpl implements BusinessPayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessPayServiceImpl.class);

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RegistrationService registrationService;

    /**
     * 取得支付时间时效对象.
     *
     * @param outTradeNo 订单号.
     * @param prepayReq 预支付对象.
     * @return 处理结果
     */
    @Override
    public PayResultRes getPayTimeRes(String outTradeNo, PrepayReq prepayReq) {
        PayResultRes resObj = new PayResultRes();
        // 查询订单
        Order order = orderService.findByOrderNo(outTradeNo);
        // 查到订单信息
        if(order != null){
            // 获取订单生成日期
            Date cTime = DateUtil.toDate(order.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT);
            Calendar cal = Calendar.getInstance();
            cal.setTime(cTime);
            // 设定订单开始时间
            prepayReq.setPayTime(DateUtil.toString(cTime, "yyyyMMddHHmmss"));
            cal.add(Calendar.MINUTE, CenterFunctionUtils.ORDER_COUNTDOWN);
            Date nowDate = new Date();
            // 计算订单失效时间
            long min = (cal.getTimeInMillis() - nowDate.getTime()) / (60 * 1000);
            // 订单已经超时
            if (min < 0) {
                resObj.setResultCode(PayResType.SYSERROR);
                resObj.setResultMsg(CenterFunctionUtils.ORDER_OVERTIME_INVALID);
                return resObj;
            }
            // 设定超时时间
            prepayReq.setOverMinuteTime(String.valueOf(min + 1));
        } else {
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.ORDER_NON_DATA_ERR);
            return resObj;
        }
        return resObj;
    }

    /**
     * 预支付订单校验.
     *
     * @param payChannel 支付渠道.
     * @param outTradeNo 订单号.
     * @param totalFee 支付金额(以分为单位).
     * @return 校验结果对象
     */
    @Override
    public PayResultRes saveCheckOrder(PayChannel payChannel, String outTradeNo, String totalFee) {
        PayResultRes resObj = new PayResultRes();
        try {
            Order order = null;
            // 一网通支付
            if(payChannel == PayChannel.WEB_UNION) {
                String cmbOrderNo = outTradeNo.substring(0, 18);
                List<OrderEntity> orderList  = orderRepo.findByOrderNoLike("%".concat(cmbOrderNo).concat("%"));
                // 获取查询条件并且只有一条
                if(orderList != null && orderList.size() == 1) {
                    order = orderList.get(0);
                } else {
                    resObj.setResultCode(PayResType.SYSERROR);
                    resObj.setResultMsg(CenterFunctionUtils.ORDER_CMB_PAY_ERR);
                    return resObj;
                }
                // 微信以及支付宝
            } else {
                order = orderService.findByOrderNo(outTradeNo);
            }
            // 处理预支付订单(是否已经支付等等)
            if(order != null){
                // 缴费
                if(order.getFormClassInstance().equals(RecipeOrderDocument.class.getName())){
                    RecipeOrderDocument recipe = recipeService.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
                    // 存在对应的缴费订单
                    if (recipe != null) {
                        boolean flag = recipeService.checkRecipeAmount(outTradeNo, totalFee, payChannel);
                        recipe = recipeService.getRecipeOrderDocumentById(order.getFormId().split("_")[0]);
                        if (!flag || (StringUtil.isEmpty(recipe.getRecipeNonPaidDetail().getPayChannelId()))) {
                            resObj.setResultCode(PayResType.SYSERROR);
                            resObj.setResultMsg(CenterFunctionUtils.ORDER_DIFF_RECIPE_ERR);
                        }
                        // 未找到对应的缴费订单
                    } else {
                        resObj.setResultCode(PayResType.SYSERROR);
                        resObj.setResultMsg(CenterFunctionUtils.ORDER_NON_RECIPE_ERR);
                    }
                    // 挂号
                } else {
                    RegistrationDocument reg = registrationService.getRegistrationDocumentById(order.getFormId());
                    // 存在对应的挂号订单
                    if (reg != null) {
                        String payWay = reg.getPayChannelId();
                        boolean paidFlag = orderService.checkOrderIsPay(payWay, reg.getOrderNum());
                        // 校验HIS已支付订单
                        if (reg.getRegistrationOrderReq() != null
                                && StringUtil.isNotEmpty(reg.getRegistrationOrderReq().getPayChannelId())) {
                            paidFlag = true;
                        }
                        // 未支付过的订单
                        if (!paidFlag) {
                            reg.setPayChannelId(String.valueOf(payChannel.getCode()));
                            registrationService.saveRegistrationDocument(reg);
                            order.setPayWay(String.valueOf(payChannel.getCode()));
                            orderService.save(order);
                            // 已经支付过的订单
                        } else {
                            resObj.setResultCode(PayResType.SYSERROR);
                            resObj.setResultMsg(CenterFunctionUtils.ORDER_ALREADY_PAID_ERR);
                        }
                        // 未找到对应的挂号订单
                    }else{
                        resObj.setResultCode(PayResType.SYSERROR);
                        resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
                    }
                }
            }else{
                resObj.setResultCode(PayResType.SYSERROR);
                resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
            }
        } catch (Exception e) {
            LOGGER.debug("预支付订单处理异常", e);
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
            return resObj;
        }
        return resObj;
    }
}
