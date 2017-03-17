package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FloorInfoEntityContext;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.utils.ConfCenter;

/**
 * 新增Web端楼层科室信息.
 *
 * old:com.proper.enterprise.isj.proxy.service.impl.HospitalNavigationServiceImpl.saveWebFloorInfo(NavigationFloorDetailEntity)
 * @param floorInfo
 *        楼层科室信息.
 * @throws Exception 异常.
 */
@Service
public class SaveWebFloorInfoBusiness<M extends FloorInfoEntityContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Override
    public void process(M ctx) throws Exception {
        
        NavigationFloorDetailEntity floorInfo = ctx.getFloorInfo();
        
        List<NavInfoEntity> saveObjList = new ArrayList<>();
        NavInfoEntity navFloorObj = new NavInfoEntity();
        NavInfoEntity navDeptObj = new NavInfoEntity();
        String buildId = floorInfo.getFloorParentId();

        NavInfoEntity topObj = navRepo.findTopByParentIdOrderByNavIdDesc(buildId);
        if(topObj != null) {
            // 楼层信息
            String navFloorTopId = topObj.getNavId();
            StringBuilder oriFloorNavId = new StringBuilder();
            StringBuilder oriDeptNavId = new StringBuilder();
            oriFloorNavId.append(buildId);
            oriDeptNavId.append(buildId);
            int navId = Integer.parseInt(navFloorTopId.substring(navFloorTopId.length() - 2, navFloorTopId.length())) + 1;
            String strNavFloorId = String.format("%02d", navId);
            // 楼层id
            oriFloorNavId.append(strNavFloorId);
            navFloorObj.setNavId(oriFloorNavId.toString());
            // 科室id
            oriDeptNavId.append("01");
            oriDeptNavId.append(strNavFloorId);
            navDeptObj.setNavId(oriDeptNavId.toString());

            // 院区没有楼宇信息
        } else {
            navFloorObj.setNavId(buildId + "01");
            navDeptObj.setNavId(buildId + "0101");
        }
        // 楼层信息
        navFloorObj.setNavName(floorInfo.getFloorName());
        navFloorObj.setParentId(buildId);
        navFloorObj.setParentType(ConfCenter.get("isj.info.build"));
        // 科室信息
        navDeptObj.setNavName(floorInfo.getDeptName());
        navDeptObj.setParentId(navFloorObj.getNavId());
        navDeptObj.setParentType(ConfCenter.get("isj.info.floor"));

        saveObjList.add(navFloorObj);
        saveObjList.add(navDeptObj);
        navRepo.save(saveObjList);
    }

}