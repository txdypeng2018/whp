package com.proper.enterprise.isj.function.opinion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.repository.ServiceUserOpinionRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class FindByIdFunction implements IFunction<ServiceUserOpinionDocument> {

    @Autowired
    ServiceUserOpinionRepository opinionRepo;

    @Override
    public ServiceUserOpinionDocument execute(Object... params) throws Exception {
        return getById((String)params[0]);
    }

    public ServiceUserOpinionDocument getById(String id) {
        return opinionRepo.findById(id);
    }
}
