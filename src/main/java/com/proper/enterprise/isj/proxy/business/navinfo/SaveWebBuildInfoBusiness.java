package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildInfoEntityContext;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * com.proper.enterprise.isj.proxy.service.impl.HospitalNavigationServiceImpl.saveWebBuildInfo(NavigationBuildDetailEntity)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveWebBuildInfoBusiness<M extends BuildInfoEntityContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Override
    public void process(M ctx) throws Exception {
        NavigationBuildDetailEntity buildInfo = ctx.getBuildInfo();
        NavInfoEntity navObj = new NavInfoEntity();
        String disId = buildInfo.getDistrictCode();

        NavInfoEntity topObj = navRepo.findTopByParentIdOrderByNavIdDesc(disId);
        // 院区有楼宇信息
        if (topObj != null) {
            String navTopId = topObj.getNavId();
            int navId = Integer.parseInt(topObj.getNavId().substring(navTopId.length() - 2, navTopId.length())) + 1;
            navObj.setNavId(disId + String.format("%02d", navId));
            // 院区没有楼宇信息
        } else {
            navObj.setNavId(disId + "01");
        }
        navObj.setNavName(buildInfo.getBuildingName());
        navObj.setParentId(disId);
        navObj.setParentType(ConfCenter.get("isj.info.district"));
        navRepo.save(navObj);
    }

}