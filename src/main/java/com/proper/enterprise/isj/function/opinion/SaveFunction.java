package com.proper.enterprise.isj.function.opinion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.repository.ServiceUserOpinionRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SaveFunction implements IFunction<Object> {

    @Autowired
    ServiceUserOpinionRepository opinionRepo;

    @Override
    public Object execute(Object... params) throws Exception {
        saveOpinion((ServiceUserOpinionDocument)params[0]);
        return null;
    }

    public void saveOpinion(ServiceUserOpinionDocument opinionDocument) {
        opinionRepo.save(opinionDocument);
    }
}
