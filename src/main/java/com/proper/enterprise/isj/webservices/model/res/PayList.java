package com.proper.enterprise.isj.webservices.model.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.webservices.model.enmus.CardType;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayList implements Serializable {

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
     * 用户证件类型，详情见 “证件类型”
     */
    @XmlElement(name = "IDCARD_TYPE")
    private int idcardType;

    /**
     * 用户证件号码
     */
    @XmlElement(name = "IDCARD_NO")
    private String idcardNo;

    /**
     * 用户卡类型，详见 “卡类型”
     */
    @XmlElement(name = "CARD_TYPE")
    private int cardType;

    /**
     * 用户卡号
     */
    @XmlElement(name = "CARD_NO")
    private String cardNo;

    @XmlElement(name = "PAY_TOTAL_FEE")
    private String payTotalFee;

    /**
     * 待缴费记录列表（就诊记录为空不返回）
     */
    @XmlElement(name = "PAY_LIST")
    private List<Pay> payList = new ArrayList<>();

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

    public IDCardType getIdcardType() {
        return IDCardType.codeOf(idcardType);
    }

    public void setIdcardType(int idcardType) {
        this.idcardType = idcardType;
    }

    public String getIdcardNo() {
        return idcardNo;
    }

    public void setIdcardNo(String idcardNo) {
        this.idcardNo = idcardNo;
    }

    public CardType getCardType() {
        return CardType.codeOf(cardType);
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public List<Pay> getPayList() {
        return payList;
    }

    public void setPayList(List<Pay> payList) {
        this.payList = payList;
    }

    public String getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(String payTotalFee) {
        this.payTotalFee = payTotalFee;
    }
}
