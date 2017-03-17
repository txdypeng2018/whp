package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.exception.DelayException;
import com.proper.enterprise.isj.function.message.SendRegistrationMsgFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderHisDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationOrderReqDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.updateEqualtodayRegAndOrder(PayRegReq, Order, RegistrationDocument, RegistrationOrderReqDocument)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class UpdateEqualtodayRegAndOrderFunction implements IFunction<Object>, ILoggable{


    @Autowired
    RepositoryFunctionToolkit toolkitx;
    

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;
    

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;
    
    @Override
    public Object execute(Object... params) throws Exception {
        updateEqualtodayRegAndOrder((PayRegReq) params[0], (Order) params[1], (RegistrationDocument) params[2],
                (RegistrationOrderReqDocument) params[3]);
        return null;
    }
    
    /**
     * 更新当日挂号.
     *
     * @param payRegReq 支付请求.
     * @param order 订单.
     * @param regBack 挂号信息.
     * @param payOrderRegDocument 订单支付报文.
     * @throws Exception 异常.
     */
    public void updateEqualtodayRegAndOrder(PayRegReq payRegReq, Order order, RegistrationDocument regBack,
            RegistrationOrderReqDocument payOrderRegDocument) throws Exception {
        // 转换当日挂号对象为his当日挂号请求对象
        OrderRegReq orderReq = toolkitx.executeFunction(ConvertRegistration2OrderRegFunction.class, regBack);
        BeanUtils.copyProperties(orderReq, payOrderRegDocument);
        regBack.setRegistrationOrderReq(payOrderRegDocument);
        // 保存当日挂号请求信息
        toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
        try {
            PayOrderRegReq payOrderRegReq = new PayOrderRegReq();
            BeanUtils.copyProperties(payOrderRegDocument, payOrderRegReq);
            registrationServiceImpl.updateRegistrationAndOrder(payOrderRegReq);
            
            toolkitx.executeFunction(SendRegistrationMsgFunction.class, SendPushMsgEnum.REG_PAY_SUCCESS,
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "findOne", order.getFormId())
                    );
            toolkitx.executeFunction(SaveOrRemoveRegCacheFunction.class, "0", order.getFormId(), null);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
        }catch (DelayException e) {
            throw e;
        } catch (Exception e) {
            RegistrationOrderHisDocument his = regBack.getRegistrationOrderHis();
            his.setClientReturnMsg(e.getMessage());
            regBack.setRegistrationOrderHis(his);
            toolkitx.executeRepositoryFunction(RegistrationRepository.class, "save", regBack);
            toolkitx.executeFunction(PayBackTodayRegFunction.class, payRegReq, order, regBack);
            info("当日挂号更新出现异常", e);
            webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(regBack.getDoctorId(),
                    regBack.getRegDate());
            throw e;
        }
    }

    
    
}
