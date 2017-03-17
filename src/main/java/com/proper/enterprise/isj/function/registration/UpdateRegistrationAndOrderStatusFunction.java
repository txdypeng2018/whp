package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.PayReg;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl.updateRegistrationAndOrderStatus(String, ResModel<PayReg>, Order)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class UpdateRegistrationAndOrderStatusFunction implements IFunction<Object> {

    @Autowired
    FetchRegistrationDocumentByIdFunction fetchRegistrationDocumentByIdFunction;
    
    @Autowired
    SaveRegistrationDocumentFunction saveRegistrationDocumentFunction;
    
    @Autowired
    OrderService orderService;
    
    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        updateRegistrationAndOrderStatus((String)params[0], (ResModel<PayReg>)params[1], (Order)params[2]);
        return null;
    }

    /**
     * 订单不为空,更新挂号单和订单状态.
     *
     * @param channelId 渠道ID.
     * @param payRegRes 支取结果.
     * @param order 订单.
     * @throws HisReturnException 异常.
     */
    public void updateRegistrationAndOrderStatus(String channelId, ResModel<PayReg> payRegRes, Order order)
            throws HisReturnException {
        RegistrationDocument regDoc = FunctionUtils.invoke(fetchRegistrationDocumentByIdFunction, order.getFormId());
        RegistrationOrderHisDocument his = regDoc.getRegistrationOrderHis();
        his.setLastModifyTime(DateUtil.getTimestamp(true));
        his.setClientReturnMsg(payRegRes.getReturnMsg() + "(" + payRegRes.getReturnCode() + ")");
        if (payRegRes.getReturnCode() == ReturnCode.SUCCESS) {
            BeanUtils.copyProperties(payRegRes.getRes(), his);
            regDoc.setRegistrationOrderHis(his);
            regDoc.setStatusCode(RegistrationStatusEnum.PAID.getValue());
            regDoc.setStatus(CenterFunctionUtils.getRegistrationStatusName(RegistrationStatusEnum.PAID.getValue()));
            FunctionUtils.invoke(saveRegistrationDocumentFunction, regDoc);
            order.setOrderStatus(String.valueOf(2));
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.payed"));
            order.setPayWay(channelId);
            orderService.save(order);
            // saveMedicalNum2UserInfo(regDoc, his);
        } else {
            regDoc.setRegistrationOrderHis(his);
            FunctionUtils.invoke(saveRegistrationDocumentFunction, regDoc);
            throw new HisReturnException(payRegRes.getReturnMsg());
        }
    }
    

}