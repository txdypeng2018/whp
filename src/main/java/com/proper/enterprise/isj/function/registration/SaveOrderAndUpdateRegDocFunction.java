package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * old: com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl.saveOrderAndUpdateReg(RegistrationDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveOrderAndUpdateRegDocFunction implements IFunction<OrderRegReq> {

    @Autowired
    ConvertRegistration2OrderRegFunction convertRegistration2OrderRegFunction;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    OrderService orderService;

    @Override
    public OrderRegReq execute(Object... params) throws Exception {
        return saveOrderAndUpdateReg((RegistrationDocument) params[0]);
    }

    /**
     * 生成未缴费订单,更新挂号单.
     *
     * @param saveReg 挂号单.
     * @return 订单.
     */
    private OrderRegReq saveOrderAndUpdateReg(RegistrationDocument saveReg) {
        Order orderInfo = orderService.saveCreateOrder(saveReg);
        saveReg.setOrderNum(orderInfo.getOrderNo());
        OrderRegReq orderReg = FunctionUtils.invoke(convertRegistration2OrderRegFunction, saveReg);
        RegistrationOrderReqDocument orderReq = new RegistrationOrderReqDocument();
        BeanUtils.copyProperties(orderReg, orderReq);
        saveReg.setRegistrationOrderReq(orderReq);
        registrationRepository.save(saveReg);
        return orderReg;
    }
}
