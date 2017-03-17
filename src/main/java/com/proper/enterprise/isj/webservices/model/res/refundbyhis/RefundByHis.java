package com.proper.enterprise.isj.webservices.model.res.refundbyhis;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 历史退款.
 * Created by think on 2016/10/2 0002.
 */
@XmlRootElement(name = "REFUNDLIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundByHis implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 序号
     */
    @XmlElement(name = "ID")
    private String id;

    /**
     * 类别,1:挂号,2:处方
     */
    @XmlElement(name = "TYPE")
    private String type;

    /**
     * 挂号订单号
     */
    @XmlElement(name = "ORDER_ID")
    private String orderId;

    /**
     * 处方号
     */
    @XmlElement(name = "RECIPE_NO")
    private String recipeNo;
    /**
     * 处方内流水号
     */
    @XmlElement(name = "SEQUENCE_NO")
    private String sequenceNo;

    /**
     * 退费名称
     */
    @XmlElement(name = "NAME")
    private String name;

    /**
     * 复方包Code
     */
    @XmlElement(name = "PACKAGE_CODE")
    private String packageCode;

    /**
     * 门诊流水号
     */
    @XmlElement(name = "CLINIC_CODE")
    private String clinicCode;

    /**
     * 金额
     */
    @XmlElement(name = "COST")
    private String cost;

    /**
     * 状态
     */
    @XmlElement(name = "STATUS")
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRecipeNo() {
        return recipeNo;
    }

    public void setRecipeNo(String recipeNo) {
        this.recipeNo = recipeNo;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
