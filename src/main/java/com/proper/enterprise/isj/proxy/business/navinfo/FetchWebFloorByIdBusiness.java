package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FloorIdContext;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchWebFloorByIdBusiness<M extends FloorIdContext<NavigationFloorDetailEntity> & ModifiedResultBusinessContext<NavigationFloorDetailEntity>>
        implements IBusiness<NavigationFloorDetailEntity, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Override
    public void process(M ctx) throws Exception {
        String floorId = ctx.getFloorId();
        NavigationFloorDetailEntity retObj = new NavigationFloorDetailEntity();
        NavInfoEntity obj = navRepo.findByNavId(floorId);
        if (obj != null) {
            // 楼层所在楼宇ID
            retObj.setFloorParentId(obj.getParentId());
            // 楼层记录ID
            retObj.setFid(obj.getId());
            // 楼层ID
            retObj.setFloorId(obj.getNavId());
            // 楼层名称
            retObj.setFloorName(obj.getNavName());
            NavInfoEntity deptObj = navRepo.findByParentId(floorId).get(0);
            if (deptObj != null) {
                // 科室记录ID
                retObj.setDid(deptObj.getId());
                // 楼层科室信息ID
                retObj.setDeptId(deptObj.getNavId());
                // 楼层科室信息
                retObj.setDeptName(deptObj.getNavName());
            }
        }
        ctx.setResult(retObj);
    }

}