package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.proper.enterprise.isj.proxy.document.registration.*;
import com.proper.enterprise.isj.webservices.model.enmus.RegType;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;
import com.proper.enterprise.isj.webservices.model.enmus.TimeFlag;
import com.proper.enterprise.platform.core.converter.AESConverter;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/8/16 0016. 挂号单
 */

@Document(collection = "registration")
public class RegistrationDocument extends BaseDocument {

    private static final long serialVersionUID = -1l;

    /**
     * 挂号单号,格式yyMMddHHmm+5位流水号
     */
    @Indexed(unique = true)
    private String num;

    /**
     * 订单号
     */
    private String orderNum;

    /**
     * 就诊时间", "example": "2016年07月20日 10:00
     */
    private String apptDate;
    /**
     * 病历号
     */
    private String clinicNum;

    /**
     * 挂号金额", 单位是分
     */
    private String amount;
    /**
     * 下单时间/操作时间
     */
    private String registerDate;
    /**
     * 挂号单状态
     */
    private String statusCode;
    /**
     * 状态名称
     */
    private String status;

    /**
     * 挂号类别
     */
    private String clinicCategoryCode;

    /**
     * 是否允许退号
     */
    private String canBack;

    /**
     * 1:预约挂号,0:当日挂号
     */
    private String isAppointment;

    /**
     * 退费方式,0:未申请,1:线上申请,2:线下申请
     */
    private String refundApplyType = String.valueOf(0);

    /*-----------------操作人信息-----------*/

    /**
     * 操作人身份证号
     */
    private String operatorCardNo;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作人手机
     */
    private String operatorPhone;

    /*-----------------操作人信息-----------*/

    /*----------患者信息--------------*/
    /**
     * 患者Id
     */
    private String patientId;

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 患者性别
     */
    private Sex patientSex;

    /**
     * 患者出生日期
     */
    private String patientBirthday;

    /**
     * 患者身份证号
     */
    private String patientIdCard;

    /**
     * 患者病历卡号
     */
    private String patientCardNo;

    /**
     * 患者手机号
     */
    private String patientPhone;

    /**
     * 付款方式
     */
    private String payChannelId;

    /*----------患者信息--------------*/

    /*------------医生出诊信息---------*/

    /**
     * 医院名称
     */
    private String hospitalId;

    /**
     * 医院名称
     */
    private String hospital;
    /**
     * 院区Id
     */
    private String districtId;
    /**
     * 院区名称
     */
    private String district;
    /**
     * 科室Id
     */
    private String deptId;
    /**
     * 科室名称
     */
    private String dept;

    /**
     * 诊室
     */
    private String roomName;

    /**
     * 医生Id
     */
    private String doctorId;
    /**
     * 医生名称
     */
    private String doctor;

    /**
     * 出诊日期
     */
    private String regDate;

    /**
     * 时段
     */
    private TimeFlag timeFlag;

    /**
     * 分时开始时间,格式：HH24:MI
     */
    private String beginTime;

    /**
     * 分时结束时间,格式：HH24:MI
     */
    private String endTime;

    /**
     * 挂号费用，单位：分
     */
    private int regFee;

    /**
     * 诊疗费用，单位：分
     */
    private int treatFee;

    /**
     * 挂号类型，详见 “挂号类型”
     */
    private RegType regType;

    /**
     * 排班id
     */
    private String regId;

    /**
     * 排班类别
     */
    private String regLevelName;

    /**
     *
     */
    private String regLevelCode;
    /*------------医生出诊信息---------*/

    @Transient
    private transient AESConverter converter = new AESConverter();

    /**
     * 申请退款
     */
    private RegistrationTradeRefundDocument registrationTradeRefund;
    /**
     * 发送HIS的订单请求参数
     */
    private RegistrationOrderReqDocument registrationOrderReq = new RegistrationOrderReqDocument();


    /**
     * 支付完成后HIS返回的信息
     * 
     */
    private RegistrationOrderHisDocument registrationOrderHis = new RegistrationOrderHisDocument();

    /**
     * 发送HIS的退费请求参数
     */
    private RegistrationRefundReqDocument registrationRefundReq = new RegistrationRefundReqDocument();

    /**
     * 退费后HIS返回的信息
     */
    private RegistrationRefundHisDocument registrationRefundHis = new RegistrationRefundHisDocument();

    /**
     * 发送HIS退号请求发起时间
     * 
     */
    private String cancelRegToHisTime;

    /**
     * 退号后HIS返回的消息
     */
    private String cancelHisReturnMsg;

    /**
     * 退号失败异常信息
     */
    private String cancelRegErrMsg = "";

    /**
     * 退费失败异常信息
     */
    private String refundErrMsg = "";

    public String getOperatorCardNo() {
        if(StringUtil.isEmpty(operatorCardNo)){
            operatorCardNo = "";
        }
        return converter.convertToEntityAttribute(operatorCardNo);
    }

    public void setOperatorCardNo(String operatorCardNo) {
        this.operatorCardNo = converter.convertToDatabaseColumn(operatorCardNo);
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPatientName() {
        if(StringUtil.isEmpty(patientName)){
            patientName = "";
        }
        return  converter.convertToEntityAttribute(patientName);
    }

    public void setPatientName(String patientName) {
        this.patientName = converter.convertToDatabaseColumn(patientName);
    }

    public String getApptDate() {
        return apptDate;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    public String getClinicNum() {
        return clinicNum;
    }

    public void setClinicNum(String clinicNum) {
        this.clinicNum = clinicNum;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClinicCategoryCode() {
        return clinicCategoryCode;
    }

    public void setClinicCategoryCode(String clinicCategoryCode) {
        this.clinicCategoryCode = clinicCategoryCode;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public TimeFlag getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(TimeFlag timeFlag) {
        this.timeFlag = timeFlag;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRegFee() {
        return regFee;
    }

    public void setRegFee(int regFee) {
        this.regFee = regFee;
    }

    public int getTreatFee() {
        return treatFee;
    }

    public void setTreatFee(int treatFee) {
        this.treatFee = treatFee;
    }

    public RegType getRegType() {
        return regType;
    }

    public void setRegType(RegType regType) {
        this.regType = regType;
    }

    public Sex getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(Sex patientSex) {
        this.patientSex = patientSex;
    }

    public String getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(String patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRegLevelName() {
        return regLevelName;
    }

    public void setRegLevelName(String regLevelName) {
        this.regLevelName = regLevelName;
    }

    public String getRegLevelCode() {
        return regLevelCode;
    }

    public void setRegLevelCode(String regLevelCode) {
        this.regLevelCode = regLevelCode;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getPatientIdCard() {
        if(StringUtil.isEmpty(patientIdCard)){
            patientIdCard = "";
        }
        return converter.convertToEntityAttribute(patientIdCard);
    }

    public void setPatientIdCard(String patientIdCard) {
        this.patientIdCard = converter.convertToDatabaseColumn(patientIdCard);
    }

    public String getPatientCardNo() {
        return patientCardNo;
    }

    public void setPatientCardNo(String patientCardNo) {
        this.patientCardNo = patientCardNo;
    }

    public String getPatientPhone() {
        if(StringUtil.isEmpty(patientPhone)){
            patientPhone = "";
        }
        return converter.convertToEntityAttribute(patientPhone);
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = converter.convertToDatabaseColumn(patientPhone);
    }

    public String getOperatorName() {
        if(StringUtil.isEmpty(operatorName)){
            operatorName = "";
        }
        return converter.convertToEntityAttribute(operatorName);
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = converter.convertToDatabaseColumn(operatorName);
    }

    public String getOperatorPhone() {
        return operatorPhone;
    }

    public void setOperatorPhone(String operatorPhone) {
        this.operatorPhone = operatorPhone;
    }


    public RegistrationOrderHisDocument getRegistrationOrderHis() {
        return registrationOrderHis;
    }

    public void setRegistrationOrderHis(RegistrationOrderHisDocument registrationOrderHis) {
        this.registrationOrderHis = registrationOrderHis;
    }

    public RegistrationOrderReqDocument getRegistrationOrderReq() {
        return registrationOrderReq;
    }

    public void setRegistrationOrderReq(RegistrationOrderReqDocument registrationOrderReq) {
        this.registrationOrderReq = registrationOrderReq;
    }

    public RegistrationRefundReqDocument getRegistrationRefundReq() {
        return registrationRefundReq;
    }

    public void setRegistrationRefundReq(RegistrationRefundReqDocument registrationRefundReq) {
        this.registrationRefundReq = registrationRefundReq;
    }

    public RegistrationRefundHisDocument getRegistrationRefundHis() {
        return registrationRefundHis;
    }

    public void setRegistrationRefundHis(RegistrationRefundHisDocument registrationRefundHis) {
        this.registrationRefundHis = registrationRefundHis;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getIsAppointment() {
        return isAppointment;
    }

    public void setIsAppointment(String isAppointment) {
        this.isAppointment = isAppointment;
    }

    public String getCanBack() {
        return canBack;
    }

    public void setCanBack(String canBack) {
        this.canBack = canBack;
    }

    public RegistrationTradeRefundDocument getRegistrationTradeRefund() {
        return registrationTradeRefund;
    }

    public void setRegistrationTradeRefund(RegistrationTradeRefundDocument registrationTradeRefund) {
        this.registrationTradeRefund = registrationTradeRefund;
    }

    public String getRefundApplyType() {
        return refundApplyType;
    }

    public void setRefundApplyType(String refundApplyType) {
        this.refundApplyType = refundApplyType;
    }

    public String getCancelRegToHisTime() {
        return cancelRegToHisTime;
    }

    public void setCancelRegToHisTime(String cancelRegToHisTime) {
        this.cancelRegToHisTime = cancelRegToHisTime;
    }

    public String getCancelHisReturnMsg() {
        return cancelHisReturnMsg;
    }

    public void setCancelHisReturnMsg(String cancelHisReturnMsg) {
        this.cancelHisReturnMsg = cancelHisReturnMsg;
    }

    public String getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(String payChannelId) {
        this.payChannelId = payChannelId;
    }

    public String getCancelRegErrMsg() {
        return cancelRegErrMsg;
    }

    public void setCancelRegErrMsg(String cancelRegErrMsg) {
        this.cancelRegErrMsg = cancelRegErrMsg;
    }

    public String getRefundErrMsg() {
        return refundErrMsg;
    }

    public void setRefundErrMsg(String refundErrMsg) {
        this.refundErrMsg = refundErrMsg;
    }
}
