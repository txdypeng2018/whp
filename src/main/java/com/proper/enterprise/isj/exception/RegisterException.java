package com.proper.enterprise.isj.exception;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 挂号异常.
 * Created by think on 2016/9/14 0014.
 */
public class RegisterException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    public RegisterException(String message) {
        super(message);
    }

    public RegisterException(String message, Throwable e) {
        super(message, e);
    }
}
