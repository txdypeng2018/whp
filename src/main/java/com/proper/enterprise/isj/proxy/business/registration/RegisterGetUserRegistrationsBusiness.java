package com.proper.enterprise.isj.proxy.business.registration;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.context.ViewTypeIdContext;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.function.registration.SaveOrUpdateRegistrationByPayStatusFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class RegisterGetUserRegistrationsBusiness<T, M extends MemberIdContext<Object> & ViewTypeIdContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {
    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    SaveOrUpdateRegistrationByPayStatusFunction saveOrUpdateRegistrationByPayStatusFunction;

    @Override
    public void process(M ctx) throws Exception {
        String memberId = ctx.getMemberId();
        String viewTypeId = ctx.getViewTypeId();

        List<RegistrationDocument> resultList = new ArrayList<>();
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RegisterException(CenterFunctionUtils.USERINFO_LOGIN_ERR);
        }
        BasicInfoDocument basicInfo;
        if (StringUtil.isEmpty(memberId)) {
            basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(user.getId());
        } else {
            basicInfo = userInfoService.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        }
        if (basicInfo == null) {
            throw new RegisterException(CenterFunctionUtils.PATIENTINFO_GET_ERR);
        }
        Date today = DateUtil.toDate(DateUtil.toDateString(new Date()));
        try {
            List<RegistrationDocument> regList = registrationService.findRegistrationDocumentList(basicInfo.getId());
            BigDecimal tempBig;
            DecimalFormat df = new DecimalFormat("0.00");
            for (RegistrationDocument registrationDocument : regList) {
                registrationDocument = saveOrUpdateRegistrationByPayStatus(registrationDocument);
                if (registrationDocument == null) {
                    continue;
                }
                if (StringUtil.isNotEmpty(registrationDocument.getAmount())) {
                    tempBig = new BigDecimal(registrationDocument.getAmount()).divide(new BigDecimal("100"), 2,
                            RoundingMode.UNNECESSARY);
                    registrationDocument.setAmount(df.format(tempBig));
                    registrationDocument.setClinicNum(registrationDocument.getNum());
                }
                if (StringUtil.isEmpty(viewTypeId)) {
                    resultList.add(registrationDocument);
                } else {
                    if (DateUtil.toDate(registrationDocument.getRegDate()).compareTo(today) >= 0) {
                        if (registrationDocument.getStatusCode().equals(RegistrationStatusEnum.CANCEL.getValue())
                                || registrationDocument.getStatusCode()
                                        .equals(RegistrationStatusEnum.EXCHANGE_CLOSED.getValue())
                                || registrationDocument.getStatusCode()
                                        .equals(RegistrationStatusEnum.REFUND.getValue())) {
                            if ("2".equals(viewTypeId)) {
                                resultList.add(registrationDocument);
                            }
                        } else {
                            if ("1".equals(viewTypeId)) {
                                resultList.add(registrationDocument);
                            }
                        }
                    } else {
                        if ("2".equals(viewTypeId)) {
                            resultList.add(registrationDocument);
                        }
                    }
                }
            }

        } catch (Exception e) {
            debug("挂号单列表初始化异常", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(resultList);
    }

    /**
     * 检查挂号单支付状态,并更新挂号单.
     *
     * @param registrationDocument 注册报文.
     * @return 应答报文.
     * @see com.proper.enterprise.isj.function.registration.SaveOrUpdateRegistrationByPayStatusFunction
     */
    private RegistrationDocument saveOrUpdateRegistrationByPayStatus(RegistrationDocument registrationDocument) {
        return (RegistrationDocument) FunctionUtils.invoke(saveOrUpdateRegistrationByPayStatusFunction,
                registrationDocument);
    }
}
