package com.proper.enterprise.isj.webservices.model.res;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.proper.enterprise.isj.webservices.model.res.stopreg.StopReg;

/**
 * Created by think on 2016/10/2 0002.
 */
@XmlRootElement(name = "RES")
@XmlAccessorType(XmlAccessType.FIELD)
public class StopRegInfo implements Serializable {

    @XmlElement(name = "STOP_LIST")
    private List<StopReg> stopRegList = new ArrayList<>();

    public List<StopReg> getStopRegList() {
        return stopRegList;
    }

    public void setStopRegList(List<StopReg> stopRegList) {
        this.stopRegList = stopRegList;
    }
}
