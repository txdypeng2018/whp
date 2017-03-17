package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl.saveNonPaidCancelRegistration(String,
 * RegistrationDocument, String, String, String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveNonPaidCancelRegistrationFunction implements IFunction<Object> {

    @Autowired
    OrderService orderService;

    @Autowired
    SaveRegistrationDocumentFunction saveRegDocFunc;

    @Override
    public Object execute(Object... params) throws Exception {
        saveNonPaidCancelRegistration((String) params[0], (RegistrationDocument) params[1], (String) params[2],
                (String) params[3], (String) params[4]);
        return null;
    }

    /**
     * 修改退号未支付的状态.
     *
     * @param registrationId 挂号ID.
     * @param reg 挂号请求.
     * @param cancelTime 取消时间.
     * @param cancelRemark 取消标记.
     * @param regStatusCode 挂号状态码.
     */
    private void saveNonPaidCancelRegistration(String registrationId, RegistrationDocument reg, String cancelTime,
            String cancelRemark, String regStatusCode) {
        if (reg.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
            reg.setStatusCode(regStatusCode);
            reg.setStatus(CenterFunctionUtils.getRegistrationStatusName(regStatusCode));
            FunctionUtils.invoke(saveRegDocFunc, reg);
            Order order = orderService.getByFormId(registrationId);
            if (order != null) {
                order.setCancelRemark(cancelRemark);
                order.setCancelDate(cancelTime);
                order.setOrderStatus(String.valueOf(0));
                orderService.save(order);
            }
        }
    }
}
