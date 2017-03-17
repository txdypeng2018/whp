package com.proper.enterprise.isj.function.registration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.repository.OrderRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * com.proper.enterprise.isj.order.service.impl.OrderServiceImpl.cancelRegistration(String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class CancelRegistrationFunction implements IFunction<Object> {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Override
    public Object execute(Object... params) throws Exception {
        cancelRegistration((String) params[0]);
        return null;
    }

    public void cancelRegistration(String orderNo) throws Exception {
        String hosId = CenterFunctionUtils.getHosId();
        Order order = toolkitx.executeFunction(OrderRepository.class, "findByOrderNo", orderNo);
        String cancelTime = DateUtil.toTimestamp(new Date());
        webServicesClient.cancelReg(hosId, orderNo, cancelTime, CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG);
        order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_OVERTIME_MSG);
        order.setCancelDate(cancelTime);
        order.setOrderStatus(String.valueOf(0));
        toolkitx.executeFunction(OrderRepository.class, "save", order);
    }

}
