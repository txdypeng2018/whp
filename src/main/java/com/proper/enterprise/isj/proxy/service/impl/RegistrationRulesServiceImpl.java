package com.proper.enterprise.isj.proxy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.RuleCatalogueContext;
import com.proper.enterprise.isj.context.RuleContext;
import com.proper.enterprise.isj.context.RuleEntityContext;
import com.proper.enterprise.isj.context.RuleInfoIdContext;
import com.proper.enterprise.isj.context.RuleInfoIdsContext;
import com.proper.enterprise.isj.context.RuleNameContext;
import com.proper.enterprise.isj.proxy.business.rule.DelRuleInfoByIdsBusiness;
import com.proper.enterprise.isj.proxy.business.rule.FetchRuleInfoByIdBusiness;
import com.proper.enterprise.isj.proxy.business.rule.FetchRulesInfoBusiness;
import com.proper.enterprise.isj.proxy.business.rule.SaveRuleInfoBusiness;
import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.proxy.service.RegistrationRulesService;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.platform.api.auth.service.UserService;

/**
 * 挂号规则ServiceImpl.
 */
@Service
public class RegistrationRulesServiceImpl extends AbstractService implements RegistrationRulesService {

    @Autowired
    UserService userService;

    @Override
    public void saveRuleInfo(RuleEntity ruleInfo) throws Exception {
        toolkit.execute(SaveRuleInfoBusiness.class, (c)->{
            ((RuleEntityContext<?>)c).setRuleEntity(ruleInfo);
        });
    }

    @Override
    public void deleteRuleInfo(List<String> idList) throws Exception {
        toolkit.execute(DelRuleInfoByIdsBusiness.class, (c)->{
            ((RuleInfoIdsContext<?>)c).setRuleInfoIds(idList);
        });
    }

    @Override
    public RuleEntity getRuleInfoById(String id) throws Exception {
        return toolkit.execute(FetchRuleInfoByIdBusiness.class, (c)->{
            ((RuleInfoIdContext<?>)c).setRuleInfoId(id);
        });
    }

    /**
     * 获取挂号规则信息
     *
     * @param catalogue
     *        挂号规则分类
     * @param name
     *        挂号规则名称
     * @param rule
     *        挂号规则内容
     * @param pageNo
     *        当前页码
     * @param pageSize
     *        每页数量
     * @return retObj
     *         挂号规则信息
     * @throws Exception 异常.
     */
    @Override
    public RegistrationRulesEntity getRulesInfo(String catalogue, String name, String rule, String pageNo,
            String pageSize) throws Exception {
        return toolkit.execute(FetchRulesInfoBusiness.class, (c)->{
            ((RuleCatalogueContext<?>)c).setCatalogue(catalogue);
            ((RuleNameContext<?>)c).setRuleName(name);
            ((RuleContext<?>)c).setRule(rule);
            ((PageNoContext<?>)c).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<?>)c).setPageSize(Integer.parseInt(pageSize));
        });
    }

}
