package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.ConfCenter;

@Service
public class SaveCreateOrderFunction implements IFunction<Order> {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    BusinessToolkit toolkitx;

    @Override
    public Order execute(Object... params) throws Exception {
        return saveCreateOrder(params[0]);
    }

    public Order saveCreateOrder(Object formOrder) {
        // 生成订单号
        String orderNo = CenterFunctionUtils.createRegOrOrderNo(2);
        // 设置订单信息
        Order orderInfo = new OrderEntity();
        // 支付状态 0:未支付
        orderInfo.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.unpay"));
        // 订单号
        orderInfo.setOrderNo(orderNo);
        // 逻辑删除 0:正常
        orderInfo.setIsdel(ConfCenter.getInt("isj.pay.isdel.nomarl"));
        if (formOrder instanceof RegistrationDocument) {
            RegistrationDocument registrationDocument = (RegistrationDocument) formOrder;
            orderInfo.setFormClassInstance(registrationDocument.getClass().getName());
            orderInfo.setFormId(registrationDocument.getId());
            orderInfo.setOrderAmount(registrationDocument.getAmount());
        } else if (formOrder instanceof RecipeOrderDocument) {
            RecipeOrderDocument recipeOrder = (RecipeOrderDocument) formOrder;
            orderInfo.setFormClassInstance(recipeOrder.getClass().getName());
            orderInfo.setOrderNo(recipeOrder.getRecipeNonPaidDetail().getOrderNum());
            orderInfo.setFormId(
                    recipeOrder.getId().concat("_").concat(recipeOrder.getRecipeNonPaidDetail().getOrderNum()));
            orderInfo.setOrderAmount(recipeOrder.getRecipeNonPaidDetail().getAmount());
        }
        orderInfo = toolkitx.executeFunction(OrderRepository.class, "save", orderInfo);
        return orderInfo;
    }

}
