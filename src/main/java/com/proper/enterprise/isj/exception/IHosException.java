package com.proper.enterprise.isj.exception;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 普通业务异常
 */
public class IHosException extends AbstractIHosException {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    public IHosException(String message) {
        super(message);
    }

}
