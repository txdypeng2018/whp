package com.proper.enterprise.isj.proxy.business.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class DelRegistrationDocumentBusiness<M extends RegistrationDocumentContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {

    @Autowired
    RegistrationRepository registrationRepository;

    @Override
    public void process(M ctx) {
        registrationRepository.delete(ctx.getRegistrationDocument());
    }

}