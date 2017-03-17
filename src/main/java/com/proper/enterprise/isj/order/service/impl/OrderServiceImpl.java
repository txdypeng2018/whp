package com.proper.enterprise.isj.order.service.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.registration.CancelRegistrationFunction;
import com.proper.enterprise.isj.function.registration.CheckOrderIsPaidFunction;
import com.proper.enterprise.isj.function.registration.SaveCreateOrderFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.support.service.AbstractService;

@Service
public class OrderServiceImpl extends AbstractService implements OrderService {

    @Override
    public Order save(Order order) {
        return toolkit.executeRepositoryFunction(OrderRepository.class, "save", order);
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        return toolkit.executeRepositoryFunction(OrderRepository.class, "findByOrderNo", orderNo);
    }

    @Override
    public Order getByFormId(String formId) {
        return toolkit.executeRepositoryFunction(OrderRepository.class, "getByFormId", formId);
    }

    @Override
    public void cancelRegistration(String orderNo) throws Exception {
        toolkit.executeFunction(CancelRegistrationFunction.class, orderNo);
    }

    @Override
    public Order saveCreateOrder(Object formOrder) {
        return toolkit.executeFunction(SaveCreateOrderFunction.class, formOrder);
    }

    @Override
    public void deleteOrder(Order order) {
        if (order != null) {
            toolkit.executeFunction(OrderRepository.class, "delete", order);
        }
    }

    @Override
    public boolean checkOrderIsPay(String payChannelId, String orderNum) throws Exception {
        return toolkit.executeFunction(CheckOrderIsPaidFunction.class, payChannelId, orderNum);
    }
}
