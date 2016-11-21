package com.proper.enterprise.isj.order.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    private WebServicesClient webServicesClient;

    @Override
    public Order save(Order order) {
        return orderRepo.save((OrderEntity) order);
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        return orderRepo.findByOrderNo(orderNo);
    }

    @Override
    public Order getByFormId(String formId) {
        return orderRepo.getByFormId(formId);
    }

    @Override
    public void cancelRegistration(String orderNo) throws Exception {
        String hosId = CenterFunctionUtils.getHosId();
        Order order = this.findByOrderNo(orderNo);
        String cancelTime = DateUtil.toTimestamp(new Date());
        webServicesClient.cancelReg(hosId, orderNo, cancelTime, CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG);
        order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_OVERTIME_MSG);
        order.setCancelDate(cancelTime);
        order.setOrderStatus(String.valueOf(0));
        this.save(order);
    }

    @Override
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
            orderInfo.setFormId(recipeOrder.getId().concat("_").concat(recipeOrder.getRecipeNonPaidDetail().getOrderNum()));
            orderInfo.setOrderAmount(recipeOrder.getRecipeNonPaidDetail().getAmount());
        }
        orderInfo = this.save(orderInfo);
        return orderInfo;
    }

    @Override
    public void deleteOrder(Order order) {
        if(order!=null){
            orderRepo.delete((OrderEntity)order);
        }
    }
}