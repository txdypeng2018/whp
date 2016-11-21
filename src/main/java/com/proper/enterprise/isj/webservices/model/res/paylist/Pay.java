package com.proper.enterprise.isj.webservices.model.res.paylist;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PAY_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pay implements Serializable {

    /**
     * 挂号流程号
     */
    @XmlElement(name = "CLINIC_CODE")
    private String clinicCode;

    /**
     * 缴费批次号
     */
    @XmlElement(name = "HOSP_SEQUENCE")
    private String hospSequence;

    /**
     * 处方号
     */
    @XmlElement(name = "RECIPE_NO")
    private String recipeNo;
    /**
     * 病历号
     */
    @XmlElement(name = "CARD_NO")
    private String cardNo;

    /**
     * 挂号时间
     */
    @XmlElement(name = "REG_DATE")
    private String regDate;
    /**
     * 项目代码
     */
    @XmlElement(name = "ITEM_CODE")
    private String itemCode;

    /**
     * 项目名称
     */
    @XmlElement(name = "ITEM_NAME")
    private String itemName;

    /**
     * 1:药品,2:非药品
     */
    @XmlElement(name = "DRUG_FLAG")
    private String drugFlag;
    /**
     * 单价
     */
    @XmlElement(name = "UNIT_PRICE")
    private int unitPrice;
    /**
     * 总数量(如:100片/瓶,开了2瓶,返回200)
     */
    @XmlElement(name = "QTY")
    private String qty;
    /**
     * 计价单位
     */
    @XmlElement(name = "PRICE_UNIT")
    private String priceUnit;
    /**
     * 执行科室名称
     */
    @XmlElement(name = "EXEC_DPNM")
    private String execDpnm;
    /**
     * 收费标识,0:未收费,1:收费
     */
    @XmlElement(name = "PAY_FLAG")
    private String payFlag;
    /**
     * 1:未作废,2:已作废
     */
    @XmlElement(name = "CANCEL_FLAG")
    private String cancelFlag;
    /**
     * 开方医生
     */
    @XmlElement(name = "DOCT_CODE")
    private String doctCode;
    /**
     * 开方科室
     */
    @XmlElement(name = "DOCT_DEPT")
    private String doctDept;
    /**
     * 医嘱号
     */
    @XmlElement(name = "MO_ORDER")
    private String moOrder;
    /**
     * 处方流水号
     */
    @XmlElement(name = "SEQUENCE_NO")
    private String sequenceNo;
    /**
     * 复合项目代码
     */
    @XmlElement(name = "PACKAGE_CODE")
    private String packageCode;
    /**
     * 复合项目名称
     */
    @XmlElement(name = "PACKAGE_NAME")
    private String packageName;

    /**
     * 单条项目总价
     */
    @XmlElement(name = "OWN_COST")
    private String ownCost;

    /**
     * 包装数量(如:100片/瓶,返回100)
     */
    @XmlElement(name = "PACK_QTY")
    private String packQty;

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getRecipeNo() {
        return recipeNo;
    }

    public void setRecipeNo(String recipeNo) {
        this.recipeNo = recipeNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDrugFlag() {
        return drugFlag;
    }

    public void setDrugFlag(String drugFlag) {
        this.drugFlag = drugFlag;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getExecDpnm() {
        return execDpnm;
    }

    public void setExecDpnm(String execDpnm) {
        this.execDpnm = execDpnm;
    }

    public String getPayFlag() {
        return payFlag;
    }

    public void setPayFlag(String payFlag) {
        this.payFlag = payFlag;
    }

    public String getCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(String cancelFlag) {
        this.cancelFlag = cancelFlag;
    }

    public String getDoctCode() {
        return doctCode;
    }

    public void setDoctCode(String doctCode) {
        this.doctCode = doctCode;
    }

    public String getDoctDept() {
        return doctDept;
    }

    public void setDoctDept(String doctDept) {
        this.doctDept = doctDept;
    }

    public String getMoOrder() {
        return moOrder;
    }

    public void setMoOrder(String moOrder) {
        this.moOrder = moOrder;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getOwnCost() {
        return ownCost;
    }

    public void setOwnCost(String ownCost) {
        this.ownCost = ownCost;
    }

    public String getPackQty() {
        return packQty;
    }

    public void setPackQty(String packQty) {
        this.packQty = packQty;
    }

    public String getHospSequence() {
        return hospSequence;
    }

    public void setHospSequence(String hospSequence) {
        this.hospSequence = hospSequence;
    }


}
