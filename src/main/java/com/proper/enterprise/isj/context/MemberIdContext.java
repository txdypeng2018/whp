package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface MemberIdContext<T> extends BusinessContext<T> {
    String getMemberId();

    void setMemberId(String memberId);
}
