package com.proper.enterprise.isj.webservices.model.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.webservices.model.res.paydetaillist.PayDetail;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayDetailList implements Serializable {

    /**
     * 用户姓名 必填
     */
    @XmlElement(name = "USER_NAME")
    private String userName;

    /**
     * 用户院内ID 必填
     */
    @XmlElement(name = "HOSP_PATIENT_ID")
    private String hospPatientId;

    /**
     * 医疗保险类型（默认为自费），例：医保，自费等 必填
     */
    @XmlElement(name = "MEDICAL_INSURANNCE_TYPE")
    private String medicalInsurannceType;

    /**
     * 总金额，单位：分 必填
     */
    @XmlElement(name = "PAY_TOTAL_FEE")
    private int payTotalFee;

    /**
     * 应付金额，单位：分 必填
     */
    @XmlElement(name = "PAY_BEHOOVE_FEE")
    private int payBehooveFee;

    /**
     * 个人自付金额，单位：分 必填
     */
    @XmlElement(name = "PAY_ACTUAL_FEE")
    private int payActualFee;

    /**
     * 医疗统筹支付金额，单位：分 必填
     */
    @XmlElement(name = "PAY_MI_FEE")
    private int payMiFee;

    /**
     * 收据号
     */
    @XmlElement(name = "RECEIPT_ID")
    private String receiptId;

    /**
     * 待缴费订单明细集合 必填
     */
    @XmlElement(name = "PAY_DETAIL_LIST")
    private List<PayDetail> payDetailList = new ArrayList<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHospPatientId() {
        return hospPatientId;
    }

    public void setHospPatientId(String hospPatientId) {
        this.hospPatientId = hospPatientId;
    }

    public String getMedicalInsurannceType() {
        return medicalInsurannceType;
    }

    public void setMedicalInsurannceType(String medicalInsurannceType) {
        this.medicalInsurannceType = medicalInsurannceType;
    }

    public int getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(int payTotalFee) {
        this.payTotalFee = payTotalFee;
    }

    public int getPayBehooveFee() {
        return payBehooveFee;
    }

    public void setPayBehooveFee(int payBehooveFee) {
        this.payBehooveFee = payBehooveFee;
    }

    public int getPayActualFee() {
        return payActualFee;
    }

    public void setPayActualFee(int payActualFee) {
        this.payActualFee = payActualFee;
    }

    public int getPayMiFee() {
        return payMiFee;
    }

    public void setPayMiFee(int payMiFee) {
        this.payMiFee = payMiFee;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public List<PayDetail> getPayDetailList() {
        return payDetailList;
    }

    public void setPayDetailList(List<PayDetail> payDetailList) {
        this.payDetailList = payDetailList;
    }

}
