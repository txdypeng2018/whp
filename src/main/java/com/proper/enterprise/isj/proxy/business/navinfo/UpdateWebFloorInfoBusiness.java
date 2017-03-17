package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.NavFloorDetailEntityContext;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class UpdateWebFloorInfoBusiness<M extends NavFloorDetailEntityContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Override
    public void process(M ctx) throws Exception {
        NavigationFloorDetailEntity floorInfo = ctx.getNavFloorDetail();
        List<NavInfoEntity> updateList = new ArrayList<>();
        NavInfoEntity navFloorObj = navRepo.findByNavId(floorInfo.getFloorId());
        NavInfoEntity navDeptObj = navRepo.findByNavId(floorInfo.getDeptId());
        if(navFloorObj != null && navDeptObj != null) {
            navFloorObj.setNavName(floorInfo.getFloorName());
            navDeptObj.setNavName(floorInfo.getDeptName());
            updateList.add(navFloorObj);
            updateList.add(navDeptObj);
            navRepo.save(updateList);
        }
    
    }

}