package com.proper.enterprise.isj.proxy.document.medicalreports;

import java.io.Serializable;

/**
 * 检查报告列表
 */
public class MedicalReportsDocument implements Serializable {

    private static final long serialVersionUID = -1l;

    /**
     * 报告ID
     */
    private String id;
    /**
     * 报告名称
     */
    private String name;
    /**
     * 检查时间
     */
    private String examinationDate;
    /**
     * 报告类别
     */
    private String reportCategory;
    /**
     * 报告状态
     */
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExaminationDate() {
        return examinationDate;
    }

    public void setExaminationDate(String examinationDate) {
        this.examinationDate = examinationDate;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
