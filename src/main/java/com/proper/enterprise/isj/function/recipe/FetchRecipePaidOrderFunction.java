package com.proper.enterprise.isj.function.recipe;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.recipe.RecipeDetailAllDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.getRecipePaidOrder(RecipeOrderDocument,
 * RefundByHis)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchRecipePaidOrderFunction implements IFunction<RecipePaidDetailDocument> {

    @Override
    public RecipePaidDetailDocument execute(Object... params) throws Exception {
        return getRecipePaidOrder((RecipeOrderDocument) params[0], (RefundByHis) params[1]);
    }

    /**
     * 取得缴费已支付信息.
     *
     * @param recipe 缴费对象.
     * @param refund his退款对象.
     * @return 支付详细信息.
     */
    public static RecipePaidDetailDocument getRecipePaidOrder(RecipeOrderDocument recipe, RefundByHis refund) {
        RecipePaidDetailDocument recipePaidOrder = null;
        List<RecipeDetailAllDocument> detailList;
        List<RecipePaidDetailDocument> paidList = recipe.getRecipePaidDetailList();
        for (RecipePaidDetailDocument recipePaidDetailDocument : paidList) {
            detailList = recipePaidDetailDocument.getDetailList();
            for (RecipeDetailAllDocument recipeDetailAllDocument : detailList) {
                if (recipeDetailAllDocument.getRecipeNo().equals(refund.getRecipeNo())) {
                    recipePaidOrder = recipePaidDetailDocument;
                    break;
                }
            }
        }
        return recipePaidOrder;
    }

}
