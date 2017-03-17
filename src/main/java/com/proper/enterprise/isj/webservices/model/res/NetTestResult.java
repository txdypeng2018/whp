package com.proper.enterprise.isj.webservices.model.res;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.core.utils.DateUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetTestResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;
    /**
     * 返回系统时间，格式：YYYY-MM-DD HH24:MI:SS 必填
     */
    @XmlElement(name = "SYSDATE")
    private String sysDate;

    public Date getSysDate() {
        return DateUtil.toDateTime(sysDate);
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

}
