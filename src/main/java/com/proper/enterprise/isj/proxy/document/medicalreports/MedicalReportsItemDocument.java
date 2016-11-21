package com.proper.enterprise.isj.proxy.document.medicalreports;

import java.io.Serializable;

/**
 * 检查报告单元列表
 */
public class MedicalReportsItemDocument implements Serializable {

    private static final long serialVersionUID = -1l;

    /**
     * 明细ID
     */
    private String id;
    /**
     * 明细项目名称
     */
    private String name;
    /**
     * 结果
     */
    private String result;
    /**
     * 参考
     */
    private String reference;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
