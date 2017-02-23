package com.proper.enterprise.isj.exception;

/**
 * 挂号异常.
 * Created by think on 2016/9/14 0014.
 */
public class RegisterException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = -162468108065773081L;

    public RegisterException(String message) {
        super(message);
    }

    public RegisterException(String message, Throwable e) {
        super(message, e);
    }
}
