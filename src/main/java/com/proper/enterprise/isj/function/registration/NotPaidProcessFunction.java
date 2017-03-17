package com.proper.enterprise.isj.function.registration;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderProcessDocument;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getNotPaidProcess(RegistrationDocument, List<RegistrationOrderProcessDocument>)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class NotPaidProcessFunction implements IFunction<Object> {

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        getNotPaidProcess((RegistrationDocument) params[0], (List<RegistrationOrderProcessDocument>) params[1]);
        return null;
    }
    

    /**
     * 订单流程(待支付).
     *
     * @param registration 挂号信息.
     * @param orders 订单列表.
     */
    public void getNotPaidProcess(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess;
        orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setStatus("1");
        orderProcess.setName("生成订单，待支付");
        detailStr = new StringBuilder();
        detailStr.append(registration.getNum());
        detailStr.append("<br/>");
        detailStr.append(DateUtil
                .toTimestamp(DateUtil.toDate(registration.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT)));
        orderProcess.setDetail(detailStr.toString());
        orderProcess.setImg("user.png");
        orders.add(orderProcess);
    }

}
