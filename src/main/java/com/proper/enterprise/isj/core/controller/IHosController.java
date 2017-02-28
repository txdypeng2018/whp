package com.proper.enterprise.isj.core.controller;

import com.proper.enterprise.isj.exception.AbstractIHosException;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.exception.ErrMsgException;
import org.springframework.oxm.UnmarshallingFailureException;

public abstract class IHosController extends BaseController {

    /**
     * 覆盖基类方法，将 controller 中抛出的各类异常自动转换为响应体中的错误信息
     * 
     * @param  ex 异常
     * @return 错误信息
     */
    @Override
    protected String handleBody(Exception ex) {
        if (ex instanceof UnmarshallingFailureException) {
            return CenterFunctionUtils.HIS_DATALINK_ERR;
        } else if (ex instanceof AbstractIHosException || ex instanceof ErrMsgException) {
            return ex.getMessage();
        } else {
            return CenterFunctionUtils.APP_SYSTEM_ERR;
        }
    }

}
