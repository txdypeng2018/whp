package com.proper.enterprise.isj.proxy.business.registration;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.IsjRefundReqContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.registration.FetchRegistrationDocumentByIdFunction;
import com.proper.enterprise.isj.function.registration.SaveRegistrationDocumentFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundHisDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.isj.webservices.model.res.Refund;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class SaveUpdateRegistrationAndOrderRefundBusiness<M extends IsjRefundReqContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    BaseInfoRepository baseInfoRepo;
    

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;
    
    @Autowired
    OrderService orderService;
    
    @Autowired
    SaveRegistrationDocumentFunction func;
    
    @Autowired
    FetchRegistrationDocumentByIdFunction fetcher;

    @Override
    public void process(M ctx) throws Exception {
        RefundReq refundReq = ctx.getIsjRefundReq();
        ResModel<Refund> refundRes = webServicesClient.refund(refundReq);
        if (refundRes.getReturnCode() == ReturnCode.SUCCESS) {
            RegistrationRefundHisDocument refundHis = new RegistrationRefundHisDocument();
            BeanUtils.copyProperties(refundRes.getRes(), refundHis);
            Order order = orderService.findByOrderNo(refundReq.getOrderId());
            if (order != null) {
                RegistrationDocument regDoc = FunctionUtils.invoke(fetcher, order.getFormId());
                regDoc.setRegistrationRefundHis(refundHis);
                regDoc.setStatusCode(RegistrationStatusEnum.REFUND.getValue());
                regDoc.setStatus(
                        CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.REFUND.getValue()));
                regDoc.setRefundApplyType(String.valueOf(1));
                FunctionUtils.invoke(func, regDoc);  
                order.setOrderStatus(String.valueOf(3));
                order.setCancelRemark(CenterFunctionUtils.ORDER_CANCEL_MANUAL_MSG);
                order.setCancelDate(DateUtil.toTimestamp(new Date()));
                // 更新订单状态
                order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.refund"));
                orderService.save(order);
            }
        } else {
            throw new HisReturnException(refundRes.getReturnMsg());
        }
    }

}