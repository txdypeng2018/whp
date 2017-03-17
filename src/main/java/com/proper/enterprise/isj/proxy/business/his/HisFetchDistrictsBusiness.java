package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.DistrictDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class HisFetchDistrictsBusiness<M extends ModifiedResultBusinessContext<Collection<DistrictDocument>>>
        implements IBusiness<Collection<DistrictDocument>, M> {

    @Override
    public void process(M ctx) throws Exception {
        List<DistrictDocument> districtList = new ArrayList<>();
        DistrictDocument dis = new DistrictDocument();
        districtList.add(dis);
        ctx.setResult(districtList);
    }
}
