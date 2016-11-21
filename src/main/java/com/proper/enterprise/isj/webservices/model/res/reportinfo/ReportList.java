package com.proper.enterprise.isj.webservices.model.res.reportinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查/检验报告列表
 */
@XmlRootElement(name = "REPORT_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportList implements Serializable {
    /**
     * 用户检查/检验报告
     */
    @XmlElement(name = "REPORT_INFO")
    private List<Report> report = new ArrayList<>();

    public List<Report> getReport() {
        return report;
    }

    public void setReport(List<Report> report) {
        this.report = report;
    }
}
