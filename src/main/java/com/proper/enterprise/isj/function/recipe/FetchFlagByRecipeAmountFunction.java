package com.proper.enterprise.isj.function.recipe;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.getFlagByRecipeAmount(String, User,
 * RecipeOrderDocument)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchFlagByRecipeAmountFunction implements IFunction<Boolean>, ILoggable {

    @Autowired
    FunctionToolkit toolkitx;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public Boolean execute(Object... params) throws Exception {
        return getFlagByRecipeAmount((String) params[0], (User) params[1], (RecipeOrderDocument) params[2]);
    }

    /**
     * 校验缴费与支付是否相等
     *
     * @param orderAmount 退款金额.
     * @param user 用户.
     * @param recipe 退款报文.
     * @return 检查结果.
     * @throws Exception 异常.
     */
    private boolean getFlagByRecipeAmount(String orderAmount, User user, RecipeOrderDocument recipe) throws Exception {
        boolean flag = false;
        BasicInfoDocument basic = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(),
                recipe.getPatientId());
        ResModel<PayList> payList = toolkitx.executeFunction(FindPayListModelFunction.class, basic,
                recipe.getClinicCode(), "0", null, null, false);
        if (payList.getReturnCode() == ReturnCode.SUCCESS) {
            if (payList.getRes().getPayList() != null) {
                List<Pay> list = payList.getRes().getPayList();
                BigDecimal total = new BigDecimal("0");
                for (Pay pay : list) {
                    if (pay.getClinicCode().equals(recipe.getClinicCode())) {
                        total = total.add(new BigDecimal(pay.getOwnCost()));
                    }
                }
                if (total.compareTo(new BigDecimal(recipe.getRecipeNonPaidDetail().getAmount())) == 0
                        && total.compareTo(new BigDecimal(orderAmount)) == 0) {
                    flag = true;
                }
                debug("预支付前对金额金额进行校验,门诊流水号:{},支付金额:{},HIS端待缴金额:{}", recipe.getClinicCode(), orderAmount,
                        total.toString());
            }
        } else {
            debug("预支付前校验,获得HIS的待缴费接口返回值有问题,返回消息:{}", payList.getReturnMsg());
        }
        return flag;
    }

}
