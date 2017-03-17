package com.proper.enterprise.isj.webservices.model.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.isj.webservices.model.res.doctorinfo.Doctor;

/**
 * Created by think on 2016/8/24 0024.
 *
 */
@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class DoctorInfo implements Serializable {

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
     * 医生信息集合
     */
    @XmlElement(name = "DOCTOR_LIST")
    private List<Doctor> doctorList = new ArrayList<>();

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }
}
