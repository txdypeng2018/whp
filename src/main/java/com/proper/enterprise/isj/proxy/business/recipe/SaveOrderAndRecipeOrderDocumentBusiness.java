package com.proper.enterprise.isj.proxy.business.recipe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ClinicCodeContext;
import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RecipeException;
import com.proper.enterprise.isj.function.payment.FetchPayListReqFunction;
import com.proper.enterprise.isj.function.recipe.CreateRecipeOrderFunction;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeDetailAllDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipePaidDetailDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.QueryType;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.req.PayListReq;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;

@Service
public class SaveOrderAndRecipeOrderDocumentBusiness<M extends MemberIdContext<RecipeOrderDocument>
& ClinicCodeContext<RecipeOrderDocument> & ModifiedResultBusinessContext<RecipeOrderDocument>>
        implements IBusiness<RecipeOrderDocument, M>, ILoggable {

    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    OrderService orderService;

    @Autowired
    FetchPayListReqFunction fetchPayListReqFunction;

    @Autowired
    CreateRecipeOrderFunction createRecipeOrderFunction;

    @Override
    public void process(M ctx) throws Exception {
        String memberId = ctx.getMemberId();
        String clinicCode = ctx.getClinicCode();
        RecipeOrderDocument recipeOrder;
        User user = userService.getCurrentUser();
        UserInfoDocument userInfo = userInfoService.getUserInfoByUserId(user.getId());
        BasicInfoDocument info = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        PayListReq listReq = FunctionUtils.invoke(fetchPayListReqFunction, info, clinicCode, null, null, null);
        listReq.setQueryType(QueryType.TO_PAY);
        ResModel<PayList> payListRes = webServicesClient.getPayDetailAll(listReq);
        if (payListRes.getReturnCode() != ReturnCode.SUCCESS) {
            LOGGER.debug("校验缴费中的未缴费项目返回错误,HIS返回的消息:{},门诊流水号:{}", payListRes.getReturnMsg(), clinicCode);
            throw new HisReturnException(payListRes.getReturnMsg());
        }
        List<Pay> payList = payListRes.getRes().getPayList();
        if (payList == null || payList.size() == 0) {
            LOGGER.debug("未找到需要:{},门诊流水号:{}", payListRes.getReturnMsg(), clinicCode);
            throw new RecipeException(CenterFunctionUtils.ORDER_NON_DATA_ERR);
        }

        List<RecipeDetailAllDocument> detailList = new ArrayList<>();
        RecipeDetailAllDocument dt;
        BigDecimal bigDecimal = new BigDecimal("0");
        String hospSeq = "";
        for (Pay pay : payList) {
            if (clinicCode.equals(pay.getClinicCode())) {
                dt = new RecipeDetailAllDocument();
                BeanUtils.copyProperties(pay, dt);
                hospSeq = pay.getHospSequence();
                detailList.add(dt);
                bigDecimal = bigDecimal.add(new BigDecimal(dt.getOwnCost()));
            }
        }
        String orderNum = CenterFunctionUtils.createRegOrOrderNo(2);
        RecipePaidDetailDocument paidDetal = new RecipePaidDetailDocument();
        paidDetal.setOrderNum(orderNum);
        paidDetal.setAmount(bigDecimal.toString());
        paidDetal.setHospSequence(hospSeq);
        paidDetal.setDetailList(detailList);
        recipeOrder = FunctionUtils.invoke(createRecipeOrderFunction, userInfo, info, clinicCode, paidDetal);
        orderService.saveCreateOrder(recipeOrder);
        ctx.setResult(recipeOrder);
    }

}