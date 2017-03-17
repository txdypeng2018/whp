package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DeptNameContext;
import com.proper.enterprise.isj.context.FloorParentIdContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HospitalNavGetFloorInfoBusiness<T, M extends ModifiedResultBusinessContext<NavigationFloorEntity> & FloorParentIdContext<NavigationFloorEntity> & DeptNameContext<NavigationFloorEntity> & PageNoContext<NavigationFloorEntity> & PageSizeContext<NavigationFloorEntity>>
        implements IBusiness<NavigationFloorEntity, M> {
    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        String floorParentId = ctx.getFloorParentId();
        NavigationFloorEntity floorInfo = new NavigationFloorEntity();
        if (StringUtil.isNotEmpty(floorParentId)) {
            // 取得Web端楼层科室列表
            floorInfo = navService.getWebFloorInfo(floorParentId, ctx.getDeptName(), String.valueOf(ctx.getPageNo()),
                    String.valueOf(ctx.getPageSize().toString()));
        }
        ctx.setResult(floorInfo);
    }
}
