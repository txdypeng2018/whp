package com.proper.enterprise.isj.proxy.business.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.proxy.service.RegistrationRulesService;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class RegistrationRulesGetRuleInfoBusiness<M extends IdContext<RuleEntity> & ModifiedResultBusinessContext<RuleEntity>>
        implements IBusiness<RuleEntity, M> {
    @Autowired
    RegistrationRulesService rulesService;

    @Override
    public void process(M ctx) throws Exception {
        String id = ctx.getId();
        if (StringUtil.isNotEmpty(id)) {
            ctx.setResult(rulesService.getRuleInfoById(id));
        } else {
            ctx.setResult(new RuleEntity());
        }
    }
}
