package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;

public interface ReportInfoReqEntityContext<T> extends BusinessContext<T> {
    ReportInfoReq getReportInfoReq();

    void setReportInfoReq(ReportInfoReq req);
}
