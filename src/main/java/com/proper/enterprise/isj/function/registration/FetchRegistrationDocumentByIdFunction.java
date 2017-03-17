package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class FetchRegistrationDocumentByIdFunction implements IFunction<RegistrationDocument> {
    
    @Autowired
    RegistrationRepository registrationRepository;
    
    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public RegistrationDocument execute(Object... params) throws Exception {
        return fetchRegistrationDocumentById((String) params[0]);
    }

    /**
     * 构造报告列表请求对象.
     *
     * @param basic
     *        用户基本信息.
     * @return 返回值.
     */
    public RegistrationDocument fetchRegistrationDocumentById(String id) {
       return registrationRepository.findOne(id);
    }

}