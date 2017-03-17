package com.proper.enterprise.isj.function.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl.createRecipeOrder(UserInfoDocument,
 * BasicInfoDocument, String, RecipePaidDetailDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class CreateRecipeOrderFunction implements IFunction<RecipeOrderDocument> {

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    @Autowired
    OrderService orderService;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public RecipeOrderDocument execute(Object... params) throws Exception {
        return createRecipeOrder((UserInfoDocument) params[0], (BasicInfoDocument) params[1], (String) params[2],
                (RecipePaidDetailDocument) params[3]);
    }

    /**
     * 保存或更新缴费已支付信息.
     *
     * @param userInfo 用户信息.
     * @param info 用户基本信息对象.
     * @param clinicCode 门诊流水号.
     * @param paidDetal 已支付信息.
     * @return 缴费信息对象.
     * @throws Exception 异常.
     */
    public RecipeOrderDocument createRecipeOrder(UserInfoDocument userInfo, BasicInfoDocument info, String clinicCode,
            RecipePaidDetailDocument paidDetal) throws Exception {
        RecipeOrderDocument recipeOrder = recipeOrderRepository.getByClinicCode(clinicCode);
        if (recipeOrder == null) {
            recipeOrder = new RecipeOrderDocument();
            recipeOrder.setPatientId(info.getId());
            recipeOrder.setPatientName(info.getName());
            recipeOrder.setClinicCode(clinicCode);
        } else {
            RecipePaidDetailDocument paid = recipeOrder.getRecipeNonPaidDetail();
            if (paid != null) {
                String payWay = paid.getPayChannelId();
                boolean paidFlag = orderService.checkOrderIsPay(payWay, paid.getOrderNum());
                if (paidFlag) {
                    throw new RecipeException(CenterFunctionUtils.ORDER_ALREADY_PAID_ERR);
                }
            }
        }
        recipeOrder.setOperatorPhone(userInfo.getPhone());
        recipeOrder.setOperatorName(userInfo.getName());
        recipeOrder.setOperatorId(userInfo.getId());
        recipeOrder.setRecipeNonPaidDetail(paidDetal);
        return recipeOrderRepository.save(recipeOrder);
    }

}