package com.proper.enterprise.isj.proxy.document.doctor;

import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.support.VersionEnum;

/**
 * Created by think on 2016/9/7 0007. 排班医生号点信息
 */

public class ScheDoctorDocument extends DoctorDocument {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    private int total;

    private int overCount;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOverCount() {
        return overCount;
    }

    public void setOverCount(int overCount) {
        this.overCount = overCount;
    }
}
