package com.proper.enterprise.isj.function.registration;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderProcessDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundHisDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundReqDocument;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getHospitalConfirmCancel(RegistrationDocument,
 * List<RegistrationOrderProcessDocument>)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchHospitalConfirmCancelFunction implements IFunction<Object> {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        getHospitalConfirmCancel((RegistrationDocument) params[0], (List<RegistrationOrderProcessDocument>) params[1]);
        return null;
    }

    /**
     * 订单流程(医院确认退号).
     *
     * @param registration 挂号信息.
     * @param orders 订单列表.
     */
    private void getHospitalConfirmCancel(RegistrationDocument registration,
            List<RegistrationOrderProcessDocument> orders) {
        RegistrationRefundReqDocument refundReq = registration.getRegistrationRefundReq();
        if (refundReq != null && StringUtil.isNotEmpty(refundReq.getRefundSerialNum())) {
            RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
            orderProcess.setName("医院确认退费");
            orderProcess.setStatus("1");
            StringBuilder detailStr = new StringBuilder();
            RegistrationRefundHisDocument refundHis = registration.getRegistrationRefundHis();
            if (refundHis == null || !"1".equals(refundHis.getRefundFlag())) {
                detailStr.append("医院确认失败");
            } else {
                detailStr.append("医院确认成功");
            }
            if (registration.getRegistrationRefundHis() != null) {
                String rt = registration.getRegistrationRefundHis().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setDetail(detailStr.toString());
            orderProcess.setImg("logo.png");
            orders.add(orderProcess);
        }
    }

}
