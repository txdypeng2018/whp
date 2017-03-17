package com.proper.enterprise.isj.proxy.business.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.context.OrderAmountContext;
import com.proper.enterprise.isj.context.OrderNumContext;
import com.proper.enterprise.isj.context.PayChannelContext;
import com.proper.enterprise.isj.function.recipe.FetchFlagByRecipeAmountFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class CheckRecipeAmountBusiness<M extends OrderNumContext<Boolean> & OrderAmountContext<Boolean> & PayChannelContext<Boolean> & ModifiedResultBusinessContext<Boolean>>
        implements IBusiness<Boolean, M>, ILoggable {

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    RecipeServiceImpl recipeServiceImpl;

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    /**
     * 校验订单下的金额与待缴费的金额是否一致.
     *
     * @param orderNum 订单编号.
     * @param orderAmount 订单金额.
     * @param payWay 支付渠道.
     * @return 校验结果.
     * @throws Exception 异常.
     */
    public boolean checkRecipeAmount(String orderNum, String orderAmount, PayChannel payWay) throws Exception {
        boolean flag = false;
        User user = userService.getCurrentUser();
        Order order = orderService.findByOrderNo(orderNum);
        if (order != null) {

            RecipeOrderDocument recipe = toolkitx.executeRepositoryFunction(RecipeOrderRepository.class, "findOne",
                    order.getFormId().split("_")[0]);

            if (recipe != null) {
                LOGGER.debug("预支付前校验,找到订单和缴费单,门诊流水号:{}", recipe.getClinicCode());
                flag = toolkitx.executeFunction(FetchFlagByRecipeAmountFunction.class, orderAmount, user, recipe);
                if (flag) {
                    recipe.getRecipeNonPaidDetail().setPayChannelId(String.valueOf(payWay.getCode()));
                    recipeServiceImpl.saveRecipeOrderDocument(recipe);
                    order.setPayWay(String.valueOf(payWay.getCode()));
                    orderService.save(order);
                } else {
                    info("预支付前校验金额是否一致,返回否,门诊流水号:{}", recipe.getClinicCode());
                }
                recipe = toolkitx.executeRepositoryFunction(RecipeOrderRepository.class, "findOne",
                        order.getFormId().split("_")[0]);
                if (StringUtil.isEmpty(recipe.getRecipeNonPaidDetail().getPayChannelId())) {
                    flag = false;
                }
            } else {
                LOGGER.debug("预支付前校验,根据订单未找到对应的缴费单,订单号:{}", orderNum);
            }
        } else {
            LOGGER.debug("预支付前校验,未查到订单号对应的订单,订单号:{}", orderNum);
        }
        return flag;
    }

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(checkRecipeAmount(ctx.getOrderNum(), ctx.getOrderAmount(), ctx.getPayChannel()));
    }
}
