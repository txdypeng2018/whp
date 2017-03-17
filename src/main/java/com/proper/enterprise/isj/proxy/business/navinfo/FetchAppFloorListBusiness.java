package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildingIdContext;
import com.proper.enterprise.isj.function.customservice.FetchFamilyUserInfoListFunction;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchAppFloorListBusiness<M extends BuildingIdContext<Collection<Map<String, Object>>> & ModifiedResultBusinessContext<Collection<Map<String, Object>>>>
        implements IBusiness<Collection<Map<String, Object>>, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Autowired
    FetchFamilyUserInfoListFunction fetchDisListFunc;

    @Override
    public void process(M ctx) throws Exception {
        List<Map<String, Object>> retList = new ArrayList<>();
        String buildId = ctx.getBuildingId();
        String buildType = ConfCenter.get("isj.info.build");
        String floorType = ConfCenter.get("isj.info.floor");

        List<NavInfoEntity> floorList = new ArrayList<>();
        if (StringUtil.isNotNull(buildId)) {
            Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "navId"));
            floorList = navRepo.findAll(sort);
        }

        if (floorList.size() > 0) {
            for (NavInfoEntity detail : floorList) {
                Map<String, Object> districtObj = new LinkedHashMap<>();
                // 指定ID的楼宇
                if (detail.getParentType().equals(buildType) && detail.getParentId().equals(buildId)) {
                    districtObj.put("id", detail.getNavId());
                    districtObj.put("name", detail.getNavName());
                    List<String> detailList = new ArrayList<>();
                    for (NavInfoEntity roomDetail : floorList) {
                        // 获取科室信息
                        if (roomDetail.getParentType().equals(floorType)
                                && roomDetail.getParentId().equals(detail.getNavId())) {
                            if (StringUtil.isNotNull(roomDetail.getNavName())) {
                                String[] deptsArr = roomDetail.getNavName().split("\\^");
                                Collections.addAll(detailList, deptsArr);
                            }
                        }
                    }
                    if (!detailList.isEmpty()) {
                        districtObj.put("depts", detailList);
                    }
                }
                if (!districtObj.isEmpty()) {
                    retList.add(districtObj);
                }
            }
        }
        ctx.setResult(retList);
    }

}