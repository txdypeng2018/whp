package com.proper.enterprise.isj.function.payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.pay.enums.PayResType;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SaveCheckOrderFunction implements IFunction<PayResultRes>, ILoggable{

    @Autowired
    OrderService orderService;
    
    @Autowired
    @Qualifier("defaultBusinessToolkit")
    protected BusinessToolkit toolkitx;
    
    @Autowired
    RecipeService recipeService;
    
    @Autowired
    RegistrationService registrationService;
    
    @Override
    public PayResultRes execute(Object... params) throws Exception {
        return saveCheckOrder((PayChannel)params[0], (String)params[1], (String)params[2]);
    }
    
    public PayResultRes saveCheckOrder(PayChannel payChannel, String outTradeNo, String totalFee) {
        PayResultRes resObj = new PayResultRes();
        try {
            Order order = null;
            // 一网通支付
            if(payChannel == PayChannel.WEB_UNION) {
                String cmbOrderNo = outTradeNo.substring(0, 18);
                List<OrderEntity> orderList  = toolkitx.executeRepositoryFunction(OrderRepository.class, "findByOrderNoLike", "%".concat(cmbOrderNo).concat("%"));
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
            debug("预支付订单处理异常", e);
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.ORDER_SAVE_ERR);
            return resObj;
        }
        return resObj;
    }

}
