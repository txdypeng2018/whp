package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DeptNameContext<T> extends BusinessContext<T> {
    String getDeptName();

    void setDeptName(String deptName);
}
