package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.controller.RegisterController;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SaveRegistrationDocumentFunction implements IFunction<RegistrationDocument> {

    @Autowired
    RegistrationRepository registrationRepository;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public RegistrationDocument execute(Object... params) throws Exception {
        return saveOrUpdateRegistrationByPayStatus((RegistrationDocument) params[0]);
    }

    /**
     * 检查挂号单支付状态,并更新挂号单.
     *
     * @param registrationDocument 注册报文.
     * @return 应答报文.
     * @see RegisterController#saveOrUpdateRegistrationByPayStatus(RegistrationDocument)
     */
    public RegistrationDocument saveOrUpdateRegistrationByPayStatus(RegistrationDocument regdoc) {
        return registrationRepository.save(regdoc);
    }

}
