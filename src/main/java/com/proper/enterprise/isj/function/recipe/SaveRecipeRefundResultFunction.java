package com.proper.enterprise.isj.function.recipe;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.function.message.SaveSingleDetailMessageFunction;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeRefundDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class SaveRecipeRefundResultFunction implements IFunction<Object> {

    @Autowired
    FunctionToolkit toolkitx;
    

    @Autowired
    RecipeServiceImpl recipeServiceImpl;

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        saveRecipeRefundResult((RecipeOrderDocument) params[0], (RefundByHis) params[1],
                (Map<String, RecipeRefundDetailDocument>) params[2], (String) params[3], (String) params[4]);
        return null;
    }

    /**
     * 保存缴费退款信息.
     *
     * @param recipe 缴费对象.
     * @param refund 退款对象.
     * @param refundMap his返回退款对象.
     * @param refundNo 退款流水号.
     * @param refunReturnMsg 退款返回的信息.
     * @throws Exception 异常.
     */
    public void saveRecipeRefundResult(RecipeOrderDocument recipe, RefundByHis refund,
            Map<String, RecipeRefundDetailDocument> refundMap, String refundNo, String refunReturnMsg)
            throws Exception {
        RecipeRefundDetailDocument refundDetail = new RecipeRefundDetailDocument();
        BeanUtils.copyProperties(refund, refundDetail);
        refundDetail.setClinicCode(recipe.getClinicCode());
        refundDetail.setRefundNo(refundNo);
        refundDetail.setRefundReturnMsg(refunReturnMsg);
        refundMap.put(
                refund.getId().concat("_").concat(DateUtil.toString(new Date(), PEPConstants.DEFAULT_DATETIME_FORMAT)),
                refundDetail);
        recipe.setRecipeRefundDetailDocumentMap(refundMap);
        recipeServiceImpl.saveRecipeOrderDocument(recipe);
        if (refunReturnMsg.equalsIgnoreCase("Success")) {
            toolkitx.executeFunction(SaveSingleDetailMessageFunction.class, recipe,
                    SendPushMsgEnum.RECIPE_REFUND_SUCCESS, refundDetail);
        } else {
            toolkitx.executeFunction(SaveSingleDetailMessageFunction.class, recipe, SendPushMsgEnum.RECIPE_REFUND_FAIL,
                    refundDetail);
        }
    }
}
