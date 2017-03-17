package com.proper.enterprise.isj.function.registration;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderProcessDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundReqDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationTradeRefundDocument;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getCancelReg2Hospital(RegistrationDocument, List<RegistrationOrderProcessDocument>)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class FetchCancelReg2HospitalFunction implements IFunction<Object> {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        getCancelReg2Hospital((RegistrationDocument)params[0],
                (List<RegistrationOrderProcessDocument>) params[1]);
        return null;
    }
    
    /**
     * 订单流程(退号).
     *
     * @param registration 挂号信息.
     * @param orders 订单列表.
     */
    private void getCancelReg2Hospital(RegistrationDocument registration,
            List<RegistrationOrderProcessDocument> orders) {
        boolean isShowCancel = false;
        boolean cancelRegFlag = false;
        if (StringUtil.isNotEmpty(registration.getCancelHisReturnMsg())) {
            if (StringUtil.isNotEmpty(registration.getCancelRegToHisTime())) {
                isShowCancel = true;
            }
            if (registration.getCancelHisReturnMsg().contains("交易成功")) {
                cancelRegFlag = true;
            }
        } else {
            RegistrationTradeRefundDocument refund = registration.getRegistrationTradeRefund();
            RegistrationRefundReqDocument refundReq = registration.getRegistrationRefundReq();
            if (refund != null) {
                cancelRegFlag = true;
            } else if (refundReq != null && StringUtil.isNotEmpty(refundReq.getOrderId())) {
                cancelRegFlag = true;
            }
        }
        RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setName("取消挂号");
        orderProcess.setStatus("1");
        StringBuilder detailStr = new StringBuilder();
        if (isShowCancel) {
            if (cancelRegFlag) {
                detailStr.append("退号成功");
            } else {
                detailStr.append("退号失败");
            }
            if (registration.getRegistrationTradeRefund() != null) {
                String rt = registration.getRegistrationTradeRefund().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setDetail(detailStr.toString());
            orderProcess.setImg("user.png");
            orders.add(orderProcess);
        }
    }

}
