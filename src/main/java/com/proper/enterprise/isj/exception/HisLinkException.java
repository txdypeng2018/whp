package com.proper.enterprise.isj.exception;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * His连接异常.
 * Created by think on 2016/9/11 0011.
 */
public class HisLinkException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    public HisLinkException(String message) {
        super(message);
    }

    public HisLinkException(String message, Throwable e) {
        super(message, e);
    }
}
