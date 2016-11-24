package com.proper.enterprise.isj.proxy.document.registration;

import com.proper.enterprise.isj.proxy.document.RegistrationConcession;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by think on 2016/9/8 0008. 挂号支付接口返回信息
 */
public class RegistrationOrderHisDocument extends BaseDocument {

    /**
     * 医院支付单号
     */
    private String hospPayId = "";


    /**
     * 医院订单号 必填
     */
    private String hospOrderId;

    /**
     * 医院病人ID 必填
     */
    private String hospPatientId;

    /**
     * 医院候诊号
     */
    private String hospSerialNum;

    /**
     * 医院病历号或门诊号
     */
//    private String hospMedicalNum;

    /**
     * 医院取号时间段，格式：HH24MI-HH24MI
     */
    private String hospGetregDate;

    /**
     * 医院就诊地址
     */
    private String hospSeeDoctAddr;

    /**
     * 本次就诊院内就诊卡，主要用户首诊用户挂号后在医院开卡，返回通知平台用户凭此卡就诊
     */
    private String hospCardNo;

    /**
     * 备注
     */
    private String hospRemark;

    /**
     * 是否享受院内优惠：0-不享受 1-享受 必填
     */
    private int isConcessions;

    /**
     * 优惠信息，当IS_CONCESSIONS=1必传
     */

    private List<RegistrationConcession> registrationConcession = new ArrayList<>();

    /**
     * 调用端口返回的消息(code)
     */
    private String clientReturnMsg;


    public String getHospPayId() {
        return hospPayId;
    }

    public void setHospPayId(String hospPayId) {
        this.hospPayId = hospPayId;
    }

    public String getHospSerialNum() {
        return hospSerialNum;
    }

    public void setHospSerialNum(String hospSerialNum) {
        this.hospSerialNum = hospSerialNum;
    }

//    public String getHospMedicalNum() {
//        return hospMedicalNum;
//    }
//
//    public void setHospMedicalNum(String hospMedicalNum) {
//        this.hospMedicalNum = hospMedicalNum;
//    }

    public String getHospGetregDate() {
        return hospGetregDate;
    }

    public void setHospGetregDate(String hospGetregDate) {
        this.hospGetregDate = hospGetregDate;
    }

    public String getHospSeeDoctAddr() {
        return hospSeeDoctAddr;
    }

    public void setHospSeeDoctAddr(String hospSeeDoctAddr) {
        this.hospSeeDoctAddr = hospSeeDoctAddr;
    }

    public String getHospRemark() {
        return hospRemark;
    }

    public void setHospRemark(String hospRemark) {
        this.hospRemark = hospRemark;
    }

    public String getClientReturnMsg() {
        return clientReturnMsg;
    }

    public void setClientReturnMsg(String clientReturnMsg) {
        this.clientReturnMsg = clientReturnMsg;
    }

    public String getHospOrderId() {
        return hospOrderId;
    }

    public void setHospOrderId(String hospOrderId) {
        this.hospOrderId = hospOrderId;
    }

    public String getHospPatientId() {
        return hospPatientId;
    }

    public void setHospPatientId(String hospPatientId) {
        this.hospPatientId = hospPatientId;
    }

    public String getHospCardNo() {
        return hospCardNo;
    }

    public void setHospCardNo(String hospCardNo) {
        this.hospCardNo = hospCardNo;
    }

    public int getIsConcessions() {
        return isConcessions;
    }

    public void setIsConcessions(int isConcessions) {
        this.isConcessions = isConcessions;
    }

    public List<RegistrationConcession> getRegistrationConcession() {
        return registrationConcession;
    }

    public void setRegistrationConcession(List<RegistrationConcession> registrationConcession) {
        this.registrationConcession = registrationConcession;
    }
}
