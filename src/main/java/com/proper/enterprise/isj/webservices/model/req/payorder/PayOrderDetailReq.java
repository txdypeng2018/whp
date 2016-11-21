package com.proper.enterprise.isj.webservices.model.req.payorder;

import java.io.Serializable;

/**
 * Created by think on 2016/10/6 0006.
 */
public class PayOrderDetailReq implements Serializable {

    /**
     * 挂号流程号
     */
    private String clinicCode;

    /**
     * 处方号
     */
    private String recipeNo;
    /**
     * 病历号
     */
    private String cardNo;

    /**
     * 挂号时间
     */
    private String regDate;
    /**
     * 项目代码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 1:药品,2:非药品
     */
    private String drugFlag;
    /**
     * 单价
     */
    private int unitPrice;
    /**
     * 总数量(如:100片/瓶,开了2瓶,返回200)
     */
    private String qty;
    /**
     * 计价单位
     */
    private String priceUnit;
    /**
     * 执行科室名称
     */
    private String execDpnm;
    /**
     * 收费标识,0:未收费,1:收费
     */
    private String payFlag;
    /**
     * 1:未作废,2:已作废
     */
    private String cancelFlag;
    /**
     * 开方医生
     */
    private String doctCode;
    /**
     * 开方科室
     */
    private String doctDept;
    /**
     * 医嘱号
     */
    private String moOrder;
    /**
     * 处方流水号
     */
    private String sequenceNo;
    /**
     * 复合项目代码
     */
    private String packageCode;
    /**
     * 复合项目名称
     */
    private String packageName;

    /**
     * 单条项目总价
     */
    private String ownCost;

    /**
     * 包装数量(如:100片/瓶,返回100)
     */
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
}
