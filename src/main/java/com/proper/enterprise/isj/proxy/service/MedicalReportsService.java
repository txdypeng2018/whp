package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDetailDocument;
import com.proper.enterprise.isj.proxy.document.medicalreports.MedicalReportsDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.webservices.model.req.ReportInfoReq;
import com.proper.enterprise.isj.webservices.model.req.ReportListReq;

import java.util.List;

/**
 * 检验检测报告Service.
 */
public interface MedicalReportsService {

    List<MedicalReportsDocument> getPacsReportsList(ReportListReq req, String searchStatus, BasicInfoDocument basicInfo, String searchTime) throws Exception;

    List<MedicalReportsDocument> getReportsList(ReportListReq req, String searchStatus, BasicInfoDocument basicInfo, String searchTime) throws Exception;

    String getRepostsDetailsInfo(String reportId) throws Exception;

    MedicalReportsDetailDocument getRepostsDetailsInfo(String reportId, ReportInfoReq req) throws Exception;

    ReportListReq getReportListReq(BasicInfoDocument basic);

    ReportInfoReq getReportDetailReq(String reportId);
}
