package com.proper.enterprise.isj.user.model;

import com.proper.enterprise.platform.auth.jwt.model.JWTPayload;

public class IHosJWTPayload implements JWTPayload {

    private boolean hasRole;

    public IHosJWTPayload(boolean hasRole) {
        this.hasRole = hasRole;
    }

    public boolean isHasRole() {
        return hasRole;
    }

    public void setHasRole(boolean hasRole) {
        this.hasRole = hasRole;
    }

}
