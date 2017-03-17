package com.proper.enterprise.isj.webservices.model.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeRegInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    /**
     * 医生排班分时信息集合
     */
    @XmlElement(name = "TIME_REG_LIST")
    private List<TimeReg> timeRegList = new ArrayList<>();

    public List<TimeReg> getTimeRegList() {
        return timeRegList;
    }

    public void setTimeRegList(List<TimeReg> timeRegList) {
        this.timeRegList = timeRegList;
    }
}
