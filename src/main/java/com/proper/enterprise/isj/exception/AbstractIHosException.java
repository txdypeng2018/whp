package com.proper.enterprise.isj.exception;

import com.proper.enterprise.isj.support.VersionEnum;

public class AbstractIHosException extends Exception {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    public AbstractIHosException() {
        super();
    }

    public AbstractIHosException(String message, Throwable cause,
                                 boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AbstractIHosException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractIHosException(String message) {
        super(message);
    }

    public AbstractIHosException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

}
