package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by think on 2016/10/25 0025.
 * his返回排班错误的记录日志表,挂号中将这部分人过滤掉
 */
@Document(collection = "schedule_mistake_log")
public class ScheduleMistakeLogDocument  extends BaseDocument {

    /**
     * 医生Id
     */
    private String doctorId;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 排班日期
     */
    private String regDate;

    /**
     * 科室
     */
    private String deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 错误自定义代码
     */
    private String mistakeCode;

    /**
     * 出错描述
     */
    private String mistakeDesc;

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getMistakeDesc() {
        return mistakeDesc;
    }

    public void setMistakeDesc(String mistakeDesc) {
        this.mistakeDesc = mistakeDesc;
    }

    public String getMistakeCode() {
        return mistakeCode;
    }

    public void setMistakeCode(String mistakeCode) {
        this.mistakeCode = mistakeCode;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
