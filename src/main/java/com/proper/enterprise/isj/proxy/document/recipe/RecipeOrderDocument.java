package com.proper.enterprise.isj.proxy.document.recipe;

import com.proper.enterprise.platform.core.converter.AESConverter;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by think on 2016/9/17 0017. 缴费订单信息
 */
@Document(collection = "recipeorder")
public class RecipeOrderDocument extends BaseDocument {

    private static final long serialVersionUID = -1;

    /**
     * 患者id
     */
    private String patientId;

    /**
     * 患者名称
     */
    private String patientName;

    /**
     * 门诊流水号
     */
    private String clinicCode;



    /*-----------------操作人信息-----------*/

    /**
     * 操作人Id
     */
    private String operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作人手机
     */
    private String operatorPhone;

    /*-----------------操作人信息-----------*/

    @Transient
    private transient AESConverter converter = new AESConverter();


    /**
     * 退费明细(key:his退费Id号_记录创建时间)
     */
    private Map<String, RecipeRefundDetailDocument> recipeRefundDetailDocumentMap = new HashMap<>();

    /*---------------缴费项目明细-----------*/

    /**
     * 待缴费
     */
    private RecipePaidDetailDocument recipeNonPaidDetail = new RecipePaidDetailDocument();

    /**
     * 已缴费集合
     */
    private List<RecipePaidDetailDocument> recipePaidDetailList = new ArrayList<>();

    /**
     * 缴费失败集合
     */
    private List<RecipePaidDetailDocument> recipePaidFailDetailList = new ArrayList<>();

    /**
     * 请求his的参数
     */
    private Map<String, RecipeOrderReqDocument> recipeOrderReqMap = new HashMap<>();

    /**
     * 请求his成功后返回的参数
     */
    private Map<String, RecipeOrderHisDocument> recipeOrderHisMap = new HashMap<>();


    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
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


    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    public Map<String, RecipeOrderReqDocument> getRecipeOrderReqMap() {
        return recipeOrderReqMap;
    }

    public void setRecipeOrderReqMap(Map<String, RecipeOrderReqDocument> recipeOrderReqMap) {
        this.recipeOrderReqMap = recipeOrderReqMap;
    }

    public Map<String, RecipeOrderHisDocument> getRecipeOrderHisMap() {
        return recipeOrderHisMap;
    }

    public void setRecipeOrderHisMap(Map<String, RecipeOrderHisDocument> recipeOrderHisMap) {
        this.recipeOrderHisMap = recipeOrderHisMap;
    }

    public Map<String, RecipeRefundDetailDocument> getRecipeRefundDetailDocumentMap() {
        return recipeRefundDetailDocumentMap;
    }

    public void setRecipeRefundDetailDocumentMap(
            Map<String, RecipeRefundDetailDocument> recipeRefundDetailDocumentMap) {
        this.recipeRefundDetailDocumentMap = recipeRefundDetailDocumentMap;
    }

    public String getClinicCode() {
        return clinicCode;
    }

    public void setClinicCode(String clinicCode) {
        this.clinicCode = clinicCode;
    }


    public RecipePaidDetailDocument getRecipeNonPaidDetail() {
        return recipeNonPaidDetail;
    }

    public void setRecipeNonPaidDetail(RecipePaidDetailDocument recipeNonPaidDetail) {
        this.recipeNonPaidDetail = recipeNonPaidDetail;
    }

    public List<RecipePaidDetailDocument> getRecipePaidDetailList() {
        return recipePaidDetailList;
    }

    public void setRecipePaidDetailList(List<RecipePaidDetailDocument> recipePaidDetailList) {
        this.recipePaidDetailList = recipePaidDetailList;
    }

    public String getPatientName() {
        return this.converter.convertToEntityAttribute(patientName);
    }

    public void setPatientName(String patientName) {
        this.patientName = this.converter.convertToDatabaseColumn(patientName);
    }

    public List<RecipePaidDetailDocument> getRecipePaidFailDetailList() {
        return recipePaidFailDetailList;
    }

    public void setRecipePaidFailDetailList(List<RecipePaidDetailDocument> recipePaidFailDetailList) {
        this.recipePaidFailDetailList = recipePaidFailDetailList;
    }
}
