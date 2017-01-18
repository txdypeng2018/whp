package com.proper.enterprise.isj.webservices.model.req;

import com.proper.enterprise.isj.webservices.model.enmus.*;

import java.util.Date;

import static com.proper.enterprise.platform.core.utils.DateUtil.safeClone;

/**
 * 订单支付请求.
 * Created by think on 2016/10/9 0009.
 */
public class PayOrderRegReq {

    /**
     * 号源锁定ID
     *
     */
    private String lockId;

    /**
     * 平台订单号，如果是事先锁定号源接口，则该字段和锁号ID一致 必填
     */
    private String orderId;

    /**
     * 医院病人ID
     */
    private String hospPatientId;

    /**
     * 挂号渠道ID，详见 “挂号渠道” 必填
     */
    private Channel channelId;

    /**
     * 是否为预约挂号，1-当天挂号 2-预约挂号（直接挂号） 3-预约挂号（锁号挂号） 必填
     */
    private IsReg isReg;

    /**
     * 排班ID，由上个接口得到 必填
     */
    private String regId;

    /**
     * 排班类别：1-普通 2-专家 3-急诊
     */
    private String regLevel;

    /**
     * 医院ID 必填
     */
    private String hosId;

    /**
     * 科室ID
     */
    private String deptId;

    /**
     * 医生ID
     */
    private String doctorId;

    /**
     * 出诊日期，格式YYYY-MM-DD
     */
    private Date regDate;

    /**
     * 时段，详见 “时段”
     */
    private TimeFlag timeFlag;

    /**
     * 分时开始时间，格式：HH24:MI
     */
    private String beginTime;

    /**
     * 分时结束时间，格式：HH24:MI
     */
    private String endTime;

    /**
     * 挂号费用，单位：分 必填
     */
    private int regFee;

    /**
     * 诊疗费用，单位：分 必填
     */
    private int treatFee;

    /**
     * 挂号类型，详见 “挂号类型” 必填
     */
    private RegType regType;

    /**
     * 患者证件类型，详情见 “证件类型”
     */
    private IDCardType idcardType;

    /**
     * 患者证件号码，（挂号类型是为本人和为他人挂号时必填，为没有身份证的子女挂号时可以为空）
     */
    private String idcardNo;

    /**
     * 患者卡类型，详见 “卡类型”
     */
    private CardType cardType;

    /**
     * 患者卡号
     */
    private String cardNo;

    /**
     * 患者姓名 必填
     */
    private String name;

    /**
     * 患者性别，详见 “性别” 必填
     */
    private Sex sex;

    /**
     * 患者出生日期，格式：YYYY-MM-DD 必填
     */
    private Date birthday;

    /**
     * 患者所在地
     */
    private String address;

    /**
     * 患者手机号码，为小孩挂号可空
     */
    private String mobile;

    /**
     * 挂号人证件类型，详见 “证件类型” 必填
     */
    private IDCardType operIdcardType;

    /**
     * 挂号人身份证号码 必填
     */
    private String operIdcardNo;

    /**
     * 挂号人姓名 必填
     */
    private String operName;

    /**
     * 挂号人手机号码 必填
     */
    private String operMobile;

    /**
     * 座席工号
     */
    private String agentId;

    /**
     * 下单时间，格式：YYYY-MM-DD HH24:MI:SS 必填
     */
    private Date orderTime;

    /**
     * 流水号（银行流水号、第三方支付流水号等） 必填
     */
    private String serialNum;
    /**
     * 交易日期（银行、第三方支付等），格式：YYYY-MM-DD 必填
     */
    private String payDate;
    /**
     * 交易时间（银行、第三方支付等），格式：HH24:MI:SS 必填
     */
    private String payTime;

    /**
     * 支付渠道ID，详见 “支付渠道” 必填
     */
    private String payChannelId;
    /**
     * 总金额，单位：分 必填
     */
    private int payTotalFee;
    /**
     * 应付金额，单位：分 必填
     */
    private int payCopeFee;
    /**
     * 实付金额，单位：分 必填
     */
    private int payFee;
    /**
     * 交易响应代码(银行、第三方支付等返回的结果码)
     */
    private String payResCode;
    /**
     * 交易响应描述
     */
    private String payResDesc;
    /**
     * 商户号，对应支付渠道的商户号
     */
    private String merchantId;
    /**
     * 终端号，对应支付渠道的终端号
     */
    private String terminalId;
    /**
     * 银行卡号
     */
    private String bankNo;
    /**
     * 第三方支付时，用户的支付帐号
     */
    private String payAccount;

    /**
     * 操作员ID
     */
    private String operatorId;


    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getHospPatientId() {
        return hospPatientId;
    }

    public void setHospPatientId(String hospPatientId) {
        this.hospPatientId = hospPatientId;
    }

    public Channel getChannelId() {
        return channelId;
    }

    public void setChannelId(Channel channelId) {
        this.channelId = channelId;
    }

    public IsReg getIsReg() {
        return isReg;
    }

    public void setIsReg(IsReg isReg) {
        this.isReg = isReg;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRegLevel() {
        return regLevel;
    }

    public void setRegLevel(String regLevel) {
        this.regLevel = regLevel;
    }

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Date getRegDate() {
        return safeClone(regDate);
    }

    public void setRegDate(Date regDate) {
        this.regDate = safeClone(regDate);
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

    public IDCardType getIdcardType() {
        return idcardType;
    }

    public void setIdcardType(IDCardType idcardType) {
        this.idcardType = idcardType;
    }

    public String getIdcardNo() {
        return idcardNo;
    }

    public void setIdcardNo(String idcardNo) {
        this.idcardNo = idcardNo;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return safeClone(birthday);
    }

    public void setBirthday(Date birthday) {
        this.birthday = safeClone(birthday);
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public IDCardType getOperIdcardType() {
        return operIdcardType;
    }

    public void setOperIdcardType(IDCardType operIdcardType) {
        this.operIdcardType = operIdcardType;
    }

    public String getOperIdcardNo() {
        return operIdcardNo;
    }

    public void setOperIdcardNo(String operIdcardNo) {
        this.operIdcardNo = operIdcardNo;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getOperMobile() {
        return operMobile;
    }

    public void setOperMobile(String operMobile) {
        this.operMobile = operMobile;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Date getOrderTime() {
        return safeClone(orderTime);
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = safeClone(orderTime);
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(String payChannelId) {
        this.payChannelId = payChannelId;
    }

    public int getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(int payTotalFee) {
        this.payTotalFee = payTotalFee;
    }

    public int getPayCopeFee() {
        return payCopeFee;
    }

    public void setPayCopeFee(int payCopeFee) {
        this.payCopeFee = payCopeFee;
    }

    public int getPayFee() {
        return payFee;
    }

    public void setPayFee(int payFee) {
        this.payFee = payFee;
    }

    public String getPayResCode() {
        return payResCode;
    }

    public void setPayResCode(String payResCode) {
        this.payResCode = payResCode;
    }

    public String getPayResDesc() {
        return payResDesc;
    }

    public void setPayResDesc(String payResDesc) {
        this.payResDesc = payResDesc;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
}
