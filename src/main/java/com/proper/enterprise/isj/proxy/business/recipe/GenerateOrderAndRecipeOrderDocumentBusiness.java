package com.proper.enterprise.isj.proxy.business.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.context.ClinicCodeContext;
import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.proxy.service.impl.RecipeServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.webservices.model.enmus.QueryType;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RecipeServiceNotxImpl.saveOrderAndRecipeOrderDocument(String, String)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class GenerateOrderAndRecipeOrderDocumentBusiness<M extends MemberIdContext<RecipeOrderDocument> & ClinicCodeContext<RecipeOrderDocument> & ModifiedResultBusinessContext<RecipeOrderDocument>>
        implements IBusiness<RecipeOrderDocument, M>, ILoggable {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    BusinessToolkit toolkit;

    @Autowired
    RecipeServiceImpl recipeServiceImpl;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(saveOrderAndRecipeOrderDocument(ctx.getMemberId(), ctx.getClinicCode()));
    }

    /**
     * 生成缴费订单.
     *
     * @param memberId 成员ID.
     * @param clinicCode 门诊流水号.
     * @return 缴费对象.
     * @throws Exception 异常.
     */
    public RecipeOrderDocument saveOrderAndRecipeOrderDocument(String memberId, String clinicCode) throws Exception {
        RecipeOrderDocument recipeOrder;
        try {
            recipeOrder = recipeServiceImpl.saveOrderAndRecipeOrderDocument(memberId, clinicCode);
            BasicInfoDocument basicInfo = userInfoService
                    .getFamilyMemberByUserIdAndMemberId(recipeOrder.getCreateUserId(), memberId);
            if (basicInfo != null) {
                for (QueryType queryType : QueryType.values()) {
                    webService4HisInterfaceCacheUtil.evictCachePayListRes(recipeOrder.getPatientId(), queryType.name(),
                            basicInfo.getMedicalNum());
                }
            }
        } catch (Exception e) {
            RecipeOrderDocument order = toolkit.executeRepositoryFunction(RecipeOrderRepository.class,
                    "getByClinicCode", clinicCode);
            if (order != null) {
                recipeOrderRepository.delete(order);
            }
            debug("保存缴费订单信息出现异常,门诊流水号:{}{}", clinicCode, e);
            throw e;
        }
        return recipeOrder;
    }

}
