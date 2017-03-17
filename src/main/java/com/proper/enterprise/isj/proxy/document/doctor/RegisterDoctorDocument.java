package com.proper.enterprise.isj.proxy.document.doctor;

import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.support.VersionEnum;

/**
 * Created by think on 2016/8/16 0016.
 */
public class RegisterDoctorDocument extends DoctorDocument {

    /**
     * 
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    /**
     * 挂号类别编码
     */
    private String clinicCategoryCode;

    /**
     * 挂号金额,example:7.00
     */
    private String amount;

    public String getClinicCategoryCode() {
        return clinicCategoryCode;
    }

    public void setClinicCategoryCode(String clinicCategoryCode) {
        this.clinicCategoryCode = clinicCategoryCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
