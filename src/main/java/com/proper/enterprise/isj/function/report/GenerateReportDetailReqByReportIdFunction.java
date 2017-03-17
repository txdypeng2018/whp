package com.proper.enterprise.isj.function.report;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.MedicalReportsServiceImpl.getReportDetailReq(String)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class GenerateReportDetailReqByReportIdFunction implements IFunction<ReportInfoReq>, ILoggable {
    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public ReportInfoReq execute(Object... params) throws Exception {
        return getReportDetailReq((String) params[0]);
    }

    /**
     * 获取检查单详细信息对象.
     *
     * @param reportId
     *        报告单号.
     * @return 返回值.
     */
    public ReportInfoReq getReportDetailReq(String reportId) {
        String hosId = CenterFunctionUtils.getHosId();
        ReportInfoReq detailReq = new ReportInfoReq();
        debug("hosId:" + hosId);
        detailReq.setHosId(hosId);
        debug("reportId:" + reportId);
        detailReq.setReportId(reportId);
        //detailReq.setHospPatientId("");
        return detailReq;
    }

}