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
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class RegistrationRulesUpdateRuleInfoBusiness<M extends RuleEntityContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {

    @Autowired
    RegistrationRulesService rulesService;

    @Override
    public void process(M ctx) throws Exception {
        RuleEntity ruleInfo = ctx.getRuleEntity();
        if (StringUtil.isNotNull(ruleInfo.getId())) {
            try {
                rulesService.saveRuleInfo(ruleInfo);
            } catch (Exception e) {
                debug("RegistrationRulesController.updateRuleInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR, e);
            }
        }
        ctx.setResult("");
    }
}
