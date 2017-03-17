package com.proper.enterprise.isj.function.registration;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderProcessDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getHospitalConfirm(RegistrationDocument,
 * List<RegistrationOrderProcessDocument>)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchHospitalConfirmFunction implements IFunction<Object> {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        getHospitalConfirm((RegistrationDocument) params[0], (List<RegistrationOrderProcessDocument>) params[1]);
        return null;
    }

    /**
     * 订单流程(医院确认)
     *
     * @param registration 挂号信息.
     * @param orders 订单列表.
     */
    public void getHospitalConfirm(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument payProcess = orders.get(1);
        if ("1".equals(payProcess.getStatus())) {
            RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
            orderProcess.setName("医院确认");
            orderProcess.setImg("logo.png");
            RegistrationOrderReqDocument orderReq = registration.getRegistrationOrderReq();
            if (orderReq != null && StringUtil.isNotEmpty(orderReq.getSerialNum())) {
                orderProcess.setStatus("1");
                detailStr = new StringBuilder();
                RegistrationOrderHisDocument orderHis = registration.getRegistrationOrderHis();
                if (orderHis != null && StringUtil.isNotEmpty(orderHis.getHospPayId())) {
                    String ct = orderHis.getLastModifyTime();
                    String hct = StringUtil.isEmpty(ct) ? "" : ct.substring(0, ct.length() - 4);
                    detailStr.append(hct);
                    if (StringUtil.isNotEmpty(orderHis.getHospRemark())) {
                        detailStr.append("<br/>");
                        detailStr.append(orderHis.getHospRemark());
                    }
                } else {
                    detailStr.append("挂号失败");
                    if (orderHis != null) {
                        if (StringUtil.isNotEmpty(orderHis.getClientReturnMsg())) {
                            detailStr.append("<br/>");
                            detailStr.append(orderHis.getClientReturnMsg());
                        }
                    }
                }
                orderProcess.setDetail(detailStr.toString());
                orders.add(orderProcess);
            } else {
                orderProcess.setStatus("0");
                orderProcess.setDetail("未确认");
                orders.add(orderProcess);
            }
        }
    }
}
