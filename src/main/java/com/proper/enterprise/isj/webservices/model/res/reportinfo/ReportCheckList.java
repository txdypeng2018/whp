package com.proper.enterprise.isj.webservices.model.res.reportinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * 检验项目列表
 */
@XmlRootElement(name = "CHECK_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportCheckList implements Serializable {

    /**
     * 用户院内ID
     */
    @XmlElement(name = "DETAIL")
    private List<ReportCheckListDetail> detail;

    public List<ReportCheckListDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<ReportCheckListDetail> detail) {
        this.detail = detail;
    }
}