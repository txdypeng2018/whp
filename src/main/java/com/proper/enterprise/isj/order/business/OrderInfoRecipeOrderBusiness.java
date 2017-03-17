package com.proper.enterprise.isj.order.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MapParamsObjectValueContext;
import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class OrderInfoRecipeOrderBusiness<M extends MapParamsObjectValueContext<Map<String, String>> & ModifiedResultBusinessContext<Map<String, String>>>
        implements IBusiness<Map<String, String>, M> {
    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RegistrationService registrationService;

    @Override
    public void process(M ctx) throws RecipeException, Exception {
        Map<String, Object> recipeMap = ctx.getMapParams();
        String memberId = (String) recipeMap.get("memberId");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> outpatientList = (List<Map<String, Object>>) recipeMap.get("outpatients");
        String clinicCode = "";
        for (Map<String, Object> map : outpatientList) {
            clinicCode = (String) map.get("outpatientNum");
        }
        if (StringUtil.isEmpty(clinicCode)) {
            throw new RecipeException(CenterFunctionUtils.ORDER_SAVE_ERR);
        }
        RecipeOrderDocument tempRecipeOrder = recipeService.getRecipeOrderDocumentByClinicCode(clinicCode);
        if (tempRecipeOrder != null) {
            RecipePaidDetailDocument nonPaid = tempRecipeOrder.getRecipeNonPaidDetail();
            if (nonPaid != null && StringUtil.isNotEmpty(nonPaid.getOrderNum())) {
                if (tempRecipeOrder.getRecipeOrderReqMap().containsKey(nonPaid.getOrderNum())) {
                    throw new RecipeException(
                            "门诊流水号:" + clinicCode + ",缴费订单未得到处理,请在意见反馈中进行反馈," + CenterFunctionUtils.ORDER_SAVE_ERR);
                }
                String payWay = nonPaid.getPayChannelId();
                boolean paidFlag = orderService.checkOrderIsPay(payWay, nonPaid.getOrderNum());
                if (paidFlag) {
                    throw new RecipeException(CenterFunctionUtils.ORDER_ALREADY_PAID_ERR);
                }
            }
        }

        RecipeOrderDocument recipeOrder = recipeService.saveOrderAndRecipeOrderDocument(memberId, clinicCode);
        if (recipeOrder == null) {
            throw new RecipeException(CenterFunctionUtils.ORDER_SAVE_ERR);
        }
        Map<String, String> map = new HashMap<>();
        map.put("orderNum", recipeOrder.getRecipeNonPaidDetail().getOrderNum());
        ctx.setResult(map);
    }
    
}
