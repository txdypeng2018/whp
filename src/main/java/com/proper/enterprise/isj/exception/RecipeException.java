package com.proper.enterprise.isj.exception;

/**
 * 业务异常.
 * Created by think on 2016/9/19 0019.
 */
public class RecipeException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = 3841671048962355768L;

    public RecipeException(String message) {
        super(message);
    }

    public RecipeException(String message, Throwable e) {
        super(message, e);
    }
}
