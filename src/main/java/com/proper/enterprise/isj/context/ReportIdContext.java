package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface ReportIdContext<T> extends BusinessContext<T> {
    String getReportId();

    void setReportId(String reportId);
}
