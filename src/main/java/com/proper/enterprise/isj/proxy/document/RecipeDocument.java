package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.isj.proxy.document.recipe.RecipeDetailDocument;
import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by think on 2016/9/13 0013.
 * 
 * 缴费信息
 */
public class RecipeDocument extends BaseDocument {

    /**
     * 门诊登记号
     */
    private String outpatientNum;

    /**
     * 挂号日期
     */
    private String outpatientDate;

    /**
     * 缴费明细
     */
    private List<RecipeDetailDocument> recipes = new ArrayList<>();

    public String getOutpatientNum() {
        return outpatientNum;
    }

    public void setOutpatientNum(String outpatientNum) {
        this.outpatientNum = outpatientNum;
    }

    public String getOutpatientDate() {
        return outpatientDate;
    }

    public void setOutpatientDate(String outpatientDate) {
        this.outpatientDate = outpatientDate;
    }

    public List<RecipeDetailDocument> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeDetailDocument> recipes) {
        this.recipes = recipes;
    }
}
