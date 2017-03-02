package com.proper.enterprise.isj.admin.service.impl;

import com.proper.enterprise.isj.admin.service.AdminCustomerService;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminCustomerServiceImpl implements AdminCustomerService {

    @Override
    public Map<String, Object> setUserInfo(String id, BasicInfoDocument basicInfoDocument,
            String type, UserInfoDocument selfUserInfoDocument) {
        Map<String, Object> userInfoMap = new HashMap<String, Object>();
        userInfoMap.put("userId", id);
        userInfoMap.put("name", basicInfoDocument.getName());
        userInfoMap.put("sexCode", basicInfoDocument.getSexCode());
        userInfoMap.put("sex", basicInfoDocument.getSex());
        userInfoMap.put("medicalNum", basicInfoDocument.getMedicalNum());
        userInfoMap.put("idCard", basicInfoDocument.getIdCard());
        userInfoMap.put("phone", basicInfoDocument.getPhone());
        userInfoMap.put("createTime", basicInfoDocument.getCreateTime());
        userInfoMap.put("lastModifyTime", basicInfoDocument.getLastModifyTime());
        if ("1".equals(type)) {
            userInfoMap.put("type", type);
            userInfoMap.put("typeName", "账号本人");
            userInfoMap.put("accountUserId", id);
            userInfoMap.put("accountName", "");
            userInfoMap.put("accountPhone", "");
        } else {
            userInfoMap.put("type", type);
            userInfoMap.put("typeName", "家庭成员");
            userInfoMap.put("accountUserId", selfUserInfoDocument.getUserId());
            userInfoMap.put("accountName", selfUserInfoDocument.getName());
            userInfoMap.put("accountPhone", selfUserInfoDocument.getPhone());
        }
        return userInfoMap;
    }

    @Override
    public Map<String, Object> setRecipeOrderInfo(RecipeOrderDocument recipeOrderDocument) {
        Map<String, Object> recipeMap = new HashMap<String, Object>();
        String status = "";
        String statusCode = "";
        if (recipeOrderDocument.getRecipePaidFailDetailList() != null
                && recipeOrderDocument.getRecipePaidFailDetailList().size() > 0) {
            statusCode = "2";
            status = "支付失败";
        } else if (recipeOrderDocument.getRecipePaidDetailList() != null
                && recipeOrderDocument.getRecipePaidDetailList().size() > 0) {
            statusCode = "1";
            status = "已支付";
        } else {
            statusCode = "0";
            status = "未支付";
        }
        recipeMap.put("status", status);
        recipeMap.put("statusCode", statusCode);
        recipeMap.put("patientId", recipeOrderDocument.getPatientId());
        recipeMap.put("patientName", recipeOrderDocument.getPatientName());
        recipeMap.put("clinicCode", recipeOrderDocument.getClinicCode());
        recipeMap.put("operatorId", recipeOrderDocument.getOperatorId());
        recipeMap.put("operatorName", recipeOrderDocument.getOperatorName());
        recipeMap.put("operatorPhone", recipeOrderDocument.getOperatorPhone());
        recipeMap.put("createTime", recipeOrderDocument.getCreateTime());
        recipeMap.put("lastModifyTime", recipeOrderDocument.getLastModifyTime());
        recipeMap.put("recipeNonPaidDetail", recipeOrderDocument.getRecipeNonPaidDetail());
        recipeMap.put("recipePaidDetailList", recipeOrderDocument.getRecipePaidDetailList());
        recipeMap.put("recipePaidFailDetailList", recipeOrderDocument.getRecipePaidFailDetailList());
        recipeMap.put("recipeOrderReqMap", recipeOrderDocument.getRecipeOrderReqMap());
        recipeMap.put("recipeOrderHisMap", recipeOrderDocument.getRecipeOrderHisMap());
        return recipeMap;
    }
}
