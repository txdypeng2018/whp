package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DeptNameContext;
import com.proper.enterprise.isj.context.FloorParentIdContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.function.paging.BuildPageRequestNavIdAscFunction;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchWebFloorInfoBusiness<M extends FloorParentIdContext<NavigationFloorEntity>
& DeptNameContext<NavigationFloorEntity> & PageNoContext<NavigationFloorEntity> & PageSizeContext<NavigationFloorEntity>
&ModifiedResultBusinessContext<NavigationFloorEntity>
>
        implements IBusiness<NavigationFloorEntity, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Autowired
    BuildPageRequestNavIdAscFunction pageFunc;

    @Override
    public void process(M ctx) {
        
        String deptName = ctx.getDeptName();
        String floorParentId = ctx.getFloorParentId();

        NavigationFloorEntity retObj = new NavigationFloorEntity();
        List<NavigationFloorDetailEntity> dataList = new ArrayList<>();
        PageRequest pageReq = FunctionUtils.invoke(pageFunc, ctx.getPageNo(), ctx.getPageSize());
        Page<NavInfoEntity> pageInfo;
        String parentType;
        String buildType = ConfCenter.get("isj.info.build");
        String floorType = ConfCenter.get("isj.info.floor");
        boolean flg = false;
        if (StringUtil.isEmpty(deptName)) {
            deptName = "%%";
            parentType = buildType;
        } else {
            deptName = "%" + deptName + "%";
            flg = true;
            parentType = floorType;
        }
        floorParentId = "%" + floorParentId + "%";
        int count = navRepo.findByParentIdLikeAndParentTypeAndNavNameLike(floorParentId, parentType, deptName).size();
        pageInfo = navRepo.findByParentIdLikeAndParentTypeAndNavNameLike(floorParentId, parentType, deptName, pageReq);

        List<NavInfoEntity> navList = pageInfo.getContent();
        List<NavInfoEntity> deptList = navRepo.findByParentType(floorType);
        List<NavInfoEntity> floorList = navRepo.findByParentType(buildType);

        for (NavInfoEntity detail : navList) {
            if (!flg) {
                for (NavInfoEntity deptDetail : deptList) {
                    if (detail.getNavId().equals(deptDetail.getParentId())) {
                        NavigationFloorDetailEntity obj = new NavigationFloorDetailEntity();
                        // 楼层所在楼宇ID
                        obj.setFloorParentId(floorParentId);
                        // 楼层记录ID
                        obj.setFid(detail.getId());
                        // 楼层ID
                        obj.setFloorId(detail.getNavId());
                        // 楼层名称
                        obj.setFloorName(detail.getNavName());
                        // 科室记录ID
                        obj.setDid(deptDetail.getId());
                        // 楼层科室信息ID
                        obj.setDeptId(deptDetail.getNavId());
                        // 楼层科室信息
                        obj.setDeptName(deptDetail.getNavName());
                        dataList.add(obj);
                    }
                }
            } else {
                for (NavInfoEntity floorDetail : floorList) {
                    if (detail.getParentId().equals(floorDetail.getNavId())) {
                        NavigationFloorDetailEntity obj = new NavigationFloorDetailEntity();
                        // 楼层所在楼宇ID
                        obj.setFloorParentId(floorParentId);
                        // 楼层记录ID
                        obj.setFid(floorDetail.getId());
                        // 楼层ID
                        obj.setFloorId(floorDetail.getNavId());
                        // 楼层名称
                        obj.setFloorName(floorDetail.getNavName());
                        // 科室记录ID
                        obj.setDid(detail.getId());
                        // 楼层科室信息ID
                        obj.setDeptId(detail.getNavId());
                        // 楼层科室信息
                        obj.setDeptName(detail.getNavName());
                        dataList.add(obj);
                    }

                }
            }

        }

        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(dataList);

        ctx.setResult(retObj);

    }

}