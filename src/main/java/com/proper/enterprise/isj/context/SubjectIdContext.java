package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface SubjectIdContext<T> extends BusinessContext<T> {
    String getSubjectId();

    void setSubjectId(String subjectId);
}
