package com.proper.enterprise.isj.proxy.service.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.navigation.MergeDept2SubjectFunction;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.isj.support.service.AbstractService;

@Service
public class SubjectServiceImpl extends AbstractService implements SubjectService {

    @Override
    @Cacheable(cacheNames = "pep-temp_600")
    public List<SubjectDocument> findSubjectDocumentListFromHis(String districtId, boolean isAppointment) throws Exception {
        return toolkit.executeSimpleFunction(MergeDept2SubjectFunction.class, districtId, isAppointment);
    }

    
}
