package com.proper.enterprise.isj.proxy.document.doctor;

import com.proper.enterprise.isj.proxy.document.DoctorDocument;

/**
 * Created by think on 2016/9/7 0007. 排班医生号点信息
 */

public class ScheDoctorDocument extends DoctorDocument {

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
