package com.proper.enterprise.isj.function.authentication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.PatRes;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SaveOrUpdatePatientMedicalNumFunction implements IFunction<UserInfoDocument>, ILoggable {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    @Override
    public UserInfoDocument execute(Object... params) throws Exception {
        return saveOrUpdatePatientMedicalNum((String) params[0], (String) params[1], (String) params[2]);
    }

    public UserInfoDocument saveOrUpdatePatientMedicalNum(String userId, String memberId, String medicalNum)
            throws Exception {
        BasicInfoDocument basicInfo = toolkitx.executeFunction(FetchFamilyMemberByUserIdAndMemberIdFunction.class,
                userId, memberId);
        ResModel<PatRes> patRes = toolkitx.executeFunction(CheckPatientMedicalNumAndReturnFunction.class, basicInfo,
                medicalNum);
        if (patRes.getReturnCode() == ReturnCode.ERROR) {
            debug(patRes.getReturnMsg());
            throw new HisReturnException("就诊人信息与医院预留信息不匹配,请核对就诊人信息或者到医院修改预留信息");
        }
        String returnMedicalNum = patRes.getRes().getCardNo();
        if (StringUtil.isEmpty(returnMedicalNum)) {
            debug("调用createPat返回的病历号为空,返回数据异常,病历号不应为空");
            throw new HisReturnException("绑定病历号失败");
        }
        UserInfoDocument userInfo = toolkitx.executeRepositoryFunction(UserInfoRepository.class, "getByUserId", userId);
        if (userInfo.getId().equals(memberId)) {
            userInfo.setMedicalNum(patRes.getRes().getCardNo());
        } else {
            List<FamilyMemberInfoDocument> familyMemberInfoDocumentList = userInfo.getFamilyMemberInfo();
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyMemberInfoDocumentList) {
                if (familyMemberInfoDocument.getId().equals(memberId)) {
                    familyMemberInfoDocument.setMedicalNum(patRes.getRes().getCardNo());
                }
            }
        }
        return toolkitx.executeRepositoryFunction(UserInfoRepository.class, "save", userInfo);
    }

}
