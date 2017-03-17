package com.proper.enterprise.isj.proxy.business.recipe;
//BasicInfoDocument basic, String payStatus, String sDate,

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.EndDateContext;
import com.proper.enterprise.isj.context.PayStatusContext;
import com.proper.enterprise.isj.context.StartDateContext;
import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.recipe.FindPayListModelFunction;
import com.proper.enterprise.isj.proxy.document.RecipeDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeDetailDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeDetailItemDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.PayList;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.paylist.Pay;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FindRecipeDocumentByUserAndDateBusiness<M extends
BasicInfoDocumentContext<List<RecipeDocument>>
&StartDateContext<List<RecipeDocument>>
&EndDateContext<List<RecipeDocument>>
&PayStatusContext<List<RecipeDocument>>
&ModifiedResultBusinessContext<List<RecipeDocument>>
> implements IBusiness<List<RecipeDocument>, M>, ILoggable{
    
    @Autowired
    @Qualifier("defaultBusinessToolkit")
    BusinessToolkit toolkitx;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(findRecipeDocumentByUserAndDate(ctx.getBasicInfoDocument(), ctx.getPayStatus(), ctx.getStartDate(), ctx.getEndDate()));
    }
    
    /**
     * 获得人员的缴费信息(Controller调用).
     *
     * @param basic 基础信息.
     * @param payStatus (0: 未支付, 1: 已支付, 2:已退款, 其他: 全部)支付状态.
     * @param sDate 开始时间.
     * @param eDate 结束时间.
     * @return 缴费报文.
     * @throws Exception 异常.
     */
    public List<RecipeDocument> findRecipeDocumentByUserAndDate(BasicInfoDocument basic, String payStatus, String sDate,
            String eDate) throws Exception {
        DecimalFormat df = new DecimalFormat("0.00");
        List<RecipeDocument> recipeList = new ArrayList<>();
        try {
            // 查询用户全部缴费信息
            ResModel<PayList> payListRes = toolkitx.executeFunction(FindPayListModelFunction.class, basic, null, payStatus, sDate, eDate, true);
            ResModel<PayList> refundPayListRes = null;
            // 查询退款的缴费信息
            if (StringUtil.isNull(payStatus) || payStatus.equals("1")) {
                refundPayListRes = toolkitx.executeFunction(FindPayListModelFunction.class, basic, null, "2", sDate, eDate, true);
            }
            Map<String, RecipeDetailDocument> detailMap = new LinkedHashMap<>();
            Map<String, RecipeDocument> recipeMap = new LinkedHashMap<>();
            RecipeDetailDocument detail;
            List<RecipeDetailItemDocument> itemList;
            RecipeDetailItemDocument item;
            BigDecimal totalBig;
            // 取得查询结果
            if (payListRes.getReturnCode() == ReturnCode.SUCCESS) {
                List<Pay> payList = new ArrayList<>();
                payList.addAll(payListRes.getRes().getPayList());
                // 获取退款列表
                if (refundPayListRes != null) {
                    List<Pay> refundList = refundPayListRes.getRes().getPayList();
                    for (Pay refundPay : refundList) {
                        Pay rPay = new Pay();
                        BeanUtils.copyProperties(refundPay, rPay);
                        rPay.setOwnCost(refundPay.getOwnCost().replace("-", ""));
                        rPay.setUnitPrice(Math.abs(refundPay.getUnitPrice()));
                        rPay.setQty(refundPay.getQty().replace("-", ""));
                        payList.add(rPay);
                    }
                    payList.addAll(refundList);
                }
                // 定义返回给App的对象
                RecipeDocument recipe;
                // 详细列表
                List<RecipeDetailDocument> detailList;
                String recipeKey;
                for (Pay pay : payList) {
                    // 以门诊流水号为对象
                    recipe = recipeMap.get(pay.getClinicCode());
                    if (recipe == null) {
                        recipe = new RecipeDocument();
                        // 门诊流水号作为键值
                        recipeMap.put(pay.getClinicCode(), recipe);
                    }
                    recipe.setOutpatientNum(pay.getClinicCode());
                    recipe.setOutpatientDate(
                            DateUtil.toString(DateUtil.toDate(pay.getRegDate().split(" ")[0]), "yyyy年MM月dd日"));
                    detailList = recipe.getRecipes();
                    // 以单条项目总价是否为负数作为区分,以处方号以及执行科室名称作为键值
                    if (pay.getOwnCost().contains("-")) {
                        recipeKey = pay.getRecipeNo().concat("_").concat(pay.getExecDpnm()).concat("-");
                    } else {
                        recipeKey = pay.getRecipeNo().concat("_").concat(pay.getExecDpnm());
                    }
                    detail = detailMap.get(recipeKey);
                    // 设置处方详细信息
                    if (detail == null) {
                        detail = new RecipeDetailDocument();
                        detail.setDept(pay.getExecDpnm());
                        detail.setRecipeNum(pay.getRecipeNo());
                        detail.setLocation("");
                        if (pay.getOwnCost().contains("-")) {
                            detail.setStatusCode("2");

                        } else {
                            detail.setStatusCode(pay.getPayFlag());
                        }
                        detail.setStatus(CenterFunctionUtils.getRecipeItemStatusName(detail.getStatusCode()));
                        detailMap.put(recipeKey, detail);
                        detailList.add(detail);
                    }
                    itemList = detail.getItems();
                    item = new RecipeDetailItemDocument();
                    item.setName(pay.getItemName());
                    String num = new BigDecimal(pay.getQty())
                            .divide(new BigDecimal(pay.getPackQty()), BigDecimal.ROUND_HALF_UP).toString();
                    item.setNumber(num);
                    item.setAmount(df.format(new BigDecimal(String.valueOf(pay.getUnitPrice()))
                            .divide(new BigDecimal("100"), 2, RoundingMode.UNNECESSARY)));
                    itemList.add(item);
                    if (StringUtil.isEmpty(detail.getTotal())) {
                        totalBig = new BigDecimal("0");
                    } else {
                        totalBig = new BigDecimal(detail.getTotal());
                    }
                    totalBig = totalBig.add(new BigDecimal(pay.getOwnCost()).divide(new BigDecimal("100"), 2,
                            RoundingMode.UNNECESSARY));
                    detail.setTotal(totalBig.toString());
                }
                // 拼接返回对象
                for (RecipeDocument recipeDocument : recipeMap.values()) {
                    recipeList.add(recipeDocument);
                }
            } else {
                // HIS返回错误信息
                if (payListRes.getReturnCode() != ReturnCode.EMPTY_RETURN) {
                    throw new HisReturnException(payListRes.getReturnMsg());
                }
            }
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS返回结果异常", e);
            throw new HisLinkException(CenterFunctionUtils.HIS_DATALINK_ERR);
        }
        return recipeList;
    }

}
