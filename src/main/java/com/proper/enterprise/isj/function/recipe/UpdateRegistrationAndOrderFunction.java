package com.proper.enterprise.isj.function.recipe;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.message.SaveSingleDetailMessageFunction;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderHisDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq;
import com.proper.enterprise.isj.webservices.model.res.PayOrder;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * old:com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.updateRegistrationAndOrder(Order, PayOrderReq,
 * RecipeOrderDocument, String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class UpdateRegistrationAndOrderFunction implements IFunction<Order>, ILoggable {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    RecipeServiceImpl recipeServiceImpl;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    protected RepositoryFunctionToolkit toolkit;

    @Override
    public Order execute(Object... params) throws Exception {
        return updateRegistrationAndOrder((Order) params[0], (PayOrderReq) params[1], (RecipeOrderDocument) params[2],
                (String) params[3]);
    }

    /**
     * 更新缴费及订单信息.
     *
     * @param order 订单信息.
     * @param payOrderReq 支付请求.
     * @param recipeOrder 缴费订单对象.
     * @param channelId 支付方式.
     * @return order 订单信息.
     * @throws Exception
     */
    public Order updateRegistrationAndOrder(Order order, PayOrderReq payOrderReq, RecipeOrderDocument recipeOrder,
            String channelId) throws Exception {
        ResModel<PayOrder> payOrderResModel;
        try {
            payOrderResModel = webServicesClient.payOrder(payOrderReq);
        } catch (InvocationTargetException ite) {
            if (ite.getCause() != null && ite.getCause() instanceof RemoteAccessException) {
                debug("缴费网络连接异常:{}", ite);
                return null;
            }
            throw ite;
        }
        if (payOrderResModel == null) {
            debug("诊间缴费返回信息解析失败,不能确定缴费是否成功");
            RecipeOrderHisDocument payHis = new RecipeOrderHisDocument();
            payHis.setClientReturnMsg("诊间缴费返回信息解析失败,不能确定缴费是否成功");
            Map<String, RecipeOrderHisDocument> hisMap = recipeOrder.getRecipeOrderHisMap();
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            throw new HisReturnException("诊间缴费返回信息解析失败,不能确定缴费是否成功");
        }
        RecipeOrderHisDocument payHis = new RecipeOrderHisDocument();
        Map<String, RecipeOrderHisDocument> hisMap = recipeOrder.getRecipeOrderHisMap();
        payHis.setClientReturnMsg(payOrderResModel.getReturnMsg().concat("(")
                .concat(String.valueOf(payOrderResModel.getReturnCode())).concat(")"));
        if (payOrderResModel.getReturnCode() == ReturnCode.SUCCESS) {
            BeanUtils.copyProperties(payOrderResModel.getRes(), payHis);
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeOrder.getRecipePaidDetailList().add(recipeOrder.getRecipeNonPaidDetail());
            recipeOrder.setRecipeNonPaidDetail(new RecipePaidDetailDocument());
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            order.setOrderStatus("2");
            // 更新订单状态
            order.setPaymentStatus(ConfCenter.getInt("isj.pay.paystatus.payed"));
            order.setPayWay(String.valueOf(channelId));
            RecipeOrderDocument recipeInfo = toolkit.executeRepositoryFunction(RecipeOrderRepository.class, "findOne",
                    order.getFormId().split("_")[0]);
            BasicInfoDocument info = userInfoService.getFamilyMemberByUserIdAndMemberId(recipeInfo.getCreateUserId(),
                    recipeInfo.getPatientId());
            if (info != null) {
                recipeInfo.setPatientName(info.getName());
            }
            // 推送消息
            toolkit.executeFunction(SaveSingleDetailMessageFunction.class, recipeInfo,
                    SendPushMsgEnum.RECIPE_PAY_SUCCESS, recipeInfo);
        } else {
            hisMap.put(payOrderReq.getOrderId(), payHis);
            recipeOrder.setRecipeOrderHisMap(hisMap);
            recipeServiceImpl.saveRecipeOrderDocument(recipeOrder);
            throw new HisReturnException(payOrderResModel.getReturnMsg());
        }
        return order;
    }

}
