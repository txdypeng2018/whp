package com.proper.enterprise.isj.proxy.business.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.RegistrationDocIdContext;
import com.proper.enterprise.isj.function.registration.FetchRegistrationDocumentByIdFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchRegistrationDocumentByIdBusiness<M extends RegistrationDocIdContext<RegistrationDocument> & ModifiedResultBusinessContext<RegistrationDocument>>
        implements IBusiness<RegistrationDocument, M> {

    @Autowired
    FetchRegistrationDocumentByIdFunction fetchRegistrationDocumentByIdFunction;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(fetchRegistrationDocumentByIdFunction.execute(ctx.getRegistrationDocumentId()));
    }

}