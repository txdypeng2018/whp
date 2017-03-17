package com.proper.enterprise.isj.function.registration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.updateOrderEntity(RegistrationDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class UpdateOrderEntityFunction implements IFunction<Object> {

    @Autowired
    OrderService orderService;

    @Override
    public Object execute(Object... params) throws Exception {
        updateOrderEntity((RegistrationDocument) params[0]);
        return null;
    }

    /**
     * 更新订单信息.
     *
     * @param reg 挂号单信息.
     */
    private void updateOrderEntity(RegistrationDocument reg) {
        Order order = orderService.findByOrderNo(reg.getOrderNum());
        if (order != null) {
            order.setOrderStatus("3");
            order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_PLATFORM_MSG);
            order.setCancelDate(DateUtil.toTimestamp(new Date()));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
            orderService.save(order);
        }
    }

}
