package com.proper.enterprise.isj.proxy.business.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RuleInfoIdsContext;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class DelRuleInfoByIdsBusiness<M extends RuleInfoIdsContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    RuleRepository ruleRepo;

    @Override
    public void process(M ctx) throws Exception {
        ruleRepo.delete(ruleRepo.findAll(ctx.getRuleInfoIds()));
    }

}