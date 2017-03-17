package com.proper.enterprise.isj.proxy.service.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ClinicCodeContext;
import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.context.RecipeOrderDocumentContext;
import com.proper.enterprise.isj.proxy.business.recipe.SaveOrderAndRecipeOrderDocumentBusiness;
import com.proper.enterprise.isj.proxy.business.recipe.SaveRecipeOrderDocumentBusiness;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.support.service.AbstractService;

@Service
public class RecipeServiceImpl extends AbstractService {

    /**
     * 保存缴费订单信息.
     *
     * @param recipeOrder 缴费订单对象.
     * @return 保存后的缴费订单信息.
     */
    public RecipeOrderDocument saveRecipeOrderDocument(RecipeOrderDocument recipeOrder) {
        return toolkit.execute(SaveRecipeOrderDocumentBusiness.class, (c)->{
            ((RecipeOrderDocumentContext<?>)c).setRecipeOrderDocument(recipeOrder);
        });
    }

    /**
     * 保存订单以及缴费订单信息.
     *
     * @param memberId 家庭成员Id.
     * @param clinicCode 门诊流水号.
     * @return 缴费订单信息.
     * @throws Exception 异常.
     */
    public RecipeOrderDocument saveOrderAndRecipeOrderDocument(String memberId, String clinicCode) throws Exception {
        
        return toolkit.execute(SaveOrderAndRecipeOrderDocumentBusiness.class, (c)->{
            ((MemberIdContext<?>)c).setMemberId(memberId);
            ((ClinicCodeContext<?>)c).setClinicCode(clinicCode);
        });
        
    }

}