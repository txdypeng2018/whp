package com.proper.enterprise.isj.proxy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.IsjRefundReqContext;
import com.proper.enterprise.isj.context.OrderCancelTypeContext;
import com.proper.enterprise.isj.context.PayOrderRegReqContext;
import com.proper.enterprise.isj.context.PayRegReqContext;
import com.proper.enterprise.isj.context.RegistrationDocIdContext;
import com.proper.enterprise.isj.proxy.business.his.HisUpdateRegistrationAndOrderBusiness;
import com.proper.enterprise.isj.proxy.business.registration.SaveCancelRegistrationBusiness;
import com.proper.enterprise.isj.proxy.business.registration.SaveUpdateRegistrationAndOrderRefundBusiness;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.service.notx.AbstractRegistrationService;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;

/**
 * 挂号服务.
 * Created by think on 2016/9/4 0004.
 */
@Service
public class RegistrationServiceImpl extends AbstractRegistrationService {

    @Autowired
    MongoTemplate mongoTemplate;


    
    /**
     * 请求his挂号退款
     *
     * @param refundReq 退款对象.
     * @throws Exception 异常
     */
    public void saveUpdateRegistrationAndOrderRefund(RefundReq refundReq) throws Exception {
        toolkit.execute(SaveUpdateRegistrationAndOrderRefundBusiness.class, (c) -> {
            ((IsjRefundReqContext<?>) c).setIsjRefundReq(refundReq);
        });
    }

    /**
     * 退号.
     *
     * @param registrationId 挂号单ID.
     * @param cancelType 取消号点累心.
     * @throws Exception 异常.
     */
    public void saveCancelRegistrationImpl(String registrationId, OrderCancelTypeEnum cancelType) throws Exception {
        toolkit.execute(SaveCancelRegistrationBusiness.class, (c) -> {
            ((RegistrationDocIdContext<?>) c).setRegistrationDocumentId(registrationId);
            ((OrderCancelTypeContext<?>) c).setOrderCancelType(cancelType);
        });
    }
    
    /**
     * 更新挂号信息进行HIS支付.
     *
     * @param req 请求对象.
     * @throws Exception 异常.
     */
    public void updateRegistrationAndOrder(Object req) throws Exception {
        try {
            toolkit.execute(HisUpdateRegistrationAndOrderBusiness.class, (c) -> {
                if (req instanceof PayRegReq) {
                    ((PayRegReqContext<?>) c).setPayRegReq((PayRegReq) req);
                } else {
                    ((PayOrderRegReqContext<?>) c).setPayOrderRegReq((PayOrderRegReq) req);
                }
            }, e -> {
                throw e;
            });
        } catch (Throwable e) {
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }


}
