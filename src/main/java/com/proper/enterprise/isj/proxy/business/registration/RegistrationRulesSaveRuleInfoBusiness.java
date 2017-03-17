package com.proper.enterprise.isj.proxy.business.registration;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RuleEntityContext;
import com.proper.enterprise.isj.proxy.service.RegistrationRulesService;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class RegistrationRulesSaveRuleInfoBusiness<M extends RuleEntityContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {
    @Autowired
    RegistrationRulesService rulesService;

    @Override
    public void process(M ctx) throws Exception {
        RuleEntity ruleInfo = ctx.getRuleEntity();
        try {
            rulesService.saveRuleInfo(ruleInfo);
            ctx.setResult("");
        } catch (Exception e) {
            debug("RegistrationRulesController.saveRuleInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
    }
}
