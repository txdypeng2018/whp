package com.proper.enterprise.isj.proxy.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.context.TipInfoContext;
import com.proper.enterprise.isj.context.TipInfoEntityContext;
import com.proper.enterprise.isj.context.TipInfoIdContext;
import com.proper.enterprise.isj.context.TipInfoIdsContext;
import com.proper.enterprise.isj.context.TipInfoTypeContext;
import com.proper.enterprise.isj.context.TipInfoTypeNameContext;
import com.proper.enterprise.isj.proxy.business.tipinfo.DelTipInfoBusiness;
import com.proper.enterprise.isj.proxy.business.tipinfo.FetchTipInfoByIdBusiness;
import com.proper.enterprise.isj.proxy.business.tipinfo.FetchTipsInfoBusiness;
import com.proper.enterprise.isj.proxy.business.tipinfo.SaveTipInfoBusiness;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.entity.PromptTipsEntity;
import com.proper.enterprise.isj.proxy.service.PromptTipsService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * 温馨提示Service接口实现类.
 */
@Service
public class PromptTipsServiceImpl extends AbstractService implements PromptTipsService {

    @Override
    public void saveTipInfo(BaseInfoEntity tipInfo) throws Exception {
        toolkit.execute(SaveTipInfoBusiness.class, (ctx)->{
            ((TipInfoEntityContext<?>) ctx).setTipInfoEntity(tipInfo);
        });
    }

    @Override
    public void deleteTipInfo(List<String> idList) throws Exception {
        toolkit.execute(DelTipInfoBusiness.class, (ctx)->{
            ((TipInfoIdsContext<?>) ctx).setTipInfoIds(idList);
        });
    }

    @Override
    public BaseInfoEntity getTipInfoById(String id) throws Exception {
        return toolkit.execute(FetchTipInfoByIdBusiness.class, (ctx)->{
            ((TipInfoIdContext<?>) ctx).setTipInfoId(id);
        });
    }

    /**
     * 获取温馨提示信息.
     *
     * @param infoType
     *            温馨提示类型编码.
     * @param typeName
     *            温馨提示类型名称.
     * @param info
     *            温馨提示内容.
     * @param pageNo
     *            当前页码.
     * @param pageSize
     *            每页数量.
     * @return 温馨提示信息.
     * @throws Exception 异常.
     */
    @Override
    public PromptTipsEntity getTipsInfo(String infoType, String typeName, String info, String pageNo, String pageSize)
            throws Exception {
        return toolkit.execute(FetchTipsInfoBusiness.class, (ctx)->{
            ((TipInfoTypeContext<?>) ctx).setTipInfoType(infoType);
            ((TipInfoTypeNameContext<?>) ctx).setTipInfoTypeName(typeName);
            ((TipInfoContext<?>) ctx).setTipInfo(info);
            ((PageNoContext<?>) ctx).setPageNo(Integer.parseInt(pageNo));
            ((PageSizeContext<?>) ctx).setPageSize(Integer.parseInt(pageSize));
        });
    }

}
