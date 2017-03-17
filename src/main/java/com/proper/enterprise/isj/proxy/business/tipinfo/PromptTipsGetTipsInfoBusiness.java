package com.proper.enterprise.isj.proxy.business.tipinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.InfoContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.TypeNameContext;
import com.proper.enterprise.isj.proxy.controller.InfoTypeContext;
import com.proper.enterprise.isj.proxy.entity.PromptTipsEntity;
import com.proper.enterprise.isj.proxy.service.PromptTipsService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class PromptTipsGetTipsInfoBusiness<T, M extends InfoTypeContext<PromptTipsEntity> & TypeNameContext<PromptTipsEntity> & InfoContext<PromptTipsEntity> & PageNoContext<PromptTipsEntity> & PageSizeContext<PromptTipsEntity> & ModifiedResultBusinessContext<PromptTipsEntity>>
        implements IBusiness<PromptTipsEntity, M> {
    @Autowired
    PromptTipsService tipService;

    @Override
    public void process(M ctx) throws Exception {
        String infoType = ctx.getInfoType();
        String typeName = ctx.getTypeName();
        String info = ctx.getInfo();
        String pageNo = String.valueOf(ctx.getPageNo());
        String pageSize = String.valueOf(ctx.getPageSize());

        // 取得温馨提示列表
        PromptTipsEntity tipsInfo = tipService.getTipsInfo(infoType, typeName, info, pageNo, pageSize);
        ctx.setResult(tipsInfo);
    }
}
