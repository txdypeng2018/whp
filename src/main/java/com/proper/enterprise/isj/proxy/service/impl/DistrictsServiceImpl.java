package com.proper.enterprise.isj.proxy.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.DistrictDocument;
import com.proper.enterprise.isj.proxy.service.DistrictsService;
import com.proper.enterprise.isj.webservices.WebServicesClient;

/**
 * Created by think on 2016/8/18 0018.
 *
 */
@Service
public class DistrictsServiceImpl implements DistrictsService {

	@Autowired
	@Lazy
	private WebServicesClient webServicesClient;

	@Override
	public List<DistrictDocument> getDistrict() {
		List<DistrictDocument> districtList = new ArrayList<>();
        DistrictDocument dis = new DistrictDocument();
		districtList.add(dis);
		return districtList;
	}
}
