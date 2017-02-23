package com.proper.enterprise.isj.payment.converter.apprefundinfo2refundreq;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.payment.converter.Converter;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractAppRefundInfo2RefundReqConv<S>
        implements Converter<AbstractAppRefundInfo2RefundReqConv.Req<S>, RefundReq>, AppRefundInfo2RefundReqHandler<S> {

    public static class Req<T> {
        private T source;
        private String orderNo;
        private String refundId;

        public Req(T source, String orderNo, String refundId) {
            super();
            this.source = source;
            this.orderNo = orderNo;
            this.refundId = refundId;
        }

        public T getSource() {
            return source;
        }

        public void setSource(T source) {
            this.source = source;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getRefundId() {
            return refundId;
        }

        public void setRefundId(String refundId) {
            this.refundId = refundId;
        }

    }

    @Autowired
    OrderService orderService;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;

    private RegistrationDocument getRegistrationDocumentById(String id) {
        return registrationServiceImpl.getRegistrationDocumentById(id);
    }

    @Override
    public RefundReq convert(Req<S> source, RefundReq target) {
        String hosId = CenterFunctionUtils.getHosId();
        target.setHosId(hosId);
        // int fee = 0;
        String orderNo = source.getOrderNo();

        if (orderNo != null) {
            Order order = orderService.findByOrderNo(orderNo);
            RegistrationDocument reg = this.getRegistrationDocumentById(order.getFormId());
            if (reg != null) {
                target.setOrderId(reg.getOrderNum());
                target.setHospOrderId(reg.getRegistrationOrderHis().getHospOrderId());
                target.setRefundId(source.getRefundId());
                target.setRefundResDesc("");
                target.setRefundRemark("");
                fillTarget(source.getSource(), target);
            }
        }
        return target;
    }

    protected abstract void fillTarget(S source, RefundReq target);

    public RefundReq convert(S source, String orderNo, String refundId) {
        return convert(new Req<S>(source, orderNo, refundId), new RefundReq());
    }

}
