package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DeptIdContext<T> extends BusinessContext<T> {
    String getDeptId();

    void setDeptId(String deptId);
}
