package com.proper.enterprise.isj.proxy.business.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RuleInfoIdContext;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchRuleInfoByIdBusiness<M extends RuleInfoIdContext<RuleEntity> & ModifiedResultBusinessContext<RuleEntity>>
        implements IBusiness<RuleEntity, M> {

    @Autowired
    RuleRepository ruleRepo;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(ruleRepo.findById(ctx.getRuleInfoId()));
    }

}