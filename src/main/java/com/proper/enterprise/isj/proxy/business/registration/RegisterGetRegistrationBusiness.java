package com.proper.enterprise.isj.proxy.business.registration;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class RegisterGetRegistrationBusiness<M extends IdContext<RegistrationDocument> & ModifiedResultBusinessContext<RegistrationDocument>>
        implements IBusiness<RegistrationDocument, M>, ILoggable {

    @Autowired
    RegistrationService registrationService;

    @Override
    public void process(M ctx) throws Exception {
        String id = ctx.getId();
        RegistrationDocument reg;
        try {
            reg = registrationService.getRegistrationDocumentById(id);
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtil.toDate(reg.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT));
            cal.add(Calendar.MINUTE, CenterFunctionUtils.ORDER_COUNTDOWN);
            if (cal.getTime().compareTo(new Date()) <= 0
                    && reg.getStatusCode().equals(RegistrationStatusEnum.NOT_PAID.getValue())) {
                registrationService.saveCancelRegistration(reg.getId(), OrderCancelTypeEnum.CANCEL_OVERTIME);
            }
            reg = registrationService.getRegistrationDocumentById(id);
            BigDecimal tempBig = new BigDecimal(reg.getAmount()).divide(new BigDecimal("100"), 2,
                    RoundingMode.UNNECESSARY);
            DecimalFormat df = new DecimalFormat("0.00");
            reg.setAmount(df.format(tempBig));
            boolean canBack = CenterFunctionUtils.checkRegCanBack(reg);
            if (canBack) {
                reg.setCanBack(String.valueOf(1));
            } else {
                reg.setCanBack(String.valueOf(0));
            }
            registrationService.setOrderProcess2Registration(reg);
        } catch (Exception e) {
            debug("挂号单初始化异常", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(reg);
    }
}
