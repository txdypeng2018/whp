package com.proper.enterprise.isj.function.registration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderProcessDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SetOrderProcess2RegistrationFunction implements IFunction<Object> {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Override
    public Object execute(Object... params) throws Exception {
        setOrderProcess2Registration((RegistrationDocument) params[0]);
        return null;
    }

    /**
     * 挂号单订单流程
     *
     * @param registration 挂号单信息.
     */
    public void setOrderProcess2Registration(RegistrationDocument registration) {
        List<RegistrationOrderProcessDocument> orders = registration.getOrders();
        // 订单流程(待支付).
        toolkitx.executeFunction(NotPaidProcessFunction.class, registration, orders);
        toolkitx.executeFunction(FillOrder2PayPlatformFunction.class, registration, orders);
        if (orders.size() > 1) {
            toolkitx.executeFunction(FetchHospitalConfirmFunction.class, registration, orders);
        }
        // 预约挂号
        if ("1".equals(registration.getIsAppointment())) {
            toolkitx.executeFunction(FetchCancelReg2HospitalFunction.class, registration, orders);
            String payStatus = "0";
            String confirmStatus = "0";
            if (orders.size() > 1) {
                payStatus = orders.get(1).getStatus();
            }
            if (orders.size() > 3) {
                confirmStatus = orders.get(2).getStatus();
            }
            if (orders.size() > 3 && payStatus.equals("1")) {
                toolkitx.executeFunction(FetchRefund2PatientFunction.class, registration, orders);
            }
            if (orders.size() > 4 && confirmStatus.equals("1")) {
                toolkitx.executeFunction(FetchHospitalConfirmCancelFunction.class, registration, orders);
            }
            // 单日挂号
        } else if ("0".equals(registration.getIsAppointment())) {
            if (orders.size() > 2) {
                RegistrationOrderProcessDocument hospConfirmProcess = orders.get(2);
                if (hospConfirmProcess.getDetail().contains("挂号失败") || (hospConfirmProcess.getDetail().contains("未确认")
                        && !registration.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue()))) {
                    toolkitx.executeFunction(FetchRefund2PatientFunction.class, registration, orders);
                }
            }
        }
    }

}
