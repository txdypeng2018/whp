package com.proper.enterprise.isj.function.recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderReqDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.getRecipeRequestOrderNoMap(RecipeOrderDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchRecipeRequestOrderNoMapFunction implements IFunction<Map<String, String>> {

    @Override
    public Map<String, String> execute(Object... params) throws Exception {
        return getRecipeRequestOrderNoMap((RecipeOrderDocument) params[0]);
    }

    /**
     * 获取已经缴费的Map信息.
     *
     * @param regBack 缴费信息.
     * @return 已经缴费的Map信息.
     */
    public static Map<String, String> getRecipeRequestOrderNoMap(RecipeOrderDocument regBack) {
        Map<String, String> requestOrderNoMap = new HashMap<>();
        List<RecipePaidDetailDocument> paidList = regBack.getRecipePaidDetailList();
        for (RecipePaidDetailDocument recipePaidDetailDocument : paidList) {
            requestOrderNoMap.put(recipePaidDetailDocument.getOrderNum(), recipePaidDetailDocument.getPayChannelId());
        }
        if (regBack.getRecipeOrderReqMap() != null) {
            for (Map.Entry<String, RecipeOrderReqDocument> stringRecipeOrderReqDocumentEntry : regBack
                    .getRecipeOrderReqMap().entrySet()) {
                requestOrderNoMap.put(stringRecipeOrderReqDocumentEntry.getKey(),
                        String.valueOf(stringRecipeOrderReqDocumentEntry.getValue().getPayChannelId().getCode()));
            }
        }
        return requestOrderNoMap;
    }

}
