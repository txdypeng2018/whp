/**
 * <p>
 * application name: ValidateHistoryOrdersBusiness.java
 * </p>
 * <p>
 * description:
 * </p>
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 产品研发中心
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * 
 * @author: 王东石<wangdongshi@propersoft.cn>
 * @version Ver 1.0 2017年2月22日 新建
 */

package com.proper.enterprise.isj.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.function.registration.SaveOrUpdateRegistrationByPayStatusFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;

/**
 * .
 * <p>
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-22 新建
 */
@Service
public class ValidateHistoryOrdersBusiness<T, C extends BasicInfoDocumentContext<T>> implements IBusiness<T, C> {

    @Autowired
    RegistrationService registrationService;

    @Autowired
    SaveOrUpdateRegistrationByPayStatusFunction function;

    /*
     * (non-Javadoc)
     * @see com.proper.enterprise.isj.support.business.Business#process(com.proper.enterprise.isj.support.business.
     * BusinessContext)
     */
    @Override
    public void process(C ctx) throws Exception {
        BasicInfoDocument basicInfo = ctx.getBasicInfoDocument();
        List<RegistrationDocument> hasRegOrderList = registrationService
                .findRegistrationDocumentList(basicInfo.getId());
        for (RegistrationDocument registrationDocument : hasRegOrderList) {
            FunctionUtils.invoke(function, registrationDocument);
        }
    }

}