package com.proper.enterprise.isj.webservices.model.res;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayReg implements Serializable {

    /**
     * 医院支付单号
     */
    @XmlElement(name = "HOSP_PAY_ID")
    private String hospPayId;

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
     * 医院取号时间段，格式：HH24:MI-HH24:MI
     */
    @XmlElement(name = "HOSP_GETREG_DATE")
    private String hospGetregDate;

    /**
     * 医院就诊地址
     */
    @XmlElement(name = "HOSP_SEE_DOCT_ADDR")
    private String hospSeeDoctAddr;

    /**
     * 备注，例如当天挂号返回请到XX诊室取号或就诊
     */
    @XmlElement(name = "HOSP_REMARK")
    private String hospRemark;

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

    public String getHospRemark() {
        return hospRemark;
    }

    public void setHospRemark(String hospRemark) {
        this.hospRemark = hospRemark;
    }
}
