package com.proper.enterprise.isj.pay.weixin.service.impl
import com.proper.enterprise.isj.order.entity.OrderEntity
import com.proper.enterprise.isj.order.repository.OrderRepository
import com.proper.enterprise.isj.pay.weixin.model.UnifiedNoticeRes
import com.proper.enterprise.isj.pay.weixin.service.WeixinService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class MockWeixinServiceImpl extends WeixinServiceImpl implements WeixinService {

    @Autowired
    OrderRepository repository

    @Override
    boolean saveWeixinNoticeProcess(UnifiedNoticeRes res) throws Exception {
        OrderEntity order = new OrderEntity(res.getOutTradeNo(), 2, 0);
        order.setFormId(RandomStringUtils.randomNumeric(10));
        order.setOrderStatus("2");
        repository.save(order);
        // avoid async task quickly end
        Thread.sleep(200);
        return true;
    }

    @Override
    boolean isValid(UnifiedNoticeRes noticeRes) {
        true
    }
}
