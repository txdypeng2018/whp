package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildingNameContext;
import com.proper.enterprise.isj.context.DistrictCodeContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.function.paging.BuildPageRequestNavIdAscFunction;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchWebBuildInfoBusiness<M extends DistrictCodeContext<NavigationBuildEntity> & BuildingNameContext<NavigationBuildEntity> & PageNoContext<NavigationBuildEntity> & PageSizeContext<NavigationBuildEntity> & ModifiedResultBusinessContext<NavigationBuildEntity>>
        implements IBusiness<NavigationBuildEntity, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Autowired
    BuildPageRequestNavIdAscFunction pageFunc;

    @Override
    public void process(M ctx) throws Exception {
        NavigationBuildEntity retObj = new NavigationBuildEntity();
        List<NavigationBuildDetailEntity> dataList = new ArrayList<>();
        String districtCode = ctx.getDistrictCode();
        String buildingName = ctx.getBuildingName();
        PageRequest pageReq = FunctionUtils.invoke(pageFunc, ctx.getPageNo(), ctx.getPageSize());

        if (StringUtil.isEmpty(districtCode)) {
            districtCode = "%%";
        } else {
            districtCode = "%" + districtCode + "%";
        }

        if (StringUtil.isEmpty(buildingName)) {
            buildingName = "%%";
        } else {
            buildingName = "%" + buildingName + "%";
        }
        String parentType = ConfCenter.get("isj.info.district");
        int count = navRepo.findByParentTypeAndParentIdLikeAndNavNameLike(parentType, districtCode, buildingName)
                .size();
        Page<NavInfoEntity> pageInfo = navRepo.findByParentTypeAndParentIdLikeAndNavNameLike(parentType, districtCode,
                buildingName, pageReq);
        List<NavInfoEntity> navList = pageInfo.getContent();

        for (NavInfoEntity detail : navList) {
            NavigationBuildDetailEntity obj = new NavigationBuildDetailEntity();
            // id
            obj.setId(detail.getId());
            // 父节点id
            obj.setDistrictCode(detail.getParentId());
            // 导航id
            obj.setBuildingCode(detail.getNavId());
            // 导航名称
            obj.setBuildingName(detail.getNavName());
            dataList.add(obj);
        }

        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(dataList);

        ctx.setResult(retObj);
    }

}