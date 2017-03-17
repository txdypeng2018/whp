package com.proper.enterprise.isj.proxy.business.rule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.RuleCatalogueContext;
import com.proper.enterprise.isj.context.RuleContext;
import com.proper.enterprise.isj.context.RuleNameContext;
import com.proper.enterprise.isj.function.paging.BuildPageRequestFunction;
import com.proper.enterprise.isj.function.paging.BuildPageRequestFunction.SortBuilder;
import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchRulesInfoBusiness<M extends RuleCatalogueContext<RegistrationRulesEntity>
    & RuleNameContext<RegistrationRulesEntity> & RuleContext<RegistrationRulesEntity>
    & PageNoContext<RegistrationRulesEntity> & PageSizeContext<RegistrationRulesEntity>
    & ModifiedResultBusinessContext<RegistrationRulesEntity>
>
        implements IBusiness<RegistrationRulesEntity, M> {

    @Autowired
    RuleRepository ruleRepo;
    
    @Autowired
    UserService userService;

    @Autowired
    BuildPageRequestFunction buildPageRequestFunction;

    @Override
    public void process(M ctx) throws Exception {

        String catalogue = ctx.getCatalogue();
        String name = ctx.getRuleName();
        String rule = ctx.getRule();

        RegistrationRulesEntity retObj = new RegistrationRulesEntity();

        PageRequest pageReq = FunctionUtils.invoke(buildPageRequestFunction, ctx.getPageNo(), ctx.getPageSize(),
                (SortBuilder) () -> new Sort(Sort.Direction.ASC, "catalogue"));

        if (StringUtil.isEmpty(catalogue)) {
            catalogue = "%%";
        } else {
            catalogue = "%" + catalogue + "%";
        }

        if (StringUtil.isEmpty(name)) {
            name = "%%";
        } else {
            name = "%" + name + "%";
        }

        if (StringUtil.isEmpty(rule)) {
            rule = "%%";
        } else {
            rule = "%" + rule + "%";
        }

        int count = ruleRepo.findByCatalogueLikeAndNameLikeAndRuleLike(catalogue, name, rule).size();

        Page<RuleEntity> pageInfo = ruleRepo.findByCatalogueLikeAndNameLikeAndRuleLike(catalogue, name, rule, pageReq);

        List<RuleEntity> rulesList = pageInfo.getContent();


        String userId = "";
        for (RuleEntity entity : rulesList) {
            userId = entity.getLastModifyUserId();
            if (StringUtil.isNotEmpty(userId)) {
                User curUser = userService.get(userId);
                if (curUser != null) {
                    entity.setLastModifyUserName(curUser.getUsername());
                }
            }
        }
        
        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(rulesList);

        ctx.setResult(retObj);

    }
    
}