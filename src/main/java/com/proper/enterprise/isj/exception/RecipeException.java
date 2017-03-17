package com.proper.enterprise.isj.exception;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 业务异常.
 * Created by think on 2016/9/19 0019.
 */
public class RecipeException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    public RecipeException(String message) {
        super(message);
    }

    public RecipeException(String message, Throwable e) {
        super(message, e);
    }
}
