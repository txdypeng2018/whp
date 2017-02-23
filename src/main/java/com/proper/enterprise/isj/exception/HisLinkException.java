package com.proper.enterprise.isj.exception;

/**
 * His连接异常.
 * Created by think on 2016/9/11 0011.
 */
public class HisLinkException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = -4635777618676146265L;

    public HisLinkException(String message) {
        super(message);
    }

    public HisLinkException(String message, Throwable e) {
        super(message, e);
    }
}
