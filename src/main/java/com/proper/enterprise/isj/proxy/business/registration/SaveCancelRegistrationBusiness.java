package com.proper.enterprise.isj.proxy.business.registration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.OrderCancelTypeContext;
import com.proper.enterprise.isj.context.RegistrationDocIdContext;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.function.registration.FetchRegistrationDocumentByIdFunction;
import com.proper.enterprise.isj.function.registration.SaveNonPaidCancelRegistrationFunction;
import com.proper.enterprise.isj.function.registration.SaveRegistrationDocumentFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SaveCancelRegistrationBusiness<M extends RegistrationDocIdContext<Object> & OrderCancelTypeContext<Object>>
        implements IBusiness<Object, M> {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    SaveNonPaidCancelRegistrationFunction saveNonPaidCancelRegistrationFunction;

    @Autowired
    SaveRegistrationDocumentFunction saveRegistrationDocument;

    @Autowired
    FetchRegistrationDocumentByIdFunction fetchRegistrationDocumentByIdFunction;

    /**
     * 退号.
     *
     * @param registrationId 挂号单ID.
     * @param cancelType 取消号点累心.
     * @throws Exception 异常.
     */
    @SuppressWarnings("rawtypes")
    public void saveCancelRegistrationImpl(String registrationId, OrderCancelTypeEnum cancelType) throws Exception {
        String hosId = CenterFunctionUtils.getHosId();
        RegistrationDocument reg = FunctionUtils.invoke(fetchRegistrationDocumentByIdFunction, registrationId);
        String cancelTime = DateUtil.toTimestamp(new Date());
        String cancelRemark;
        String regStatusCode;
        if (cancelType == OrderCancelTypeEnum.CANCEL_HANDLE) {
            cancelRemark = CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG;
            regStatusCode = RegistrationStatusEnum.CANCEL.getValue();
        } else if (cancelType == OrderCancelTypeEnum.CANCEL_OVERTIME) {
            cancelRemark = CenterFunctionUtils.ORDER_CANCEL_OVERTIME_MSG;
            regStatusCode = RegistrationStatusEnum.EXCHANGE_CLOSED.getValue();
        } else {
            cancelRemark = CenterFunctionUtils.ORDER_CANCEL_SYS_MSG;
            regStatusCode = RegistrationStatusEnum.EXCHANGE_CLOSED.getValue();
        }

        if (reg != null) {
            if (reg.getIsAppointment().equals("1")) {
                if (StringUtil.isEmpty(reg.getOrderNum())) {
                    throw new RegisterException("挂号单未找到订单号信息,不能进行退号");
                }
                if (StringUtil.isNotEmpty(reg.getCancelHisReturnMsg())
                        && reg.getCancelHisReturnMsg().contains(ReturnCode.SUCCESS.toString())) {
                    return;
                }
                ResModel res = webServicesClient.cancelReg(hosId, reg.getOrderNum(), cancelTime, cancelRemark);
                reg.setCancelHisReturnMsg(res.getReturnMsg() + "(" + res.getReturnCode() + ")");
                FunctionUtils.invoke(saveRegistrationDocument, reg);
                if (res.getReturnCode() != ReturnCode.SUCCESS) {
                    if (reg.getRegistrationOrderHis() == null
                            || StringUtil.isEmpty(reg.getRegistrationOrderHis().getHospOrderId())) {
                        FunctionUtils.invoke(saveNonPaidCancelRegistrationFunction, registrationId, reg, cancelTime,
                                cancelRemark, regStatusCode);
                    } else {
                        throw new RegisterException(res.getReturnMsg());
                    }
                }
                FunctionUtils.invoke(saveNonPaidCancelRegistrationFunction, registrationId, reg, cancelTime,
                        cancelRemark, regStatusCode);

            } else {
                FunctionUtils.invoke(saveNonPaidCancelRegistrationFunction, registrationId, reg, cancelTime,
                        cancelRemark, regStatusCode);
            }
        }
    }

    @Override
    public void process(M ctx) throws Exception {
        saveCancelRegistrationImpl(ctx.getRegistrationDocumentId(), ctx.getOrderCancelType());
    }
}
