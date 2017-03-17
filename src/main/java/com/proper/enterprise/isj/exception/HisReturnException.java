package com.proper.enterprise.isj.exception;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * His返回异常.
 * Created by think on 2016/9/9 0009.
 */
public class HisReturnException extends AbstractIHosException {
    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    public HisReturnException(String message) {
        super(message);
    }

    public HisReturnException(String message, Throwable e) {
        super(message, e);
    }
}
