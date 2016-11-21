package com.proper.enterprise.isj.proxy.document.medicalreports;

import java.util.ArrayList;
import java.util.List;

/**
 * 检验/检查报告详细
 */
public class MedicalReportsDetailDocument extends MedicalReportsDocument {
    /**
     * 患者姓名
     */
    private String patient;
    /**
     * 检查医生
     */
    private String inspectionDoctor;
    /**
     * 审核医生
     */
    private String examineDoctor;
    /**
     * 结果列表
     */
    private List<MedicalReportsItemDocument> items = new ArrayList<>();

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getInspectionDoctor() {
        return inspectionDoctor;
    }

    public void setInspectionDoctor(String inspectionDoctor) {
        this.inspectionDoctor = inspectionDoctor;
    }

    public String getExamineDoctor() {
        return examineDoctor;
    }

    public void setExamineDoctor(String examineDoctor) {
        this.examineDoctor = examineDoctor;
    }

    public List<MedicalReportsItemDocument> getItems() {
        return items;
    }

    public void setItems(List<MedicalReportsItemDocument> items) {
        this.items = items;
    }
}
