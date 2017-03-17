package com.proper.enterprise.isj.exception;

import com.proper.enterprise.isj.support.VersionEnum;

public class DelayException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    private int position;

    public DelayException(int position) {
        this.position = position;

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
