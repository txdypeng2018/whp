package com.proper.enterprise.isj.webservices.model.res;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.webservices.model.res.orderreg.Concession;
import com.proper.enterprise.platform.core.enums.WhetherType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderReg implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 医院订单号 必填
     */
    @XmlElement(name = "HOSP_ORDER_ID")
    private String hospOrderId;

    /**
     * 医院病人ID 必填
     */
    @XmlElement(name = "HOSP_PATIENT_ID")
    private String hospPatientId;

    /**
     * 医院候诊号
     */
    @XmlElement(name = "HOSP_SERIAL_NUM")
    private String hospSerialNum;

    /**
     * 医院病历号或门诊号
     */
    @XmlElement(name = "HOSP_MEDICAL_NUM")
    private String hospMedicalNum;

    /**
     * 医院取号时间段，格式：HH24MI-HH24MI
     */
    @XmlElement(name = "HOSP_GETREG_DATE")
    private String hospGetregDate;

    /**
     * 医院就诊地址
     */
    @XmlElement(name = "HOSP_SEE_DOCT_ADDR")
    private String hospSeeDoctAddr;

    /**
     * 本次就诊院内就诊卡，主要用户首诊用户挂号后在医院开卡，返回通知平台用户凭此卡就诊
     */
    @XmlElement(name = "HOSP_CARD_NO")
    private String hospCardNo;

    /**
     * 备注
     */
    @XmlElement(name = "HOSP_REMARK")
    private String hospRemark;

    /**
     * 是否享受院内优惠：0-不享受 1-享受 必填
     */
    @XmlElement(name = "IS_CONCESSIONS")
    private int isConcessions;

    /**
     * 优惠信息，当IS_CONCESSIONS=1必传
     */
    @XmlElement(name = "CONCESSIONS")
    private List<Concession> concessions = new ArrayList<>();

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

    public String getHospSerialNum() {
        return hospSerialNum;
    }

    public void setHospSerialNum(String hospSerialNum) {
        this.hospSerialNum = hospSerialNum;
    }

    public String getHospMedicalNum() {
        return hospMedicalNum;
    }

    public void setHospMedicalNum(String hospMedicalNum) {
        this.hospMedicalNum = hospMedicalNum;
    }

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

    public String getHospCardNo() {
        return hospCardNo;
    }

    public void setHospCardNo(String hospCardNo) {
        this.hospCardNo = hospCardNo;
    }

    public String getHospRemark() {
        return hospRemark;
    }

    public void setHospRemark(String hospRemark) {
        this.hospRemark = hospRemark;
    }

    public WhetherType getIsConcessions() {
        return WhetherType.codeOf(isConcessions);
    }

    public void setIsConcessions(int isConcessions) {
        this.isConcessions = isConcessions;
    }

    public List<Concession> getConcessions() {
        return concessions;
    }

    public void setConcessions(List<Concession> concessions) {
        this.concessions = concessions;
    }
}
