package com.proper.enterprise.isj.proxy.business.tipinfo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.TipInfoContext;
import com.proper.enterprise.isj.context.TipInfoTypeContext;
import com.proper.enterprise.isj.context.TipInfoTypeNameContext;
import com.proper.enterprise.isj.function.paging.BuildPageRequestCreateTmDescTypeNameDescFunction;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.entity.PromptTipsEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchTipsInfoBusiness<M extends TipInfoTypeContext<PromptTipsEntity> & TipInfoTypeNameContext<PromptTipsEntity> & TipInfoContext<PromptTipsEntity> & PageNoContext<PromptTipsEntity> & PageSizeContext<PromptTipsEntity> & ModifiedResultBusinessContext<PromptTipsEntity>>
        implements IBusiness<PromptTipsEntity, M> {

    @Autowired
    BaseInfoRepository baseInfoRepo;

    @Autowired
    BuildPageRequestCreateTmDescTypeNameDescFunction buildPageRequestCreateTmDescTypeNameDescFunction;

    @Override
    public void process(M ctx) throws Exception {

        String infoType = ctx.getTipInfoType();
        String typeName = ctx.getTipInfoTypeName();
        String info = ctx.getTipInfo();

        PromptTipsEntity retObj = new PromptTipsEntity();

        PageRequest pageReq = FunctionUtils.invoke(buildPageRequestCreateTmDescTypeNameDescFunction, ctx.getPageNo(),
                ctx.getPageSize());

        if (StringUtil.isEmpty(infoType)) {
            infoType = "%%";
        } else {
            infoType = "%" + infoType + "%";
        }

        if (StringUtil.isEmpty(typeName)) {
            typeName = "%%";
        } else {
            typeName = "%" + typeName + "%";
        }

        if (StringUtil.isEmpty(info)) {
            info = "%%";
        } else {
            info = "%" + info + "%";
        }

        int count = baseInfoRepo.findByInfoTypeLikeAndTypeNameLikeAndInfoLike(infoType, typeName, info).size();
        Page<BaseInfoEntity> pageInfo = baseInfoRepo.findByInfoTypeLikeAndTypeNameLikeAndInfoLike(infoType, typeName,
                info, pageReq);

        List<BaseInfoEntity> tipsList = pageInfo.getContent();

        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(tipsList);

        ctx.setResult(retObj);

    }

}