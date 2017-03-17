package com.proper.enterprise.isj.proxy.document.recipe;

import java.util.ArrayList;
import java.util.List;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/9/13 0013.
 */
public class RecipeDetailDocument extends BaseDocument {

    /**
     *
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 处方号
     */
    private String recipeNum;

    /**
     * 执行科室
     */
    private String dept;

    /**
     * 执行地点
     */
    private String location;

    /**
     * 合计
     */
    private String total;

    /**
     * 缴费状态code
     */
    private String statusCode;
    /**
     * 缴费状态
     */
    private String status;

    /**
     *
     */
    private List<RecipeDetailItemDocument> items = new ArrayList<>();

    public String getRecipeNum() {
        return recipeNum;
    }

    public void setRecipeNum(String recipeNum) {
        this.recipeNum = recipeNum;
    }

    public List<RecipeDetailItemDocument> getItems() {
        return items;
    }

    public void setItems(List<RecipeDetailItemDocument> items) {
        this.items = items;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
