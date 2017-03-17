package com.proper.enterprise.isj.proxy.business.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class SaveRegistrationDocumentBusiness<M extends RegistrationDocumentContext<RegistrationDocument> & ModifiedResultBusinessContext<RegistrationDocument>>
        implements IBusiness<RegistrationDocument, M> {

    @Autowired
    RegistrationRepository registrationRepository;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(registrationRepository.save(ctx.getRegistrationDocument()));
    }

}