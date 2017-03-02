package com.proper.enterprise.isj.admin.service;

import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;

import java.util.Map;

public interface AdminCustomerService {

    /**
     * 设置患者信息
     */
    Map<String, Object> setUserInfo(String id, BasicInfoDocument basicInfoDocument,
            String type, UserInfoDocument selfUserInfoDocument);

    /**
     * 设置缴费信息
     */
    Map<String, Object> setRecipeOrderInfo(RecipeOrderDocument recipeOrderDocument);
}
