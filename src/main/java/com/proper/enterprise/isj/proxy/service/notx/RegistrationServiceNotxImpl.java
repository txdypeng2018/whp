package com.proper.enterprise.isj.proxy.service.notx;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.function.message.SendRegistrationMsgFunction;
import com.proper.enterprise.isj.function.registration.ConvertAppInfo2PayRegFunction;
import com.proper.enterprise.isj.function.registration.ConvertAppRefundInfo2RefundReqFunction;
import com.proper.enterprise.isj.function.registration.FecthRefundQueryFlagFunction;
import com.proper.enterprise.isj.function.registration.FetchPayRegReqFunction;
import com.proper.enterprise.isj.function.registration.SaveCancelRegistrationFunction;
import com.proper.enterprise.isj.function.registration.SaveCreateRegistrationAndOrderFunction;
import com.proper.enterprise.isj.function.registration.SaveOrRemoveCacheRegKeyInRegFunction;
import com.proper.enterprise.isj.function.registration.SaveOrUpdateRegRefundLogFunction;
import com.proper.enterprise.isj.function.registration.SaveRegRefundFunction;
import com.proper.enterprise.isj.function.registration.SaveUpdateRegistrationAndOrderFunction;
import com.proper.enterprise.isj.function.registration.SaveUpdateRegistrationAndOrderRefundFunction;
import com.proper.enterprise.isj.function.registration.SetOrderProcess2RegistrationFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationRefundLogDocument;
import com.proper.enterprise.isj.proxy.document.registration.RegistrationRefundReqDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class RegistrationServiceNotxImpl extends AbstractRegistrationService implements RegistrationService {


    /**
     * 更新挂号单以及订单信息.
     *
     * @param payRegReq 向HIS发送异步处理结果对象.
     * @throws Exception
     */
    @Override
    public void saveUpdateRegistrationAndOrder(PayRegReq payRegReq) throws Exception {
        toolkit.executeFunction(SaveUpdateRegistrationAndOrderFunction.class, payRegReq);
    }

    /**
     * 创建挂号单.
     *
     * @param reg 挂号单信息.
     * @param isAppointment 是否预约挂号.
     * @return 挂号单.
     * @throws Exception 异常.
     */
    @Override
    public RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument reg, String isAppointment)
            throws Exception {
        return toolkit.executeFunction(SaveCreateRegistrationAndOrderFunction.class, reg, isAppointment);
    }

    /**
     * 三方支付平台退款操作.
     *
     * @param registrationId 挂号信息.
     * @return 退款请求.
     * @throws Exception 异常.
     */
    @Override
    public RefundReq saveRegRefund(String registrationId) throws Exception {
        return toolkit.executeFunction(SaveRegRefundFunction.class, registrationId);
    }

    /**
     * 付款成功后,发送挂号成功短信
     *
     * @param registrationId 挂号ID.
     * @throws Exception 异常.
     */
    @Override
    public void sendRegistrationMsg(String registrationId, SendPushMsgEnum pushType) throws Exception {
        RegistrationDocument updateReg = this.getRegistrationDocumentById(registrationId);
        toolkit.executeFunction(SendRegistrationMsgFunction.class, pushType, updateReg);
    }

    /**
     * 更新挂号退款信息.
     *
     * @param refundReq 退款对象.
     * @throws Exception 异常.
     */
    @Override
    public void saveUpdateRegistrationAndOrderRefund(RefundReq refundReq) throws Exception {
        toolkit.executeFunction(SaveUpdateRegistrationAndOrderRefundFunction.class, refundReq);
    }

    /**
     * 查询未付款订单,更新状态.
     *
     * @param registrationDocument 挂号报文.
     */
    @Override
    public RegistrationDocument saveQueryPayTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception {
        PayRegReq payReg = toolkit.executeFunction(FetchPayRegReqFunction.class, registrationDocument);
        if (payReg == null) {
            if (registrationDocument.getIsAppointment().equals("0")) {
                registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
                registrationDocument.setStatusCode(RegistrationStatusEnum.EXCHANGE_CLOSED.getValue());
                registrationDocument
                        .setStatus(CenterFunctionUtils.getRegistrationStatusName(registrationDocument.getStatusCode()));
                registrationDocument.setNum("F" + DateUtil.toString(new Date(), "yyMMddHHmmssSSS"));
                registrationDocument = toolkit.executeRepositoryFunction(RegistrationRepository.class, "save", registrationDocument);
                sendRegistrationMsg(registrationDocument.getId(), SendPushMsgEnum.REG_TODAY_NOT_PAY_HIS_MSG);
            }
        }
        return registrationDocument;
    }

    /**
     * 查询挂号单退款信息.
     *
     * @param registrationDocument 挂号.
     * @return 挂号单退款信息.
     * @throws Exception 异常.
     */
    @Override
    public RegistrationDocument saveQueryRefundTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception {
        boolean queryFlag = toolkit.executeFunction(FecthRefundQueryFlagFunction.class, registrationDocument);
        if (queryFlag) {
            RegistrationRefundReqDocument req = registrationDocument.getRegistrationRefundReq();
            RefundReq refundReq = new RefundReq();
            BeanUtils.copyProperties(req, refundReq);
            this.saveUpdateRegistrationAndOrderRefund(refundReq);
            registrationDocument = this.getRegistrationDocumentById(registrationDocument.getId());
        }
        return registrationDocument;
    }

    /**
     * 退号.
     *
     * @param registrationId 挂号单.
     * @param cancelType 取消方式 1:手动,2:超时自动.
     * @throws Exception 异常.
     */
    @Override
    public void saveCancelRegistration(String registrationId, OrderCancelTypeEnum cancelType) throws Exception {
        toolkit.executeFunction(SaveCancelRegistrationFunction.class, registrationId, cancelType);
    }

    /**
     * 更新挂号退款日志
     *
     * @param regBack 挂号记录.
     * @param refundLogDocument 退款日志信息.
     * @param cancelRegStatus 取消挂号单状态(-1:未退号,1:成功,0:失败).
     * @param refundStatus 退款状态(-1:未退费,1:成功,0:失败).
     * @param refundHisStatus his退款状态(1:成功,0:失败或者未通知).
     */
    @Override
    public void saveOrUpdateRegRefundLog(RegistrationDocument regBack, RegistrationRefundLogDocument refundLogDocument,
            String cancelRegStatus, String refundStatus, String refundHisStatus) {
        toolkit.executeFunction(SaveOrUpdateRegRefundLogFunction.class, regBack, refundLogDocument, cancelRegStatus,
                refundStatus, refundHisStatus);
    }

    /**
     * 线下申请退费,根据支付平台返还支付费用,修改相应的数据为已退费.
     *
     * @param reg 挂号单.
     */
    @Override
    public void saveRefundAndUpdateRegistrationDocument(RegistrationDocument reg) throws Exception {
        toolkit.executeFunction(SaveUpdateRegistrationAndOrderFunction.class, reg);
    }

    /**
     * 挂号单订单流程
     *
     * @param registration 挂号单信息.
     */
    @Override
    public void setOrderProcess2Registration(RegistrationDocument registration) {
        toolkit.executeFunction(SetOrderProcess2RegistrationFunction.class, registration);
    }

    // -----------------------------------订单流程相关---------------END--------------------------------------

    /**
     * 将支付平台异步通知对象转换为HIS请求对象.
     *
     * @param infoObj 支付平台异步通知对象.
     * @param regId 挂号信息ID.
     * @return 转换后的请求对象.
     */
    @Override
    public PayRegReq convertAppInfo2PayReg(Object infoObj, String regId) {
        return toolkit.executeFunction(ConvertAppInfo2PayRegFunction.class, infoObj, regId);
    }

    /**
     * 将支付平台异步通知对象转换为HIS请求对象.
     *
     * @param infoObj 支付平台异步通知对象.
     * @param orderNo 订单号.
     * @param refundId 退款流水号.
     * @return 转换后的请求对象.
     */
    @Override
    public RefundReq convertAppRefundInfo2RefundReq(Object infoObj, String orderNo, String refundId) {
        return toolkit.executeFunction(ConvertAppRefundInfo2RefundReqFunction.class, infoObj, orderNo, refundId);
    }



    @Override
    public void saveOrRemoveCacheRegKey(RegistrationDocument reg, String cacheType) throws RegisterException {
        toolkit.executeFunction(SaveOrRemoveCacheRegKeyInRegFunction.class, reg, cacheType);
    }
}
