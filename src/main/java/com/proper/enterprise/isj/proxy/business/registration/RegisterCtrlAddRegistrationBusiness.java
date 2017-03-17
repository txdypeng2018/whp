package com.proper.enterprise.isj.proxy.business.registration;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FetchBasicInfoDocBusiness;
import com.proper.enterprise.isj.business.FetchUserBusiness;
import com.proper.enterprise.isj.business.RegistationBusiness;
import com.proper.enterprise.isj.business.ValidateHistoryOrdersBusiness;
import com.proper.enterprise.platform.core.business.AbstractGroupBusiness;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.BusinessContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class RegisterCtrlAddRegistrationBusiness<T>
        extends AbstractGroupBusiness<T, BusinessContext<T>, ArrayList<IBusiness<T, BusinessContext<T>>>> {

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
        
        this.addHandler(fetchUserBiz);
        this.addHandler(fetchBaseInfoBiz);
        this.addHandler(validateHisOdrBiz);
        this.addHandler(regBiz);
    }
}
