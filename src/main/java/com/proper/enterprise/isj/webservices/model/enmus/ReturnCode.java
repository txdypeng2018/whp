package com.proper.enterprise.isj.webservices.model.enmus;

import com.proper.enterprise.platform.core.enums.IntEnum;

public enum ReturnCode implements IntEnum {

    SUCCESS(0),
    SYS_ERR(99),
    WRONG_PARAMS(1),
    STRING_ERR(2),
    AUTHZ_ERR(3),
    AUTHC_ERR(4),
    FUNCODE_ERR(5),
    KEY_ERR(6),
    SIGN_ERR(7),
    SIGNTYPE_ERR(8),
    CIPHER_ERR(9),
    DUP_COMMIT(10),
    EMPTY_REQ(11),
    EMPTY_RETURN(12),
    ERROR(-1),
    DATABASE_ERR(-2);

    private int code;

    ReturnCode(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static ReturnCode codeOf(int code) {
        for (ReturnCode returnCode : values()) {
            if (returnCode.getCode() == code) {
                return returnCode;
            }
        }
        return null;
    }
}
