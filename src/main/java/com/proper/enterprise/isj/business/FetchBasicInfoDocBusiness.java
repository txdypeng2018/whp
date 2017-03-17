/**
 * <p>
 * application name: FetchBasicInfoDocBusiness.java
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.context.UserContext;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * .
 * <p>
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version: 1.0 2017-02-22 新建
 */

@Service
public class FetchBasicInfoDocBusiness<T, C extends RegistrationDocumentContext<T> & UserContext<T> & BasicInfoDocumentContext<T>>
        implements IBusiness<T, C> {

    @Autowired
    UserInfoService userInfoService;

    /*
     * (non-Javadoc)
     * @see com.proper.enterprise.isj.support.business.Business#process(com.proper.enterprise.isj.support.business.
     * BusinessContext)
     */
    @Override
    public void process(C ctx) throws Exception {
        User user = ctx.getUser();
        RegistrationDocument reg = ctx.getRegistrationDocument();
        BasicInfoDocument basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(),
                reg.getPatientId());
        if (basicInfo == null) {
            throw new RegisterException(CenterFunctionUtils.PATIENTINFO_GET_ERR);
        }
        if (StringUtil.isEmpty(basicInfo.getMedicalNum())) {
            userInfoService.saveOrUpdatePatientMedicalNum(user.getId(), basicInfo.getId(), null);
        }
        ctx.setBasicInfoDocument(basicInfo);
    }

}
