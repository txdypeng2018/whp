package com.proper.enterprise.isj.proxy.service;

import java.util.List;
import java.util.Map;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationRefundLogDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;

/**
 * 挂号服务.
 * Created by think on 2016/9/4 0004.
 */

public interface RegistrationService {

    /**
     * 通过ID获取挂号信息.
     *
     * @param id 挂号单ID.
     * @return RegistrationDocument 获取的挂号单对象.
     */
    RegistrationDocument getRegistrationDocumentById(String id);

    /**
     * 保存或更新挂号单信息.
     *
     * @param reg 挂号报文.
     * @return 挂号报文.
     */
    RegistrationDocument saveRegistrationDocument(RegistrationDocument reg);

    /**
     * 通过用户ID以及支付状态获取挂号单信息.
     *
     * @param userId 患者ID.
     * @param status 支付状态.
     * @param isAppointment 挂号类别.
     * @return 挂号单信息.
     */
    List<RegistrationDocument> findRegistrationByCreateUserIdAndPayStatus(String userId, String status, String isAppointment);

    /**
     * 根据挂号单号查询挂号单.
     *
     * @param num 挂号单号.
     * @return 挂号单.
     */
    RegistrationDocument getRegistrationDocumentByNum(String num);

    /**
     * 删除挂号信息.
     *
     * @param reg 挂号信息.
     */
    void deleteRegistrationDocument(RegistrationDocument reg);

    /**
     * 通过患者ID查询挂号单信息.
     *
     * @param patientId 患者ID.
     * @return 挂号单列表.
     */
    List<RegistrationDocument> findRegistrationDocumentList(String patientId);

    /**
     * 获取订单超时未付款的挂号单.
     *
     * @return 挂号单.
     */
    List<RegistrationDocument> findOverTimeRegistrationDocumentList(int overTimeMinute);

    /**
     * 查询已支付,进行了退号操作的记录
     */
    List<RegistrationDocument> findAlreadyCancelRegAndRefundErrRegList();

    /**
     * 通过参数查询挂号单信息.
     *
     * @param paramMap 参数集合.
     * @return 查询结果.
     */
    List<RegistrationDocument> findRegistrationDocumentByStopReg(Map<String, String> paramMap);

    /**
     * 通过创建挂号单用户ID以及患者身份证号查询挂号信息.
     *
     * @param createUserId 创建挂号单用户ID.
     * @param patientIdCard 患者身份证号.
     * @return 查询结果.
     */
    List<RegistrationDocument> findRegistrationDocumentByCreateUserIdAndPatientIdCard(String createUserId,
                                                                                      String patientIdCard);

    /**
     * 将支付平台异步通知对象转换为HIS请求对象.
     *
     * @param infoObj 支付平台异步通知对象
     * @param regId 挂号信息ID
     * @return 转换后的请求对象
     */
    PayRegReq convertAppInfo2PayReg(Object infoObj, String regId);

    /**
     * 更新挂号单以及订单信息.
     *
     * @param payRegReq 向HIS发送异步处理结果对象.
     * @throws Exception
     */
    void saveUpdateRegistrationAndOrder(PayRegReq payRegReq) throws Exception;

    //------------------------------------------------------------------------------------------------

    /**
     * 生成挂号单和订单.
     * 
     * @param reg
     *            挂号单信息.
     * @return 订单.
     * @throws Exception 异常.
     */
    RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument reg, String isAppointment) throws Exception;

    /**
     * 更新挂号退款信息.
     *
     * @param refundReq 退款保存对象.
     * @throws Exception 异常.
     */
    void saveUpdateRegistrationAndOrderRefund(RefundReq refundReq) throws Exception;

    /**
     * 退款操作.
     *
     * @param registrationId 挂号信息.
     * @return 退款请求.
     * @throws Exception 异常.
     */
    RefundReq saveRegRefund(String registrationId) throws Exception;

    /**
     * 将支付平台异步通知对象转换为HIS请求对象.
     *
     * @param infoObj 支付平台异步通知对象.
     * @param orderNo 订单号.
     * @param refundId 退款ID.
     * @return 转换后的请求对象.
     */
    RefundReq convertAppRefundInfo2RefundReq(Object infoObj, String orderNo, String refundId);

    /**
     * 查询未付款订单,更新状态.
     * 
     * @param registrationDocument 挂号报文.
     */
    RegistrationDocument saveQueryPayTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception;

    /**
     * 查询已退款订单,更新状态.
     * 
     * @param registrationDocument 挂号报文.
     * @return 挂号报文.
     * @throws Exception 异常.
     */
    RegistrationDocument saveQueryRefundTradeStatusAndUpdateReg(RegistrationDocument registrationDocument)
            throws Exception;

    /**
     * 退号.
     * 
     * @param registrationId 挂号单.
     * @param cancelType 取消方式 1:手动,2:超时自动.
     * @throws Exception 异常.
     */
    void saveCancelRegistration(String registrationId, OrderCancelTypeEnum cancelType) throws Exception;

    /**
     * 线下申请退费,根据支付平台返还支付费用,修改相应的数据为已退费.
     * 
     * @param reg 挂号单.
     */
    void saveRefundAndUpdateRegistrationDocument(RegistrationDocument reg) throws Exception;

    /**
     * 更新挂号退款日志
     *
     * @param regBack 挂号记录.
     * @param refundLogDocument 退款日志信息.
     * @param cancelRegStatus 取消挂号单状态.
     * @param refundStatus 退款状态.
     * @param refundHisStatus his退款状态.
     */
    void saveOrUpdateRegRefundLog(RegistrationDocument regBack, RegistrationRefundLogDocument refundLogDocument,
            String cancelRegStatus, String refundStatus, String refundHisStatus);

    /**
     * 设置挂号单订单流程
     *
     * @param registration 挂号单信息.
     */
    void setOrderProcess2Registration(RegistrationDocument registration);
}
