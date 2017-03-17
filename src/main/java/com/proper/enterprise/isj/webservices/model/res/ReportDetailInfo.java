package com.proper.enterprise.isj.webservices.model.res;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportCheckList;
import com.proper.enterprise.isj.webservices.model.res.reportinfo.ReportUserDetailInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查/检验报告详细
 */
@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportDetailInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 医院ID
     */
    @XmlElement(name = "HOS_ID")
    private String hosId;

    /**
     * 用户报告信息
     */
    @XmlElement(name = "REPORT_INFO")
    private List<ReportUserDetailInfo> reportInfo = new ArrayList<>(); // TODO 是LIST还是一条数据???

    /**
     * 检验项目列表
     */
    @XmlElement(name = "CHECK_LIST")
    private List<ReportCheckList> checkList = new ArrayList<>();

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public List<ReportUserDetailInfo> getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(List<ReportUserDetailInfo> reportInfo) {
        this.reportInfo = reportInfo;
    }

    public List<ReportCheckList> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<ReportCheckList> checkList) {
        this.checkList = checkList;
    }
}
