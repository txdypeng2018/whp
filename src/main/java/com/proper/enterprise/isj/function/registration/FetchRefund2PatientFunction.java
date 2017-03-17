package com.proper.enterprise.isj.function.registration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderProcessDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationTradeRefundDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getRefund2Patient(RegistrationDocument,
 * List<RegistrationOrderProcessDocument>)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchRefund2PatientFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        getRefund2Patient((RegistrationDocument) params[0], (List<RegistrationOrderProcessDocument>) params[1]);
        return null;
    }

    /**
     * 订单流程(退款)
     *
     * @param registration 挂号信息.
     * @param orders 订单列表.
     */
    public void getRefund2Patient(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setName("向支付平台申请退款");
        detailStr = new StringBuilder();
        RegistrationTradeRefundDocument refund = registration.getRegistrationTradeRefund();
        String img = "money.png";
        if (String.valueOf(PayChannel.ALIPAY.getCode()).equals(registration.getPayChannelId())) {
            img = "alipay_icon.png";
        } else if (String.valueOf(PayChannel.WECHATPAY.getCode()).equals(registration.getPayChannelId())) {
            img = "weixin.png";
        } else if (String.valueOf(PayChannel.WEB_UNION.getCode()).equals(registration.getPayChannelId())) {
            img = "CMB_icon.jpg";
        }
        if (refund == null) {
            orderProcess.setStatus("1");
            if (registration.getStatusCode().equals(RegistrationStatusEnum.REFUND.getValue())) {
                detailStr.append("申请退款成功");
            } else {
                detailStr.append("申请退款失败");
            }
            if (registration.getRegistrationTradeRefund() != null) {
                String rt = registration.getRegistrationRefundReq().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setImg(img);
            orderProcess.setDetail(detailStr.toString());
            orders.add(orderProcess);
        } else {
            boolean refundFlag = false;
            try {
                refundFlag = toolkitx.executeFunction(FecthRefundQueryFlagFunction.class, registration);
            } catch (Exception e) {
                debug("流程定义查询退款信息出现异常", e);
            }
            if (refundFlag) {
                detailStr.append("申请退款成功");
            } else {
                detailStr.append("申请退款失败");
            }
            if (registration.getRegistrationTradeRefund() != null) {
                String rt = registration.getRegistrationRefundReq().getCreateTime();
                String rtm = StringUtil.isEmpty(rt) ? "" : rt.substring(0, rt.length() - 4);
                detailStr.append("<br>").append(rtm);
            }
            orderProcess.setStatus("1");
            orderProcess.setImg(img);
            orderProcess.setDetail(detailStr.toString());
            orders.add(orderProcess);
        }
    }

}
