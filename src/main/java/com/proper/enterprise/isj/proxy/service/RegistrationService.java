package com.proper.enterprise.isj.proxy.service;

import java.util.List;
import java.util.Map;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationRefundLogDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;

/**
 * Created by think on 2016/9/4 0004.
 */

public interface RegistrationService {

    /**
     * 生成挂号单和订单
     * 
     * @param reg
     *            挂号单信息
     * @return 订单
     * @throws Exception
     */
    RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument reg, String isAppointment) throws Exception;

    /**
     *
     */
    List<RegistrationDocument> findRegistrationByCreateUserIdAndPayStatus(String patientId, String status, String isAppointment);

    void saveUpdateRegistrationAndOrder(PayRegReq payRegReq) throws Exception;

    void saveUpdateRegistrationAndOrderRefund(RefundReq refundReq) throws Exception;

    RefundReq saveRegRefund(String registrationId) throws Exception;

    PayRegReq convertAppInfo2PayReg(Object infoObj, String regId);

    RefundReq convertAppRefundInfo2RefundReq(Object infoObj, String orderNo, String refundId);

    RegistrationDocument getRegistrationDocumentById(String id);

    /**
     * 查询未付款订单,更新状态
     * 
     * @param registrationDocument
     */
    RegistrationDocument saveQueryPayTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception;

    /**
     * 查询已退款订单,更新状态
     * 
     * @param registrationDocument
     * @return
     * @throws Exception
     */
    RegistrationDocument saveQueryRefundTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception;

    /**
     * 根据挂号单号查询挂号单
     * 
     * @param num
     *            挂号单号
     * @return 挂号单
     */
    RegistrationDocument getRegistrationDocumentByNum(String num);

    void deleteRegistrationDocument(RegistrationDocument reg);

    /**
     * 保存或更新挂号单信息
     * 
     * @param reg
     * @return
     */
    RegistrationDocument saveRegistrationDocument(RegistrationDocument reg);

    List<RegistrationDocument> findRegistrationDocumentList(String patientId);

    /**
     * 获取订单超时未付款的挂号单
     * 
     * @return
     */
    List<RegistrationDocument> findOverTimeRegistrationDocumentList();

    /**
     * 查询已支付,进行了退号操作的记录
     */
    List<RegistrationDocument> findAlreadyCancelRegAndRefundErrRegList();

    /**
     * 退号
     * 
     * @param registrationId
     *            挂号单
     * @param cancelType
     *            取消方式 1:手动,2:超时自动
     * @throws Exception
     */
    void saveCancelRegistration(String registrationId, OrderCancelTypeEnum cancelType) throws Exception;

    List<RegistrationDocument> findRegistrationDocumentByStopReg(Map<String, String> paramMap);

    /**
     * 线下申请退费,根据支付平台返还支付费用,修改相应的数据为已退费
     * 
     * @param reg
     */
    void saveRefundAndUpdateRegistrationDocument(RegistrationDocument reg) throws Exception;

    List<RegistrationDocument> findRegistrationDocumentByCreateUserIdAndPatientIdCard(String createUserId,
            String patientIdCard);

    void saveOrUpdateRegRefundLog(RegistrationDocument regBack, RegistrationRefundLogDocument refundLogDocument,
            String cancelRegStatus, String refundStatus, String refundHisStatus);

    void setOrderProcess2Registration(RegistrationDocument registration);
}
