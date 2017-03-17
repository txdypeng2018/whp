package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;

public interface ReportListReqContext<T> extends BusinessContext<T> {
    ReportListReq getReportListReq();

    void setReportListReq(ReportListReq floorId);
}
