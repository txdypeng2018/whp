package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildingIdContext;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchWebBuildByIdBusiness<M extends BuildingIdContext<NavigationBuildDetailEntity> & ModifiedResultBusinessContext<NavigationBuildDetailEntity>>
        implements IBusiness<NavigationBuildDetailEntity, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Override
    public void process(M ctx) throws Exception {
        String buildingId = ctx.getBuildingId();
        NavigationBuildDetailEntity retObj = new NavigationBuildDetailEntity();
        NavInfoEntity obj = navRepo.findByNavId(buildingId);
        if (obj != null) {
            retObj.setDistrictCode(obj.getParentId());
            retObj.setBuildingCode(obj.getNavId());
            retObj.setBuildingName(obj.getNavName());
        }
        ctx.setResult(retObj);
    }

}