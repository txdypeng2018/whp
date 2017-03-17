package com.proper.enterprise.isj.proxy.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.business.his.HisFetchDistrictsBusiness;
import com.proper.enterprise.isj.proxy.document.DistrictDocument;
import com.proper.enterprise.isj.proxy.service.DistrictsService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * Created by think on 2016/8/18 0018.
 *
 */
@Service
public class DistrictsServiceImpl extends AbstractService implements DistrictsService {

    @Override
    public List<DistrictDocument> getDistrict() {
        return toolkit.execute(HisFetchDistrictsBusiness.class, ctx->{});
    }
}
