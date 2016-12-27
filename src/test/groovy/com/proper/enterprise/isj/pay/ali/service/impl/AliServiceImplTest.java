package com.proper.enterprise.isj.pay.ali.service.impl;

import com.proper.enterprise.isj.order.entity.OrderEntity;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Primary
public class AliServiceImplTest extends AliServiceImpl implements AliService {

    @Autowired
    private OrderRepository repository;

    @Override
    public boolean verify(Map<String, String> params) throws Exception {
        return true;
    }

    @Override
    public boolean saveAliNoticeProcess(String orderNo, Map<String, String> params, String dealType) throws Exception {
        OrderEntity order = new OrderEntity(orderNo, 2, 0);
        order.setFormId(RandomStringUtils.randomNumeric(10));
        order.setOrderStatus("2");
        repository.save(order);
        // avoid async task quickly end
        Thread.sleep(10);
        return true;
    }

}