package com.proper.enterprise.isj.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BasicInfoDocumentContext;
import com.proper.enterprise.isj.context.RegistrationDocumentContext;
import com.proper.enterprise.isj.context.UserContext;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.function.registration.CreateRegistrationAndReturnOrderFunction;
import com.proper.enterprise.isj.function.registration.SaveOrUpdateRegistrationByPayStatusFunction;
import com.proper.enterprise.isj.function.schedule.CheckRegOrderTimeIsValidFunction;
import com.proper.enterprise.isj.function.schedule.SaveOrRemoveCacheRegKeyInScheduleFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.service.rule.RegistrationRuleService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class RegistationBusiness<T, C extends RegistrationDocumentContext<T> & BasicInfoDocumentContext<T> & UserContext<T>
&ModifiedResultBusinessContext<T>
>
        implements IBusiness<T, C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistationBusiness.class);

    @Autowired
    RegistrationService registrationService;

    @Autowired
    SaveOrUpdateRegistrationByPayStatusFunction function;

    @Autowired
    RegistrationRuleService registrationRuleService;

    @Autowired
    CreateRegistrationAndReturnOrderFunction createRegistrationAndReturnOrderFunction;

    @Autowired
    SaveOrRemoveCacheRegKeyInScheduleFunction saveOrRemoveCacheRegKeyFunction;

    @Autowired
    CheckRegOrderTimeIsValidFunction checkRegOrderTimeIsValidFunction;

    /*
     * (non-Javadoc)
     * @see com.proper.enterprise.isj.support.business.Business#process(com.proper.enterprise.isj.support.business.
     * BusinessContext)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void process(C ctx) throws Exception {
        RegistrationDocument reg = ctx.getRegistrationDocument();
        BasicInfoDocument basicInfo = ctx.getBasicInfoDocument();
        User user = ctx.getUser();
        try {
            // 预约挂号
            if (reg.getIsAppointment().equals(String.valueOf(1))) {
                // 预约挂号过滤同一号点的并发
                FunctionUtils.invoke(saveOrRemoveCacheRegKeyFunction, reg, "0");
                // 预约挂号校验是否有未付款的订单
                List<RegistrationDocument> regisList = registrationService.findRegistrationByCreateUserIdAndPayStatus(
                        user.getId(), RegistrationStatusEnum.NOT_PAID.getValue(), reg.getIsAppointment());
                if (regisList != null && regisList.size() > 0) {
                    throw new RuntimeException(CenterFunctionUtils.ORDER_NON_PAY_ERR);
                }
            }
            // 校验挂号单的时间是否有冲突
            FunctionUtils.invoke(checkRegOrderTimeIsValidFunction, reg);
            // 根据规则判断是否可挂此号
            String checkRegMsg = registrationRuleService.checkPersonRegistration(reg.getDeptId(),
                    basicInfo.getIdCard());
            if (StringUtil.isNotEmpty(checkRegMsg)) {
                throw new RegisterException(checkRegMsg);
            }
        } catch (Exception e) {
            LOGGER.debug("添加挂号单出现异常", e);
            FunctionUtils.invoke(saveOrRemoveCacheRegKeyFunction, reg, "0");
            throw e;
        }
        ctx.setResult((T) FunctionUtils.invoke(createRegistrationAndReturnOrderFunction, reg));
    }

}