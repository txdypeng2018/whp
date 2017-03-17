package com.proper.enterprise.isj.proxy.business.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.CatalogueContext;
import com.proper.enterprise.isj.context.NameContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.RuleContext;
import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.proxy.service.RegistrationRulesService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class RegistrationRulesGetRulesInfoBusiness<T, M extends CatalogueContext<RegistrationRulesEntity> & NameContext<RegistrationRulesEntity> & RuleContext<RegistrationRulesEntity> & PageNoContext<RegistrationRulesEntity> & PageSizeContext<RegistrationRulesEntity> & ModifiedResultBusinessContext<RegistrationRulesEntity>>
        implements IBusiness<RegistrationRulesEntity, M> {
    @Autowired
    RegistrationRulesService rulesService;

    @Override
    public void process(M ctx) throws Exception {
        String catalogue = ctx.getCatalogue();
        String name = ctx.getName();
        String rule = ctx.getRule();
        String pageNo = String.valueOf(ctx.getPageNo());
        String pageSize = String.valueOf(ctx.getPageSize());
        // 取得挂号规则列表
        RegistrationRulesEntity rulesInfo = rulesService.getRulesInfo(catalogue, name, rule, pageNo, pageSize);
        ctx.setResult(rulesInfo);
    }
}
