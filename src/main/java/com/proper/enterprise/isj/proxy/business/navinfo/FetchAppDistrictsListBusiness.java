package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.function.customservice.FetchDisListFunction;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchAppDistrictsListBusiness<M extends DistrictIdContext<Collection<Map<String, Object>>> & ModifiedResultBusinessContext<Collection<Map<String, Object>>>>
        implements IBusiness<Collection<Map<String, Object>>, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Autowired
    FetchDisListFunction fetchDisListFunc;

    @Override
    public void process(M ctx) {
        String districtId = ctx.getDistrictId();
        List<Map<String, Object>> retList = new ArrayList<>();

        List<NavInfoEntity> ditrictList;
        if (StringUtil.isNotNull(districtId)) {
            ditrictList = navRepo.findByNavIdOrderByNavIdAsc(districtId);
        } else {
            Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "navId"));
            ditrictList = navRepo.findAll(sort);
        }
        List<SubjectDocument> disList = FunctionUtils.invoke(fetchDisListFunc);
        for (SubjectDocument subject : disList) {
            NavInfoEntity navInfo = new NavInfoEntity();
            navInfo.setParentType("HISDISTRICT");
            navInfo.setNavId(subject.getId());
            navInfo.setNavName(subject.getName());
            ditrictList.add(navInfo);
        }
        if (ditrictList.size() > 0) {
            for (NavInfoEntity detail : ditrictList) {
                Map<String, Object> districtObj = new LinkedHashMap<>();
                // 院区级别
                if (detail.getParentType().equals("HISDISTRICT")) {
                    districtObj.put("id", detail.getNavId());
                    districtObj.put("name", detail.getNavName());
                    List<Map<String, String>> detailList = new ArrayList<>();
                    for (NavInfoEntity buildDetail : ditrictList) {
                        // 当前院区并且是建筑级别
                        if (buildDetail.getParentType().equals(ConfCenter.get("isj.info.district"))
                                && buildDetail.getParentId().equals(detail.getNavId())) {
                            Map<String, String> buildObj = new LinkedHashMap<>();
                            buildObj.put("id", buildDetail.getNavId());
                            buildObj.put("name", buildDetail.getNavName());
                            buildObj.put("function", buildDetail.getNavFunction());
                            detailList.add(buildObj);
                        }
                    }
                    districtObj.put("builds", detailList);
                }
                if (!districtObj.isEmpty()) {
                    retList.add(districtObj);
                }
            }
        }
        ctx.setResult(retList);
    }

}