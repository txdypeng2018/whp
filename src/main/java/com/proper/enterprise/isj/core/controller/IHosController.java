package com.proper.enterprise.isj.core.controller;

import com.proper.enterprise.isj.exception.AbstractIHosException;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.exception.ErrMsgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.UnmarshallingFailureException;

public abstract class IHosController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(IHosController.class);

    /**
     * 覆盖基类方法，将 controller 中抛出的各类异常自动转换为响应体中的错误信息
     * 
     * @param  ex 异常
     * @return 错误信息
     */
    @Override
    protected String handleBody(Exception ex) {
        String msg;
        if (ex instanceof UnmarshallingFailureException) {
            msg = CenterFunctionUtils.HIS_DATALINK_ERR;
        } else if (ex instanceof AbstractIHosException || ex instanceof ErrMsgException) {
            msg = ex.getMessage();
        } else {
            msg = CenterFunctionUtils.APP_SYSTEM_ERR;
        }
        LOGGER.debug("Convert {} to {}", ex, msg);
        return msg;
    }

}
