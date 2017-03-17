package com.proper.enterprise.isj.proxy.business.registration;

import java.util.ArrayList;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FetchBasicInfoDocBusiness;
import com.proper.enterprise.isj.business.FetchUserBusiness;
import com.proper.enterprise.isj.business.RegistationBusiness;
import com.proper.enterprise.isj.business.ValidateHistoryOrdersBusiness;
import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.context.UserContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.business.AbstractGroupBusiness;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class RegisterCtrlAddRegistrationBusiness<T, M extends RegistrationDocumentContext<T> & UserContext<T>
    & BasicInfoDocumentContext<T> & ModifiedResultBusinessContext<T>>
        extends AbstractGroupBusiness<T, M, ArrayList<IBusiness<T, M>>> implements InitializingBean {

    @Autowired
    FetchUserBusiness fetchUserBiz;

    @Autowired
    FetchBasicInfoDocBusiness fetchBaseInfoBiz;

    @Autowired
    ValidateHistoryOrdersBusiness validateHisOdrBiz;

    @Autowired
    RegistationBusiness regBiz;

    public RegisterCtrlAddRegistrationBusiness() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addHandler(fetchUserBiz);
        this.addHandler(fetchBaseInfoBiz);
        this.addHandler(validateHisOdrBiz);
        this.addHandler(regBiz);
    }
}
