package com.proper.enterprise.isj.function.registration;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderProcessDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.getOrder2PayPlatform(RegistrationDocument,
 * List<RegistrationOrderProcessDocument>)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FillOrder2PayPlatformFunction implements IFunction<Object>, ILoggable {

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        getOrder2PayPlatform((RegistrationDocument) params[0], (List<RegistrationOrderProcessDocument>) params[1]);
        return null;
    }

    /**
     * 订单流程(支付给支付平台).
     *
     * @param registration 挂号信息.
     * @param orders 订单列表.
     */
    public void getOrder2PayPlatform(RegistrationDocument registration, List<RegistrationOrderProcessDocument> orders) {
        StringBuilder detailStr;
        RegistrationOrderProcessDocument orderProcess = new RegistrationOrderProcessDocument();
        orderProcess.setName("订单未支付");
        RegistrationOrderReqDocument orderReq = registration.getRegistrationOrderReq();
        if (orderReq == null || StringUtil.isEmpty(orderReq.getSerialNum())) {
            PayRegReq payRegReq = null;
            try {
                payRegReq = toolkitx.executeFunction(FetchOrderProcessPayRegReqFunction.class, registration);
            } catch (Exception e) {
                debug("流程定义查询支付信息出现异常", e);
            }
            if (payRegReq != null) {
                orderReq = new RegistrationOrderReqDocument();
                BeanUtils.copyProperties(payRegReq, orderReq);
            }
        }
        String img = "money.png";
        if (orderReq != null && StringUtil.isNotEmpty(orderReq.getSerialNum())) {
            orderProcess.setStatus("1");
            detailStr = new StringBuilder();
            detailStr.append(orderReq.getSerialNum());
            detailStr.append("<br/>");
            detailStr.append(orderReq.getPayDate().concat(" ").concat(orderReq.getPayTime()));
            if (String.valueOf(PayChannel.WECHATPAY.getCode()).equals(orderReq.getPayChannelId())) {
                img = "weixin.png";
            } else if (String.valueOf(PayChannel.ALIPAY.getCode()).equals(orderReq.getPayChannelId())) {
                img = "alipay_icon.png";
            } else if (String.valueOf(PayChannel.WEB_UNION.getCode()).equals(orderReq.getPayChannelId())) {
                img = "CMB_icon.jpg";
            }

            orderProcess.setDetail(detailStr.toString());
            orderProcess.setName("订单已支付");
            orderProcess.setImg(img);
            orders.add(orderProcess);
        } else {
            orderProcess.setStatus("0");
            orderProcess.setDetail("");
            orderProcess.setImg(img);
            orders.add(orderProcess);
        }
    }

}
