package com.proper.enterprise.isj.exception;

/**
 * His返回异常.
 * Created by think on 2016/9/9 0009.
 */
public class HisReturnException extends AbstractIHosException {
    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = 2512161418515733127L;

    public HisReturnException(String message) {
        super(message);
    }

    public HisReturnException(String message, Throwable e) {
        super(message, e);
    }
}
