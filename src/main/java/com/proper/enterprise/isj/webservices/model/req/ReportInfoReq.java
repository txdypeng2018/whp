package com.proper.enterprise.isj.webservices.model.req;

import java.io.Serializable;

import com.proper.enterprise.isj.support.VersionEnum;

/**
 * 检验/检测报告详细信息请求对象.
 */
public class ReportInfoReq implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 医院ID
     */
    private String hosId = "";

    /**
     * 检验报告单号 必填
     */
    private String reportId = "";

    /**
     * 用户院内ID 必填
     */
    private String hospPatientId = "";

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getHospPatientId() {
        return hospPatientId;
    }

    public void setHospPatientId(String hospPatientId) {
        this.hospPatientId = hospPatientId;
    }
}
