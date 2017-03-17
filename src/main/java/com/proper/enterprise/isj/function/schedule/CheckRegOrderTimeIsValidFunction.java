package com.proper.enterprise.isj.function.schedule;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class CheckRegOrderTimeIsValidFunction implements IFunction<Object> {

    @Autowired
    RegistrationService registrationService;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public Object execute(Object... params) throws Exception {
        checkRegOrderTimeIsValid((RegistrationDocument) params[0]);
        return new Object();
    }

    protected void checkRegOrderTimeIsValid(RegistrationDocument reg) throws RegisterException {
        if (StringUtil.isEmpty(reg.getIsAppointment())) {
            throw new RegisterException(CenterFunctionUtils.ORDER_SAVE_ERR);
        }
        Date regTimeDate = DateUtil.toDate(reg.getRegisterDate(), "yyyy-MM-dd HH:mm");
        if (regTimeDate.compareTo(new Date()) <= 0) {
            throw new RegisterException(CenterFunctionUtils.ORDER_PAGE_OVERTIME_ERR);
        }
        String dateStr = reg.getRegisterDate().split(" ")[0];
        String today = DateUtil.toDateString(new Date());
        // 预约挂号,预约日期与操作日期不能相同
        if (reg.getIsAppointment().equals(String.valueOf(1)) && dateStr.equals(today)) {
            throw new RegisterException(CenterFunctionUtils.ORDER_PAGE_OVERTIME_ERR);
        }
        // 当日挂号,预约日期必须与操作日期相同
        if (reg.getIsAppointment().equals(String.valueOf(0)) && !dateStr.equals(today)) {
            throw new RegisterException(CenterFunctionUtils.ORDER_PAGE_OVERTIME_ERR);
        }
    }

}
