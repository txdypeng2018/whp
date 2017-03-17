package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.NavBuildDetailEntityContext;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class UpdateWebBuildInfoBusiness<M extends NavBuildDetailEntityContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Override
    public void process(M ctx) throws Exception {
        NavigationBuildDetailEntity buildInfo = ctx.getNavBuildDetail();
        NavInfoEntity navObj = navRepo.findByNavId(buildInfo.getBuildingCode());
        if(navObj != null) {
            navObj.setNavName(buildInfo.getBuildingName());
            navObj.setParentId(buildInfo.getDistrictCode());
            navRepo.save(navObj);
        }
    }

}