package com.proper.enterprise.isj.proxy.business.tipinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.service.PromptTipsService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class PromptTipsGetTipInfoBusiness<M extends IdContext<BaseInfoEntity> & ModifiedResultBusinessContext<BaseInfoEntity>>
        implements IBusiness<BaseInfoEntity, M> {
    @Autowired
    PromptTipsService tipService;

    @Override
    public void process(M ctx) throws Exception {
        String id = ctx.getId();
        BaseInfoEntity tipInfo = new BaseInfoEntity();
        if (StringUtil.isNotEmpty(id)) {
            tipInfo = tipService.getTipInfoById(id);
        }
        ctx.setResult(tipInfo);
    }
}
