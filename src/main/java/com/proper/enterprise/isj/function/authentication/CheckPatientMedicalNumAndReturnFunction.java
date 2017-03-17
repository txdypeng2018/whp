package com.proper.enterprise.isj.function.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;
import com.proper.enterprise.isj.webservices.model.req.PatReq;
import com.proper.enterprise.isj.webservices.model.res.PatRes;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class CheckPatientMedicalNumAndReturnFunction implements IFunction<ResModel<PatRes>> {

    @Autowired
    @Lazy
    public WebServicesClient webServicesClient;

    @Override
    public ResModel<PatRes> execute(Object... params) throws Exception {
        return checkPatientMedicalNumAndReturn((BasicInfoDocument) params[0], (String) params[1]);
    }

    public ResModel<PatRes> checkPatientMedicalNumAndReturn(BasicInfoDocument basicInfo, String medicalNum)
            throws Exception {
        PatReq req = new PatReq();
        req.setHosId(CenterFunctionUtils.getHosId());
        req.setCardType(IDCardType.IDCARD);
        req.setMarkNo(basicInfo.getIdCard());
        if (StringUtil.isEmpty(medicalNum)) {
            req.setCardNo("");
        } else {
            req.setCardNo(medicalNum);
        }
        req.setAddress("");
        req.setName(basicInfo.getName());
        String sex = IdcardUtils.getGenderByIdCard(basicInfo.getIdCard());
        switch (sex) {
        case "F":
            req.setSex(Sex.FEMALE);
            break;
        case "M":
            req.setSex(Sex.MALE);
            break;
        default:
            req.setSex(Sex.SECRET);
            break;
        }
        req.setMobile(basicInfo.getPhone());
        req.setBirthday(DateUtil
                .toDateString(DateUtil.toDate(IdcardUtils.getBirthByIdCard(basicInfo.getIdCard()), "yyyyMMdd")));
        req.setAddress("");
        return webServicesClient.createPat(req);
    }

}
